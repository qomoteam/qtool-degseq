package qomo.diffexpress;

import java.io.Reader;
import java.io.Writer;

import qomo.data.dataframe.DataFrame;

public class PoissonLikelihoodRatioTest extends AbstractDiffExpressTest {

	public PoissonLikelihoodRatioTest(Reader input, Writer output,
			int[][] expCol, ThresholdType thresholdType, double thresholdValue) {
		super(input, output, expCol, thresholdType, thresholdValue);
	}

	@Override
	protected void findDiff() {
		double[] lambda = new double[numSample];
		double[] elambda = new double[numSample];
		double[] ob = new double[numSample];

		for (int j = 0; j < numGene; j++) {
			if (Double.isNaN(exp[0][j]) || Double.isNaN(exp[1][j])
					|| (exp[0][j] == 0 && exp[1][j] == 0)) {
				pvalue[j] = Double.NaN;
				continue;
			}
			for (int i = 0; i < numSample; i++) {
				ob[i] = Math.ceil(exp[i][j]);
			}
			for (int i = 0; i < numSample; i++) {
				lambda[i] = ob[i];
				elambda[i] = (ob[0] + ob[1]) * total[i] / (total[0] + total[1]);
			}
			double chisq = -2
					* (Math.log(MathUtil.dpois(ob[0], elambda[0]))
							+ Math.log(MathUtil.dpois(ob[1], elambda[1]))
							- Math.log(MathUtil.dpois(ob[0], lambda[0])) - Math
								.log(MathUtil.dpois(ob[1], lambda[1])));
			pvalue[j] = 1 - MathUtil.pchisq(chisq, 1);
			if (pvalue[j] > 1)
				pvalue[j] = 1.0;
			if (pvalue[j] < 0)
				pvalue[j] = 0.0;
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
