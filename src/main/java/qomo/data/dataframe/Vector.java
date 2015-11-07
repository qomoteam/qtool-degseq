package qomo.data.dataframe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores a vector of any type.
 * 
 * @author liu
 */
public class Vector<T> {

	private Class<T> clazz;
	private List<T> vector;

	public Vector(Class<T> clazz) {
		this.clazz = clazz;
		this.vector = new ArrayList<T>();
	}

	public Vector(T[] arr) {
		this.clazz = (Class<T>) arr.getClass().getComponentType();
		this.vector = new ArrayList<T>();
		for (T e : arr) {
			this.add(e);
		}
	}

	public Vector(Class<T> clazz, List<T> list) {
		this.clazz = clazz;
		Collections.copy(this.vector, list);
	}

	public Vector(Class<T> clazz, Vector<?> src) {
		this(clazz);
		for (Object elem : src.vector) {
			this.add(elem);
		}
	}

	private T fromString(String str) throws ClassCastException {
		if (clazz.equals(Long.class)) {
			return ((T) new Long(parseDouble(str).longValue()));
		} else if (clazz.equals(Integer.class)) {
			return ((T) new Integer(parseDouble(str).intValue()));
		} else if (clazz.equals(Float.class)) {
			return ((T) new Float(parseDouble(str).floatValue()));
		} else if (clazz.equals(Double.class)) {
			return ((T) parseDouble(str));
		} else if (clazz.equals(Boolean.class)) {
			return ((T) Boolean.valueOf(str));
		} else if (clazz.equals(String.class)) {
			return ((T) str);
		} else {
			throw new ClassCastException("Convert from string failed");
		}
	}

	private Double parseDouble(String str) {
		try {
			return Double.valueOf(str);
		} catch (NumberFormatException e) {
			return Double.NaN;
		}
	}

	public int size() {
		return vector.size();
	}

	public T get(int index) {
		return vector.get(index);
	}

	public void set(int index, Object elem) {
		vector.set(index, clazz.cast(elem));
	}

	public void add(Object elem) {
		try {
			vector.add(clazz.cast(elem));
		} catch (ClassCastException e) {
			vector.add(this.fromString(elem.toString()));
		}
	}

	public T remove(int index) {
		return vector.remove(index);
	}

	public <P> Vector<P> as(Class<P> clazz) {
		return new Vector<P>(clazz, this);
	}

	public Class<T> type() {
		return clazz;
	}

	public static <P> Vector<P> clone(Vector<P> src) {
		return new Vector<P>(src.clazz, src);
	}

	public static <P> Vector<P> newInstance(Class<P> clazz) {
		return new Vector<P>(clazz);
	}

	public static void main(String[] args) {
		Integer[] arr = new Integer[] { 11, 2, -19999 };
		Vector v = new Vector<Integer>(arr);

		v = v.as(Object.class);
		v.add("Hi");

		for (int i = 0; i < v.size(); i++) {
			System.out.println(v.get(i) + "\t" + v.get(i).getClass());
		}

		v = v.as(Double.class);
		v.add("Infinity");
		for (int i = 0; i < v.size(); i++) {
			System.out.println(v.get(i) + "\t" + v.get(i).getClass());
		}

		Vector<Integer> v2 = v.as(Integer.class);
		for (int i = 0; i < v2.size(); i++) {
			int k = v2.get(i);
			System.out.println(k + "\t" + v2.get(i).getClass());
		}
		// Collection< List<String> > c1 = new ArrayList<List<String>>();
		// Collection< List<String> > c2 = c1; // fine
		// Collection< List<?> > c3 = c1; // error
		// Collection< ? extends List<?> > c4 = c1; // fine
	}

}
