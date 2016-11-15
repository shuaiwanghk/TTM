package utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileWriterAndDirGenerator {

	public static PrintWriter createDirAndWriter(String outputFilePath)
			 {
		PrintWriter out = null;
		File outputFile = new File(outputFilePath);
		outputFile.getParentFile().mkdirs();
		try {
		if (!outputFile.exists()) {

			outputFile.createNewFile();

		}

			out = new PrintWriter(new FileWriter(new File(outputFilePath)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}

}
