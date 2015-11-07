package qomo.data.dataframe;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Matrix for storing data of different (comparable) types.
 * 
 * @author Siqi Liu
 * 
 */
public class DataFrame {

	private List<Vector<?>> columns;
	private List<String> columnNames;
	private List<Integer> rowIndex;

	public DataFrame() {
		this.columnNames = new ArrayList<String>();
		this.columns = new ArrayList<Vector<?>>();
		this.rowIndex = new ArrayList<Integer>();
	}

	public int columnSize() {
		return columns.size();
	}

	public int rowSize() {
		if (columns.isEmpty()) {
			return 0;
		} else {
			return columns.get(0).size();
		}
	}

	private void fillRowIndex(int n) {
		for (int i = 0; i < n; i++) {
			rowIndex.add(i);
		}
	}

	public Vector<Object> getColumn(int k) {
		return getColumnAs(k, Object.class);
	}

	public <T> Vector<T> getColumnAs(int k, Class<T> clazz) {
		return new Vector<T>(clazz, columns.get(k));
	}

	public void addColumn(String name, List<Object> column) {
		addColumn(name, new Vector<Object>(column.toArray()));
	}

	public void addColumn(String name, Object[] column) {
		addColumn(name, new Vector<Object>(column));
	}

	/**
	 * Appends a column named columnName to the matrix. Does not change the
	 * number of rows unless the matrix is null.
	 * 
	 * @param name
	 * @param column
	 */
	public void addColumn(String name, Vector<?> column) {
		addColumn(name, column, columnSize());
	}

	public void addColumn(String name, Vector<?> column, int pos) {
		if (pos < 0 || pos > columnSize()) {
			throw new IndexOutOfBoundsException();
		}
		if (columns.isEmpty()) {
			fillRowIndex(column.size());
		}
		columnNames.add(pos, name);
		columns.add(pos, Vector.clone(column));
	}

	public Vector<?> removeColumn(int pos) {
		columnNames.remove(pos);
		Vector<?> column = columns.remove(pos);
		if (columns.isEmpty()) {
			rowIndex.clear();
		}
		return column;
	}

	public Vector<?> repalceColumn(String name, Vector<?> column, int pos) {
		Vector<?> old = removeColumn(pos);
		addColumn(name, column, pos);
		return old;
	}

	public List<Object> getRow(int pos) {
		List<Object> row = new ArrayList<Object>();
		for (int i = 0; i < columnSize(); i++) {
			row.add(columns.get(i).get(pos));
		}
		return row;
	}

	/**
	 * Appends a row to the matrix. Does not change the number of columns unless
	 * the matrix is null.
	 * 
	 * @param row
	 */
	public void addRow(List<Object> row) {
		addRow(row, rowSize());
	}

	public void addRow(Object[] row) {
		addRow(Arrays.asList(row));
	}

	public void addRow(List<Object> row, int pos) {
		if (pos < 0 || pos > rowSize()) {
			throw new IndexOutOfBoundsException();
		}
		if (columns.isEmpty()) {
			for (Object obj : row) {
				Object[] temp = new Object[1];
				temp[0] = obj;
				addColumn("", new Vector<Object>(temp));
			}
		} else {
			rowIndex.add(pos, rowSize());
			int k = 0;
			for (Object e : row) {
				if (k >= columnSize()) {
					break;
				}
				columns.get(k).add(e);
				k++;
			}
		}
	}

	public List<Object> removeRow(int pos) {
		int j = rowIndex.remove(pos);
		List<Object> row = new ArrayList<Object>();
		for (Vector<?> column : columns) {
			row.add(column.remove(j));
		}
		return row;
	}

	public List<Object> replaceRow(List<Object> row, int pos) {
		List<Object> old = removeRow(pos);
		addRow(row, pos);
		return old;
	}

	public List<String> getColumnNames() {
		return new ArrayList<String>(columnNames);
	}

	public boolean isNull() {
		return columnSize() == 0;
	}

	public boolean isEmpty() {
		return rowSize() == 0;
	}

	public Object get(int row, int col) {
		return columns.get(col).get(rowIndex.get(row));
	}

	public void set(int row, int col, Object value) {
		columns.get(col).set(rowIndex.get(row), value);
	}

	public void sortByColumn(final int k, final Comparator<Object> comp) {
		Collections.sort(rowIndex, new Comparator<Integer>() {
			public int compare(Integer a, Integer b) {
				return comp.compare(columns.get(k).get(a), columns.get(k)
						.get(b));
			}
		});
	}

	public void sortByColumn(final int k, final boolean descend) {
		sortByColumn(k, new Comparator<Object>() {
			@SuppressWarnings("unchecked")
			public int compare(Object a, Object b) {
				if (descend) {
					return -((Comparable<Object>) a).compareTo(b);
				} else {
					return ((Comparable<Object>) a).compareTo(b);
				}
			}
		});
	}

	public static void main(String[] args) throws IOException {
		String[] header = { "ID", "Word", "Frequency", "Stopped" };
		DataFrame dm = new DataFrame();
		Integer[] col1 = { 2, 21, 33, 4, 52 };
		String[] col2 = { "Hi", "hello", "nothing", "this", "happen" };
		Double[] col3 = { 4.5, 0.618, 3.1415, -99.99, 1.0 };
		Boolean[] col4 = { true, true, false, false, false };
		dm.addColumn(header[0], col1);
		dm.addColumn(header[1], col2);
		dm.addColumn(header[2], col3);
		dm.addColumn(header[3], col4);
		DataFrameWriter dmw = new DataFrameWriter(new FileWriter(
				"/home/liu/test.txt"), "\t", true);
		dmw.write(dm);
		dmw.close();

		DataFrameReader dmr = new DataFrameReader(new FileReader(
				"/home/liu/test.txt"), "\t", true);
		dm = dmr.read();
		dmr.close();
		dmw = new DataFrameWriter(new OutputStreamWriter(System.out), "\t",
				true);
		dmw.write(dm);
		dm.sortByColumn(0, true);
		dmw.write(dm);
		dm.sortByColumn(2, false);
		dmw.write(dm);
		dmw.close();
	}
}
