package qomo.diffexpress;

import java.io.Reader;
import java.io.Writer;

import org.apache.commons.math3.distribution.NormalDistribution;

import qomo.data.dataframe.DataFrame;

public class MARandomSample extends AbstractDiffExpressTest {

	public MARandomSample(Reader input, Writer output, int[][] expCol,
			ThresholdType thresholdType, double thresholdValue) {
		super(input, output, expCol, thresholdType, thresholdValue);
	}

	@Override
	protected void preprocessExp() {
		for (int j = 0; j < numGene; j++) {
			if (exp[0][j] == 0 && exp[1][j] == 0) {
				exp[0][j] = exp[1][j] = Double.NaN;
			}
			if (exp[0][j] == 0 && exp[1][j] >= 5) {
				exp[0][j] = 0.5;
			}
			if (exp[1][j] == 0 && exp[0][j] >= 5) {
				exp[1][j] = 0.5;
			}
		}
	}

	@Override
	protected void findDiff() {
		for (int j = 0; j < numGene; j++) {
			double p, sd, mean;
			double a = A[j];
			p = Math.pow(2, a) / Math.pow(total[0] * total[1], 0.5);
			sd = Math.pow((4 * (1 - p) / ((total[0] + total[1]) * p)), 0.5)
					/ Math.log(2);
			mean = (Math.log(total[0] * p) - Math.log(total[1] * p))
					/ Math.log(2);
			zscore[j] = (M[j] - mean) / sd;
			NormalDistribution normDist = new NormalDistribution();
			pvalue[j] = 2 * normDist
					.cumulativeProbability(-Math.abs(zscore[j]));
		}
	}

	@Override
	protected void output() {
		// DataFrame dm = initOutput();
		// dm.addColumn("\"z-score\"",
		// new Vector<Double>(ArrayUtils.toObject(zscore)), 5);
		// dm.sortByColumn(5, new Comparator<Object>() {
		// public int compare(Object a, Object b) {
		// Double da = Math.abs((Double) a);
		// Double db = Math.abs((Double) b);
		// if (da.isNaN() || db.isNaN())
		// return da.compareTo(db);
		// else
		// return -da.compareTo(db);
		// }
		// });
		// dm.sortByColumn(8, true);
		// outputDM(dm);
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
