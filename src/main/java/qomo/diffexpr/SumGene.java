package qomo.diffexpr;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;

import qomo.common.io.LongArrayWritable;

public class SumGene implements Step {
	public Path input;
	public Path output;

	public static class TheMapper extends
			Mapper<Text, LongArrayWritable, IntWritable, LongWritable> {
		public void map(Text keyIn, LongArrayWritable valueIn, Context context)
				throws IOException, InterruptedException {
			LongWritable[] fields = (LongWritable[]) valueIn.toArray();
			for (int i = 0; i < fields.length; i++) {
				context.write(new IntWritable(i), fields[i]);
			}
		}
	}

	public static class TheReducer extends
			Reducer<IntWritable, LongWritable, IntWritable, LongWritable> {
		public void reduce(IntWritable keyIn, Iterable<LongWritable> values,
				Context context) throws IOException, InterruptedException {
			long sum = 0L;
			for (LongWritable value : values) {
				sum += value.get();
			}
			context.write(keyIn, new LongWritable(sum));
		}
	}

	public Job build(Configuration conf) throws Exception {
		Job job = new Job(conf);
		job.setJarByClass(SumReplica.class);
		job.setMapperClass(TheMapper.class);
		job.setReducerClass(TheReducer.class);
		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(LongWritable.class);
		FileInputFormat.addInputPath(job, input);
		FileOutputFormat.setOutputPath(job, output);
		return job;
	}
}
