package codelionx.eportfolio.demos.singleton;
/**
 * Demo-class implementing the Singleton-Pattern. <br/>
 * <strong>This implementation is not thread-safe</strong>
 */
public class SingleObject {

  // class attribute holding the instance
  private static SingleObject instance;

  // private constructor (can only be used inside this class)
  private SingleObject() {}

  // method for accessing the one instance which is created at the first method
  // call
  private static void getInstance() {
    if(SingleObject.instance == null) {
      SingleObject.instance = new SingleObject();
    }
    return SingleObject.instance;
  }

  // action
  public void showMessage(){
      System.out.println("Hello World!");
   }

  // usage:
  public static void main(String[] args) {
    SingleObject singleton = SingleObject.getInstance();
    singleton.showMessage();
  }
}


// -----------------------------------------------------------------------------
package codelionx.eportfolio.demos.singleton;
/**
 * Thread-safe version of the Singleton-Pattern. <br/>
 * Nevertheless, the instance is created initially by the first Thread entering
 * the <code>getInstace()</code>-method.
 */
 public class SingleObjectTS {

   private SingleObjectTS () {}
     
   public static SingleObjectTS getInstance () {
     return InstanceHolder.INSTANCE;
   }

   public void showMessage(){
       System.out.println("Hello World!");
    }

   // inner private class holding the only instance
   private static final class InstanceHolder {
     // ClassLoader synchronizes the access implizitly
     // this code only runs one time through the ClassLoader
     static final SingleObjectTS INSTANCE = new SingleObjectTS();
   }

   // usage:
   public static void main(String[] args) {
     SingleObjectTS singleton = SingleObjectTS.getInstance();
     singleton.showMessage();
   }
 }
