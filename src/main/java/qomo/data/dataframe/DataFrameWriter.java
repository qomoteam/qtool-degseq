package qomo.data.dataframe;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class DataFrameWriter implements Closeable {
	private BufferedWriter writer;
	private String delim;
	private Boolean hasColumnNames;
	
	public DataFrameWriter(Writer writer){
		this(writer,"\t",true);
	}

	public DataFrameWriter(Writer writer, String delim, Boolean hasColumnNames) {
		this.writer = new BufferedWriter(writer);
		this.delim = delim;
		this.hasColumnNames = hasColumnNames;
	}

	public void write(DataFrame dm) throws IOException {
		if (dm.isNull()) {
			return;
		}
		if (hasColumnNames) {
			writeLine(dm.getColumnNames());
		}
		if (dm.isEmpty()) {
			return;
		}
		for (int j = 0; j < dm.rowSize(); j++) {
			writer.write(dm.get(j, 0).toString());
			for (int k = 1; k < dm.columnSize(); k++) {
				writer.write(delim);
				writer.write(dm.get(j, k).toString());
			}
			writer.newLine();
		}
	}

	private void writeLine(Iterable<String> list) throws IOException {
		Iterator<String> it = list.iterator();
		if (!it.hasNext())
			return;
		writer.write(it.next());
		while (it.hasNext()) {
			writer.write(delim);
			writer.write(it.next());
		}
		writer.newLine();
	}

	public void flush() throws IOException {
		writer.flush();
	}

	public void close() throws IOException {
		writer.close();
	}

	public String getDelim() {
		return delim;
	}

	public void setDelim(String delim) {
		this.delim = delim;
	}

	public Boolean getHasColumnNames() {
		return hasColumnNames;
	}

	public void setHasColumnNames(Boolean hasHeader) {
		this.hasColumnNames = hasHeader;
	}
}
