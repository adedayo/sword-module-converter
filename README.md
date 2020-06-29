# Sword Module Converter

Automates the conversion of encoded sword modules (and a few other formats) to parsable textual files such as YAML 
and OSIS format.

Written primarily as a tool to help me automate the extraction of bible verses for various use cases
such as preparing teaching slides.

If someone stumbles upon this and thinks they may benefit from a more shrink-wrapped distributable version
please drop a note or if you wish to contribute, ask for a feature or extend it for the Kingdom, please reach out.  

This tool is never intended to be used to make money or to infringe copyright - it is purely for the edification of the
body of Christ. 

## Developer notes

Uses JSword libary, which may be found here: git@github.com:crosswire/jsword.git

* Use Maven build a local copy of of the JSword package 
* Add as dependency to the project as done here - see [Pom](pom.xml) file
* Ensure you have the sword module required downloaded in the standard locations, for example, install Eloquent or 
BibleDesktop and use those front-ends to download Sword modules from various public sources
* Use the Books class to load installed modules, extract and transform as desired. The heavy lifting has been done
by JSword


## Sample Books

Sample extracted bibles in different output formats may be found in [books](books)


## Other public domain books
The directory [raw](raw) contains public domain books (that I could not find in Crosswire collections), which may or may not be sword module format,
 but I have annotated them so that they could also be read and transformed by this tool. Each book 
 is in its own dedicated directory and contains annotations to help parse it and other metadata such as where the 
 copy was obtained from
