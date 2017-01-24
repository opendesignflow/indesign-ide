package com.idyria.osi.tea.thread

/**
 * A trait to provide a few utility methods to run code on Threads
 */
trait ThreadLanguage {
  
  def fork(cl: => Any) : Thread = {
    
    //-- Create Thread in local thread group
    var th = new Thread(new Runnable {
      def run() = {
        cl
      } 
    })
    //th.getThreadGroup()
    th.start
    th
    
  }
  
  /**
   * Forks the provided closure in a Thread and waits for it to end
   */
  def forkJoin[T](cl: => T) :T = {
    
    //-- Create Thread in local thread group
    var res : Any = null
    var th = new Thread(new Runnable {
      def run() = {
        res = cl
      }
    })
    //th.getThreadGroup()
    th.start
    th.join
    
    res.asInstanceOf[T]
  }
  
  def createThread (cl: => Any) : Thread = {
    var th = new Thread(new Runnable {
      def run() = {
        cl
      }
    })
    th
  }
  
}