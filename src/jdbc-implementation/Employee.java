package jdbc-implementation;
import java.util.ArrayList;

public class Employee {
	private Integer id;
	private String name = "";
	private Integer salary = 0;
	ArrayList<Integer> employeeList = new ArrayList<Integer>();

	// Constructor
	public Employee(Integer id) {
		super();
		this.id = id;
	}

	// Constructor
	 public Employee(Integer id, String name, Integer salary) {
		super();
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	// toString Method Override to convert into a string
	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", salary=" + salary
				+ ", employees=" + employeeList + "]";
	}

	// Getter for id
	public int getId() {
		return id;
	}

	// Getter for name
	public String getName() {
		return name;
	}

	//Getter for salary
	public int getSalary() {
		return salary;
	}

	// Getter for employee list
	public ArrayList<Integer> getEmployees() {
		return employeeList;
	}
	
	// Method to add an employee id to the list of employee ids
	public void addEmployee(Integer employeeID) {
		employeeList.add(employeeID);
	}
}