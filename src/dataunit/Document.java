package dataunit;


public class Document {
	int label = -1;
	public int getLabel() {
		return label;
	}
	
	public void setLabel(int label) {
		this.label = label;
	}

	public int[][] getSentenceWordMatrix() {
		return sentenceWordMatrix;
	}

	public void setSentenceWordMatrix(int[][] sentenceWordMatrix) {
		this.sentenceWordMatrix = sentenceWordMatrix;
	}

	int sentenceWordMatrix [][] = null;
	
	public void initSentenceLength(int length){
		this.sentenceWordMatrix = new int[length][];
	}
	
	public void initWordLengthInSentence(int row, int length){
		this.sentenceWordMatrix[row] = new int[length];
	}
	
	public void setMatricValue(int rowIdx, int colIdx, int value){
		sentenceWordMatrix[rowIdx][colIdx] = value;		
	}
	
	
	public int getNumberOfSentence(){
		return  sentenceWordMatrix.length;
	}
	
	public int getWordNumberInOneSentenceint(int sentenceIndex){
		return  sentenceWordMatrix[sentenceIndex].length;
	}
	
	
	public int getNumberOfWords(){
		int wc = 0;
		for(int row = 0 ; row < sentenceWordMatrix.length; row++){
			wc+=sentenceWordMatrix[row].length;
		}
		return wc;
	}
	
	public int getMatricValue(int rowIdx, int colIdx){
		return sentenceWordMatrix[rowIdx][colIdx];
	}
	
	
	
}
