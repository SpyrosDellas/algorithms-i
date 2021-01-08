/* *****************************************************************************
 *  Name: Spyros Dellas
 *  Date: 13/05/2020
 *  Description: Classic heap sort algorithm implementation
 **************************************************************************** */

import java.math.BigInteger;

public class HeapSort {

    // This class should not be instantiated
    private HeapSort() {
    }

    @SuppressWarnings({ "rawtypes" })
    public static void sort(Comparable[] a) {
        int N = a.length;

        // Max oriented heap construction
        for (int k = N / 2 - 1; k >= 0; k--) {
            sink(a, k, N);
        }

        // Sortdown.
        // Most of the work during heapsort is done during
        // the second phase, where we remove the largest remaining item
        // from the heap and put it into the array position vacated as the
        // heap shrinks. This process is a bit like selection sort (taking the
        // items in decreasing order instead of in increasing order), but it
        // uses many fewer compares because the heap provides a much
        // more efficient way to find the largest item in the unsorted part
        // of the array.
        for (int cursor = N - 1; cursor > 0; cursor--) {
            exch(a, 0, cursor);
            sink(a, 0, cursor);
        }
    }

    // Top-down reheapify
    @SuppressWarnings({ "rawtypes" })
    private static void sink(Comparable[] a, int k, int N) {
        while (2 * k + 1 < N) {
            int j = 2 * k + 1;
            if (j < N - 1 && less(a, j, j + 1)) {
                j++;
            }
            if (less(a, k, j)) {
                exch(a, k, j);
                k = j;
            }
            else {
                break;
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static boolean less(Comparable[] a, int i, int j) {
        return a[i].compareTo(a[j]) < 0;
    }

    @SuppressWarnings({ "rawtypes" })
    private static void exch(Comparable[] a, int i, int j) {
        Comparable buffer = a[i];
        a[i] = a[j];
        a[j] = buffer;
    }

    // Certify that the array is sorted
    @SuppressWarnings({ "rawtypes" })
    private static boolean isSorted(Comparable[] a) {
        for (int k = 1; k < a.length; k++) {
            if (less(a, k, k - 1))
                return false;
        }
        return true;
    }

    private static void show(Object[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ");
        }
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        BigInteger[] t = new BigInteger[n];
        BigInteger k = BigInteger.ONE;
        for (int i = 1; i <= n; i++) {
            t[n - i] = k.multiply(k).multiply(k);
            k = k.add(BigInteger.ONE);
        }

        System.out.println("Big Integer array created");
        HeapSort.sort(t);
        System.out.println("Array certified sorted: " + isSorted(t));
        // show(a);
    }
}
