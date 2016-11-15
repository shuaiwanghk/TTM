package launcher;



import java.io.IOException;

import task.RunTTMwithOneSingleTask;
import argument.ProgramArgument;


public class TTMSingleTaskEntry {
	
	public static void main(String args[]) throws ClassNotFoundException{
		try {
			
			
		ProgramArgument ttmParas = new ProgramArgument();
		
		RunTTMwithOneSingleTask hierTask = new RunTTMwithOneSingleTask(ttmParas);
		hierTask.run();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}

}

