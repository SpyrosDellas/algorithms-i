/* *****************************************************************************
 *  Name: Spyridon Theodoros Dellas
 *  Date: 12/05/2020
 *  Description: 3-way recursive QuickSort implementation
 **************************************************************************** */

public class QuickSortX {

    private static final int INSERTION_CUTOFF = 15;
    private static final int MEDIAN_OF_3_CUTOFF = 50;

    // This class should not be instantiated.
    private QuickSortX() {
    }

    /* ************************************************************************
                        PARTITION FOR DOUBLES
     *************************************************************************/

    private static int[] partition(double[] a, int start, int end) {

        int N = end - start;
        int pivotIndex;

        if (N <= MEDIAN_OF_3_CUTOFF) {
            pivotIndex = median3(a, start, start + N / 2, end - 1);
        }
        else {
            pivotIndex = ninther(a, start, end - 1);
        }

        double pivot = a[pivotIndex];
        a[pivotIndex] = a[start];
        a[start] = pivot;

        int i = start;
        int j = start + 1;
        int k = end - 1;

        while (j <= k) {
            double valueAtJ = a[j];
            int cmp = Double.compare(valueAtJ, pivot);
            if (cmp < 0) {
                a[i++] = valueAtJ;
                a[j++] = pivot;
            }
            else if (cmp == 0) {
                j++;
            }
            else {
                while (k > j && Double.compare(a[k], pivot) > 0) k--;
                double valueAtK = a[k];
                a[k--] = valueAtJ;
                a[j] = valueAtK;
            }
        }
        return new int[] { i, j };
    }

    /* ************************************************************************
                              QUICK SORT FOR DOUBLES
     *************************************************************************/

    public static void sort(double[] a) {
        sort(a, 0, a.length);
    }


    /**
     * Sort a[start..end), a[end] is excluded
     */
    public static void sort(double[] a, int start, int end) {

        if (a == null) throw new NullPointerException("Cannot sort null array");

        /* Recursive case
        To limit the depth of the recursion in case of unbalanced splits,
        we sort recursively the smallest subarray only and iterate over the
        largest subarray
         */
        while (end - start > 1) {

            if (end - start <= INSERTION_CUTOFF) {
                insertionSort(a, start, end);
                break;
            }

            int[] pivot = partition(a, start, end);

            if (pivot[0] - start <= end - pivot[1]) {
                sort(a, start, pivot[0]);
                start = pivot[1];
            }
            else {
                sort(a, pivot[1], end);
                end = pivot[0];
            }
        }
    }

    /* ************************************************************************
                        INSERTION SORT FOR DOUBLES
      *************************************************************************/
    private static void insertionSort(double[] a, int start, int end) {

        for (int cursor = start + 1; cursor < end; cursor++) {
            double valueAtCursor = a[cursor];
            for (int pointer = cursor; pointer > start; pointer--) {
                if (Double.compare(valueAtCursor, a[pointer - 1]) < 0) {
                    a[pointer] = a[pointer - 1];
                }
                else {
                    a[pointer] = valueAtCursor;
                    break;
                }
            }
        }
    }

    /* ************************************************************************
                        MEDIAN OF 3 FOR DOUBLES
      *************************************************************************/

    // return the index of the median element among a[i], a[j], and a[k]
    private static int median3(double[] a, int i, int j, int k) {
        return Double.compare(a[i], a[j]) < 0 ?
               (Double.compare(a[j], a[k]) < 0 ? j : Double.compare(a[i], a[k]) < 0 ? k : i) :
               (Double.compare(a[k], a[j]) < 0 ? j : Double.compare(a[k], a[i]) < 0 ? k : i);
    }

    /* ************************************************************************
                        NINTHER FOR DOUBLES
      *************************************************************************/

    private static int ninther(double a[], int start, int end) {
        int n = end - start;
        int eps = n / 8;
        int mid = start + n / 2;

        int m1 = median3(a, start, start + eps, start + 2 * eps);
        int m2 = median3(a, mid - eps, mid, mid + eps);
        int m3 = median3(a, end - 2 * eps, end - eps, end);

        return median3(a, m1, m2, m3);
    }

    /* ************************************************************************
                                HELPER METHODS
     *************************************************************************/

    public static boolean isSorted(double[] a, int start, int end) {
        // Test whether the array entries are in order
        for (int i = start + 1; i < end; i++)
            if (Double.compare(a[i], (a[i - 1])) < 0) return false;
        return true;
    }

    private static void show(double[] a, int start, int end) {
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

        double[] a = new double[N];
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
