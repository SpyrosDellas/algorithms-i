/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class Test {

    public static void main(String[] args) {
        double a = 0.0;
        double b = -0.0;
        Double x = 0.0;
        Double y = -0.0;
        System.out.println("a = " + a + ", b = " + b);
        System.out.println("x = " + x + ", y = " + y);
        System.out.println("a == b ? " + (a == b));  // true
        System.out.println("x.equals(y) ? " + (x.equals(y))); // false

        a = Double.NaN;
        b = Double.NaN;
        x = Double.NaN;
        y = Double.NaN;
        System.out.println("a = " + a + ", b = " + b);
        System.out.println("x = " + x + ", y = " + y);
        System.out.println("a == b ? " + (a == b));  // false
        System.out.println("x.equals(y) ? " + (x.equals(y))); // true
    }
}
