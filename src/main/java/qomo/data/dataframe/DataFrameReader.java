package qomo.data.dataframe;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

public class DataFrameReader implements Closeable {

	private BufferedReader reader;
	private String delim;
	private Boolean hasColumnNames;
	private String[] columnNames;

	public DataFrameReader(Reader reader) {
		this(reader, "\t", true);
	}

	public DataFrameReader(Reader reader, String delim, Boolean hasColumnNames) {
		this.reader = new BufferedReader(reader);
		this.delim = delim;
		this.hasColumnNames = hasColumnNames;
	}

	private void addColumnByElem(DataFrame dm, String name, String elem) {
		try {
			Long.valueOf(elem);
			dm.addColumn(name, new Vector<Long>(Long.class));
		} catch (NumberFormatException e1) {
			try {
				Double.valueOf(elem);
				dm.addColumn(name, new Vector<Double>(Double.class));
			} catch (NumberFormatException e2) {
				dm.addColumn(name, new Vector<String>(String.class));
			}
		}

	}

	private String columnName(int index) {
		try {
			return columnNames[index];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public DataFrame read() throws IOException {
		DataFrame dm;
		String line = reader.readLine();
		if (line == null) {
			return null;
		}

		if (hasColumnNames) {
			dm = new DataFrame();
			columnNames = line.split(delim);
			line = reader.readLine();
			if (line == null) {
				return null;
			}
		} else {
			dm = new DataFrame();
		}
		String[] firstRow = line.split(delim);
		for (int i = 0; i < firstRow.length; i++) {
			addColumnByElem(dm, columnName(i), firstRow[i]);
		}
		while (line != null) {
			dm.addRow(line.split(delim));
			line = reader.readLine();
		}
		return dm;
	}

	public void close() throws IOException {
		reader.close();
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
