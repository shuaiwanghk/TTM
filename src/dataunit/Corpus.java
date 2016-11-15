package dataunit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

public class Corpus implements Serializable {

	private static final long serialVersionUID = 1L;

	public String domain = null; // Domain name
	public String domainDirPath = null; // Domain path;
	public Vocabulary vocab = null;

	public int[][] docs = null;

	public HashMap<Integer, Integer>[] docsTermFreq = null;

	public String[][] docsStr = null;

	public Map<Integer, HashSet<Integer>> wordIDToSetOfDocsMap = null;

	public Corpus() {
		wordIDToSetOfDocsMap = new TreeMap<Integer, HashSet<Integer>>();
	}

	public Corpus(String domainName, String domainDirPath) {
		this();
		this.domain = domainName;
		this.domainDirPath = domainDirPath;
	}

	public int getNumberOfDocuments() {
		return docs.length;
	}

	public void parseFromDocumetConsistOfSentence(String docsPath,
			String vacabPath) throws IOException {

		String abDocsPath = domainDirPath + File.separator + docsPath;

		BufferedReader inputDocsFile = new BufferedReader(new FileReader(
				new File(abDocsPath)));

		int docCount = 0;

		String line;

		Vector<Document> documents = new Vector<Document>();// intermediate processing

		int docsNum = 0; 

		while (true) {
			line = inputDocsFile.readLine();
			if (line == null)
				break;
			StringTokenizer st = new StringTokenizer(line); // The 1st line

			int numSentences = Integer.valueOf(st.nextToken());// read the
																// sentence
																// number

			Document currentDoc = new Document();// consist of sentences

			line = inputDocsFile.readLine();
			st = new StringTokenizer(line);

			currentDoc.setLabel(Integer.valueOf(st.nextToken())); // The 2nd line. dummy field.


			currentDoc.initSentenceLength(numSentences);

			for (int s = 0; s < numSentences; s++) {
				line = inputDocsFile.readLine();
				st = new StringTokenizer(line);

				currentDoc.initWordLengthInSentence(s, st.countTokens());

				int w = 0;
				while (st.hasMoreElements()) {
					int wordNo = Integer.valueOf(st.nextToken());
					currentDoc.setMatricValue(s, w, wordNo);
					w++;
				}

			}

			documents.add(currentDoc);

			docsNum = docsNum + numSentences;

		}

		inputDocsFile.close();

		/** 
		 * from Document file to the Corpus data format
		 */

		System.out.println("Parseing documents...");

		docs = new int[docsNum][];

		int docsIndex = 0;

		// document level
		for (Document document : documents) {

			// sentence level
			for (int sentence = 0; sentence < document.getNumberOfSentence(); sentence++) {
				docs[docsIndex] = new int[document
						.getWordNumberInOneSentenceint(sentence)];

				// word level
				for (int wordIndex = 0; wordIndex < document
						.getWordNumberInOneSentenceint(sentence); wordIndex++) {

					int wordNo = document.getMatricValue(sentence, wordIndex);

					docs[docsIndex][wordIndex] = wordNo;

					if (!wordIDToSetOfDocsMap.containsKey(wordNo)) {
						wordIDToSetOfDocsMap
								.put(wordNo, new HashSet<Integer>());
					}

					HashSet<Integer> setOfDocs = wordIDToSetOfDocsMap
							.get(wordNo);
					setOfDocs.add(docsIndex);

				}

				docsIndex++;
			}

		}

		/**
		 * read vocabulary
		 */
		Vector<String> wordList = null;
		wordList = new Vector<String>();

		String abVavabPath = domainDirPath + File.separator + vacabPath;// File.separator

		BufferedReader wordListFile = new BufferedReader(new FileReader(
				new File(abVavabPath)));
		while ((line = wordListFile.readLine()) != null)
			if (line != "")
				wordList.add(line);
		wordListFile.close();

		vocab = Vocabulary.getVocabularyFromList(wordList);

		/*docs-term frequency. for implementing the idea of aspect sparsity.*/
		docsTermFreq = new HashMap[docs.length];
		for (int d = 0; d < docs.length; d++) {
			docsTermFreq[d] = new HashMap<Integer, Integer>();
			for (int n = 0; n < docs[d].length; n++) {
				int key = docs[d][n];
				if (docsTermFreq[d].containsKey(key)) {
					docsTermFreq[d].put(key, docsTermFreq[d].get(key) + 1);
				} else {
					docsTermFreq[d].put(key, 1);
				}
			}
		}
	}

	public HashMap<Integer, Integer> getDocsTermFrequency(int doc) {

		return docsTermFreq[doc];

	}

	public int getDocumentFrequency(int wordID) {
		if (!wordIDToSetOfDocsMap.containsKey(wordID)) {
			return 0;
		}
		return wordIDToSetOfDocsMap.get(wordID).size();
	}

	public int getCoDocumentFrequency(int wordID1, int wordID2) {
		if (!wordIDToSetOfDocsMap.containsKey(wordID1)
				|| !wordIDToSetOfDocsMap.containsKey(wordID2)) {
			return 0;
		}
		HashSet<Integer> setOfDocs1 = wordIDToSetOfDocsMap.get(wordID1);
		HashSet<Integer> setOfDocs2 = wordIDToSetOfDocsMap.get(wordID2);
		HashSet<Integer> intersection = new HashSet<Integer>(setOfDocs1);
		intersection.retainAll(setOfDocs2);
		return intersection.size();
	}
}
