package qomo.diffexpress;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.lang3.ArrayUtils;

import qomo.data.dataframe.DataFrame;
import qomo.data.dataframe.DataFrameWriter;

public abstract class AbstractDiffExpressTest {
	protected String[] geneNames; // A string array of gene names
	protected long[][][] expMatrix; // 2 matrix of expression

	protected int numSample;
	protected int numGene;
	protected int[] numCol;

	// intermediate results ()
	// protected long[][] depth;
	protected double[][] exp;
	protected long[] total;
	protected double[] M;
	protected double[] A;

	protected double[] zscore;
	protected double[] pvalue;
	protected double[] qvalue;
	protected boolean[] isDiff;

	protected ThresholdType thresholdType;
	// Thresholds of pvalue, qvalue and zscore
	protected double threshold;

	protected Reader input;
	protected Writer output;

	protected abstract void findDiff();

	protected abstract void output();

	public AbstractDiffExpressTest(Reader input, Writer output, int[][] expCol,
			ThresholdType thresholdType, double threshold) {
		// Initialize inputs
		this.input = input;
		this.output = output;
		this.thresholdType = thresholdType;
		this.threshold = threshold;

		ExpressionTableReader reader = new ExpressionTableReader(input);
		try {
			reader.read(expCol);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.geneNames = reader.getGeneNames();
		this.expMatrix = reader.getExpMatrix();
		this.numSample = this.expMatrix.length;
		this.numGene = reader.getNumRow();
		this.numCol = new int[numSample];
		for (int i = 0; i < numSample; i++) {
			this.numCol[i] = expCol[i].length;
		}

		// Initialize outputs
		exp = new double[numSample][numGene];
		total = new long[numSample];
		M = new double[numGene];
		A = new double[numGene];
		zscore = new double[numGene];
		pvalue = new double[numGene];
		qvalue = new double[numGene];
		isDiff = new boolean[numGene];
	}

	protected void count() {
		for (int i = 0; i < numSample; i++) {
			// depth = new long[nSample][nCol[i]];
			for (int j = 0; j < numGene; j++) {
				for (int k = 0; k < numCol[i]; k++) {
					long val = expMatrix[i][j][k];
					exp[i][j] += val;
					// depth[i][k] += val;
					total[i] += val;
				}
			}
		}
	}

	protected void calculateMA() {
		// Pair2Norm
		double[][] logExp = new double[numSample][numGene];
		for (int j = 0; j < numGene; j++) {
			for (int i = 0; i < numSample; i++) {
				logExp[i][j] = Math.log(exp[i][j]) / Math.log(2);
			}
			M[j] = (logExp[0][j] - logExp[1][j]);
			A[j] = 0.5 * (logExp[0][j] + logExp[1][j]);
		}
	}

	protected void preprocessExp() {
		// getPairs
		for (int j = 0; j < numGene; j++) {
			if (exp[0][j] == 0 && exp[1][j] == 0) {
				exp[0][j] = exp[1][j] = Double.NaN;
			}
		}
	}

	protected void preprocess() {
		count();
		preprocessExp();
		calculateMA();
	}

	public void run() {
		preprocess();
		findDiff();
		qvalue = MathUtil.qvalueB95(pvalue);
		setIsDiff();
		output();
	}

	protected DataFrame initOutput() {
		DataFrame dm = new DataFrame();
		dm.addColumn("\"GeneNames\"", geneNames);
		dm.addColumn("\"value1\"", ArrayUtils.toObject(exp[0]));
		dm.addColumn("\"value2\"", ArrayUtils.toObject(exp[1]));
		dm.addColumn("\"log2(Fold_change)\"", ArrayUtils.toObject(M));
		dm.addColumn("\"log2(Fold_change) normalized\"",
				ArrayUtils.toObject(getNormalizedMValue()));
		dm.addColumn("\"p-value\"", ArrayUtils.toObject(pvalue));
		dm.addColumn("\"q-value(Benjamini et al. 1995)\"",
				ArrayUtils.toObject(qvalue));
		dm.addColumn(getLastColumnName(), ArrayUtils.toObject(isDiff));
		return dm;
	}

	protected void outputDM(DataFrame dm) {
		DataFrameWriter dmw = new DataFrameWriter(output);
		try {
			dmw.write(dm);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dmw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected String getLastColumnName() {
		String columnName = new String();
		switch (thresholdType) {
		case PVALUE:
			columnName = "\"Signature(p-value < " + threshold + ")\"";
			break;
		case QVALUE:
			columnName = "\"Signature(q-value(Benjamini et al. 1995) < "
					+ threshold + ")\"";
			break;
		// case ZSCORE:
		// columnName = "\"Signature(z-socre < " + threshold + ")\"";
		// break;
		}
		return columnName;
	}

	protected double[] getNormalizedMValue() {
		double[] normalizedMVal = new double[numGene];
		for (int j = 0; j < numGene; j++) {
			normalizedMVal[j] = M[j] + Math.log(((double) total[1]) / total[0])
					/ Math.log(2);
		}
		return normalizedMVal;
	}

	protected void setIsDiff() {
		for (int j = 0; j < numGene; j++) {
			switch (thresholdType) {
			case PVALUE:
				isDiff[j] = pvalue[j] < threshold;
				break;
			// case ZSCORE:
			// isDiff[j] = zscore[j] < threshold;
			// break;
			case QVALUE:
				isDiff[j] = qvalue[j] < threshold;
				break;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		Reader input = new FileReader(
				"/share/RNA-seq/extdata/GeneExpExample5000.txt");
		Writer output = new FileWriter("/tmp/de.txt");
		AbstractDiffExpressTest des = new MARandomSample(input, output,
				new int[][] { { 6, 8, 11, 14, 17 }, { 7, 9, 10, 12, 15 } },
				ThresholdType.PVALUE, 0.001);
		des.run();
	}
}
