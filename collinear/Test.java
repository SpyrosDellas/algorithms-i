/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        double[] test = {
                Double.NaN, Double.POSITIVE_INFINITY, +0.0, -0.0,
                Double.NEGATIVE_INFINITY, 10, Double.NaN
        };

        double[] test1 = Arrays.copyOf(test, test.length);
        QuickSortX.sort(test);
        Arrays.sort(test1);

        for (double d : test) {
            System.out.print(d + " ");
        }
        System.out.println();
        for (double d : test1) {
            System.out.print(d + " ");
        }

        boolean less = -0.0 > 0.0;
        System.out.println("\n" + less);
    }

}
