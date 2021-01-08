/* *
 * Compilation:  javac Selection.java
 * Execution:    java  Selection < input.txt

 * Sorts a sequence of strings from standard input using selection sort.
 *
 * % more tiny.txt
 * S O R T E X A M P L E
 *
 * % java Selection < tiny.txt
 * A E E L M O P R S T X                 [ one string per line ]
 *
 * % more words3.txt
 * bed bug dad yes zoo ... all bad yet
 *
 * % java Selection < words3.txt
 * all bad bed bug dad ... yes yet zoo    [ one string per line ]
 *
 * The Selection class provides static methods for sorting an array using
 * selection sort.
 * This implementation makes (1/2)*n^2 compares to sort any array of length n,
 * so it is not suitable for sorting large arrays.
 * It performs exactly n exchanges.
 *
 * This sorting algorithm is not stable. It uses Theta(1) extra memory
 * (not including the input array).
 */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;


public class Selection {

    /*
     * Rearranges the array in ascending order, using the natural order.
     */
    public static <Key extends Comparable<Key>> void sort(Key[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (less(a[j], a[min])) min = j;
            }
            exch(a, i, min);
            assert isSorted(a, 0, i);
        }
        assert isSorted(a);
    }

    /*
     * Rearranges the array in ascending order, using a comparator.
     */
    public static <Key> void sort(Key[] a, Comparator<Key> comparator) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (less(comparator, a[j], a[min])) min = j;
            }
            exch(a, i, min);
            assert isSorted(a, comparator, 0, i);
        }
        assert isSorted(a, comparator);
    }


    /* *************************************************************************
     *  Helper sorting functions.
     **************************************************************************/

    // is v < w ?
    private static <Key extends Comparable<Key>> boolean less(Key v, Key w) {
        return v.compareTo(w) < 0;
    }

    // is v < w ?
    private static <Key> boolean less(Comparator<Key> comparator, Key v, Key w) {
        return comparator.compare(v, w) < 0;
    }


    // exchange a[i] and a[j]
    private static void exch(Object[] a, int i, int j) {
        Object swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }


    /* *************************************************************************
     *  Check if array is sorted - useful for debugging.
     **************************************************************************/

    // is the array a[] sorted?
    private static <Key extends Comparable<Key>> boolean isSorted(Key[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    // is the array sorted from a[lo] to a[hi]
    private static <Key extends Comparable<Key>> boolean isSorted(Key[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(a[i], a[i - 1])) return false;
        return true;
    }

    // is the array a[] sorted?
    private static <Key> boolean isSorted(Key[] a, Comparator<Key> comparator) {
        return isSorted(a, comparator, 0, a.length - 1);
    }

    // is the array sorted from a[lo] to a[hi]
    private static <Key> boolean isSorted(Key[] a, Comparator<Key> comparator, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(comparator, a[i], a[i - 1])) return false;
        return true;
    }

    // print array to standard output
    private static <Key extends Comparable<Key>> void show(Key[] a) {
        for (int i = 0; i < a.length; i++) {
            StdOut.println(a[i]);
        }
    }

    /*
     * Reads in a sequence of strings from standard input; selection sorts them;
     * and prints them to standard output in ascending order.
     */
    public static void main(String[] args) {
        String[] a = StdIn.readAllStrings();
        Selection.sort(a);
        show(a);
    }
}
