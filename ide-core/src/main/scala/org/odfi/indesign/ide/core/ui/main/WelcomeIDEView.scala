package org.odfi.indesign.ide.core.ui.main

class WelcomeIDEView extends IDEBaseView {
   
  this.definePart("page-body") {
    
    div {
      
      h1("Welcome to Indesign IDE") {
        
      }
      
      a("https://idp.scc.kit.edu/idp/profile/SAML2/Redirect/SSO") {
        text("Go to S") 
      }
      
      
    }
    
    
  }
  
}