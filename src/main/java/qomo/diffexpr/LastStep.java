package qomo.diffexpr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;

public abstract class LastStep implements Step {
	public Path input;
	public Path total;
	public Path output;

	protected void setConf(Configuration conf) throws IOException {
		FileSystem fs = FileSystem.get(conf);
		for (Path file : LastStep.ls(fs, total)) {
			SequenceFile.Reader reader = new SequenceFile.Reader(fs, file,
					fs.getConf());
			IntWritable key = new IntWritable();
			LongWritable val = new LongWritable();
			while (reader.next(key, val)) {
				conf.setLong(key.toString(), val.get());
			}
			reader.close();
		}
	}

	public static List<Path> ls(FileSystem fs, Path path) throws IOException {
		List<Path> subpaths = new ArrayList<Path>();
		FileStatus[] fstats = fs.listStatus(path);
		for (FileStatus fstat : fstats) {
			Path subpath = fstat.getPath();
			if (subpath.getName().startsWith("_"))
				continue;
			subpaths.add(subpath);
		}
		return subpaths;
	}
}