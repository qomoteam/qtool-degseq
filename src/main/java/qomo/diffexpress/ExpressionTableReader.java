package qomo.diffexpress;

import java.io.Closeable;

import java.io.IOException;
import java.io.Reader;

import qomo.data.dataframe.DataFrame;
import qomo.data.dataframe.DataFrameReader;

public class ExpressionTableReader implements Closeable{
	private String[] geneNames; // 2 vectors of gene names
	private long[][][] expMatrix; // 2 matrix of exp
	private DataFrame dm;
	private DataFrameReader reader;

	public ExpressionTableReader(Reader reader) {
		this.reader = new DataFrameReader(reader);
	}

	public void read(int expCol[][]) throws IOException {
		dm = reader.read();
		int nSample = expCol.length;
		expMatrix = new long[nSample][][];
		int[] nCol = new int[nSample];
		int nRow = dm.rowSize();
		geneNames = new String[nRow];
		for (int j = 0; j < nRow; j++) {
			geneNames[j] = (String)dm.get(j, 0);
		}
		for (int i = 0; i < nSample; i++) {
			nCol[i] = expCol[i].length;
			expMatrix[i] = new long[nRow][nCol[i]];
			for (int j = 0; j < nRow; j++) {
				for (int k = 0; k < nCol[i]; k++) {
					expMatrix[i][j][k] = (Long)(dm.get(j, expCol[i][k]-1));
				}
			}
		}
	}

	public void close() throws IOException {
		reader.close();
	}

	public String[] getGeneNames() {
		return geneNames;
	}

	public long[][][] getExpMatrix() {
		return expMatrix;
	}
	
	public int getNumRow(){
		return dm.rowSize();
	}
	
	public int getNumCol(){
		return dm.columnSize();
	}
}
