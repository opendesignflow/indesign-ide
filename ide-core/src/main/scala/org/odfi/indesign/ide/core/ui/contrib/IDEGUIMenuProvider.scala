package org.odfi.indesign.ide.core.ui.contrib

trait IDEGUIMenuProvider {
  
  def getMenuLinks = List[(String,String)]()
  
}