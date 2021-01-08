/* *****************************************************************************
 *  Name:              Spyros Dellas
 *  Coursera User ID:  spyrosdellas@yahoo.com
 *  Last modified:     20/04/2020
 *
 * Monte Carlo simulation of percolation.
 * To estimate the percolation threshold, we run the following computational
 * experiment:
 * - Initialize all sites to be blocked.
 * - Repeat the following until the system percolates:
 *    Choose a site uniformly at random among all blocked sites.
 *    Open the site.
 * - The fraction of sites that are opened when the system percolates provides
 * an estimate of the percolation threshold.
 *
 * By repeating this computation experiment T times and averaging the results,
 * we obtain a more accurate estimate of the percolation threshold. Let xt be
 * the fraction of open sites in computational experiment t. The sample mean
 * m provides an estimate of the percolation threshold; the sample standard
 * deviation s measures the sharpness of the threshold.
 *
 * Assuming T is sufficiently large (say, at least 30), the following provides
 * a 95% confidence interval for the percolation threshold:
 * [mean - (1.96 * s) / sqrt(T), mean - (1.96 * s) / sqrt(T)]
 *
 * Corner cases:
 * Throws an IllegalArgumentException in the constructor if either n ≤ 0 or
 * trials ≤ 0.
 *
 * The main() method takes two command-line arguments n and T, performs T
 * independent computational experiments (discussed above) on an n-by-n grid,
 * and prints the sample mean, sample standard deviation, and the 95% confidence
 * interval for the percolation threshold.
 * Use StdRandom to generate random numbers; use StdStats to compute the
 * sample mean and sample standard deviation.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    // normal distribution Z-score for 95% confidence interval
    private static final double CONFIDENCE_95 = 1.96;

    private final double mean;
    private final double stddev;
    private final double confidenceLo;
    private final double confidenceHi;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {

        // Handle corner cases
        if (n < 1)
            throw new IllegalArgumentException("Percolation grid size must be at least 1-by-1");
        if (trials < 1)
            throw new IllegalArgumentException("Number of trial must be greater than 0");

        // The number of sites in the n-by-n grid
        int sites = n * n;

        // Stores the number of open sites for each trial when the system percolates
        double[] results = new double[trials];

        // Monte Carlo simulation for T trials
        for (int trial = 0; trial < trials; trial++) {
            Percolation grid = new Percolation(n);
            while (!grid.percolates()) {
                grid.open(StdRandom.uniform(1, n + 1), StdRandom.uniform(1, n + 1));
            }
            results[trial] = (double) grid.numberOfOpenSites() / sites;
        }

        // Calculate statistics
        mean = StdStats.mean(results);
        stddev = StdStats.stddev(results);
        confidenceLo = (mean - (CONFIDENCE_95 * stddev) / Math.sqrt(results.length));
        confidenceHi = (mean + (CONFIDENCE_95 * stddev) / Math.sqrt(results.length));
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return confidenceLo;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return confidenceHi;
    }

    // test client
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);       // size of n-by-n grid
        int trials = Integer.parseInt(args[1]);  // number of trials

        // Handle corner cases
        if (n < 1)
            throw new IllegalArgumentException("Percolation grid size must be at least 1-by-1");
        if (trials < 1)
            throw new IllegalArgumentException("Number of trial must be greater than 0");

        PercolationStats test = new PercolationStats(n, trials);
        System.out.printf("mean\t\t\t\t\t= %f\n", test.mean());
        System.out.printf("stddev\t\t\t\t\t= %f\n", test.stddev());
        System.out.printf("95%% confidence interval = [%f, %f]", test.confidenceLo(),
                          test.confidenceHi());
    }

}
