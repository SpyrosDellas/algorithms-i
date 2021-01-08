/* *****************************************************************************
 *  Name: Spyridon Theodoros Dellas
 *  Date: 05/05/2020
 *  Description: Compare the efficiency of different sorting algorithms
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Random;

public class SortCompare {

    public static double timeRandomInput(String alg, int N, int T) {

        // Use alg to sort T random arrays of length N.
        double total = 0.0;
        double[] a = new double[N];
        Random generator = new Random();

        for (int t = 0; t < T; t++) {
            // Perform one experiment (generate and sort an array).
            for (int i = 0; i < N; i++) {
                a[i] = generator.nextDouble();
            }
            double start = System.currentTimeMillis();
            if (alg.compareTo("QuickSort") == 0) {
                QuickSort.sort(a);
            }
            if (alg.compareTo("Java") == 0) {
                Arrays.sort(a);
            }
            if (alg.compareTo("QuickSortX") == 0) {
                QuickSortX.sort(a);
            }
            double end = System.currentTimeMillis();
            total += end - start;
        }

        // return time in seconds
        return total / 1000;
    }

    public static void main(String[] args) {
        String alg1 = "Java";
        String alg2 = "QuickSortX";
        // String alg3 = "QuickSort";
        int N = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);
        double t1 = timeRandomInput(alg1, N, T); // total for alg1
        double t2 = timeRandomInput(alg2, N, T); // total for alg2
        // double t3 = timeRandomInput(alg3, N, T); // total for alg3
        System.out.println("For an array of "
                                   + N + " doubles, the average time for "
                                   + T + " trials is:");
        StdOut.printf("For %s : %.3f seconds \n", alg1, t1 / T);
        StdOut.printf("For %s : %.3f seconds \n", alg2, t2 / T);
        // StdOut.printf("For %s : %.3f seconds \n", alg3, t3 / T);
    }

}
