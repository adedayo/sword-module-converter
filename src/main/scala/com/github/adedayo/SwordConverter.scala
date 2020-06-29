package com.github.adedayo

import java.io.{FileInputStream, FileOutputStream}
import java.nio.file.{Files, Path, Paths}
import java.util.zip.ZipInputStream

import com.fasterxml.jackson.databind.{MapperFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.commons.compress.utils.IOUtils
import org.crosswire.jsword.book._

import scala.jdk.CollectionConverters._
import scala.xml._

object SwordConverter {

  val elementStripper = "<[^>]*>"

  private val mapper = new ObjectMapper(new YAMLFactory)
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)

  def main(args: Array[String]): Unit = {
    println("Converting sword bibles ...")
    convertSwordBibles()
    println("Converting other bibles ...")
    convertNonSwordBibles()
  }


  private def convertSwordBibles(): Unit = {
    var books = Books.installed().getBooks.asScala.filter(book => book.getBookCategory.name() == "BIBLE")
    for (book <- books) {
      val meta = book.getBookMetaData
      val content: List[Content] = book.getGlobalKeyList.asScala.flatMap(key => {
        val coordinate = key.getOsisID.split('.')
        if (coordinate.length > 2) {
          val Array(b, c, v, _@_*) = coordinate
          val text = book.getRawText(key).replaceAll(elementStripper, "")
          Some(Content(Book = b, Chapter = c, Verse = v, text))
        } else None
      }).toList

      serialiseBible(BibleModel(Abbreviation = meta.getAbbreviation, Name = meta.getName,
        Language = meta.getLanguage.getCode,
        Source = "https://www.crosswire.org/sword/modules/ModDisp.jsp?modType=Bibles", Content = content))
    }
  }


  private def convertNonSwordBibles(): Unit = {
    try {
      Files.find(Paths.get("raw"), Integer.MAX_VALUE,
        (path, attribute) => attribute.isRegularFile && path.toString.endsWith(".yaml")
      ).forEach(yaml => {
        val meta = mapper.readValue(yaml.toFile, classOf[NonSwordMetadata])
        val bible = yaml.resolveSibling(meta.fileName)
        val zis = new ZipInputStream(new FileInputStream(bible.toFile))
        var ze = zis.getNextEntry
        while (ze != null) {
          if (!ze.isDirectory && ze.getName.endsWith(s".${meta.format}")) {
            processFormat(meta.format, zis, yaml.resolveSibling(ze.getName), meta)
          }
          ze = zis.getNextEntry
        }
        zis.close()

      })
    } catch {
      case x: Throwable => println(x.getMessage)
        x.printStackTrace()
    }
  }

  private def extractFile(zis: ZipInputStream, path: Path): Unit = {
    val file = path.toFile
    val out = new FileOutputStream(file)
    IOUtils.copy(zis, out)
    out.close()
  }

  private def processFormat(format: String, zis: ZipInputStream, path: Path, meta: NonSwordMetadata): Unit = {
    if (path.toString.contains("__MACOSX")) return //sidestep spurious macos temp file in Zip :-(
    format match {
      case "xmm" =>
        extractFile(zis, path)
        serialiseBible(processXMM(path, meta))

      case x => println(s"Unsuported format $x")
    }
    path.toFile.deleteOnExit()
  }

  private def processXMM(path: Path, meta: NonSwordMetadata): BibleModel = {
    def fmap(n: Node) = n.attribute("n").head.text

    val xml = XML.loadFile(path.toFile)

    val books = for (book <- (xml \ "b")) yield {
      val chapters = for (chapter <- (book \ "c")) yield {
        val verses = for (verse <- (chapter \ "v")) yield {
          Content(Book = MyOSISUtil.convertToOSISID(fmap(book)), Chapter = fmap(chapter), Verse = fmap(verse), Text = verse.text)
        }
        verses
      }
      chapters.flatten
    }
    val contents = books.flatten.toList
    BibleModel(meta.abbreviation, meta.name, meta.language, meta.source, contents)
  }


  private def serialiseBible(bible: BibleModel): Unit = {
    //    println(bible)
    val basePath = Paths.get("books", "yaml", bible.Abbreviation)
    basePath.toFile.mkdirs()
    val out = basePath.resolve(s"${bible.Abbreviation}.yaml").toFile
    mapper.writeValue(out, bible)

    val osisBible = bible.generateOSIS()
    val osisBasePath = Paths.get("books", "osis", bible.Abbreviation)
    osisBasePath.toFile.mkdirs()
    val osisPath = osisBasePath.resolve(s"${bible.Abbreviation}.xml").toString
    val prettyPrinter = new PrettyPrinter(80, 2)
    XML.save(osisPath, XML.loadString(prettyPrinter.format(osisBible)), enc = "UTF-8", xmlDecl = true)
  }


}


