/* *****************************************************************************
 *  Compilation:  javac LoadBalance.java
 *  Execution:    java LoadBalance m n s
 *  Dependencies: Queue.java RandomQueue.java StdDraw.java StdStats.java
 *
 *  Simulate the process of assignment n items to a set of m servers.
 *  Requests are put on the shortest of a sample of s queues chosen
 *  at random.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdStats;

public class LoadBalance {
    public static void main(String[] args) {

        int m = Integer.parseInt(args[0]);  // servers
        int n = Integer.parseInt(args[1]);  // items
        int s = Integer.parseInt(args[2]);  // random sample size

        // Create server queues.
        RandomizedQueue<Queue<Integer>> servers = new RandomizedQueue<Queue<Integer>>();

        for (int i = 0; i < m; i++) {
            servers.enqueue(new Queue<Integer>());
        }

        // Assign an item to a server
        for (int j = 0; j < n; j++) {

            // Pick a random server, update if new min.
            Queue<Integer> min = servers.sample();
            for (int k = 1; k < s; k++) {
                Queue<Integer> queue = servers.sample();
                if (queue.size() < min.size()) min = queue;
            }

            // min is the shortest server queue
            min.enqueue(j);
        }

        int i = 0;
        double[] lengths = new double[m];
        for (Queue<Integer> queue : servers) {
            lengths[i++] = queue.size();
            StdDraw.setYscale(0, 2.0 * n / m);
            StdStats.plotBars(lengths);
        }
    }
}

