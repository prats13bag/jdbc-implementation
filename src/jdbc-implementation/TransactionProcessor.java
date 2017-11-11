package jdbc-implementation;
import java.util.ArrayList;

public class TransactionProcessor {
	private int trans_code;
	private Employee emp;
	
	//Constructor
	public TransactionProcessor(String line) throws Exception {
		
			
		// Splitting the line and extracting transaction code
		line = line.replaceAll("\\s+", " ");
		String[] fields = line.split(" ");
		trans_code = Integer.parseInt(fields[0]);

		switch(trans_code){
		case 1: // follow
		case 4: // follow
		case 5:
			try {
				emp = new Employee(new Integer(fields[1]));
			}
			catch (Exception e) {
			}
			break;
		case 2:
			try {
				emp = new Employee (new Integer(fields[1]), fields[2], new Integer(fields[3]));
				for(int i = 4; i < fields.length; i++){
					emp.addEmployee(new Integer(fields[i]));
				}
			}
			catch (Exception e) {
			}
			break;

		case 3:
		case 6:
			if (fields.length > 1)
				System.out.print("Extra fields are encountered than required for transaction code " + trans_code + ". Query is being processed after ignoring these extra fields. ");
			break;

		default:
			System.out.println("Invalid transaction code " + trans_code + " found in transfile");
			break;
		}
	}

	public void execute(DatabaseInterface di)
	{
		ArrayList<Integer> ids = null;
		
		switch(trans_code) {
		case 1:
			try {
				di.deleteFromEmployeeWithCascade(emp);
			}
			catch  (Exception e) {
				System.out.println("Remaining fields for transaction code " + trans_code +" is either missing or is in invalid form");
			}

			break;
		case 2:
			try {
				di.insertIntoEmployee(emp);
			}
			catch  (Exception e) {
				System.out.println("Remaining fields for transaction code " + trans_code +" is either missing or is in invalid form");
			}
			break;
		case 3:
			System.out.println("The average salary of all the employees is equal to " + di.avgSal().toString());
			break;
		case 4:
			try {
				ids = di.fetchAlleids(emp.getId());
				if (ids.size() > 0) {
					System.out.println("Following employees directly or indirectly work under Manager ID: " + emp.getId() + " - " + di.fetchEmpNames(ids));
				} else {
					System.out.println("error");
				}
			}
			catch (Exception e) {
				System.out.println("Remaining fields for transaction code " + trans_code + " is either missing or is in invalid form");
			}
			break;
		case 5:
			try{
				ids = di.fetchEIDSofEmpsUnderMID(emp.getId());
				if (ids.size()> 0) {
					di.avgSalOfEmps(ids, emp.getId());
				}
				else {
					System.out.println("error");
				}
			}
			catch (Exception e) {
				System.out.println("Remaining fields for transaction code " + trans_code + " is either missing or is in invalid form");
			}
			break;

		case 6:
			ArrayList<String> nameList = di.getMultipleManagers();
			if (nameList.size() > 0) {
				System.out.println("Following employees have more than one direct managers - " + nameList);
			}
			else {
				System.out.println("No employees with more than one manager");
			}
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public String toString() {
		return "Command [code=" + trans_code + ", employee=" + emp + "]";
	}
}
