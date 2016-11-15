package launcher;

import java.io.IOException;

import task.RunTTMwithMultiTasks;
import argument.ProgramArgument;

public class TTMMultipleTasksEntry {

	public static void main(String args[]) throws ClassNotFoundException {
		try {

			ProgramArgument ttmParas = new ProgramArgument();

			RunTTMwithMultiTasks ttmMultiTasks = new RunTTMwithMultiTasks(ttmParas);
			ttmMultiTasks.run();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
