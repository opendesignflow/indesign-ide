package org.odfi.indesign.ide.core.sources.analyze

/**
 * Tries to find useful stuff in comments
 */
trait CCommentAnalyzerHelper extends SourceCodeAnalyzer {
  
  val cCommentRegexp = """(?m)\/\/.*$""".r
  val cMultiCommentRegexp = """\/\*([^*\/]*\n)+\*\/""".r
  
  def getTextWithoutCComment = {
    
    var text = getTextContent
    
    // Match Comments
    cCommentRegexp.findAllMatchIn(text).foreach {
      m => 
        println("Found C "+m.start+" -> "+m.end)
        text = text.replace(m.matched,"")
        
    }
    
    // Multiline comment
    cMultiCommentRegexp.findAllMatchIn(text).foreach {
      m => 
        println("Found m "+m.start+" -> "+m.end)
        var lines = m.matched.split("\n").map { l => ""}.mkString("\n","\n","\n")
        text = text.replace(m.matched,lines)
    }
    
    text
    
  }
  
  
  def commentBeforeLine(line:Int) = {
    
    var targetLine = line - 1
    var lines = this.getTextLines
    (targetLine >=0 && targetLine<lines.length) match {
      case true => 
        val foundLine = this.getTextLines(targetLine)
        
        // Check
        foundLine.trim().startsWith("//") match {
          case true =>
            Some(foundLine.trim().stripPrefix("//").trim())
          case false => 
            None
        }
        
        
      case false => 
        None
    }
    
  }
  
}