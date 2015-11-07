package qomo.diffexpr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class DEGSeqTool extends Configured implements Tool {
	public String input;

	public String output;

	public String expCol;

	public String model;

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		int res = ToolRunner.run(conf, new DEGSeqTool(), args);
		System.exit(res);
	}

	private LastStep makeLastStep() {
		if (model.equals("FET")) {
			return new FisherExactTest();
		} else if (model.equals("LRT")) {
			return new PoissonLikelihoodRatioTest();
		} else {
			return new MARandomSample();
		}
	}

	public int run(String[] args) throws Exception {
		input = args[1];
		output = args[2];
		expCol = args[3];
		model = args[4];
		System.out.println("Model: " + model);
		Path tmpDir = new Path(args[0]);

		Path expMatrix = tmpDir.suffix("/expMat");
		Path total = tmpDir.suffix("/total");

		SumReplica step1 = new SumReplica();
		step1.input = new Path(input);
		step1.output = expMatrix;
		step1.expCol = expCol;
		if (!step1.build(new Configuration(getConf())).waitForCompletion(true))
			return 1;

		SumGene step2 = new SumGene();
		step2.input = expMatrix;
		step2.output = total;
		if (!step2.build(new Configuration(getConf())).waitForCompletion(true))
			return 1;

		LastStep step3 = makeLastStep();
		step3.input = expMatrix;
		step3.total = total;
		step3.output = new Path(output);
		if (!step3.build(new Configuration(getConf())).waitForCompletion(true))
			return 1;

		return 0;
	}
}
