package codelionx.eportfolio.demos.decorator;
public interface Component {
   public void performOperation();
}

package codelionx.eportfolio.demos.decorator;
public class ConcreteComponent {
  @Override
  public void performOperation() {
    System.out.println("TextView");
  }
}

package codelionx.eportfolio.demos.decorator;
public abstract class Decorator implements Component {
   protected Component component;

   public Decorator(Component component){
      this.component = component;
   }

   @Override
   public void performOperation(){
      component.performOperation();
   }
}

package codelionx.eportfolio.demos.decorator;
public class ConcreteDecoratorA extends Decorator {

   public ConcreteDecoratorA(Shape component) {
      super(component);
   }

   @Override
   public void performOperation() {
      component.performOperation();
      setRedBorder(component);
   }

   private void setRedBorder(Shape component){
      System.out.println(" with red coloured border");
   }
}

package codelionx.eportfolio.demos.decorator;
public class ConcreteDecoratorB extends Decorator {

   public ConcreteDecoratorB(Shape component) {
      super(component);
   }

   @Override
   public void performOperation() {
      component.performOperation();
      setRedBorder(component);
   }

   private void setRedBorder(Shape component){
      System.out.println(" with scroll bar");
   }
}

package codelionx.eportfolio.demos.decorator;
public class Demo {
  public static void main(String[] args) {
    Component component = new ConcreteComponent();
    component.performOperation(); // TextView
    Component decoratorA = new ConcreteDecoratorA(component);
    decoratorA.performOperation(); // TextView
                                   // with red coloured border
    Component decoratorB = new ConcreteDecoratorB(decoratorA);
    decoratorB.performOperation(); // TextView
                                   // with red coloured border
                                   // with scroll bar
  }
}
