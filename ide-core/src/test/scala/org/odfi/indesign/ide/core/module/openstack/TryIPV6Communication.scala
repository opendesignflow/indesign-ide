package org.odfi.indesign.ide.core.module.openstack

import java.net.Inet6Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

object TryIPV6Communication extends App {
  
  var address = "2001:41d0:302:1100::2:549f"
  
  var inetAddress = InetAddress.getByName(address)
  
  var socketAddress = new InetSocketAddress(inetAddress,22)
  
  var socket = new Socket()
  socket.connect(socketAddress,200)
  
  
}