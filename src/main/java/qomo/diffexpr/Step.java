package qomo.diffexpr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;

public interface Step {
	Job build(Configuration conf) throws Exception;
}
