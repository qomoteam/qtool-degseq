package qomo.diffexpress;

import java.io.Reader;
import java.io.Writer;

public class FisherExactTestTool extends AbstractDiffExpressTool {

	@Override
	protected boolean innerRun(Reader input, Writer output, int[][] expCol,
			ThresholdType thresholdType, double thresholdValue) {
		new FisherExactTest(input, output, expCol, thresholdType,
				thresholdValue).run();
		return true;
	}

}
