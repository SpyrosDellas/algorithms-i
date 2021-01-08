/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Stack;

import java.util.HashMap;

public class FourSum {

    private static double[] fourSum(int size) {

        int[] a = new int[size];
        for (int i = 0; i < size; i++) {
            a[i] = i - (size / 2);
        }

        HashMap<Integer, Integer[]> twoSums = new HashMap<>();
        double counter = 0;
        double start = System.currentTimeMillis();

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                int sum = a[i] + a[j];
                if (twoSums.containsKey(sum)) {
                    counter++;
                    // System.out.printf("%d + %d = %d + %d\n", twoSums.get(sum)[0],
                    // twoSums.get(sum)[1], a[i], a[j]);
                }
                else {
                    twoSums.put(sum, new Integer[] { a[i], a[j] });
                }
            }
        }
        double end = System.currentTimeMillis();
        return new double[] { counter, (end - start) / 1000 };
    }

    public static void main(String[] args) {

        int maxSize = Integer.parseInt(args[0]);
        Stack<String> result = new Stack<>();

        for (int size = maxSize; size > 1000; size /= 2) {
            double[] trial = fourSum(size);
            String message = "For N = " + size + " points, a total of " + (long) trial[0]
                    + " 4-sum sets were found in " + trial[1] + " seconds";
            result.push(message);
        }

        for (String trial : result) {
            System.out.println(trial);
        }
    }

}

