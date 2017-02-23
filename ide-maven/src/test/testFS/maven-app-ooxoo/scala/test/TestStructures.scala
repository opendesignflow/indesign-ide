package test
            

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import scala.language.implicitConversions
            
@xelement(name="TestStructures")
class TestStructures extends com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer  {
            
    @xattribute(name="name")
    var __name : com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer = null
                        
    def name_=(v:com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer) = __name = v
                        
    def name : com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer = __name 
                        
}
object TestStructures {

    def apply() = new TestStructures
    
    
def apply(url : java.net.URL) = {
  
  // Instanciate
  var res = new TestStructures
  
  // Set Stax Parser and streamIn
  var io = com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer(url)
  res.appendBuffer(io)
  io.streamIn
  
  // Return
  res
  
}


    

def apply(xml : String) = {
  
  // Instanciate
  var res = new TestStructures
  
  // Set Stax Parser and streamIn
  var io = com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer(xml)
  res.appendBuffer(io)
  io.streamIn
  
  // Return
  res
  
}

}
