/* *****************************************************************************
 *  Name:              Spyros Dellas
 *  Coursera User ID:  spyrosdellas@yahoo.com
 *  Last modified:     20/04/2020
 *
 * Percolation.
 * Given a composite systems comprised of randomly distributed insulating and
 * metallic materials: what fraction of the materials need to be metallic so
 * that the composite system is an electrical conductor? Given a porous
 * landscape with water on the surface (or oil below), under what conditions
 * will the water be able to drain through to the bottom (or the oil to gush
 * through to the surface)? Scientists have defined an abstract process known
 * as percolation to model such situations.
 *
 * The model.
 * We model a percolation system using an n-by-n grid of sites. Each site is
 * either open or blocked. A full site is an open site that can be connected
 * to an open site in the top row via a chain of neighboring (left, right, up,
 * down) open sites. We say the system percolates if there is a full site in
 * the bottom row. In other words, a system percolates if we fill all open
 * sites connected to the top row and that process fills some open site on the
 * bottom row. (For the insulating/metallic materials example, the open sites
 * correspond to metallic materials, so that a system that percolates has a
 * metallic path from top to bottom, with full sites conducting. For the porous
 * substance example, the open sites correspond to empty space through which
 * water might flow, so that a system that percolates lets water fill open
 * sites, flowing from top to bottom.)
 *
 * The problem.
 * In a famous scientific problem, researchers are interested in the following
 * question: if sites are independently set to be open with probability p (and
 * therefore blocked with probability 1 − p), what is the probability that the
 * system percolates? When p equals 0, the system does not percolate; when p
 * equals 1, the system percolates. When n is sufficiently large, there is a
 * threshold value p* such that when p < p* a random n-by-n grid almost never
 * percolates, and when p > p*, a random n-by-n grid almost always percolates.
 * No mathematical solution for determining the percolation threshold p* has
 * yet been derived.
 * Percolation.java runs a Monte Carlo simulation to estimate the threshold
 * value p*.
 *
 * Corner cases.
 * By convention, the row and column indices are integers between 1 and n,
 * where (1, 1) is the upper-left site: Throw an IllegalArgumentException if
 * any argument to open(), isOpen(), or isFull() is outside its prescribed
 * range. Throw an IllegalArgumentException in the constructor if n ≤ 0.
 *
 * Performance requirements.
 * The constructor should take time proportional to n^2; all methods should
 * take constant time plus a constant number of calls to the union–find methods
 * union(), find(), connected(), and count().
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class PercolationSimple {

    // dimension n of the grid
    private final int gridSize;

    // Stores the state of the n-by-n grid of sites
    // 0 -> blocked, 1 -> open, 2-> full
    private byte[][] grid;

    // Union-Find data structures representing the connections of the n-by-n
    // grid of sites, serialised in one dimension
    private WeightedQuickUnionUF gridUF;
    private WeightedQuickUnionUF gridUFNoBottomVS;

    private final int topRowVirtualSite;     // virtual site for top row
    private final int bottomRowVirtualSite;  // virtual site for bottom row
    private int openSites;                   // number of open sites in the grid

    private boolean percolated = false;     // has the grid percolated?

    // creates n-by-n grid, with all sites initially blocked
    public PercolationSimple(int n) {

        if (n < 1) throw new IllegalArgumentException("Grid must be at least 1-by-1");

        // Create n-by-n grid of sites.
        gridSize = n;
        grid = new byte[gridSize][gridSize];
        openSites = 0;

        // Create a Union-Find data structure for the grid of sites, plus two
        // extra virtual sites:
        // gridUF[0] connects all of the top row sites
        // gridUF[n*n + 1] connects all of the bottom row site
        gridUF = new WeightedQuickUnionUF(gridSize * gridSize + 2);
        topRowVirtualSite = 0;
        bottomRowVirtualSite = gridSize * gridSize + 1;

        // Create a Union-Find data structure for the grid of sites, plus one
        // extra virtual site for the top row
        gridUFNoBottomVS = new WeightedQuickUnionUF(gridSize * gridSize + 1);
    }

    // Translates an index of the form (row, col) to its corresponding index in
    // the serialised grid
    private int index(int row, int col) {
        int index = 1 + (row - 1) * gridSize + (col - 1);
        return index;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row < 1 || row > gridSize)
            throw new IllegalArgumentException("Row must be between 1 and n.");
        if (col < 1 || col > gridSize)
            throw new IllegalArgumentException("Column must be between 1 and n.");

        // If site is already open, no further action is required
        if (grid[row - 1][col - 1] != 0) return;

        // Open site
        grid[row - 1][col - 1] = 1;
        openSites++;

        if (row == 1) {
            // Connect site to the virtual site for top row
            if (!percolated) {
                gridUF.union(topRowVirtualSite, index(row, col));
            }
            gridUFNoBottomVS.union(topRowVirtualSite, index(row, col));
        }

        if (row == gridSize && !percolated) {
            // Connect site to the virtual site for bottom row
            gridUF.union(bottomRowVirtualSite, index(row, col));
        }

        if (row > 1) {
            if (grid[row - 2][col - 1] != 0) {
                if (!percolated) {
                    gridUF.union(index(row, col), index(row - 1, col));
                }
                gridUFNoBottomVS.union(index(row, col), index(row - 1, col));
            }
        }
        if (row < gridSize) {
            if (grid[row][col - 1] != 0) {
                if (!percolated) {
                    gridUF.union(index(row, col), index(row + 1, col));
                }
                gridUFNoBottomVS.union(index(row, col), index(row + 1, col));
            }
        }
        if (col > 1) {
            if (grid[row - 1][col - 2] != 0) {
                if (!percolated) {
                    gridUF.union(index(row, col), index(row, col - 1));
                }
                gridUFNoBottomVS.union(index(row, col), index(row, col - 1));
            }
        }
        if (col < gridSize) {
            if (grid[row - 1][col] != 0) {
                if (!percolated) {
                    gridUF.union(index(row, col), index(row, col + 1));
                }
                gridUFNoBottomVS.union(index(row, col), index(row, col + 1));
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row < 1 || row > gridSize)
            throw new IllegalArgumentException("Row must be between 1 and n.");
        if (col < 1 || col > gridSize)
            throw new IllegalArgumentException("Column must be between 1 and n.");
        return grid[row - 1][col - 1] != 0;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (row < 1 || row > gridSize)
            throw new IllegalArgumentException("Row must be between 1 and n.");
        if (col < 1 || col > gridSize)
            throw new IllegalArgumentException("Column must be between 1 and n.");

        if (grid[row - 1][col - 1] == 0) return false;
        if (grid[row - 1][col - 1] == 2) return true;
        if (gridUFNoBottomVS.find(index(row, col)) ==
                gridUFNoBottomVS.find(topRowVirtualSite)) {
            grid[row - 1][col - 1] = 2;
            return true;
        }
        return false;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        if (!percolated) {
            percolated = gridUF.find(bottomRowVirtualSite) == gridUF.find(topRowVirtualSite);
        }
        return percolated;
    }

    // test client (optional)
    public static void main(String[] args) {
        int n = StdIn.readInt();
        System.out.println("Grid size = " + n);
        PercolationSimple test = new PercolationSimple(n);
        while (!StdIn.isEmpty()) {
            int row = StdIn.readInt();
            int col = StdIn.readInt();
            test.open(row, col);
        }
        System.out.println("Number of open sites = " + test.numberOfOpenSites());
        System.out.println("Percolates = " + test.percolates());
    }

}
