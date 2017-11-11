package jdbc-implementation;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

//  TransfileProcessor processes transfile
  
public class TransfileProcessor {

	
	private String filepath;
	DatabaseInterface di = null;

	// Constructor
	public TransfileProcessor(String filepath, DatabaseInterface di) {
		this.filepath = filepath;
		this.di = di;
	}
	
	// Method which opens and processes the file
	public void openFile() throws IOException {
		FileReader fr = new FileReader(filepath);
		BufferedReader br = new BufferedReader(fr);
		String line;
		
		while ((line = br.readLine()) != null) {
		    processLine(line);
		}
		
		br.close();
	}
	
	// Method which processes a line of the file.
	private void processLine(String line)
	{
		// Ignore comment lines
		if (line.charAt(0) == '*') return;
		
		try {
			TransactionProcessor transaction = new TransactionProcessor(line);
			transaction.execute(di);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
