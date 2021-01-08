/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 26/04/2020
 *
 * Description:
 * Permutation.java takes an integer k as a command-line argument; reads a
 * sequence of strings from standard input using StdIn.readString(); and prints
 * exactly k of them, uniformly at random. Prints each item from the sequence at
 * most once.
 *
 * Command-line argument.
 * We assume that 0 ≤ k ≤ n, where n is the number of strings on standard input.
 *
 * Performance requirements.
 * The running time of Permutation is linear in the size of the input.
 * We use only a constant amount of memory plus either one RandomizedQueue
 * object of maximum size at most k.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;

public class Permutation {

    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        if (k == 0) return;
        int n = 0;
        RandomizedQueue<String> rQ = new RandomizedQueue<>();
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            n++;
            if (n > k) {
                double newEntryProb = (double) k / n;
                if (Math.random() < newEntryProb) {
                    rQ.dequeue();
                    rQ.enqueue(s);
                }
            }
            else {
                rQ.enqueue(s);
            }
        }
        for (String s : rQ) {
            System.out.println(s);
        }
    }
}
