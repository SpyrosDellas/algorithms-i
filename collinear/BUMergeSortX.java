/* *****************************************************************************
 *  Name: Spyridon Theodoros Dellas
 *  Date: 11/05/2020
 *  Description: Bottom up implementation of mergesort
 **************************************************************************** */

import java.util.Comparator;

public class BUMergeSortX {

    private static final int INSERTION_CUTOFF = 10;

    /* ************************************************************************
                   MERGE METHOD FOR COMPARABLES AND COMPARATORS
     *************************************************************************/

    private static void merge(Comparable[] a, Comparable[] aux, int lo, int mid,
                              int hi, int offset, int offset1) {
        // Merge the subarrays a[offset + lo..offset + mid] and
        // a[offset + mid + 1..offset + hi) into aux[offset1 + lo..offset1 + hi)
        int cursor1 = offset + lo;
        int cursor2 = offset + mid + 1;
        for (int i = offset1 + lo; i <= offset1 + hi; i++) {
            if (cursor1 > offset + mid) {
                aux[i] = a[cursor2++];
            }
            else if (cursor2 > offset + hi) {
                aux[i] = a[cursor1++];
            }
            else if (a[cursor2].compareTo(a[cursor1]) < 0) {
                aux[i] = a[cursor2++];
            }
            else {
                aux[i] = a[cursor1++];
            }
        }
    }

    private static <T> void merge(T[] a, T[] aux, int lo, int mid, int hi,
                                  int offset, int offset1,
                                  Comparator<? super T> comparator) {

        // Merge the subarrays a[offset + lo..offset + mid] and
        // a[offset + mid + 1..offset + hi) into aux[offset1 + lo..offset1 + hi)
        int cursor1 = offset + lo;
        int cursor2 = offset + mid + 1;
        for (int i = offset1 + lo; i <= offset1 + hi; i++) {
            if (cursor1 > offset + mid) {
                aux[i] = a[cursor2++];
            }
            else if (cursor2 > offset + hi) {
                aux[i] = a[cursor1++];
            }
            else if (comparator.compare(a[cursor2], a[cursor1]) < 0) {
                aux[i] = a[cursor2++];
            }
            else {
                aux[i] = a[cursor1++];
            }
        }
    }

    /* ************************************************************************
                        SORT METHOD FOR COMPARABLES
     *************************************************************************/

    public static void sort(Comparable[] a) {
        sort(a, 0, a.length);
    }

    /* Sort a[start..end), a[end] is excluded
     */
    public static void sort(Comparable[] a, int start, int end) {

        if (a == null) throw new NullPointerException("Cannot sort null array");
        int N = end - start;

        // Insertion sort all subarrays of length equal to INSERTION_CUTOFF
        for (int lo = start; lo < end; lo += INSERTION_CUTOFF) {
            int hi = Math.min(end - 1, lo + INSERTION_CUTOFF - 1);
            insertionSort(a, lo, hi);
        }

        // Create auxiliary array to be used during merging
        Comparable[] aux = new Comparable[N];
        int offset = start;
        int offset1 = 0;

        // If an odd number of merges will occur, switch roles between a and
        // aux, such that a is sorted after the final merge
        if (N > INSERTION_CUTOFF) {
            int repeats = 1 + (int) (Math.log((double) (N - 1) / INSERTION_CUTOFF) / Math.log(2));
            if (repeats % 2 != 0) {
                System.arraycopy(a, start, aux, 0, N);
                Comparable[] copy = a;
                a = aux;
                aux = copy;
                int c = offset;
                offset = offset1;
                offset1 = c;
            }
        }

        // Sort the array bottom up using merge with doubling size steps
        for (int step = INSERTION_CUTOFF; step < N; step *= 2) {

            for (int lo = 0; lo + step < N; lo += 2 * step) {
                int hi = Math.min(N - 1, lo + 2 * step - 1);
                int mid = lo + step - 1;

                // Avoid calling merge if the two subarrays are already in
                // order, just copy the entries (necessary due to switching
                // roles of a and aux)
                if (a[offset + mid].compareTo(a[offset + mid + 1]) > 0) {
                    merge(a, aux, lo, mid, hi, offset, offset1);
                }
                else {
                    System.arraycopy(a, offset + lo, aux, offset1 + lo, hi + 1 - lo);
                }
            }

            // Copy the last bit of the array if not included in the merge
            int remainder = N % (2 * step);
            if (step < (N + 1) / 2 && remainder <= step) {
                System.arraycopy(a, offset + N - remainder, aux, offset1 + N - remainder,
                                 remainder);
            }

            // Switch roles between the array to be sorted and the auxilliary
            // array in order to save time (but not space) by avoiding copying
            // back in merge
            Comparable[] copy = a;
            a = aux;
            aux = copy;
            int c = offset;
            offset = offset1;
            offset1 = c;

        }
    }

    /* ************************************************************************
                        SORT METHOD FOR COMPARATORS
     *************************************************************************/
    public static <T> void sort(T[] a, Comparator<? super T> comparator) {
        sort(a, 0, a.length, comparator);
    }

    /* Sort a[start..end), a[end] is excluded
     */
    public static <T> void sort(T[] a, int start, int end, Comparator<? super T> comparator) {

        if (a == null) throw new NullPointerException("Cannot sort null array");
        int N = end - start;

        // Insertion sort all subarrays of length equal to INSERTION_CUTOFF
        for (int lo = start; lo < end; lo += INSERTION_CUTOFF) {
            int hi = Math.min(end - 1, lo + INSERTION_CUTOFF - 1);
            insertionSort(a, lo, hi, comparator);
        }

        // Create auxiliary array to be used during merging
        T[] aux = (T[]) new Object[N];
        int offset = start;
        int offset1 = 0;

        // If an odd number of merges will occur, switch roles between a and
        // aux, such that a is sorted after the final merge
        if (N > INSERTION_CUTOFF) {
            int repeats = 1 + (int) (Math.log((double) (N - 1) / INSERTION_CUTOFF) / Math.log(2));
            if (repeats % 2 != 0) {
                System.arraycopy(a, start, aux, 0, N);
                T[] copy = a;
                a = aux;
                aux = copy;
                int c = offset;
                offset = offset1;
                offset1 = c;
            }
        }

        // Sort the array bottom up using merge with doubling size steps
        for (int step = INSERTION_CUTOFF; step < N; step *= 2) {

            for (int lo = 0; lo + step < N; lo += 2 * step) {
                int hi = Math.min(N - 1, lo + 2 * step - 1);
                int mid = lo + step - 1;

                // Avoid calling merge if the two subarrays are already in
                // order, just copy the entries (necessary due to switching
                // roles of a and aux)
                if (comparator.compare(a[offset + mid], a[offset + mid + 1]) > 0) {
                    merge(a, aux, lo, mid, hi, offset, offset1, comparator);
                }
                else {
                    System.arraycopy(a, offset + lo, aux, offset1 + lo, hi + 1 - lo);
                }
            }

            // Copy the last bit of the array if not included in the merge
            int remainder = N % (2 * step);
            if (step < (N + 1) / 2 && remainder <= step) {
                System.arraycopy(a, offset + N - remainder, aux, offset1 + N - remainder,
                                 remainder);
            }

            // Switch roles between the array to be sorted and the auxilliary
            // array in order to save time (but not space) by avoiding copying
            // back in merge
            T[] copy = a;
            a = aux;
            aux = copy;
            int c = offset;
            offset = offset1;
            offset1 = c;

        }
    }

    /* ************************************************************************
                INSERTION SORT FOR COMPARABLES AND COMPARATORS
     *************************************************************************/

    private static void insertionSort(Comparable[] a, int lo, int hi) {

        // Put the smallest element in position to serve as sentinel
        // checking if the array is already sorted at the same time
        int exchanges = 0;
        for (int cursor = hi; cursor > lo; cursor--) {
            if (a[cursor].compareTo(a[cursor - 1]) < 0) {
                Comparable valueAtCursor = a[cursor];
                a[cursor] = a[cursor - 1];
                a[cursor - 1] = valueAtCursor;
                exchanges++;
            }
        }
        if (exchanges == 0) return;  // Array was sorted

        // insertion sort with half-exchanges
        for (int cursor = lo + 2; cursor <= hi; cursor++) {
            Comparable valueAtCursor = a[cursor];
            int dest = cursor;
            while (valueAtCursor.compareTo(a[dest - 1]) < 0) {
                dest--;
                a[dest + 1] = a[dest];
            }
            a[dest] = valueAtCursor;
        }
    }

    private static <T> void insertionSort(T[] a, int lo, int hi,
                                          Comparator<? super T> comparator) {

        // Put the smallest element in position to serve as sentinel
        // checking if the array is already sorted at the same time
        int exchanges = 0;
        for (int cursor = hi; cursor > lo; cursor--) {
            if (comparator.compare(a[cursor], a[cursor - 1]) < 0) {
                T valueAtCursor = a[cursor];
                a[cursor] = a[cursor - 1];
                a[cursor - 1] = valueAtCursor;
                exchanges++;
            }
        }
        if (exchanges == 0) return;  // Array was sorted

        // insertion sort with half-exchanges
        for (int cursor = lo + 2; cursor <= hi; cursor++) {
            T valueAtCursor = a[cursor];
            int dest = cursor;
            while (comparator.compare(valueAtCursor, a[dest - 1]) < 0) {
                dest--;
                a[dest + 1] = a[dest];
            }
            a[dest] = valueAtCursor;
        }
    }

    /* ************************************************************************
                                HELPER METHODS
     *************************************************************************/

    public static boolean isSorted(Comparable[] a, int start, int end) {
        // Test whether the array entries are in order
        for (int i = start + 1; i < end; i++)
            if (a[i].compareTo(a[i - 1]) < 0) return false;
        return true;
    }

    public static <T> boolean isSorted(T[] a, int start, int end,
                                       Comparator<? super T> comparator) {
        // Test whether the array entries are in order
        for (int i = start + 1; i < end; i++)
            if (comparator.compare(a[i], a[i - 1]) < 0) return false;
        return true;
    }

    private static void show(Object[] a, int start, int end) {
        // Print the subarray a[start..end), on a single line.
        for (int i = start; i < end; i++)
            System.out.print(a[i] + " ");
        System.out.println();
    }

    /* ************************************************************************
                                UNIT TESTING
     *************************************************************************/

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

        Double[] a = new Double[N];
        java.util.Random generator = new java.util.Random();
        for (int i = 0; i < N; i++) {
            a[i] = generator.nextDouble();
        }


        System.out.println("Array size: " + a.length);
        double timeStart = System.currentTimeMillis();
        sort(a, start, end);
        double timeEnd = System.currentTimeMillis();
        System.out.println("Array certified sorted: " + isSorted(a, start, end));
        System.out.println("Total time to sort: " + (timeEnd - timeStart) / 1000 + " sec");
        // show(a, start, end);
    }

}
