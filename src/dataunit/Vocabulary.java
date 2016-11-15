package dataunit;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class Vocabulary implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Map<Integer, String> wordidToWordstrMap = null;
	public Map<String, Integer> wordstrToWordidMap = null;

	public Vocabulary() {
		wordstrToWordidMap = new TreeMap<String, Integer>();
		wordidToWordstrMap = new TreeMap<Integer, String>();

	}
	
	public String getWordStr(int wordID){
		return wordidToWordstrMap.get(wordID);
	}
	
	public int getWordID(String word){
		return wordstrToWordidMap.get(word);
	}
	
	public boolean containsWordID(int wordID){
		return wordidToWordstrMap.containsKey(wordID);
	}

	public boolean containsWord(String wordStr){
		return wordstrToWordidMap.containsKey(wordStr);
	}
	
	public int size(){
		return wordidToWordstrMap.size();
	}
	
	public static Vocabulary getVocabularyFromList(Vector<String> wordList) {
		Vocabulary vocab = new Vocabulary();
		for (int wordID = 0; wordID < wordList.size(); wordID++) {
			if (!vocab.wordidToWordstrMap.containsKey(wordID)) {
				vocab.wordidToWordstrMap.put(wordID, wordList.get(wordID));
			}
			if(!vocab.wordstrToWordidMap.containsKey(wordList.get(wordID))){
				vocab.wordstrToWordidMap.put(wordList.get(wordID), wordID);
			}
		}
		return vocab;
	}
}
