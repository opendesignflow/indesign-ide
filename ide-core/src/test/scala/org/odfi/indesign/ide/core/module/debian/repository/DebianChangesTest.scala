package org.odfi.indesign.ide.core.module.debian.repository

import org.scalatest.FunSuite
import java.io.File
import org.odfi.indesign.ide.core.module.debian.deb.ChangesFile

class DebianChangesTest  extends FunSuite {
  
  val changesFile = new File("src/examples/resources/debian/odfi_1.0.0-1_amd64.changes")
  
  test("File Change") {
    
    val changeFile = new ChangesFile( changesFile)
    
    val otherFiles = changeFile.getDependendFiles
    
    assertResult(4)(otherFiles.length)
    
    assertResult("odfi_1.0.0-1.dsc")(otherFiles(0).getFileName)
    
  }
  
  
  test("Test Incoming") {
    
    var repo = new DebianRepository(new File("target/test-out/debian"))
    
    repo.incomingChanges(changesFile)
    
  }
}