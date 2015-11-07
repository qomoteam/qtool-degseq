package qomo.diffexpr;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import qomo.common.CSVUtil;
import qomo.common.io.LongArrayWritable;
import qomo.diffexpress.MathUtil;

public class PoissonLikelihoodRatioTest extends LastStep {
	public static class TheMapper extends
			Mapper<Text, LongArrayWritable, DoubleWritable, Text> {
		public long[] total;

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			super.setup(context);
			Configuration conf = context.getConfiguration();
			int numSample = 2;
			total = new long[numSample];
			for (int i = 0; i < numSample; i++) {
				total[i] = conf.getLong(Integer.toString(i), 0L);
			}
		}

		public void map(Text keyIn, LongArrayWritable valueIn, Context context)
				throws IOException, InterruptedException {
			LongWritable[] fields = (LongWritable[]) valueIn.toArray();
			int numSample = fields.length;
			double[] exp = new double[fields.length];
			for (int i = 0; i < numSample; i++) {
				exp[i] = fields[i].get();
			}

			double pvalue;
			double[] logExp = Util.logExp(exp);
			double M = Util.M(logExp[0], logExp[1]);

			double[] lambda = new double[numSample];
			double[] elambda = new double[numSample];
			double[] ob = new double[numSample];

			if (Double.isNaN(exp[0]) || Double.isNaN(exp[1])
					|| (exp[0] == 0 && exp[1] == 0)) {
				pvalue = Double.NaN;
			} else {
				for (int i = 0; i < numSample; i++) {
					ob[i] = Math.ceil(exp[i]);
				}
				for (int i = 0; i < numSample; i++) {
					lambda[i] = ob[i];
					elambda[i] = (ob[0] + ob[1]) * total[i]
							/ (total[0] + total[1]);
				}
				double chisq = -2
						* (Math.log(MathUtil.dpois(ob[0], elambda[0]))
								+ Math.log(MathUtil.dpois(ob[1], elambda[1]))
								- Math.log(MathUtil.dpois(ob[0], lambda[0])) - Math
									.log(MathUtil.dpois(ob[1], lambda[1])));
				pvalue = 1 - MathUtil.pchisq(chisq, 1);
				if (pvalue > 1)
					pvalue = 1.0;
				if (pvalue < 0)
					pvalue = 0.0;
			}

			Text result = new Text(CSVUtil.join(keyIn, exp[0], exp[1], M,
					Util.normalizedM(M, total), pvalue));
			context.write(new DoubleWritable(pvalue), result);
		}
	}

	public static class TheReducer extends
			Reducer<DoubleWritable, Text, NullWritable, Text> {
		public static final NullWritable nil = NullWritable.get();

		public void reduce(DoubleWritable key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			for (Text value : values) {
				context.write(nil, value);
			}
		}
	}

	public Job build(Configuration conf) throws IOException,
			InterruptedException, ClassNotFoundException {
		Job job = new Job(conf);
		conf = job.getConfiguration();
		setConf(conf);
		job.setJarByClass(SumReplica.class);
		job.setMapperClass(TheMapper.class);
		job.setReducerClass(TheReducer.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		job.setMapOutputKeyClass(DoubleWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);
		job.setSortComparatorClass(DoubleComparator.class);
		FileInputFormat.addInputPath(job, input);
		FileOutputFormat.setOutputPath(job, output);
		return job;
	}
}
