/* *****************************************************************************
 *  Name:              Spyros Dellas
 *  Coursera User ID:  spyrosdellas@yahoo.com
 *  Last modified:     22/04/2020
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

public class Percolation {

    // Union-Find data structure representing the connections of the n-by-n
    // grid of sites, serialised in one dimension
    private UnionFind gridUF;

    // Stores the state of the individual sites of the two dimensional grid
    // Bit 0 (least significant): 0 -> blocked,  1 -> open
    // Bit 1:                     0 -> not full, 1 -> full (i.e. connected to top row)
    // Bit 2:                     0 -> not connected to bottom row
    //                            1 -> connected to bottom row
    // Bits 3 to 7:               Not used
    private byte[][] grid;

    private final int gridSize;          // dimension n of the grid
    private int openSites;               // number of open sites in the grid
    private boolean percolated = false;  // has the grid percolated?


    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {

        if (n < 1) throw new IllegalArgumentException("Grid must be at least 1-by-1");

        gridUF = new UnionFind(n * n);
        grid = new byte[n][n];
        gridSize = n;
        openSites = 0;
    }


    // Translates an index of the form (row, col) to its corresponding index in
    // the serialised grid
    private int toIndex(int row, int col) {
        int index = (row - 1) * gridSize + (col - 1);
        return index;
    }


    // Connects two open sites and keeps track of their connections to the
    // top and bottom rows by updating their parent sites accordingly
    private void connect(int thisRow, int thisCol, int thatRow, int thatCol) {

        // find both parents
        int thisParentIndex = gridUF.find(toIndex(thisRow, thisCol));
        int thisParentRow = thisParentIndex / gridSize;
        int thisParentCol = thisParentIndex - thisParentRow * gridSize;
        byte thisParent = grid[thisParentRow][thisParentCol];

        int thatParentIndex = gridUF.find(toIndex(thatRow, thatCol));
        int thatParentRow = thatParentIndex / gridSize;
        int thatParentCol = thatParentIndex - thatParentRow * gridSize;
        byte thatParent = grid[thatParentRow][thatParentCol];

        // connect the two sites
        gridUF.union(toIndex(thisRow, thisCol), toIndex(thatRow, thatCol));

        // Merge the attributes of the two parents and update both:
        // This is permisible as both of them now belong to the same tree
        // eventhough only one of them survives and remains referenced in the
        // Union-Find data structure as a parent
        byte mergedParent = (byte) (thisParent | thatParent);
        grid[thisParentRow][thisParentCol] = mergedParent;
        grid[thatParentRow][thatParentCol] = mergedParent;

        // check if the merged parent site is connected to both the
        // top and bottom rows
        if ((mergedParent >> 1 & 1) == 1 && (mergedParent >> 2 & 1) == 1) {
            percolated = true;
        }
    }


    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {

        // Handle corner cases
        if (row < 1 || row > gridSize)
            throw new IllegalArgumentException("Row must be between 1 and n.");
        if (col < 1 || col > gridSize)
            throw new IllegalArgumentException("Column must be between 1 and n.");

        // If site is already open, no further action is required
        if ((grid[row - 1][col - 1] & 1) == 1) return;

        // Open site
        grid[row - 1][col - 1] |= 0b00000001;
        openSites++;

        // Mark site as full if in top row
        if (row == 1) {
            grid[row - 1][col - 1] |= 0b00000010;
        }

        // Mark site as connected to bottom row
        if (row == gridSize) {
            grid[row - 1][col - 1] |= 0b00000100;
        }

        // Handle corner case for 1-by-1 grid with one open site
        if (gridSize == 1) {
            percolated = true;
        }

        // Connect to site above
        if (row > 1 && (grid[row - 2][col - 1] & 1) == 1) {
            connect(row, col, row - 1, col);
        }

        // connect to site below
        if (row < gridSize && (grid[row][col - 1] & 1) == 1) {
            connect(row, col, row + 1, col);
        }

        // connect to site to the left
        if (col > 1 && (grid[row - 1][col - 2] & 1) == 1) {
            connect(row, col, row, col - 1);
        }

        // connect to site to the right
        if (col < gridSize && (grid[row - 1][col] & 1) == 1) {
            connect(row, col, row, col + 1);
        }
    }


    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {

        if (row < 1 || row > gridSize)
            throw new IllegalArgumentException("Row must be between 1 and n.");
        if (col < 1 || col > gridSize)
            throw new IllegalArgumentException("Column must be between 1 and n.");

        return (grid[row - 1][col - 1] & 1) == 1;
    }


    // is the site (row, col) full?
    public boolean isFull(int row, int col) {

        if (row < 1 || row > gridSize)
            throw new IllegalArgumentException("Row must be between 1 and n.");
        if (col < 1 || col > gridSize)
            throw new IllegalArgumentException("Column must be between 1 and n.");

        // site is blocked
        if ((grid[row - 1][col - 1] & 1) == 0) return false;

        // site already full
        if ((grid[row - 1][col - 1] >> 1 & 1) == 1) return true;

        // site open but not marked as full, check if parent is full
        // if full, mark as full and return true
        int parentIndex = gridUF.find(toIndex(row, col));
        int pRow = parentIndex / gridSize;
        int pCol = parentIndex - pRow * gridSize;
        if ((grid[pRow][pCol] >> 1 & 1) == 1) {
            grid[row - 1][col - 1] |= 0b00000010;
            return true;
        }

        // site remains open but not full, return false
        return false;
    }


    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }


    // does the system percolate?
    public boolean percolates() {
        return percolated;
    }


    // test client (optional)
    public static void main(String[] args) {
        int n = StdIn.readInt();
        System.out.println("Grid size = " + n);
        Percolation test = new Percolation(n);
        while (!StdIn.isEmpty()) {
            int row = StdIn.readInt();
            int col = StdIn.readInt();
            test.open(row, col);
        }
        System.out.println("Number of open sites = " + test.numberOfOpenSites());
        System.out.println("Percolates = " + test.percolates());
    }

}
