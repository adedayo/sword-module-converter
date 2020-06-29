package com.github.adedayo

import scala.xml.{Elem, Node}

case class NonSwordMetadata(fileName: String, abbreviation: String, name: String, source: String, format: String, language: String)

case class BibleModel(Abbreviation: String, Name: String, Language: String, Source: String, Content: List[Content]) {

  private def contentToOSIS(): Elem = {
    var book = ""
    var chapter = ""

    var bookGroup: Elem = <div type="bookGroup"></div>
    var bE: Elem = null
    var cE: Elem = null
    var vE: Elem = null

    var books = Seq.empty[Node]
    var chapters = Seq.empty[Node]
    var verses = Seq.empty[Node]
    Content.foreach(c => {

      if (c.Book != book) {
        book = c.Book
        chapter = c.Chapter
        if (verses.nonEmpty) {
          //we've been processing a previous book, add all the verses and chapters found so far
          cE = cE.copy(child = cE.child ++ verses)
          verses = Seq.empty[Node]
          chapters = chapters ++ cE
          bE = bE.copy(child = bE.child ++ chapters)
          chapters = Seq.empty[Node]
          books ++= bE
        }
        //create first verse and chapter and this book
        vE = <verse osisID={s"${c.Book}.${c.Chapter}.${c.Verse}"}>
          {c.Text}
        </verse>
        verses ++= vE
        cE = <chapter osisID={s"${c.Book}.${c.Chapter}"}>

        </chapter>
        bE = <div type="book" osisID={c.Book}>

        </div>
        //        bookGroup = bookGroup.copy(child = bookGroup.child ++ bE)
      } else if (c.Chapter != chapter) {
        chapter = c.Chapter
        //add previous verses to previous chapter and store that chapter away
        cE = cE.copy(child = cE.child ++ verses)
        verses = Seq.empty[Node]
        chapters ++= cE

        //create first verse and this chapter
        vE = <verse osisID={s"${c.Book}.${c.Chapter}.${c.Verse}"}>
          {c.Text}
        </verse>
        verses ++= vE
        cE = <chapter osisID={s"${c.Book}.${c.Chapter}"}></chapter>
      } else {
        vE = <verse osisID={s"${c.Book}.${c.Chapter}.${c.Verse}"}>
          {c.Text}
        </verse>
        verses ++= vE
      }
    })
    //add final verses and chapter and book to collections
    cE = cE.copy(child = cE.child ++ verses)
    chapters = chapters ++ cE
    bE = bE.copy(child = bE.child ++ chapters)
    books ++= bE
    bookGroup = bookGroup.copy(child = bookGroup.child ++ books)
    bookGroup
  }

  def generateOSIS(): Elem = {
    //    <?xml version="1.0" encoding="UTF-8"?>
    <osis xmlns="http://www.bibletechnologies.net/2003/OSIS/namespace"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://www.bibletechnologies.net/2003/OSIS/namespace http://www.bibletechnologies.net/osisCore.2.1.1.xsd">
      <osisText osisIDWork={Abbreviation} osisRefWork="bible" xml:lang={MyOSISUtil.languageCode(Language)} canonical="true">

        <header>
          <work osisWork={Abbreviation}>
            <title source={Source}>
              {Name}
            </title>
          </work>
          <work osisWork="defaultReferenceScheme">
            <refSystem>Bible.
              {Abbreviation}
            </refSystem>
          </work>
        </header>{contentToOSIS()}

      </osisText>
    </osis>
  }

}

case class Content(Book: String, Chapter: String, Verse: String, Text: String)