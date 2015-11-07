package qomo.diffexpress;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configured;

public abstract class AbstractDiffExpressTool extends Configured {
	public String input;
	public String output;

	public String expCol = "{ 7, 9, 12, 15, 18 }, { 8, 10, 11, 13, 16 }";
	public String thresholdType;
	public double threshold = 0.001;

	protected int[][] parseExpCol(String expCol) {
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

	protected ThresholdType parseThresholdType(String type) {
		return ThresholdType.valueOf(type);
	}

	protected abstract boolean innerRun(Reader input, Writer output,
			int[][] expCol, ThresholdType thresholdType, double thresholdValue);
}
