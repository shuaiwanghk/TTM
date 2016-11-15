package task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.SingleSelectionModel;

import model.ModelSaver;
import model.TargetedTopicModel;
import model.ModelParameter;
import argument.ProgramArgument;
import dataunit.Corpus;

public class RunTTMwithOneSingleTask {

	ProgramArgument ttmParas;

	public RunTTMwithOneSingleTask(ProgramArgument pa) {
		this.ttmParas = pa;
	}

	public void run() throws IOException, ClassNotFoundException {
		System.out.println("TTM starts running...");
		
		System.out.println("Domain: "+ttmParas.domainName+", Traget: "+ttmParas.targetWord);
		
		Corpus corpus = getCorpus(ttmParas.inputCorpusDirectory);

		corpus.parseFromDocumetConsistOfSentence(ttmParas.inputDocsName,
				ttmParas.inputVocabName);

		String modelName = "TTM";

		long startTime = System.currentTimeMillis();

		runModel(modelName, corpus, ttmParas);

		long endTime = System.currentTimeMillis();

		System.out.println("The program ends with time: " + (endTime - startTime)
				/ 60);
		
		System.out.println("Please check the output file dir "+ttmParas.outputRootDirectory);
	}

	private void runModel(String modelName, Corpus corpus,
			ProgramArgument pa) throws FileNotFoundException, IOException,
			ClassNotFoundException {
		ModelParameter param = new ModelParameter(corpus, pa);

		param.midfix = modelName + "-target-" + param.targetWord + "-PT-" + param.tTopicNum
				+ "-" + param.R_status + "-Iter-" + param.nIterations + "-svn-"
				+ param.svn + "-stn-" + param.stn;

		String oneDomainModelPath = param.outputModelDirectory + File.separator
				+ corpus.domain + param.midfix + ".m";

		System.out.println(oneDomainModelPath);

		/* load existing model for a domain or generate a new one */
		if (new File(oneDomainModelPath).exists()) { // if the model exists
														// (saved by previous
														// operations)
			System.out.println("load model from: " + oneDomainModelPath);
			ObjectInputStream oin = new ObjectInputStream(new FileInputStream(
					oneDomainModelPath));

			TargetedTopicModel ttm = (TargetedTopicModel) oin.readObject();

			ttm.param.pTopicWordFileName = pa.pTopicWordFileName;

			ttm.generateOutputs();

		} else { // if the model has not been created.
			TargetedTopicModel ttmNew = new TargetedTopicModel(corpus, param);
			ttmNew.run();

			ModelSaver modelSaver = new ModelSaver();
			modelSaver.saveModelToPath(ttmNew, oneDomainModelPath);
			System.out.println("Model Saved for domain " + modelName);
		}
	}

	private Corpus getCorpus(String inputCorpusDirectory) {

		File domainFile = new File(inputCorpusDirectory);
		return new Corpus(domainFile.getName(), domainFile.getAbsolutePath());

	}

}
