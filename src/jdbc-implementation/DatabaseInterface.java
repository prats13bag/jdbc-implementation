package jdbc-implementation;
import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.InputStream;
import java.util.Properties;

// DatabaseInterface class is the interfaces with the database
 
public class DatabaseInterface {


	//  Please change database connection values in config.properties file only!

	static String connectionName = "jdbc:mysql://localhost:3306/";
	static String user = "root";
	static String pass = "root";
	static String dbname = "employeedata";
	static final String driver = "com.mysql.jdbc.Driver";
	Connection link = null;
	private Statement statement = null;

	// Constructor
	public DatabaseInterface() {
		setupProperties();
		setupConnection();
		getConnection();
	}

	// Method to get connection
	private void getConnection() {
		try {
			link = DriverManager.getConnection(connectionName, user, pass);
			statement = link.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(3);
		}
	}

	// Method for setting up a connection
	private void setupConnection() {
		try {
			Class.forName(driver).newInstance();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			System.exit(1);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			System.exit(2);
		} catch (InstantiationException ex) {
			ex.printStackTrace();
			System.exit(3);
		}
	}

	// Method to set up connection properties
	private void setupProperties() {
		Properties property = new Properties();
		InputStream input = null;

		// Set up the connection info
		try {

			input = new FileInputStream("config.properties");
			property.load(input);

			if (property.containsKey("dbusername")) {
				user = property.getProperty("dbusername");
			}
			if (property.containsKey("dbpassword")) {
				pass = property.getProperty("dbpassword");
			}
			if (property.contains("dbname")) {
				dbname = property.getProperty("dbname");
			}
			if (property.contains("dbserver") && property.contains("dbport")) {
				connectionName = "jdbc:mysql://" + property.getProperty("dbserver") + ":" + property.getProperty("dbport") + "/";
			} else if (property.contains("dbserver")) {
				connectionName = "jdbc:mysql://" + property.getProperty("dbserver") + "/";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Method to drop the schema at the end of the program
	protected void dropSchema() {
		try {
			statement.executeUpdate("DROP DATABASE " + dbname);
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		try {
			link.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Method to create schema and tables
	public void createDatabase() {
		try {
			statement.executeUpdate("CREATE DATABASE " + dbname);

			statement.executeUpdate("USE " + dbname);

			statement.executeUpdate("CREATE TABLE employee " +
					"(eid INT PRIMARY KEY, " +
					"name VARCHAR(20), " +
					"salary INT, CHECK (salary<=60000))");

			statement.executeUpdate("CREATE TABLE worksfor " +
					"(eid INT NOT NULL, " +
					"mid INT NOT NULL, " +
					"PRIMARY KEY (eid, mid), " +
					"FOREIGN KEY (eid) REFERENCES employee(eid) ON DELETE CASCADE ON UPDATE CASCADE, " +
					"FOREIGN KEY (mid) REFERENCES employee(eid) ON DELETE CASCADE ON UPDATE CASCADE)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method to insert an employee into the employee relation
	public void insertIntoEmployee(Employee emp) {

		ArrayList<Integer> employee_list = emp.getEmployees();
		String sql = "INSERT INTO employee " +
				"(eid, name, salary) VALUES " +
				"(" + emp.getId() + ", '" + emp.getName() + "', " + emp.getSalary() + ")";

		try {
			statement.executeUpdate(sql);
			for (int i = 0; i < employee_list.size(); i++) {
				insertIntoWorksfor(emp.getId(), employee_list.get(i));
			}
			System.out.println("done");
		} catch (Exception e) {
			System.out.println("error");
		}
	}

	// Method to insert an employee-manager relationship into worksfor relation
	private void insertIntoWorksfor(Integer employee, Integer manager) throws Exception {
		String sql = "INSERT INTO worksfor " +
					"(eid, mid) VALUES " +
					"(" + employee + ", " + manager + ")";
		if (manager != 0)
			statement.executeUpdate(sql);
	}

	// Method to delete an employee from relation 'employee'. Deletes tuples from worksfor relation also using on delete cascade.
	public void deleteFromEmployeeWithCascade(Employee emp) {

			String sql = "DELETE FROM employee " +
				"WHERE eid = " + emp.getId();
			try {
				int numRows = statement.executeUpdate(sql);
				if (numRows == 0) {
					System.out.println("error");
				}
				else {
					System.out.println("done");
				}
			}
			catch (Exception e) {
				System.out.println("error");
			}
	}

	// Method to compute the average salary of all employees
	public Integer avgSal() {

		String sql = "SELECT AVG(salary) " +
				"FROM employee ";

		ResultSet rs = null;
		try {
			rs = statement.executeQuery(sql);
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error");
		}
		return -1;
	}

	// Method to get the average salary of a list of employees
	public void avgSalOfEmps(ArrayList<Integer> idList, Integer mid) {

		String eids = "";
		double davg=0.0;
		int iavg=0;
		for (Integer id : idList) {
			eids = eids.concat(id.toString() + ", ");
		}
		if (eids.length() > 0) {
			eids = eids.substring(0, eids.length() - 2);
		} else {
			return;
		}
		String sql = "SELECT AVG(salary) " +
				"FROM employee " +
				"WHERE eid IN (" + eids + ")";

		ResultSet rs = null;
		try {
			rs = statement.executeQuery(sql);

			if (rs.next()) {
				davg = rs.getDouble(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		iavg = (int) Math.round(davg);
		System.out.println("Following is the average salary (rounded off to the nearest integer value) of all the employees working directly under Manager ID: " + mid + " - " + iavg);
		return;
	}

	// Method to get the employee names from a list of employee IDs
	public ArrayList<String> fetchEmpNames(ArrayList<Integer> idList) {

		String eids = "";
		ResultSet rs = null;
		ArrayList<String> namelist = new ArrayList<String>();

		for (Integer id : idList) {
			eids = eids.concat(id.toString() + ", ");
		}
		if (eids.length() > 0) {
			eids = eids.substring(0, eids.length() - 2);
		} else {
			return namelist;
		}
		String sql = "SELECT name " +
				"FROM employee " +
				"WHERE eid IN (" + eids + ")";

		try {
			rs = statement.executeQuery(sql);

			while (rs.next()) {
				namelist.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return namelist;
	}

	// Method to get the employee ID's of all of the employees that a person manages directly
	public ArrayList<Integer> fetchEIDSofEmpsUnderMID(Integer ID) {

		String sql = "SELECT eid " + "FROM worksfor " + "WHERE mid = " + ID;

		ArrayList<Integer> eidList = new ArrayList<Integer>();
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(sql);

			while (rs.next()) {
				eidList.add(rs.getInt(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return eidList;
	}
	// Method to get the employee ID's of all of the employees that a person manages directly or indirectly
	public ArrayList<Integer> fetchAlleids(Integer ID) {

		String sql = "SELECT eid " + "FROM worksfor " + "WHERE mid = " + ID;

		ArrayList<Integer> eidList = new ArrayList<Integer>();
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(sql);

			while (rs.next()) {
				eidList.add(rs.getInt(1));
			}

			ArrayList<Integer> employeesEmployees = new ArrayList<Integer>();
			for (Integer eid : eidList) {
				ArrayList<Integer> eidTempList = new ArrayList<Integer>();
				eidTempList = this.fetchAlleids(eid);
				for (Integer eidTemp : eidTempList) {
					if (!employeesEmployees.contains(eidTemp)) {
						employeesEmployees.add(eidTemp);
					}
				}
			}
			eidList.addAll(employeesEmployees);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return eidList;
	}

	// Method to get the names of employees that have multiple direct managers
	public ArrayList<String> getMultipleManagers() {

		String sql = "SELECT e.name " +
				"FROM employee AS e NATURAL JOIN (" +
				"SELECT wf.eid " +
				"FROM worksfor AS wf " +
				"GROUP BY wf.eid " +
				"HAVING count(*) > 1) AS w";

		ArrayList<String> nameList = new ArrayList<String>();
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(sql);
			while (rs.next()) {
				nameList.add(rs.getString(1));
			}
		} catch (Exception e) {
			System.out.println("error");
		}
		return nameList;
	}

	// Method to gets the names of employees that have multiple managers (direct or indirect)
	public ArrayList<String> getAllMultipleManagers() {

			String sql = "SELECT DISTINCT (name) " +
				"FROM (SELECT e.name AS name " +
				"FROM employee AS e " +
				"NATURAL JOIN (SELECT wf.eid " +
				"FROM worksfor AS wf " +
				"GROUP BY wf.eid " +
				"HAVING count(*) > 1) AS w " +
				"UNION " +
				"SELECT employee.name " +
				"FROM employee " +
				"NATURAL JOIN (SELECT wf1.eid " +
				"FROM worksfor AS wf1 " +
				"JOIN worksfor AS wf2 ON wf1.mid = wf2.eid) AS w2) AS q";

		ArrayList<String> nameList = new ArrayList<String>();
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(sql);
			while (rs.next()) {
				nameList.add(rs.getString(1));
			}
		} catch (Exception e) {
			System.out.println("error");
		}
		return nameList;
	}
}