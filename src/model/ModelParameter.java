package model;

import java.io.Serializable;

import org.kohsuke.args4j.Option;

import argument.ProgramArgument;
import dataunit.Corpus;

/**
 *  The following initialized parameter settings will be covered by argument settings
 */

public class ModelParameter implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Task/Thread Specific Parameters
	 */

	public String modelName = null;
	public String domain = null; // The name of the domain

	/**
	 * General Parameter for Topic Models
	 */

	public int D = 0; // #Documents. Initial value is 0, will be lated assigned value from program argument.
	public int V = 0; // #Words. Initial value is 0, will be lated assigned value from program argument.
	public int R_status = 0; // #Relevant status. Initial value is 0, will be lated assigned value from program argument. 
	
	public int tTopicNum = 0;
	
	// The number of iterations for burn-in period.
	public int nBurnin = 200;
	// The number of Gibbs sampling iterations.
	public int nIterations = 500;

	/* Hyper-parameters. These might be updated by the specified options from the program argument */

	public double st = 1;
	public double stn = 1;

	public double sv = 1;
	public double svn = 1;

	public double pi = 0.1;
	public double piS = 0.0000001;

	public double gamma = 0.01;
	public double gammaS = 0.0000001;

	public String targetWord;
	
	public String pDelim;
	
	// Random seed.
	public int randomSeed = 0;

	/**
	 * Output.
	 */
	public int twords = 0; // Print out top words per each topic.
	public String outputModelDirectory = null;

	public String outputFileRootDir = null;
	
	public String pTopicWordFileName = null;
	
	
	public String midfix = null;

	public ModelParameter(Corpus corpus, ProgramArgument pa) {
		domain = corpus.domain;

		D = corpus.getNumberOfDocuments();
		V = corpus.vocab.size();
		R_status = pa.nRelevantStatus;

		nBurnin = pa.nBurnin;
		nIterations = pa.nIterations;

		st = pa.st;
		stn = pa.stn;
		sv = pa.sv;
		svn = pa.svn;
		pi = pa.pi;
		piS = pa.piSmooth;
		gamma = pa.gamma;
		gammaS = pa.gammaSmooth;

		targetWord = pa.targetWord;
		tTopicNum = pa.tTopicNum;

		pDelim = "/";

		randomSeed = pa.randomSeed;

		twords = pa.twords;

		outputFileRootDir = pa.outputRootDirectory;

		/*** model write and read ***/

		outputModelDirectory = pa.modelOutputDirectory;
		
		pTopicWordFileName = pa.pTopicWordFileName;

		midfix  = pa.midfix;
		
	}

}
