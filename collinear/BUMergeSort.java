/* *****************************************************************************
 *  Name: Spyridon Theodoros Dellas
 *  Date: 05/05/2020
 *  Description: Bottom up implementation of mergesort
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Random;

public class BUMergeSort {

    private static Comparable[] aux;

    private static void merge(Comparable[] a, int lo, int mid, int hi) {
        // Merge the subarrays a[lo..mid] with a[mid+1..hi]
        int cursor1 = lo;
        int cursor2 = mid + 1;
        for (int i = lo; i <= hi; i++) {
            if (cursor1 > mid) {
                aux[i] = a[cursor2++];
            }
            else if (cursor2 > hi) {
                aux[i] = a[cursor1++];
            }
            else if (a[cursor2].compareTo(a[cursor1]) < 0) {
                aux[i] = a[cursor2++];
            }
            else {
                aux[i] = a[cursor1++];
            }
        }
        for (int i = lo; i <= hi; i++) {
            a[i] = aux[i];
        }
    }

    public static void sort(Comparable[] a) {
        if (a == null) throw new NullPointerException("Cannot sort null array");
        int N = a.length;
        if (N <= 1) return;

        // Create auxiliary array to be used during merging
        aux = new Comparable[N];

        // Sort the array bottom up using merge with doubling size steps
        for (int step = 1; step < N; step *= 2) {
            for (int lo = 0; lo + step < N; lo += 2 * step) {
                int hi = Math.min(N - 1, lo + 2 * step - 1);
                int mid = lo + step - 1;
                // Avoid calling merge if the two subarrays are already in order
                if (a[mid].compareTo(a[mid + 1]) <= 0) continue;
                merge(a, lo, mid, hi);
            }
        }

        // Release the memory occupied by the auxiliary array
        aux = null;
    }

    public static boolean isSorted(Comparable[] a) {
        // Test whether the array entries are in order
        for (int i = 1; i < a.length; i++)
            if (a[i].compareTo(a[i - 1]) < 0) return false;
        return true;
    }

    private static void show(Comparable[] a) {
        // Print the array, on a single line.
        for (int i = 0; i < a.length; i++)
            StdOut.print(a[i] + " ");
        StdOut.println();
    }

    public static void main(String[] args) {

        // Perform one experiment (generate and sort an array).
        int N = (int) 5e6;
        Double[] a = new Double[N];
        Random generator = new Random();
        for (int i = 0; i < N; i++) {
            a[i] = generator.nextDouble();
        }

        System.out.println("Array size: " + a.length);
        double start = System.currentTimeMillis();
        sort(a);
        double end = System.currentTimeMillis();
        System.out.println("Array certified sorted: " + isSorted(a));
        System.out.println("Total time to sort: " + (end - start) / 1000 + " sec");
    }
}
