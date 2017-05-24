package org.odfi.indesign.ide.core.sources.outline

import org.odfi.indesign.ide.core.sources.SourceCodeProvider
import org.odfi.indesign.ide.core.regexp.RegexpUtils

trait SourceOutlineProvider extends SourceCodeProvider with RegexpUtils {
  
  var outlineClass : Option[String] = None
  
  def toOutline: Option[Outline]
  
}
 
trait SourceOutlineProviderWithErrorCatching extends SourceOutlineProvider {

  var currentHiearchy: Option[Outline] = None

  def toOutline = {
    try {

      currentHiearchy = Some(buildOutline)

    } catch {
      case e: Throwable =>
        e.printStackTrace()
    }

    currentHiearchy
  }

  def buildOutline: Outline

}