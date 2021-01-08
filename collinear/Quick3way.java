/* *****************************************************************************
 *
 * Sorts a sequence of strings from standard input using 3-way quicksort.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * The {@code Quick3way} class provides static methods for sorting an
 * array using quicksort with 3-way partitioning.
 * <p>
 * For additional documentation,
 * see <a href="https://algs4.cs.princeton.edu/23quick">Section 2.3</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class Quick3way {

    /**
     * Rearranges the array in ascending order, using the natural order.
     *
     * @param a the array to be sorted
     */
    public static void sort(double[] a) {
        StdRandom.shuffle(a);
        sort(a, 0, a.length - 1);
        assert isSorted(a);
    }

    // quicksort the subarray a[lo .. hi] using 3-way partitioning
    private static void sort(double[] a, int lo, int hi) {
        if (hi <= lo) return;
        int lt = lo, gt = hi;
        double v = a[lo];
        int i = lo + 1;
        while (i <= gt) {
            int cmp = Double.compare(a[i], v);
            if (cmp < 0) exch(a, lt++, i++);
            else if (cmp > 0) exch(a, i, gt--);
            else i++;
        }

        // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
        sort(a, lo, lt - 1);
        sort(a, gt + 1, hi);
        assert isSorted(a, lo, hi);
    }


    /***************************************************************************
     *  Helper sorting functions.
     ***************************************************************************/

    // is v < w ?
    private static boolean less(double v, double w) {
        return Double.compare(v, w) < 0;
    }

    // exchange a[i] and a[j]
    private static void exch(double[] a, int i, int j) {
        double swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }


    /***************************************************************************
     *  Check if array is sorted - useful for debugging.
     ***************************************************************************/
    private static boolean isSorted(double[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    private static boolean isSorted(double[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(a[i], a[i - 1])) return false;
        return true;
    }


    // print array to standard output
    private static void show(double[] a) {
        for (int i = 0; i < a.length; i++) {
            StdOut.println(a[i]);
        }
    }

    public static void main(String[] args) {
        // Perform one experiment (generate and sort a whole array, or a
        // subarray a[start..end).
        int N = Integer.parseInt(args[0]);
        int start = 0;
        int end = N;
        if (args.length > 1) {
            start = Integer.parseInt(args[1]);
            end = Integer.parseInt(args[2]);
        }

        double[] a = new double[N];
        java.util.Random generator = new java.util.Random();
        for (int i = 0; i < N; i++) {
            a[i] = generator.nextDouble();
        }


        System.out.println("Array size: " + a.length);
        double timeStart = System.currentTimeMillis();
        sort(a, start, end - 1);
        double timeEnd = System.currentTimeMillis();
        System.out.println("Array certified sorted: " + isSorted(a, start, end - 1));
        System.out.println("Total time to sort: " + (timeEnd - timeStart) / 1000 + " sec");
        // show(a, start, end);
    }

}
