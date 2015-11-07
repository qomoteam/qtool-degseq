package qomo.diffexpr;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class DoubleComparator extends WritableComparator {

	protected DoubleComparator() {
		super(DoubleWritable.class, true);
	}

	@Override
	public int compare(WritableComparable db1, WritableComparable db2) {
		return Double.compare(((DoubleWritable) db1).get(),
				((DoubleWritable) db2).get());
	}

}
