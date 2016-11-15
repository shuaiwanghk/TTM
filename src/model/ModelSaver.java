package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ModelSaver {
	
	ObjectOutputStream oos;

	public void saveModelToPath(TargetedTopicModel model, String outputPath) throws FileNotFoundException, IOException{
		File outputFile = new File(outputPath);
		
		
		if(!outputFile.exists()){
			outputFile.getParentFile().mkdirs();
		}
		oos = new ObjectOutputStream(new FileOutputStream(outputPath));
		oos.writeObject(model);
	}
	
}
