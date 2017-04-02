package org.odfi.indesign.ide.www.welcome

import org.odfi.indesign.ide.www.IDEBaseView

class WelcomeIDEView extends IDEBaseView {
   
  this.placePage {
    
    div {
      
      h1("Welcome to Indesign IDE") {
        
      }
      
      a("https://idp.scc.kit.edu/idp/profile/SAML2/Redirect/SSO") {
        text("Go to S") 
      }
      
      
    }
    
    
  }
  
}