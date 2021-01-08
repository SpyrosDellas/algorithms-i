/* *****************************************************************************
 *  Name:              Spyridon Theodoros Dellas
 *  Coursera User ID:  spyrosdellas@yahoo.com
 *  Last modified:     14/05/2020
 *
 * Ramanujan numbers:
 * When the English mathematician G. H. Hardy came to visit the Indian
 * mathematician Srinivasa Ramanujan in the hospital one day, Hardy remarked
 * that the number of his taxi was 1729, a rather dull number. To which
 * Ramanujan replied, No, Hardy! No, Hardy! It is a very interesting number;
 * it is the smallest number expressible as the sum of two cubes in two
 * different ways.
 * An integer n is a Ramanujan number if can be expressed as the sum of two
 * positive cubes in two different ways. That is, there are four distinct
 * positive integers a, b, c, and d such that n = a^3 + b^3 = c^3 + d^3.
 * For example 1729 = 1^3 + 12^3 = 9^3 + 10^3.
 *
 * TaxiCab.java takes two integer command-line arguments n and k and finds all
 * taxicab numbers of order k with a, b, c, and d less than or equal to n.
 *
 * Note:
 * The algorith takes time proportional to n^2 * log(n) and space
 * proportional to n (using a min oriented indexed priority queue).
 *
 * The smallest taxi cab numbers Ta(k) of order k are:
 * Ta(2) = 1729 ( set n = 15)
 * Ta(3) = 87539319 (set n = 450)
 * Ta(4) = 6963472309248 (set n = 20000, running time approx. 95 secs)
 * Ta(5) = 48988659276962496 (set n = 366000, running time approx. 24 hours)
 *
 **************************************************************************** */

import java.math.BigInteger;

public class TaxiCab {

    private BigInteger[] taxiCab;
    private int size = 0;

    public TaxiCab(int n, int k) {

        if (n < 5 || n <= 2 * k) {
            taxiCab = new BigInteger[0];
            return;
        }

        taxiCab = new BigInteger[100];  // Hold the taxicab numbers found
        int repetitions = 0; // number of repeated cube sums coming out of the queue

        // Create an index min oriented priority queue of size n (index 0 is not
        // used). The indices are the integers 1, 2, ..., n-1 and the keys are
        // the cube sums 1^3 + 2^3, 2^3 + 3^3, ..., (n-1)^3 + n^3
        IndexMinPriorityQueue<BigInteger> cubeSums = new IndexMinPriorityQueue<>(n);

        // Array that holds the next cube to be added to the integer
        // corresponding to the array index, i.e. if next[5] = 99 then the
        // sum 5^3 + 99^3 will be inserted in the priority queue when
        // 5^3 + 98^3 becomes the minimum and is removed
        int[] next = new int[n];

        // Populate next[] and cubeSums
        BigInteger thisCube = BigInteger.valueOf(1);
        for (int i = 1; i < n; i++) {
            next[i] = i + 2;
            BigInteger nextInt = BigInteger.valueOf(i + 1);
            BigInteger nextCube = nextInt.multiply(nextInt).multiply(nextInt);
            cubeSums.insert(i, thisCube.add(nextCube));
            thisCube = nextCube;
        }

        BigInteger lastSum = BigInteger.valueOf(0);
        while (!cubeSums.isEmpty()) {
            BigInteger minSum = cubeSums.min();
            int minInt = cubeSums.delMin();

            int nextInQueue = next[minInt];
            if (nextInQueue <= n) {
                BigInteger thisInt = BigInteger.valueOf(minInt);
                BigInteger cube1 = thisInt.multiply(thisInt).multiply(thisInt);
                BigInteger nextInt = BigInteger.valueOf(nextInQueue);
                BigInteger cube2 = nextInt.multiply(nextInt).multiply(nextInt);
                cubeSums.insert(minInt, cube1.add(cube2));
                next[minInt] = nextInQueue + 1;
            }

            if (minSum.compareTo(lastSum) != 0) {
                lastSum = minSum;
                repetitions = 1;
            }
            else {
                repetitions++;
            }

            if (repetitions == k) {
                taxiCab[size++] = minSum;
                if (size == taxiCab.length)
                    resize(2 * size);
            }
        }
        resize(size);
    }

    public BigInteger get(int i) {
        return taxiCab[i];
    }

    public int size() {
        return size;
    }

    private void resize(int newSize) {
        BigInteger[] copy = new BigInteger[newSize];
        System.arraycopy(taxiCab, 0, copy, 0, size);
        taxiCab = copy;
    }

    public static void main(String[] args) {
        /* Takes a long integer command-line arguments n and prints true if n
        is a Ramanujan number, and false otherwise.
         */

        int n = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);

        TaxiCab taxi = new TaxiCab(n, k);
        int i = 0;
        while (i < taxi.size()) {
            System.out.print(taxi.get(i) + " ");
            if ((i + 1) % 15 == 0) System.out.println();
            i++;
        }
    }

}
