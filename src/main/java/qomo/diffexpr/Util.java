package qomo.diffexpr;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	public static int[] strings2ints(String[] strings) {
		int[] ints = new int[strings.length];
		for (int i = 0; i < strings.length; i++) {
			ints[i] = Integer.parseInt(strings[i]);
		}
		return ints;
	}

	public static String[] ints2strings(int[] ints) {
		String[] strings = new String[ints.length];
		for (int i = 0; i < ints.length; i++) {
			strings[i] = String.valueOf(ints[i]);
		}
		return strings;
	}

	public static double[] longs2doubles(long[] longs) {
		double[] doubles = new double[longs.length];
		for (int i = 0; i < longs.length; i++) {
			doubles[i] = (double) longs[i];
		}
		return doubles;
	}

	public static int[][] parseExpCol(String expCol) {
		Pattern groupPattern = Pattern.compile("\\([^)]+\\)|\\{[^}]+\\}");
		Pattern colPattern = Pattern.compile("\\d+");
		Matcher groupMatcher = groupPattern.matcher(expCol);
		List<String> group = new ArrayList<String>();
		int[][] result;
		while (groupMatcher.find()) {
			group.add(groupMatcher.group());
		}
		result = new int[group.size()][];
		for (int i = 0; i < result.length; i++) {
			Matcher colMatcher = colPattern.matcher(group.get(i));
			List<String> cols = new ArrayList<String>();
			while (colMatcher.find()) {
				cols.add(colMatcher.group());
			}
			result[i] = new int[cols.size()];
			for (int j = 0; j < result[i].length; j++) {
				result[i][j] = Integer.parseInt(cols.get(j));
			}
		}
		return result;
	}

	public static double[] logExp(double[] exp) {
		double[] logExp = new double[exp.length];
		for (int i = 0; i < exp.length; i++) {
			logExp[i] = Math.log(exp[i]) / Math.log(2);
		}
		return logExp;
	}

	public static double M(double logExp1, double logExp2) {
		return (logExp1 - logExp2);
	}

	public static double A(double logExp1, double logExp2) {
		return 0.5 * (logExp1 + logExp2);
	}

	public static double normalizedM(double M, long[] total) {
		return M + Math.log(((double) total[1]) / total[0]) / Math.log(2);
	}

	public static void preprocessExp(double[] exp) {
		if (exp[0] == 0 && exp[1] == 0) {
			exp[0] = exp[1] = Double.NaN;
		}
	}
}
