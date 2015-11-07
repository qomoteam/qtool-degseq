package qomo.diffexpr;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import qomo.common.CSVUtil;
import qomo.common.io.LongArrayWritable;

public class SumReplica implements Step {

	public Path input;
	public Path output;
	public String expCol;

	public static class TheMapper extends
			Mapper<LongWritable, Text, Text, LongArrayWritable> {

		private int[][] expCol;
		private int numSample;

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			super.setup(context);
			Configuration conf = context.getConfiguration();
			numSample = 2;
			expCol = new int[numSample][];
			for (int i = 0; i < numSample; i++) {
				expCol[i] = Util.strings2ints(conf.getStrings("expCol" + i));
			}
		}

		public void map(LongWritable _, Text valIn, Context context)
				throws IOException, InterruptedException {
			LongWritable[] valueOut = new LongWritable[numSample];
			String[] fields = CSVUtil.split(valIn.toString());
			for (int i = 0; i < numSample; i++) {
				long sum = 0L;
				for (int j = 0; j < expCol[i].length; j++) {
					sum += Long.parseLong(fields[expCol[i][j] - 1]);
				}
				valueOut[i] = new LongWritable(sum);
			}
			Text keyOut = new Text(fields[0]);
			context.write(keyOut, new LongArrayWritable(valueOut));
		}
	}

	public Job build(Configuration conf) throws Exception {
		Job job = new Job(conf);
		conf = job.getConfiguration();
		int[][] expCol = Util.parseExpCol(this.expCol);
		for (int i = 0; i < expCol.length; i++) {
			conf.setStrings("expCol" + i, Util.ints2strings(expCol[i]));
		}
		job.setJarByClass(SumReplica.class);
		job.setMapperClass(TheMapper.class);
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongArrayWritable.class);
		job.setNumReduceTasks(0);
		FileInputFormat.addInputPath(job, input);
		FileOutputFormat.setOutputPath(job, output);
		return job;
	}
}
