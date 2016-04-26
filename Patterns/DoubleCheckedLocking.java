package codelionx.eportfolio.demos.doublecheckedLocking;
/**
 * Demo-class implementing the Singleton-Pattern. <br/>
 * <strong>This implementation is not thread-safe</strong>
 */
public class Singleton {

  // class attribute holding the instance
  // !!-- must be VOLATILE --!!
  private static volatile Singleton instance;

  // private constructor (can only be used inside this class)
  private Singleton() {}

  // method for accessing the one instance which is created at the first method
  // call
  private static void getInstance() {
    if(Singleton.instance == null) {      // first_time_in_flag check 1
      synchronized(this) {                // acquire lock
        if(Singleton.instance == null) {  // first_time_in_flag check 2
          Singleton.instance = new Singleton();
        }
      }                                   // release lock
    }
    return Singleton.instance;
  }

  // action
  public void showMessage(){
      System.out.println("Hello World!");
   }

  // usage:
  public static void main(String[] args) {
    new Thread(){
      public void run() {
        Singleton.getInstance().showMessage();
      }
    }.start();
    new Thread(){
      public void run() {
        Singleton.getInstance().showMessage();
      }
    }.start();
  }
}
