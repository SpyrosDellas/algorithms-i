/* *****************************************************************************
 *  Name:              Spyridon Theodoros Dellas
 *  Coursera User ID:  spyrosdellas@yahoo.com
 *  Last modified:     19/03/2020
 *
 * Implements the Quick Sort algorithm for an array of integers.
 *
 **************************************************************************** */

public class QuickSort {

    private static int[] partition(double[] a, int start, int end) {
        /* Puts the elements less than the pivot value at the beginning of the
          array and the elements greater than the pivot value at the end of
          the array
         */

        // To achieve a balanced split, we choose the median of the first,
        // mid and last entry of the array as the pivot value.
        // We then switch it with a[start] if a[start] is not the pivot already
        // (this us equivalent to moving the pivot to the start of the array).
        int mid = start + (end - start) / 2;
        double startval = a[start];
        double midval = a[mid];
        double endval = a[end - 1];
        double pivot = 0;
        if (startval == midval || startval == endval) {
            pivot = startval;
        }
        else if (midval == endval) {
            pivot = midval;
            a[mid] = a[start];
        }
        else if (startval < midval && startval < endval) {
            if (midval < endval) {
                pivot = midval;
                a[mid] = a[start];
            }
            else {
                pivot = endval;
                a[end - 1] = a[start];
            }
        }
        else if (startval > midval && startval > endval) {
            if (midval > endval) {
                pivot = midval;
                a[mid] = a[start];
            }
            else {
                pivot = endval;
                a[end - 1] = a[start];
            }
        }
        else {
            pivot = startval;
        }

        // pivotStart and pivotEnd keep track of the pivot position, taking into
        // account that can be multiple occurences of the pivot in the array
        int pivotStart = start;
        int pivotEnd = start;

        // Put the elements less than the pivot value at the beginning of the
        // array and the elements greater than the pivot value at the end of
        // the array
        int index = start + 1;
        while (index < end) {
            if (a[index] == pivot) {
                a[index] = a[pivotEnd + 1];
                pivotEnd++;
            }
            else if (a[index] < pivot) {
                a[pivotStart] = a[index];
                a[index] = a[pivotEnd + 1];
                pivotStart++;
                pivotEnd++;
            }
            index++;
        }

        // Write the pivot value to it's final position
        for (int i = pivotStart; i <= pivotEnd; i++) {
            a[i] = pivot;
        }

        int[] partition = new int[] { pivotStart, pivotEnd };
        return partition;
    }


    private static void quickSort(double[] a, int start, int end) {
        /* Implements quick sort algorithm for a subarray of a, from start
        (inclusive) to end (exclusive).

        The sorting is performed in place.

        To limit the depth of the recursion in the case of unbalanced splits,
        we sort recursively the smallest subarray only and iterate over the
        largest subarray
         */

        while (end - start > 1) {
            // Partition the array
            int[] pivot = partition(a, start, end);

            // Recursively sort the elements of the smallest part of the
            // partitioned array and update 'start' and 'end' to the values
            // corresponding to the largest part of the array
            if ((pivot[0] - start) < (end - (pivot[1] + 1))) {
                quickSort(a, start, pivot[0]);
                start = pivot[1] + 1;
            }
            else {
                quickSort(a, pivot[1] + 1, end);
                end = pivot[0];
            }
        }
    }

    public static void sort(double[] a) {
        quickSort(a, 0, a.length);
    }

    public static void main(String[] args) {
    }
}
