package qomo.diffexpress;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.special.Gamma;

public class MathUtil {

    /**
     * Emulate pchisq in R
     *
     * @param x
     * @param df
     * @return
     */
    public static double pchisq(double x, int df) {
        if (df < 0) {
            return Double.NaN;
        }
        if (x == Double.POSITIVE_INFINITY) {
            return 1.0;
        }
        ChiSquaredDistribution chisqDist = new ChiSquaredDistribution(df);
        return chisqDist.cumulativeProbability(x);
    }

    /**
     * Emulate dpois in R
     *
     * @param x
     * @param lambda
     * @return
     */
    public static double dpois(double x, double lambda) {
        if (lambda == 0) {
            if (x == 0) {
                return 1.0;
            } else {
                return 0.0;
            }
        }
        return new PoissonDistribution(lambda).probability((int) x);
    }

    /**
     * Estimate qValues from pValues (Benjamini et al. 1995)
     *
     * @param pValue
     * @return qValue
     */
    public static double[] qvalueB95(double[] pValue) {
        int n = pValue.length;
        double[] qValue = new double[n];
        for (int i = 0; i < pValue.length; i++) {
            if (Double.isNaN(pValue[i])) {
                n--;
            }
        }
        double[] rank = MathUtil.rank(pValue);
        for (int i = 0; i < pValue.length; i++) {
            qValue[i] = pValue[i] * n / rank[i];
            if (qValue[i] < 0) {
                qValue[i] = 0.0;
            } else if (qValue[i] > 1) {
                qValue[i] = 1.0;
            }
        }
        return qValue;
    }

    /**
     * Estimate qValues from pValues (Storey et al. 2003)
     *
     * @param pValue
     * @return qValue
     */
    public static double[] qvalueS03(double[] pValue) {
        // Double[] qValue = new Double[pValue.length];
        // int n=0;
        // for (int i = 0; i < pValue.length; i++) {
        // if(pValue[i].isNaN()){
        // qValue[i]=NaN;
        // }else{
        // n++;
        // }
        // }
        // double[] p=new double[n];
        // for(int i=0,j=0;i<pValue.length;i++){
        // if(!pValue[i].isNaN())
        // p[j++]=pValue[i];
        // }
        // if (!Rengine.versionCheck()) {
        // System.err
        // .println("** Version mismatch - Java files don't match library version.");
        // System.exit(1);
        // }
        // String[] args = new String[] { "--no-save" };
        // Rengine re = new Rengine(args, false, null);
        // if (!re.waitForR()) {
        // System.err.println("Cannot load R");
        // System.exit(1);
        // }
        // double[] q=new double[n];
        // try {
        // REXP x;
        // re.assign("pValue", p);
        // re.eval("library(qvalue)");
        // re.eval("res=qvalue(pValue)");
        // x = re.eval("res$qvalues");
        // q = x.asDoubleArray();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // for(int i=0,j=0;i<qValue.length;i++){
        // if(null==qValue[i])
        // qValue[i]=q[j++];
        // }
        // re.end();
        // return qValue;
        return pValue;
    }

    /**
     * Emulate rank in R
     *
     * @param arr
     * @return
     */
    public static double[] rank(final double[] arr) {
        final int n = arr.length;
        if (n == 0) {
            return null;
        }
        Integer[] index = new Integer[n];
        for (int i = 0; i < n; i++) {
            index[i] = i;
        }
        Arrays.sort(index, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return Double.compare(arr[o1], arr[o2]);
            }
        });
        double[] rank = new double[n];
        for (int i = 0; i < n; i++) {
            // Average rank of same numbers
            double r = (double) (i + 1);
            if (Double.isNaN(arr[index[i]])) {
                rank[index[i]] = r;
                continue;
            }
            int m = 1;
            for (int j = i + 1; j < n && arr[index[i]] == arr[index[j]]; j++) {
                r += j + 1;
                m++;
            }
            r /= m;
            for (; m > 1; m--, i++) {
                rank[index[i]] = r;
            }
            rank[index[i]] = r;
        }
        return rank;
    }

    public static double factorial3(long n) {
        return Gamma.logGamma(n + 1);
    }

    public static double factorial2(long n) {
        return n * Math.log(n) - n
                + Math.log(1 / 30 + n * (1 + 4 * n * (1 + 2 * n))) / 6
                + Math.log(Math.PI) / 2;
    }

//	public static double factorial1(long n) {
//		double temp = 1.0;
//		if (n > 0) {
//			n = n + 1;
//			double x = 0;
//			x += 0.1659470187408462e-06 / (n + 7);
//			x += 0.9934937113930748e-05 / (n + 6);
//			x -= 0.1385710331296526 / (n + 5);
//			x += 12.50734324009056 / (n + 4);
//			x -= 176.6150291498386 / (n + 3);
//			x += 771.3234287757674 / (n + 2);
//			x -= 1259.139216722289 / (n + 1);
//			x += 676.5203681218835 / (n);
//			x += 0.9999999999995183;
//			temp = Math.log(x) - 5.58106146679532777 - n + (n - 0.5)
//					* Math.log(n + 6.5);
//		}
//		return (temp);
//	}
//	
    public static double factorial0(long n) {
        double temp = 0.0;
        for (long i = 2; i <= n; i++) {
            temp += Math.log(i);
        }
        return temp;
    }

    public static double factorial(long n) {
        return factorial3(n);
    }

    /**
     * @param 2-by-2 matrix
     * @return p-value of Fisher's exact test
     */
    public static double fishersExactTest2by2(long matrix[][]) {
        double denominator, numerator, prob_current;
        double fac_sum;
        long sum;
        long[] R = new long[2];
        long[] C = new long[2];

        // Row & Column
        R[0] = matrix[0][0] + matrix[0][1];
        R[1] = matrix[1][0] + matrix[1][1];
        C[0] = matrix[0][0] + matrix[1][0];
        C[1] = matrix[0][1] + matrix[1][1];
        sum = R[0] + R[1];

        // Calculate the numberator that is a constant
        numerator = factorial(R[0]) + factorial(R[1]) + factorial(C[0])
                + factorial(C[1]);

        // Log of Factorial of N
        fac_sum = factorial(sum);
        denominator = fac_sum;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                denominator += factorial(matrix[i][j]);
            }
        }

        // Probability of current situtation
        prob_current = Math.exp(numerator - denominator);

        // Two-tail probabilities if less than prob_current
        long[] mat = new long[4];
        double prob_total = 0;
        long low = (R[0] - C[1] > 0) ? R[0] - C[1] : 0;
        long high = R[0] < C[0] ? R[0] : C[0];
        double relError = 1 + 1e-7;
        prob_current *= relError;
        for (long x = low; x <= high; x++) {
            mat[0] = x;
            mat[1] = C[0] - x;
            mat[2] = R[0] - x;
            mat[3] = R[1] - C[0] + x;
            // if (mat[0] >= 0 && mat[1] >= 0 && mat[2] >= 0 && mat[3] >= 0) {
            denominator = fac_sum;
            for (int j = 0; j < 4; j++) {
                denominator += factorial(mat[j]);
            }
            double temp = Math.exp(numerator - denominator);
            if (temp <= prob_current) {
                prob_total += temp;
            }
            // }
        }
        return prob_total;
    }

    public static void main(String[] args) {
        double f, f0;
        long n = 1000000000;
        long start = System.currentTimeMillis();
        f0 = f = MathUtil.factorial0(n);
        long end = System.currentTimeMillis();
        System.out.println("Result: " + f + "\nTime elapsed: " + (end - start));
        start = System.currentTimeMillis();
        f = MathUtil.factorial2(n);
        end = System.currentTimeMillis();
        System.out.println("Result: " + f + "\nTime elapsed: " + (end - start)
                + "\nDifference: " + Math.abs(f0 - f));
        start = System.currentTimeMillis();
        f = MathUtil.factorial3(n);
        end = System.currentTimeMillis();
        System.out.println("Result: " + f + "\nTime elapsed: " + (end - start)
                + "\nDifference: " + Math.abs(f0 - f));
    }
}
