package com.github.adedayo

object MyOSISUtil {
  def languageCode(lang: String):String = {
    lang.toLowerCase match {
      case "english" => "en"
      case x if x.length > 1 => //lazily attempts english only, others first two characters
        x.substring(0,2)
      case x => x //give up
    }
  }

  val map = Map("Genesis" -> "Gen",
    "Exodus" -> "Exod",
    "Leviticus" -> "Lev",
    "Numbers" -> "Num",
    "Deuteronomy" -> "Deut",
    "Joshua" -> "Josh",
    "Judges" -> "Judg",
    "Ruth" -> "Ruth",
    "1 Samuel" -> "1Sam",
    "2 Samuel" -> "2Sam",
    "1 Kings" -> "1Kgs",
    "2 Kings" -> "2Kgs",
    "1 Chronicles" -> "1Chr",
    "2 Chronicles" -> "2Chr",
    "Ezra" -> "Ezra",
    "Nehemiah" -> "Neh",
    "Esther" -> "Esth",
    "Job" -> "Job",
    "Psalms" -> "Ps",
    "Proverbs" -> "Prov",
    "Ecclesiastes" -> "Eccl",
    "Song of Solomon" -> "Song",
    "Isaiah" -> "Isa",
    "Jeremiah" -> "Jer",
    "Lamentations" -> "Lam",
    "Ezekiel" -> "Ezek",
    "Daniel" -> "Dan",
    "Hosea" -> "Hos",
    "Joel" -> "Joel",
    "Amos" -> "Amos",
    "Obadiah" -> "Obad",
    "Jonah" -> "Jonah",
    "Micah" -> "Mic",
    "Nahum" -> "Nah",
    "Habakkuk" -> "Hab",
    "Zephaniah" -> "Zeph",
    "Haggai" -> "Hag",
    "Zechariah" -> "Zech",
    "Malachi" -> "Mal",
    "Matthew" -> "Matt",
    "Mark" -> "Mark",
    "Luke" -> "Luke",
    "John" -> "John",
    "Acts" -> "Acts",
    "Romans" -> "Rom",
    "1 Corinthians" -> "1Cor",
    "2 Corinthians" -> "2Cor",
    "Galatians" -> "Gal",
    "Ephesians" -> "Eph",
    "Philippians" -> "Phil",
    "Colossians" -> "Col",
    "1 Thessalonians" -> "1Thess",
    "2 Thessalonians" -> "2Thess",
    "1 Timothy" -> "1Tim",
    "2 Timothy" -> "2Tim",
    "Titus" -> "Titus",
    "Philemon" -> "Phlm",
    "Hebrews" -> "Heb",
    "James" -> "Jas",
    "1 Peter" -> "1Pet",
    "2 Peter" -> "2Pet",
    "1 John" -> "1John",
    "2 John" -> "2John",
    "3 John" -> "3John",
    "Jude" -> "Jude",
    "Revelation" -> "Rev")

  def convertToOSISID(name: String) : String = {
    if (map.contains(name))
      map(name)
    else
      "Uknown"

  }
}
