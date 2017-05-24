package imported.com.idyria.osi.tea.listeners

import scala.reflect.ClassTag

trait ListenerTransient {
  
}

/**
 *
 * Trait to be implemented by classes whishing to offer support for listening points
 *
 * FIXME: Documentation here:
 *
 * @odfi-doc
 *
 *
 *
 */
trait ListeningSupport {

  // Register Closures
  //-------------------

  // All the registered closures
  var listeningPoints = Map[String, scala.collection.mutable.Set[(() => Unit)]]()

  var listeningPointsWith = Map[String, scala.collection.mutable.Set[(Any => Unit)]]()

  // Deregister
  //---------------
  
  /**
   * Deregister a closure from where it could be
   */
  def deregister(listener: AnyRef) = {

    listeningPoints.foreach {
      case (id, closures) =>
        
        closures.find( c => c==listener) match {
          case Some(toRemove) => closures.remove(toRemove)
          case None => 
        }
       
    }
    
    listeningPointsWith.foreach {
      case (id, closures) =>
        
        closures.find( c => c==listener) match {
          case Some(toRemove) => closures.remove(toRemove)
          case None => 
        }
       
    }

  }

  /**
   * Used to provide a closure to be run on a specific listening point
   *
   * Example:
   *
   * instance.on "listening.point.name" {
   * println("Hello World from closure")
   * }
   *
   */
  def on(point: String)(closure: => Unit) : (() => Unit) = {

    // println("Registering Closure for point "+point)

    // Prepare Final closure
    var wrappedClosure = { () => closure }

    // - Create Set if non existend
    // - Update otherwise
    //-------------------
    this.listeningPoints.get(point) match {
      case Some(set) => set += wrappedClosure
      case None => {

        this.listeningPoints += (point -> scala.collection.mutable.Set[(() => Unit)](wrappedClosure))

        //println("Setting up set, size is now: "+this.listeningPoints.size)
      }
    }

    // Return reference to closure
    return wrappedClosure
  }

  def onWith[T <: Any](point: String)(closure: T => Unit)(implicit tag:ClassTag[T]): T => Unit = {

    // println("Registering Closure for point "+point)

    // Prepare closure
    var cl = { 
      (in: Any) => 
        if (tag.runtimeClass.isAssignableFrom(in.getClass())) {
          closure(in.asInstanceOf[T]) 
        }
        
    }
    
    // - Create Set if non existend
    // - Update otherwise
    //-------------------
    this.listeningPointsWith.get(point) match {
      case Some(set) => 
        set += cl
        //this.listeningPointsWith += (point -> scala.collection.mutable.Set[(Any => Unit)](cl))
      case None => {

        this.listeningPointsWith += (point -> scala.collection.mutable.Set[(Any => Unit)](cl))

        //println("Setting up set, size is now: "+this.listeningPoints.size)
      }
    }
    return cl
  }
  
  /**
   * Listener removed after one hit
   */
  def onWithTransient[T <: Any](point: String)(closure: T => Unit)(implicit tag:ClassTag[T]): T => Unit = {

    var realCl = new Function1[T,Unit] with ListenerTransient {
      def apply(v:T) = {
        closure(v)
      }
    }
    
    var id  = this.onWith[T](point) {
      v=> 
      realCl(v)
    }
    
    id
  }

  def onMatch(point: String)(closure: PartialFunction[Any, Unit]) : Any => Unit = {

    // println("Registering Closure for point "+point)

    var cl = { (in: Any) => closure(in) }
    
    // - Create Set if non existend
    // - Update otherwise
    //-------------------
    this.listeningPointsWith.get(point) match {
      case Some(set) => set += cl
      case None => {

        this.listeningPointsWith += (point -> scala.collection.mutable.Set[(Any => Unit)](cl))

        //println("Setting up set, size is now: "+this.listeningPoints.size)
      }
    }
    
    return cl
  }

  // Call Listenening point
  //-----------------

  /**
   * This method is used to call the closures registered for the listenerning point
   *
   * @param point The Listenening point name to be called
   */
  def @->[T <: Any](point: String) = {

    //println("Running point "+point+", size: "+this.listeningPoints.size)

    this.listeningPoints.get(point) match {
      case Some(set) => set.foreach(cl => cl())
      case None      =>
    }
  }

  def @->[T <: Any](point: String, input: T) = {

    //println("Running point "+point+", size: "+this.listeningPoints.size)

    this.listeningPointsWith.get(point) match {
      case Some(set) => set.foreach {
       cl =>
         try {
         cl(input)
         } finally {
           if (cl.isInstanceOf[ListenerTransient]) {
             deregister(cl)
           }
         }
      }
      case None      =>
    }
  }

}
