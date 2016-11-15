package task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;


import model.ModelSaver;
import model.TargetedTopicModel;
import model.ModelParameter;
import multithread.TTMModelMultiThreadPool;
import argument.ProgramArgument;
import dataunit.Corpus;

public class RunTTMwithMultiTasks {

	ProgramArgument ttmParas;

	public RunTTMwithMultiTasks(ProgramArgument pa) {
		this.ttmParas = pa;
	}

	public void run() throws IOException, ClassNotFoundException {
		
		/**
		 * Assign domain name and target words
		 */
		
		/* Camera */
		ttmParas.domainName="camera";
		String[] target_words = {"weight", "screen","lens"};
		
		/* Cigar */
//		 ttmParas.domainName="cigar";
//		 String[] target_words = {"event","box","horse"};
		
		ttmParas.inputCorpusDirectory="data/input/dataset/"+ttmParas.domainName;
		
		System.out
				.println("Program starts running... in multiple tasks manner");

		Corpus corpus = getCorpus(ttmParas.inputCorpusDirectory);
		corpus.parseFromDocumetConsistOfSentence(ttmParas.inputDocsName,
				ttmParas.inputVocabName);
		String modelName = "TTMs";
		long startTime = System.currentTimeMillis();
		TTMModelMultiThreadPool threadPool = new TTMModelMultiThreadPool(
				ttmParas.nIterations);
		ArrayList<TargetedTopicModel> models = new ArrayList<TargetedTopicModel>();

		
		/* multiple threads/tasks, with multiple keywords */
		for (String target_word : target_words) {
			ttmParas.targetWord = target_word; // assign the keyword

				ModelParameter param = new ModelParameter(corpus, ttmParas);
				System.out.println("Domain: " + corpus.domain +", target: " + target_word);
				param.midfix = modelName + "-target-keyword-"
						+ param.targetWord + "-PT-" + param.tTopicNum + "-"
						+ param.R_status + "-Iter-" + param.nIterations + "-svn-"
						+ param.svn + "-stn-" + param.stn;
				String onepPathWithSettings = param.outputModelDirectory
						+ File.separator + corpus.domain + param.midfix + ".m";

				if (new File(onepPathWithSettings).exists()) { // if the model
																// exists
					System.out.println("load model from: "
							+ onepPathWithSettings);
					ObjectInputStream oin = new ObjectInputStream(
							new FileInputStream(onepPathWithSettings));

					TargetedTopicModel ttm = (TargetedTopicModel) oin
							.readObject();

					models.add(ttm);
				} else {
					threadPool.addTask(corpus, param);

				}


		}// loop with keywords

		threadPool.awaitTermination();

		models.addAll(threadPool.modelList);

		long endTime = System.currentTimeMillis();

		System.out.println("The program ends with time: " + (endTime - startTime)
				/ 60);
		
		System.out.println("Please check the output file dir "+ttmParas.outputRootDirectory);
		

	}

	private Corpus getCorpus(String inputCorpusDirectory) {
		File domainFile = new File(inputCorpusDirectory);
		return new Corpus(domainFile.getName(), domainFile.getAbsolutePath());
	}

}
