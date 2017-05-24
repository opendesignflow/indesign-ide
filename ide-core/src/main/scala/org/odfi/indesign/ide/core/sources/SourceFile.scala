package org.odfi.indesign.ide.core.sources

import org.odfi.indesign.core.harvest.fs.HarvestedFile
import java.nio.file.Path
import org.odfi.indesign.core.resources.TextSourceResource
import org.odfi.indesign.core.resources.FileTextSourceResource
import org.odfi.indesign.core.resources.StringTextSourceResource

trait SourceCodeProvider extends TextSourceResource {
  
}

class SourceCodeFile(p:Path) extends FileTextSourceResource(p) with SourceCodeProvider {
  

  
}

//  with CodeSource 
class SourceCodeStringContent(str:String) extends StringTextSourceResource(str)  with SourceCodeProvider {
  
}

/*trait JavaSourceFile extends SourceFile {
  
  def ensureCompiled
  def loadClass: Class[_]
}*/