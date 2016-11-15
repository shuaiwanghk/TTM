package argument;

import java.io.Serializable;

import org.kohsuke.args4j.Option;

/**
 * Program Argument.
 * 
 * The class defines and stores user-provided arguments, with default value settings initialized.
 * 
 */

public class ProgramArgument implements Serializable{
	
	private static final long serialVersionUID = 1L; 
	
	/**
	 * Data and Target Setting
	 */
	
	/* (1) Domain: Camera */
	@Option(name = "-dn", usage = "Specify the domain(dataset) name")
	public String domainName = "camera";
	@Option(name = "-targetWord", usage = "Specify the target word (or called positive word)")
	public String targetWord = "weight";

	/* (2) Domain: Cepphone */
//	@Option(name = "-dn", usage = "Specify the domain(dataset) name")
//	public String domainName = "cellphone";
//	@Option(name = "-targetWord", usage = "Specify the target word (or called positive word)")
//	public String targetWord = "sound";
	
	/* (3) Domain: Computer */
//	@Option(name = "-dn", usage = "Specify the domain(dataset) name")
//	public String domainName = "computer";
//	@Option(name = "-targetWord", usage = "Specify the target word (or called positive word)")
//	public String targetWord = "software";
	
	/* (4) Domain: Cigar */
//	@Option(name = "-dn", usage = "Specify the domain(dataset) name")
//	public String domainName = "cigar";
//	@Option(name = "-targetWord", usage = "Specify the target word (or called positive word)")
//	public String targetWord = "event";
	
	/* (5) Domain: E-Cig */
//	@Option(name = "-dn", usage = "Specify the domain(dataset) name")
//	public String domainName = "ecig";
//	@Option(name = "-targetWord", usage = "Specify the target word (or called positive word)")
//	public String targetWord = "children";

	@Option(name = "-i", usage = "Specify the input dierectory for only one dataset")
	public String inputCorpusDirectory = "data/input/dataset/"+domainName; 
	
	@Option(name = "-targetedTopicNumber", usage = "Specify the number of target-related topics (or called positive/relevant topic numbr)")
	public int tTopicNum = 5; // when the target is a infrequent topic
//	public int tTopicNum = 10; // when the target is popular

	@Option(name = "-sdocs", usage = "Specify the suffix of input document file")
	public String inputDocsName = "docs.txt"; 
	
	@Option(name = "-svocab", usage = "Specify the suffix of input vocabulary file")
	public String inputVocabName = "wordlist.txt";

	@Option(name = "-positiveWordDelimiter", usage = "Separate words")
	public String pDelim = "/";
	
	@Option(name = "-burnin", usage = "Specify the number of iterations for burn-in period")
	public int nBurnin = 200;

	@Option(name = "-niters", usage = "Specify the number of gibbs sampling iterations")
	public int nIterations = 250; // similar results but faster
//	public int nIterations = 500; // 

	@Option(name = "-m", usage = "Specify the input (previously saved) model ouptut directory")
	public String modelOutputDirectory = "model_ttm";

	@Option(name = "-t", usage = "Specify the topic numbers")
	public int nRelevantStatus = 1; // Now we only consider relevant (0) or irrelevant (1) status. More possible assignments left for future works.

	@Option(name = "-rseed", usage = "Specify the seed for random number generator")
	public int randomSeed = 837191;
	
	/**
	 * hyper-parameters setting
	 */
	
	@Option(name = "-st", usage = "The prior for topic selection - yes")
	public double st = 1;
	
	@Option(name = "-stn", usage = "The prior for topic selection - no")
	public double stn = 1;
	
	@Option(name = "-sv", usage = "The prior for word selection - yes")
	public double sv = 1;
	
	@Option(name = "-svn", usage = "The prior for word selection - no")
	public double svn = 1;
	
	@Option(name = "-pi", usage = "Specify the hyperparameter pi")
	public double pi = 1;
	
	@Option(name = "-piSmooth", usage = "Specify the smoothing parameter")
	public double piSmooth = 0.00000001;
	
	@Option(name = "-gamma", usage = "Specify the hyperparameter gamma")
	public double gamma = 0.001;
	
	@Option(name = "-gammaSmooth", usage = "Specify the smoothing parameter for gamma")
	public double gammaSmooth = 0.00000001;
	
	/**
	 * output setting
	 **/
	@Option(name = "-twords", usage = "Specify the number of top words for each topic for demonstration")
	public int twords = 50;
	
	@Option(name = "-o", usage= "Specify the output root directory")
	public String outputRootDirectory = "data/output/";
	
	@Option(name = "-positiveTopicDocsFileName", usage = "Dispaly the targeted topic-word assignment")
	public String pTopicWordFileName = "targeted_topic_words.csv";
	
	@Option(name= "midfix", usage = "Store the configuation information. It is used as a midfix in part of the file name.")
	public String midfix = null;
	
}

