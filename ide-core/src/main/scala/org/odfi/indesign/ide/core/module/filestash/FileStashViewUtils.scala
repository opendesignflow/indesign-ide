package org.odfi.indesign.ide.core.module.filestash

import org.odfi.wsb.fwapp.lib.files.semantic.FileSemanticUpload
import org.odfi.wsb.fwapp.lib.security.views.SecurityView
import org.odfi.indesign.core.module.filestash.FileStash
import org.odfi.indesign.core.module.filestash.FileStashTraitOwnerTraitStash

trait FileStashViewUtils extends FileSemanticUpload with SecurityView {

  /**
   * Upload to a new Stash and run closure when done
   */
  def fileStashUploader(validationText:String)(cl: FileStashTraitOwnerTraitStash => Any) = {

    securitySessionGetUser match {
      case Some(user) =>

        // Get an empty stash
        var stash = FileStash.getStashConfig.getOwnerStash(user.getEmail).getEmptyStash

        form {
          +@("enctype" -> """multipart/form-data""")
          +@("accept" -> "audio/*")

          semanticFilesInput("stashFile", validationText)

          hiddenInput("stashId") {
            +@("value" -> stash.eid)
          }

          onSubmit {
            //println("Upload done:" + withRequestParameter("uploadedFile"))
            val targetStash = FileStash.getStashConfig.getOwnerStash(user.getEmail)
              .getStashById(withRequiredRequestParameter("stashId")).get
              
              // If A file is being uploaded, save to stash, otherwise kick finish
            withRequestParameter("uploadedFile") match {
              case Some(fileup) =>
                println("Saving FIle to stash")
                targetStash.writeFile(fileup,request.get.getPartForFileName(withRequiredRequestParameter("uploadedFile")).get.bytes)
              case None =>
                cl(targetStash)
            }

            /* FileStash.getStashConfig.getOwnerStash(user.email)
                            .getStashById(withRequiredRequestParameter("stashId")).get
                            .writeFile(withRequiredRequestParameter("uploadedFile"),request.get.getPartForFileName(withRequiredRequestParameter("uploadedFile")).get.bytes)

            */
            
          }

        }

      case None =>
        "ui warning message" :: " File Stash Uploader must run with an authenticated user"
    }

  }

}