package codelionx.eportfolio.demos.composite;
import java.util.ArrayList;
import java.util.List;
import java.lang.StringBuilder;

/**
* Demo-class implementing the Composite-Pattern. <br/>
*/
public class Employee {
  private String name;
  private String dept;
  private int salary;
  private List<Employee> subordinates;

  // constructor
  public Employee(String name,String dept, int sal) {
     this.name = name;
     this.dept = dept;
     this.salary = sal;
     subordinates = new ArrayList<Employee>();
  }

  public void add(Employee e) {
     subordinates.add(e);
  }

  public void remove(Employee e) {
     subordinates.remove(e);
  }

  public List<Employee> getSubordinates(){
    return subordinates;
  }

  // print employee information and do that also recursivly for all subordinates
  public String toString(){
    StringBuilder builder = new StringBuilder();
    builder.append("Employee :[ Name : " + name + ", dept : " + dept + ", salary :"
      + salary+", subordinates = \n");
    for(Employee e : this.subordinates) {
      builder.append("\t" + e + "\n");
    }
    builder.append(" ]");
    return builder.toString();
  }

  // Usage:
  public static void main(String[] args) {
    Employee CEO = new Employee("John","CEO", 30000);
    Employee headSales = new Employee("Robert","Head Sales", 20000);
    CEO.add(headSales);

    System.out.println(CEO);
    // prints the CEO and his subordinates: Head Sales
  }
}
