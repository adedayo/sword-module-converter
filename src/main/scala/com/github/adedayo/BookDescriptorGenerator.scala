package com.github.adedayo

import java.nio.file.Paths

import com.fasterxml.jackson.databind.{MapperFeature, ObjectMapper}
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule

//For names, see https://www.logos.com/bible-book-abbreviations
object BookDescriptorGenerator {
  private val mapper = new ObjectMapper(new YAMLFactory)
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)

  var allNames = Set.empty[String]
  val bibles = List(
    BibleBook("Genesis", List("Gen", "Gn")).register(),
    BibleBook("Exodus", List("Ex")).register(),
    BibleBook("Leviticus", List("Lev", "Lv")).register(),
    BibleBook("Numbers", List("Num", "Nm", "Nb")).register(),
    BibleBook("Deuteronomy", List("Deut", "Dt")).register(),
    BibleBook("Joshua", List("Josh", "Jsh")).register(),
    BibleBook("Judges", List("Judg", "Jdg", "Jg", "Jdgs")).register(),
    BibleBook("Ruth", List("Ruth", "Rth")).register(),
    BibleBook("1 Samuel", List("Sam", "Sm")).register(),
    BibleBook("2 Samuel", List("Sam", "Sm")).register(),
    BibleBook("1 Kings", List("Kings", "Kgs")).register(),
    BibleBook("2 Kings", List("Kings", "Kgs")).register(),
    BibleBook("1 Chronicles", List("Chron")).register(),
    BibleBook("2 Chronicles", List("Chron")).register(),
    BibleBook("Ezra", List("Ezra")).register(),
    BibleBook("Nehemiah", List("Neh")).register(),
    BibleBook("Esther", List("Est")).register(),
    BibleBook("Job", List("Job", "Jb")).register(),
    BibleBook("Psalms", List("Ps", "Pslm", "Psm", "Pss")).register(),
    BibleBook("Proverbs", List("Prov", "Prv")).register(),
    BibleBook("Ecclesiastes", List("Eccles", "Qoh")).register(),
    BibleBook("Song of Solomon", List("Song", "Song of Songs", "SOS", "Canticle of Canticles", "Canticles", "Cant")).register(),
    BibleBook("Isaiah", List("Isa")).register(),
    BibleBook("Jeremiah", List("Jer", "Jr")).register(),
    BibleBook("Lamentations", List("La")).register(),
    BibleBook("Ezekiel", List("Ezek", "Ezk")).register(),
    BibleBook("Daniel", List("Dan", "Dn")).register(),
    BibleBook("Hosea", List("Hos")).register(),
    BibleBook("Joel", List("Joel", "Jl")).register(),
    BibleBook("Amos", List("Amos")).register(),
    BibleBook("Obadiah", List("Obad")).register(),
    BibleBook("Jonah", List("Jonah", "Jnh", "Jon")).register(),
    BibleBook("Micah", List("Mic", "Mc")).register(),
    BibleBook("Nahum", List("Nah")).register(),
    BibleBook("Habakkuk", List("Hab", "Hb")).register(),
    BibleBook("Zeph", List("Zeph", "Zp")).register(),
    BibleBook("Haggai", List("Hag", "Hg")).register(),
    BibleBook("Zechariah", List("Zech", "Zc")).register(),
    BibleBook("Malachi", List("Mal", "Ml")).register(),
    BibleBook("Matthew", List("Matt", "Mt")).register(),
    BibleBook("Mark", List("Mark", "Mrk", "Mk", "Mr")).register(),
    BibleBook("Luke", List("Luke", "Lk")).register(),
    BibleBook("John", List("John", "Jhn", "Jn")).register(),
    BibleBook("Acts", List("Acts")).register(),
    BibleBook("Romans", List("Rom", "Rm")).register(),
    BibleBook("1 Corinthians", List("Cor")).register(),
    BibleBook("2 Corinthians", List("Cor")).register(),
    BibleBook("Galatians", List("Gal")).register(),
    BibleBook("Ephesians", List("Eph")).register(),
    BibleBook("Philippians", List("Phil", "Php", "Pp")).register(),
    BibleBook("Colossians", List("Col")).register(),
    BibleBook("1 Thessalonians", List("Thess")).register(),
    BibleBook("2 Thessalonians", List("Thess")).register(),
    BibleBook("1 Timothy", List("Tim")).register(),
    BibleBook("2 Timothy", List("Tim")).register(),
    BibleBook("Titus", List("Titus")).register(),
    BibleBook("Philemon", List("Philem", "Phm", "Pm")).register(),
    BibleBook("Hebrews", List("Heb")).register(),
    BibleBook("James", List("James", "Jas", "Jm")).register(),
    BibleBook("1 Peter", List("Pet", "Pt")).register(),
    BibleBook("2 Peter", List("Pet", "Pt")).register(),
    BibleBook("1 John", List("John", "Jhn", "Jn")).register(),
    BibleBook("2 John", List("John", "Jhn", "Jn")).register(),
    BibleBook("3 John", List("John", "Jhn", "Jn")).register(),
    BibleBook("Jude", List("Jude", "Jd")).register(),
    BibleBook("Revelation", List("Rev", "The Revelation")).register()
  )

  def main(args: Array[String]): Unit = {
    val abbrevs = bibles.flatMap(b => b.abbreviations.map(ab => {
      List(BookAbbreviation(abbrev = ab.replace('.', ' '), name = b.name,
        canonicalAbbreviation = b.abbreviations.head)) ++ {
        if (ab.charAt(0).isDigit || ab.toUpperCase.startsWith("I.") || ab.toUpperCase.startsWith("II.") || ab.toUpperCase.startsWith("III.")) {
          List(BookAbbreviation(abbrev = ab.replace(".", ""), name = b.name,
            canonicalAbbreviation = b.abbreviations.head))
        } else
          List.empty
      }
    }
    )).flatten
    abbrevs.foreach(println)
    val basePath = Paths.get("books", "abbreviations")
    basePath.toFile.mkdirs()
    val out = basePath.resolve("abbreviations.yaml").toFile
    mapper.writeValue(out, BibleAbbreviations(abbreviations = abbrevs))
  }
}


case class BibleBook(name: String, abbreviations: List[String]) {
  def register(): BibleBook = {
    val canonicalAbbreviations = {
      val (pref, _) = getPrefixes
      val canonicals = makeCanonicalPrefixes(pref)
      abbreviations.flatMap(book => canonicals.flatMap(pre => List(s"$pre$book",s"$pre${book.toLowerCase}" )))
    }
    BookDescriptorGenerator.allNames ++= canonicalAbbreviations
    val abbrevs = generateAlternatives()
    this.copy(abbreviations = canonicalAbbreviations ++ abbrevs)
  }

  private def generateAlternatives(): List[String] = {
    val (prefix, prefixLessName) = getPrefixes
    val prefixes = makeCanonicalPrefixes(prefix)
    val alternatives = for (pref <- prefixes; namePrefix <- prefixSequence(prefixLessName)) yield {
      val abbrv = s"$pref$namePrefix"
      if (!BookDescriptorGenerator.allNames.contains(abbrv)) {
        BookDescriptorGenerator.allNames += abbrv
        List(abbrv)
      } else {
        List.empty[String]
      }

    }
    alternatives.flatten
  }

  private def makeCanonicalPrefixes(prefix: String) = {
    prefix match {
      case "1" => List("1.", "I.", "i.", "First.", "first.")
      case "2" => List("2.", "II.", "ii.", "Second.", "second.")
      case "3" => List("3.", "III.", "iii.", "Third.", "third.")
      case _ => List("")
    }
  }

  private def getPrefixes = {
    if (name.charAt(0).isDigit) (name.charAt(0).toString, name.substring(2)) else ("", name)
  }

  def prefixSequence(pName: String): List[String] = {
    val seq = for (i <- 1 to pName.length) yield {
      val n = pName.substring(0, i)
      List(n, n.toLowerCase)
    }
    seq.flatten.toList
  }
}

case class BibleAbbreviations(description: String = "Common Bible Book Abbreviations", abbreviations: List[BookAbbreviation])

case class BookAbbreviation(abbrev: String, name: String, canonicalAbbreviation: String)