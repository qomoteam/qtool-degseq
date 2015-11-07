package qomo.diffexpress;

import java.io.Reader;
import java.io.Writer;

import qomo.data.dataframe.DataFrame;

public class FisherExactTest extends AbstractDiffExpressTest {

	public FisherExactTest(Reader input, Writer output, int[][] expCol,
			ThresholdType thresholdType, double thresholdValue) {
		super(input, output, expCol, thresholdType, thresholdValue);
	}

	@Override
	protected void findDiff() {
		long[][] matrix = new long[2][2];
		long[] ob = new long[numSample];
		for (int j = 0; j < numGene; j++) {
			if (Double.isNaN(exp[0][j]) || Double.isNaN(exp[1][j])) {
				pvalue[j] = Double.NaN;
				continue;
			}
			for (int i = 0; i < numSample; i++) {
				ob[i] = Math.round(exp[i][j]);
			}
			matrix[0][0] = ob[0];
			matrix[1][0] = ob[1];
			matrix[0][1] = total[0] - ob[0];
			matrix[1][1] = total[1] - ob[1];
			pvalue[j] = MathUtil.fishersExactTest2by2(matrix);
		}
	}

	@Override
	protected void output() {
		DataFrame dm = initOutput();
		switch (thresholdType) {
		case PVALUE:
			dm.sortByColumn(5, false);
			break;
		case QVALUE:
			dm.sortByColumn(6, false);
			break;
		}
		outputDM(dm);
	}

}
