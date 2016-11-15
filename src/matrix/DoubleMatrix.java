package matrix;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class DoubleMatrix implements Serializable {
	
	private static final long serialVersionUID = -5033150593640676995L;
			
	private int numOfRow;
	private int numOfColumn;
	private double [][] row;
	
	public DoubleMatrix(int numOfRow, int numOfColumn){
		this.numOfRow = numOfRow;
		this.numOfColumn = numOfColumn;
		row = new double[numOfRow][numOfColumn];
		for (int i = 0; i < numOfRow; i++)
			for (int j = 0; j < numOfColumn; j++)
				row[i][j] = 0;
	}
	
	// import matrix from file
	public DoubleMatrix(int numOfRow, int numOfColumn, String fileType, String file) throws Exception{
		String line;
		int lineNumber=0;
		this.numOfRow = numOfRow;
		this.numOfColumn = numOfColumn;
		row = new double[numOfRow][numOfColumn];
		
		BufferedReader fileReader = new BufferedReader(new FileReader(file));
		
		if(fileType.toLowerCase() == "normal"){
		    while((line = fileReader.readLine()) != null){
		    	String[] elements = line.split("\\s+");
		    	for(int j=0; j<elements.length;j++){
					row[lineNumber][j] = Integer.valueOf(elements[j]);
		    	}
		    	lineNumber++;
		    }
		}else if(fileType.toLowerCase() == "special"){
			for (int i = 0; i < numOfRow; i++)
				for (int j = 0; j < numOfColumn; j++)
					row[i][j] = 0;
			
			String docLine;
		    while((line = fileReader.readLine()) != null){
		    	docLine = fileReader.readLine();
		    	String[] elements = line.split("\\s+");
		    	int docWords = Integer.valueOf(elements[0]);
		    	
		    	String[] wordByCount = docLine.split("\\s+");
		    	
		    	if(docWords*2 != wordByCount.length){
		    		System.err.println("Matrix File Corrupted");
		    		System.err.println(line);
		    		System.exit(0);
		    	}
		    	
		    	for(int i = 0; i < docWords ; i++){
		    		row[lineNumber][Integer.valueOf(wordByCount[2*i])] = Integer.valueOf(wordByCount[2*i+1]);
		    	}
		    	lineNumber++;
		    }
		}
	    fileReader.close();
	}
	
	public int getNumOfRow() {
		return numOfRow;
	}

	public int getNumOfColumn() {
		return numOfColumn;
	}
	
	// set value of matrix(rowIdx, colIdx) to value
	public void setValue(int rowIdx, int colIdx, double value){
		row[rowIdx][colIdx] = value;		
	}
	
	// get value of matrix(rowIdx, colIdx)
	public double getValue(int rowIdx, int colIdx){
		return row[rowIdx][colIdx];
	}
	
	public void incValue(int rowIdx, int colIdx){
		row[rowIdx][colIdx]++;
	}
	
	public void incValue(int rowIdx, int colIdx, double value){
		row[rowIdx][colIdx] += value;
	}
	
	public void decValue(int rowIdx, int colIdx){
		row[rowIdx][colIdx]--;
	}
	
	public void decValue(int rowIdx, int colIdx, double value){
		row[rowIdx][colIdx] -= value;
	}
	
	public double[] getRow(int rowIdx){
		return row[rowIdx];
	}
	
	// get sum of row elements
	public double getRowSum(int rowIdx){
		double sum = 0;
		for (int i = 0; i < numOfColumn; i++)
			sum += row[rowIdx][i];
		return sum;
	}
	
	public double[] getColumn(int colIdx){
		if(colIdx > numOfColumn){
			return null;
		}
		double[] col = new double[numOfRow];
		for(int i=0; i<numOfRow; i++){
			col[i] = row[i][colIdx];
		}
		return col;
	}
	
	// get sum of column elements
	public double getColSum(int colIdx){
		if(colIdx > numOfColumn){
			return 0;
		}
		double sum=0;
		for(int i=0; i<numOfRow; i++){
			sum += row[i][colIdx];
		}		
		return sum;
	}
	
	public double [][] getMatrix() {
		return row;
	}
	
	public Vector<Integer> getSortedRowIndex(int row, int n){
		Vector<Integer> sortedList = new Vector<Integer>();
		
		for(int i=0 ; i < n ; i++){
			double maxValue = Integer.MIN_VALUE;
			int maxIndex = -1;
			for(int col=0 ; col<numOfColumn ; col++){
				if(getValue(row, col) > maxValue){
					boolean exist = false;
					for(int j=0 ; j<sortedList.size(); j++){
						if (sortedList.get(j) == col){
							exist = true;
							break;
						}
					}
					if(!exist){
						maxValue = getValue(row, col);
						maxIndex = col;
					}
				}
			}
			sortedList.add(maxIndex);
		}
		
		return sortedList;
	}

	public Vector<Integer> getSortedColIndex(int col, int n){
		Vector<Integer> sortedList = new Vector<Integer>();
		
		for(int i=0 ; i < n ; i++){
			double maxValue = Integer.MIN_VALUE;
			int maxIndex = -1;
			for(int row=0 ; row<numOfRow ; row++){ 
				if(getValue(row, col) > maxValue){
					boolean exist = false;
					for(int j=0 ; j<sortedList.size(); j++){
						if (sortedList.get(j) == row){
							exist = true;
							break;
						}
					}
					if(!exist){
						maxValue = getValue(row, col);
						maxIndex = row;
					}
				}//if
			}//for
			sortedList.add(maxIndex);
		}
		
		return sortedList;
	}
	
	public void writeMatrixToCSVFile(String outputFilePath) throws Exception{
		PrintWriter out = new PrintWriter(new FileWriter(new File(outputFilePath)));
		
		for(int row=0; row < numOfRow ; row++){
			for(int col=0; col < numOfColumn ; col++){
				if(col == 0) out.print(getValue(row, col));
				else out.print(","+getValue(row, col));
			}
			out.println();
		}

		out.close();
	}

	public DoubleMatrix copy() {
		DoubleMatrix temp = new DoubleMatrix(this.numOfRow, this.numOfColumn);
		
		for(int row=0; row < numOfRow ; row++){
			for(int col=0; col < numOfColumn ; col++){
				temp.setValue(row, col, this.getValue(row, col));
			}
		}
		
		return temp;
	}

	public List<Double> getRowList(int row){
		List<Double> list = new ArrayList<Double>();
		
		for(int i=0; i<numOfColumn; i++){
			list.add(getValue(row, i));
		}
		return list;
	}

	public List<Double> getColList(int col){
		List<Double> list = new ArrayList<Double>();
		
		for(int i=0; i<numOfRow; i++){
			list.add(getValue(i, col));
		}
		return list;
	}
	

}
