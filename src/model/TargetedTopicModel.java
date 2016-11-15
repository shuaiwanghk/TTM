package model;

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.special.Gamma;

import argument.Constant;
import sun.tools.jar.resources.jar;
import utility.ArrayAllocationAndInitialization;
import utility.FileWriterAndDirGenerator;
import utility.InverseTransformSampler;
import utility.OSFilePathConvertor;
import utility.SortingUtility;
import matrix.DoubleMatrix;
import dataunit.Corpus;

public class TargetedTopicModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * current process. 
	 */

	public Corpus corpus = null;
	public ModelParameter param = null;
	public String modelName = null;

	int[][] z = null;

	Random randomGenerator = null;

	/** (1) Hyperparameters **/
	/* Default settings. Will be updated with the argument options. */
	double st = 1;
	double stn = 1;

	double sv = 1;
	double svn = 1;

	double pi = 0.1;
	double piS = 0.0000001;

	double gamma = 0.01;
	double gammaS = 0.0000001;
	
	public boolean[] alphaT; 

	/** (2) Posterior distribution **/
	public DoubleMatrix phi; // Phi[topic][word]
	public DoubleMatrix theta; // Theta[document][topic] . As a global one, we
								// have [0][V+1]

	/** (3) Spike and slab parameters **/
	public boolean[][] betaTW;
	public int betaTWSum[]; // The sum of all beta values equals 1 in betaTW[][]

	/** (4) Sum of all Counts under relevance **/
	int[] cD; 
	int D; 
	public int[][] cTW; 
	int[] cT; 

	/** (4) target word **/
	HashSet<Integer> pIDSet;// target/positive words assignment

	/** (5) Positive/Relevant Topic (i.e., target-related topics) **/
	public int[][] pZ;
	public int[][] cpDT;
	public int[] cpD;
	public int[][] cpTW;
	public int[] cpT;
	
	
	// local variables
	double pAlpha = 0.1;
	double pAlphaSum;

	public boolean pBetapTW[][];
	public int pBetapTWSum[]; // with the sparse modeling

	public DoubleMatrix pPhi;

	public TargetedTopicModel(Corpus corpus, ModelParameter param) {
		super();
		this.corpus = corpus;
		this.param = param;

		this.st = param.st;
		this.stn = param.stn;
		this.sv = param.sv;
		this.svn = param.svn;
		this.pi = param.pi;
		this.piS = param.piS;
		this.gamma = param.gamma;
		this.gammaS = param.gammaS;

		System.out
				.println("Initialize the TTM mdole with the following parameter settings");
		System.out.println("st: " + this.st + " stn: " + this.stn + " sv: "
				+ this.sv + " svn: " + this.svn + " pi: " + this.pi
				+ " piSmooth: " + this.piS + " gamma: " + this.gamma
				+ " gammaSmooth" + this.gammaS);

		randomGenerator = new Random(param.randomSeed);

		/** (6) Positive Sub Topics **/
		// pAlphaSum = pAlpha * param.pT;
		/**
		 * only init "pAlpha" here as it is not sparse the "pBeta" will be
		 * inited in the allocation part
		 */
		pAlpha = pi;
		pAlphaSum = pAlpha * param.tTopicNum;

		allocateMemoryForVariables();

		initFirstMarkovChainRandomly();

	}

	public void  run() {
		gibbsSampling();

		computePosteriorDistribution();

		generateOutputs();

	}


	public void generateOutputs() {

		String midfix = param.midfix;

		/**
		 * The targeted topic-word file generation.
		 */

		String pPostivePhiOutputFilePath = param.outputFileRootDir
				+ File.separator + corpus.domain + File.separator
				+ corpus.domain + midfix + param.pTopicWordFileName;
		pPostivePhiOutputFilePath = OSFilePathConvertor
				.convertOSFilePath(pPostivePhiOutputFilePath);

		PrintWriter targetOut = FileWriterAndDirGenerator
				.createDirAndWriter(pPostivePhiOutputFilePath);

		/**
		 * with the sparse output
		 */

		HashMap[] sparsePositiveTopicWordValueMap = new HashMap[param.tTopicNum];
		Map[] sortedSparsePositiveTopicWordValueMap = new Map[param.tTopicNum];

		for (int pt = 0; pt < param.tTopicNum; pt++) {
			sparsePositiveTopicWordValueMap[pt] = new HashMap<Integer, Double>();
			for (int v = 0; v < param.V; v++) {
				if (pBetapTW[pt][v]) {
					sparsePositiveTopicWordValueMap[pt].put(v,
							pPhi.getValue(pt, v));
				}
			}
			sortedSparsePositiveTopicWordValueMap[pt] = SortingUtility
					.sortHashMapByValuesD(sparsePositiveTopicWordValueMap[pt]);
		}

		int pMaxWordSize = 0;
		for (int pt = 0; pt < param.tTopicNum; pt++) {
			if (sortedSparsePositiveTopicWordValueMap[pt].size() > pMaxWordSize)
				pMaxWordSize = sortedSparsePositiveTopicWordValueMap[pt].size();
		}

		System.err.println("max size: " + pMaxWordSize);

		Iterator[] positiveIt = new Iterator[param.tTopicNum + 1];
		for (int pt = 0; pt < param.tTopicNum; pt++) {
			positiveIt[pt] = sortedSparsePositiveTopicWordValueMap[pt]
					.entrySet().iterator();
		}

		sparsePositiveTopicWordValueMap = null;
		sortedSparsePositiveTopicWordValueMap = null;

		// print out the title first
		for (int pt = 0; pt < param.tTopicNum; pt++) {
			targetOut.print("T-" + pt + ", ");
		}

		targetOut.println();

//		// print out the beta
//		for (int pt = 0; pt < param.tTopicNum; pt++) {
//			targetOut.print(pBetapTWSum[pt] + ",");
//		}
//		targetOut.println();

		// print out the content then
		for (int w = 0; w < pMaxWordSize; w++) {
			for (int pt = 0; pt < param.tTopicNum; pt++) {
				if (positiveIt[pt].hasNext()) {
					Map.Entry pair = (Map.Entry) positiveIt[pt].next();
					targetOut.print(corpus.vocab.getWordStr(Integer
							.parseInt((String) pair.getKey())) + ", ");
				} else {
					targetOut.print(" ,");
				}
			}
			targetOut.println();
		}

		targetOut.flush();
		targetOut.close();

		System.out
				.println("Done. The targeted topic-word file is successfully generated.");

	}

	private void computePosteriorDistribution() {

		computeTopicWordDistribution();

		computePostiveTopicWordDistribution();

	}

	private void computePostiveTopicWordDistribution() {
		for (int pt = 0; pt < param.tTopicNum; pt++) {
			for (int v = 0; v < param.V; v++) {
				double pBeataAsDouble = (double) convertValueFromBooleanToInt(pBetapTW[pt][v]);
				double pBetaSumAsDouble = (double) pBetapTWSum[pt];
				// pPhi.setValue(pt, v,
				// (cpTW[pt][v]+pBeta)/(cpT[pt]+pBetaOnTSum)); // non-sparse
				pPhi.setValue(pt, v, (cpTW[pt][v] + pBetaSumAsDouble)
						/ (cpT[pt] + pBetaSumAsDouble));// sparse
			}
		}
	}

	private void computeTopicWordDistribution() {
		for (int t = 0; t < param.R_status + 1; t++) {
			for (int v = 0; v < param.V; v++) {
				double betaAsDouble = (double) convertValueFromBooleanToInt(betaTW[t][v]);
				double betaSumAsDouble = (double) betaTWSum[t];
				phi.setValue(t, v, (cTW[t][v] + betaAsDouble)
						/ (cT[t] + betaSumAsDouble));
			}
		}
	}

	private void gibbsSampling() {
		for (int i = 0; i < param.nIterations; i++) {
			if (i % 20 == 0) {
				System.out.println("running iteration " + i);
			}

			// (1) sample topics
			sampleTopicAssignments();

			// (2) sample beta
			sampleBetaAssignments();

		}
	}

	private void sampleBetaAssignments() {
		for (int v = 0; v < param.V; v++) {
			/**
			 * Asymmetrical sampling (for the class/label stage)
			 */
			// (a) sample for relevant status
			// r = 0, i.e., r < param.R_status when param.R_status =1
			for (int r = 0; r < param.R_status; r++) {
				/**
				 * the following lines forces the seed in topics
				 */
				if (pIDSet.contains(v)) {// positive/word is relevant
					betaTW[r][v] = true;
				} else {
					sampleOneBetaAssignment(r, v); // Experimental setting two
				}

			}

			// (b) sample for irrelevant status
			// r = 1, i.e., i = param.R_status when param.R_status =1
			if (pIDSet.contains(v)) {// positive/word is relevant
				boolean oldBeta = betaTW[param.R_status][v];

				betaTW[param.R_status][v] = false;

				// update beta sum
				if (oldBeta == true && betaTW[param.R_status][v] == false)
					betaTWSum[param.R_status]--;

			} else {
				boolean oldBeta = betaTW[param.R_status][v];
				betaTW[param.R_status][v] = true;
				if (oldBeta == false)
					betaTWSum[param.R_status]++;

			}

		}

		for (int v = 0; v < param.V; v++) {
			for (int pt = 0; pt < param.tTopicNum; pt++) {
				sampleOnePositiveBetaAssignment(pt, v);
			}
		}

	}

	private void sampleOnePositiveBetaAssignment(int pt, int v) {
		boolean oldPositiveBeta = pBetapTW[pt][v];

		double pFalse = approximatePositiveBetaIsFalse(pt, v);
		double pTrue = approximatePositiveBetaIsTrue(pt, v);

		double p[] = new double[2];
		p[0] = pFalse;
		p[1] = pTrue;

		p = logToRations(p);

		int newPositiveBetaInt = InverseTransformSampler.sample(p,
				randomGenerator.nextDouble());
		boolean newPostiveBeta = converValueFromIntToBoolean(newPositiveBetaInt);

		// update the current beta value
		pBetapTW[pt][v] = newPostiveBeta;

		// update the sum
		if (oldPositiveBeta == true && newPostiveBeta == false)
			pBetapTWSum[pt]--;

		if (oldPositiveBeta == false && newPostiveBeta == true)
			pBetapTWSum[pt]++;
	}

	private double approximatePositiveBetaIsTrue(int pt, int v) {
		int pBetaAllOthers = pBetapTWSum[pt]
				- convertValueFromBooleanToInt(pBetapTW[pt][v]);
		double logP = 0;

		assert (logP != 0);

		logP += logOn2Gamma(cpTW[pt][v] + gamma + gammaS);

		logP += logOn2Gamma(gamma + param.V * gammaS + gamma * pBetaAllOthers);

		logP += logOn2Gamma(cpT[pt] + gamma * pBetaAllOthers + param.V * gammaS);

		logP += logOn2(sv + pBetaAllOthers);

		return logP;
	}

	private double approximatePositiveBetaIsFalse(int pt, int v) {
		int pBetaAllOthers = pBetapTWSum[pt]
				- convertValueFromBooleanToInt(pBetapTW[pt][v]);
		double logP = 0;

		assert (logP != 0);
		logP += logOn2Gamma(gamma + gammaS);

		logP += logOn2Gamma(param.V * gammaS + gamma * pBetaAllOthers);

		logP += logOn2Gamma(cpT[pt] + gamma + gamma * pBetaAllOthers + param.V
				* gammaS);

		logP += logOn2(svn + param.V - pBetaAllOthers - 1);

		return logP;
	}

	private void sampleOneBetaAssignment(int t, int v) {
		boolean oldBeta = betaTW[t][v];

		double pFalse = approximateBeatIsFalse(t, v);
		double pTrue = approximateBetaIsTrue(t, v);

		double p[] = new double[2];
		p[0] = pFalse;
		p[1] = pTrue;

		p = logToRations(p);
		int newBetaInt = InverseTransformSampler.sample(p,
				randomGenerator.nextDouble());
		boolean newBeta = converValueFromIntToBoolean(newBetaInt);

		// update current beta
		betaTW[t][v] = newBeta;

		// update beta sum
		if (oldBeta == true && newBeta == false)
			betaTWSum[t]--;

		if (oldBeta == false && newBeta == true)
			betaTWSum[t]++;
	}

	public double approximateBetaIsTrue(int t, int v) {
		int betaAllOthers = betaTWSum[t]
				- convertValueFromBooleanToInt(betaTW[t][v]);
		double logP = 0;

		double tempCountlog = logP;

		assert (logP != 0);
		logP += logOn2Gamma(cTW[t][v] + gamma + gammaS);

		logP += logOn2Gamma(gamma + param.V * gammaS + gamma * betaAllOthers);

		logP += logOn2Gamma(cT[t] + gamma * betaAllOthers + param.V * gammaS);

		logP += logOn2(sv + betaAllOthers);

		if (Double.isNaN(logP) || logP == Double.POSITIVE_INFINITY) {
			System.out.println("In beta True Assignment");
			System.out.println("***");
			System.out.println(tempCountlog);
			System.out.println(betaAllOthers * gamma + gamma);
			System.out.println(Gamma.gamma(betaAllOthers * gamma + gamma));
			System.out.println(sv + betaAllOthers);
			System.out.println(logOn2(sv + betaAllOthers));
			System.out.println("***");
		}

		return logP;
	}

	public double approximateBeatIsFalse(int t, int v) {
		int betaAllOthers = betaTWSum[t]
				- convertValueFromBooleanToInt(betaTW[t][v]);
		double logP = 0;

		assert (logP != 0);
		logP += logOn2Gamma(gamma + gammaS);

		logP += logOn2Gamma(param.V * gammaS + gamma * betaAllOthers);

		logP += logOn2Gamma(cT[t] + gamma + gamma * betaAllOthers + param.V
				* gammaS);

		logP += logOn2(svn + param.V - betaAllOthers - 1);

		return logP;
	}

	private double logOn2Gamma(double value) {
		return com.aliasi.util.Math.log2Gamma(value);
	}

	private double nanAndInfinityCheck(double e) {
		if (Double.isNaN(e) || Double.POSITIVE_INFINITY == e) { // Handle the
																// big Gamma
			// System.out.println("there is nan vlaue!");
			return Float.MAX_VALUE;
		} else {
			return e;
		}

	}

	private double computeBetaIsTrue(int t, int v) {
		int betaSumOfAllOthers = betaTWSum[t]
				- convertValueFromBooleanToInt(betaTW[t][v]);
		double p = 1;
		return p
				* Gamma.gamma(gamma + cTW[t][v] + gammaS)
				* Gamma.gamma(betaSumOfAllOthers * gamma + gamma + param.V
						* gammaS)
				* Gamma.gamma(betaSumOfAllOthers * gamma + cT[t] + param.V
						* gammaS) * (sv + betaSumOfAllOthers);
	}

	private double computeBetaIsFalse(int t, int v) {
		int betaSumOfAllOthers = betaTWSum[t]
				- convertValueFromBooleanToInt(betaTW[t][v]);
		double p = 1;
		return p
				* Gamma.gamma(gamma + gammaS)
				* Gamma.gamma(betaSumOfAllOthers * gamma + param.V * gammaS)
				* Gamma.gamma(betaSumOfAllOthers * gamma + cT[t] + param.V
						* gammaS + gamma)
				* (svn + param.V - 1 - betaSumOfAllOthers);
	}

	public double[] logToRations(double[] logJointProbs) {
		for (int i = 0; i < logJointProbs.length; ++i) {
			if (logJointProbs[i] > 0.0 && logJointProbs[i] < 0.0000000001)
				logJointProbs[i] = 0.0;
			if (Double.isNaN(logJointProbs[i])) {
				StringBuilder sb = new StringBuilder();
				sb.append("probs is NaN." + " Found log2JointProbs[" + i + "]="
						+ logJointProbs[i]);
				for (int k = 0; k < logJointProbs.length; ++k)
					sb.append("\nlog[" + k + "]=" + logJointProbs[k]);
				throw new IllegalArgumentException(sb.toString());
			}

		}
		double max = com.aliasi.util.Math.maximum(logJointProbs);
		double[] probRatios = new double[logJointProbs.length];
		for (int i = 0; i < logJointProbs.length; ++i) {
			probRatios[i] = java.lang.Math.pow(2.0, logJointProbs[i] - max); // diff
																				// is
																				// <=
																				// 0.0

			if (probRatios[i] == Double.POSITIVE_INFINITY) {
				System.err.println("Here we have positive infinity in p[" + i
						+ "]" + probRatios[0]);
				probRatios[i] = Float.MAX_VALUE;
			} else if (probRatios[i] == Double.NEGATIVE_INFINITY
					|| Double.isNaN(probRatios[i])) {
				System.err.println("Here we have negative infinity in p[" + i
						+ "]" + probRatios[1]);
				probRatios[i] = 0.0;
			}
		}


		return com.aliasi.stats.Statistics.normalize(probRatios);
	}

	private double logOn2(double value) {
		return com.aliasi.util.Math.log2(value);
	}

	private void sampleTopicAssignments() {
		for (int d = 0; d < param.D; d++) {// document level
			int oldTopic = z[d][0];
			int N = corpus.docs[d].length;

			for (int n = 0; n < N; n++) {
				// (a) update the word level counts
				updateAllCountsForOneWord(d, n, oldTopic, -1);
			}

			// (b) update the sentence level counts
			updateAllCountsForDocument(oldTopic, -1);

			assert (z[d][0] != z[d][1]) : "Topics are not the same in one sentence.";

			boolean pWordIncluded = checkPostiveWordInside(d);
			double p[];

			if (pWordIncluded) { // sentence include target/positive word
				p = new double[param.R_status];
				p = computeTopicAssignmentProbs(d, p, param.R_status);

			} else { // sentence without target/positive word

				p = new double[param.R_status + 1];
				p = computeTopicAssignmentProbs(d, p, param.R_status + 1);
			}


			int newTopic = InverseTransformSampler.sample(p,
					randomGenerator.nextDouble());

			for (int n = 0; n < N; n++) {
				z[d][n] = newTopic;
				updateAllCountsForOneWord(d, n, newTopic, +1);

			}

			// (b) update the sentence level counts
			updateAllCountsForDocument(newTopic, +1);

			/**
			 * update target-related topics
			 */
			
			// Case one. If old z is not positive and the new z is also not
			// positive, then do nothing.
			
			// Case two. If old z is not positive and the new z is positive,
			// then only add new value;
			
			if (oldTopic != Constant.POSITIVE_PART
					&& newTopic == Constant.POSITIVE_PART) {
				for (int n = 0; n < N; n++) {

					int wordId = corpus.docs[d][n];

					// update the p-topic assignment pZ
					double[] pTProbs = new double[param.tTopicNum];
					for (int pt = 0; pt < param.tTopicNum; pt++) {
						if (pBetapTW[pt][wordId]) {
							pTProbs[pt] = ((cpDT[d][pt] + pAlpha) / (cpD[d] + pAlphaSum))
									* ((cpTW[pt][wordId] + gamma + gammaS) / (cpT[pt]
											+ pBetapTWSum[pt] * gamma + param.V
											* gammaS));

						} else {
							pTProbs[pt] = ((cpDT[d][pt] + pAlpha) / (cpD[d] + pAlphaSum))
									* ((gammaS) / (cpT[pt] + pBetapTWSum[pt]
											* gamma + param.V * gammaS));

						}

					}

					int pTopic = InverseTransformSampler.sample(pTProbs,
							randomGenerator.nextDouble());
					pZ[d][n] = pTopic;

					// update(adding) the counts
					updateAllCountsInPT(d, n, pTopic, +1);
				}
			}

			// Case three. If old z is positive and the new z is no positive,
			// then only reduce the old value;
			
			if (oldTopic == Constant.POSITIVE_PART
					&& newTopic != Constant.POSITIVE_PART) {
				for (int n = 0; n < N; n++) {
					// update(reducing) the counts
					int oldPtopic = pZ[d][n];
					updateAllCountsInPT(d, n, oldPtopic, -1);

					// update the p-topic assignment pZ
					pZ[d][n] = Constant.DUMMY_POSTIVE_TOPIC_INEX;
				}
			}

			// Case four. If the old z and new z are both positive.
			
			if (oldTopic == Constant.POSITIVE_PART
					&& newTopic == Constant.POSITIVE_PART) {
				for (int n = 0; n < N; n++) {
					// update(reducing) the counts
					int oldPtopic = pZ[d][n];
					updateAllCountsInPT(d, n, oldPtopic, -1);

					double[] pTProbs = new double[param.tTopicNum];
					int wordId = corpus.docs[d][n];

					for (int pt = 0; pt < param.tTopicNum; pt++) {
						if (pBetapTW[pt][wordId]) {
							pTProbs[pt] = ((cpDT[d][pt] + pAlpha) / (cpD[d] + pAlphaSum))
									* ((cpTW[pt][wordId] + gamma + gammaS) / (cpT[pt]
											+ pBetapTWSum[pt] * gamma + param.V
											* gammaS));

						} else {
							pTProbs[pt] = ((cpDT[d][pt] + pAlpha) / (cpD[d] + pAlphaSum))
									* ((gammaS) / (cpT[pt] + pBetapTWSum[pt]
											* gamma + param.V * gammaS));

						}
					}

					int newPtopic = InverseTransformSampler.sample(pTProbs,
							randomGenerator.nextDouble());
					pZ[d][n] = newPtopic;

					// update(adding) the p-topic assignment pZ
					updateAllCountsInPT(d, n, newPtopic, +1);
				}
			}

		}// end of the document.
	}

	private double[] computeTopicAssignmentProbs(int d, double[] p, int topicNum) {
		for (int t = 0; t < topicNum; t++) {// topic probs computation

			/**
			 * new one. true.
			 */
			double logExpectedDT = 0.0;

			if (alphaT[t]) {
				logExpectedDT += logOn2(pi + cD[t] + piS);
			} else {
				logExpectedDT += logOn2(piS);
			}

			double logExpectedWT = 0.0;

			double Nd = 0; // count total word increment in the current sentence

			HashMap<Integer, Integer> wordCntMap = corpus
					.getDocsTermFrequency(d);

			// start
			for (int wordId : wordCntMap.keySet()) {
				for (int i = 0; i < wordCntMap.get(wordId); i++) {
					/**
					 * approximate the probs for ducument's relevance assignment
					 */
					if (betaTW[t][wordId]) {

						logExpectedWT += logOn2(((convertValueFromBooleanToInt(betaTW[t][wordId])
								* gamma + gammaS + cTW[t][wordId] + i) / (betaTWSum[t]
								* gamma + param.V * gammaS + cT[t] + Nd)));

					} else {

						logExpectedWT += logOn2(((gammaS + i) / (betaTWSum[t]
								* gamma + param.V * gammaS + cT[t] + Nd)));
					}

					Nd++;
				}
			}// end-start

			// every unique word

			p[t] = logExpectedDT + logExpectedWT;
		}
		// back from log to ratio
		p = logToRations(p);
		return p;
	}

	private void allocateMemoryForVariables() {
		phi = new DoubleMatrix(param.R_status + 1, param.V);
		theta = new DoubleMatrix(1, param.R_status + 1); // because we are now
															// using a
		// global topic distribution

		pIDSet = getpVIDs(param.targetWord.split(param.pDelim));

		boolean initValueForSparceModeling = true;
		// boolean initValueForSparceModeling = false;

		alphaT = ArrayAllocationAndInitialization.allocateAndInitialize(alphaT,
				param.R_status + 1, initValueForSparceModeling);
		alphaT[alphaT.length - 1] = true;

		betaTW = ArrayAllocationAndInitialization.allocateAndInitialize(betaTW,
				param.R_status + 1, param.V, initValueForSparceModeling);
		betaTW = adjustPWordAssignment(betaTW, pIDSet);

		// updateAlphaSum();

		betaTWSum = ArrayAllocationAndInitialization.allocateAndInitialize(
				betaTWSum, param.R_status + 1);
		betaTWSum = computeBetaSumUnderEveryTopic(betaTWSum, betaTW);

		assert (betaTWSum[0] != param.V);

		// fix. the size should be the R_status - to record the word
		// distribution under
		// topics!
		cD = ArrayAllocationAndInitialization.allocateAndInitialize(cD,
				param.R_status + 1);
		D = param.D;

		cTW = ArrayAllocationAndInitialization.allocateAndInitialize(cTW,
				param.R_status + 1, param.V);
		cT = ArrayAllocationAndInitialization.allocateAndInitialize(cT,
				param.R_status + 1);

		/** Init the Positive Sub Topics **/
		pPhi = new DoubleMatrix(param.tTopicNum + 1, param.V);

		cpD = ArrayAllocationAndInitialization.allocateAndInitialize(cpD,
				param.D); // the sum of documens with all topics
		cpDT = ArrayAllocationAndInitialization.allocateAndInitialize(cpDT,
				param.D, param.tTopicNum);

		cpTW = ArrayAllocationAndInitialization.allocateAndInitialize(cpTW,
				param.tTopicNum, param.V);

		cpT = ArrayAllocationAndInitialization.allocateAndInitialize(cpT,
				param.tTopicNum);

		pBetapTW = ArrayAllocationAndInitialization.allocateAndInitialize(
				pBetapTW, param.tTopicNum, param.V, initValueForSparceModeling);
		pBetapTWSum = ArrayAllocationAndInitialization.allocateAndInitialize(
				pBetapTWSum, param.tTopicNum);
		pBetapTWSum = computePositiveBetaSumUnderEveryTopic(pBetapTWSum,
				pBetapTW);

	}

	private boolean[][] adjustPWordAssignment(boolean[][] betaTW2,
			HashSet<Integer> pIDSet2) {
		for (int i : pIDSet2) {
			betaTW2[param.R_status][i] = false;
		}
		return betaTW2;

	}

	private int[] computePositiveBetaSumUnderEveryTopic(int[] pBetapTWSum,
			boolean[][] pBetapTW) {
		for (int pt = 0; pt < pBetapTWSum.length; pt++) {
			int pBetaTrueCount = 0;
			for (int w = 0; w < pBetapTW[pt].length; w++) {
				pBetaTrueCount += convertValueFromBooleanToInt(pBetapTW[pt][w]);
			}
			pBetapTWSum[pt] = pBetaTrueCount;

		}
		return pBetapTWSum;
	}

	private int[] computeBetaSumUnderEveryTopic(int[] betaTWSum,
			boolean[][] betaTW) {
		for (int t = 0; t < betaTW.length; t++) {
			int betaTrueCount = 0;
			for (int w = 0; w < betaTW[t].length; w++) {
				betaTrueCount += convertValueFromBooleanToInt(betaTW[t][w]);
			}
			betaTWSum[t] = betaTrueCount;
		}
		return betaTWSum;
	}

	public int convertValueFromBooleanToInt(boolean value) {
		return value == true ? 1 : 0;
	}

	private boolean converValueFromIntToBoolean(int value) {
		assert (value == 1 || value == 0) : "unexpect value for alpha assignment";
		if (value == 1) {
			return true;
		} else {
			return false;
		}
	}

	private HashSet<Integer> getpVIDs(String[] pV) {
		HashSet<Integer> idSet = new HashSet<Integer>();

		for (int i = 0; i < pV.length; i++) {
			idSet.add(corpus.vocab.getWordID(pV[i]));
		}
		return idSet;
	}

	private void initFirstMarkovChainRandomly() {
		z = new int[param.D][];

		for (int d = 0; d < param.D; d++) {
			int N = corpus.docs[d].length;
			z[d] = new int[N];

			/**
			 * check whether the doc contains the positive word
			 */

			boolean pWordIncluded = checkPostiveWordInside(d);

			int topic;
			if (pWordIncluded) { // when true. contain
				topic = (int) Math.floor(randomGenerator.nextDouble()
						* param.R_status);
			} else {
				topic = (int) Math.floor(randomGenerator.nextDouble()
						* param.R_status + 1);
			}

			/**
			 * assign topic and update counts
			 */

			// (1) update the word level counts
			for (int n = 0; n < N; n++) {
				z[d][n] = topic;
				updateAllCountsForOneWord(d, n, topic, +1);
			}

			// (2) update the sentence level counts
			updateAllCountsForDocument(topic, +1);


		}

		pZ = new int[param.D][];
		for (int d = 0; d < param.D; d++) {
			int N = corpus.docs[d].length;
			pZ[d] = new int[N];

			for (int n = 0; n < N; n++) {
				if (z[d][n] == Constant.POSITIVE_PART) { // only sample inside
															// the positive
															// class
					int pTopic;

					pTopic = (int) Math.floor(randomGenerator.nextDouble()
							* param.tTopicNum);

					pZ[d][n] = pTopic;

					updateAllCountsInPT(d, n, pTopic, +1);

				} else { // for the non-positive part
					pZ[d][n] = Constant.DUMMY_POSTIVE_TOPIC_INEX;
				}
			}
		}

	}

	private boolean checkPostiveWordInside(int d) {
		boolean pWordIncluded = false;
		for (int n = 0; n < corpus.docs[d].length; n++) {
			if (pIDSet.contains(corpus.docs[d][n])) {
				pWordIncluded = true;
			}
		}
		return pWordIncluded;
	}

	private void updateAllCountsInPT(int d, int n, int t, int flag) {
		cpDT[d][t] += flag;
		cpD[d] += flag;
		cpTW[t][corpus.docs[d][n]] += flag;
		cpT[t] += flag;
	}

	private void updateAllCountsForDocument(int topic, int flag) {
		cD[topic] += flag;
	}

	private void updateAllCountsForOneWord(int d, int n, int topic, int flag) {
		cTW[topic][corpus.docs[d][n]] += flag;
		cT[topic] += flag;
	}
}
