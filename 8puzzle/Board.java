/* *****************************************************************************
 * Name: Spyros Dellas
 * Date: 15/05/2020
 *
 * Description:
 * Board data type is an immutable date type that models an n-by-n puzzle board
 * with sliding tiles.
 *
 * Constructor.
 * The constructor receives an n-by-n array containing the n^2 integers between
 * 0 and n^2 − 1, where 0 represents the blank square, with 2 ≤ n < 128.
 *
 * String representation.
 * The toString() method returns a string composed of n + 1 lines. The first
 * line contains the board size n; the remaining n lines contain the n-by-n grid
 * of tiles in row-major order, using 0 to designate the blank square.
 *
 * Performance requirements.
 * The implementation should support all Board methods in time proportional
 * to n^2 (or better) in the worst case.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

public final class Board {

    private final short[][] board;
    private final int hammingDistance;
    private final int manhattanDistance;
    private final boolean isGoalBoard;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        int dimension = tiles.length;
        board = new short[dimension][dimension];

        int hamming = 0;
        int manhattan = 0;
        for (int i = 0; i < dimension; i++) {
            int previousRows = i * dimension;
            for (int j = 0; j < dimension; j++) {
                short tile = (short) tiles[i][j];
                board[i][j] = tile;
                if (tile != 0 && tile != previousRows + j + 1) {
                    hamming++;
                    int homeColumn = (tile - 1) % dimension;
                    int homeRow = (tile - 1) / dimension;
                    manhattan += Math.abs(homeColumn - j) + Math.abs(homeRow - i);
                }
            }
        }
        hammingDistance = hamming;
        manhattanDistance = manhattan;
        isGoalBoard = (hammingDistance == 0);
    }

    // board dimension n
    public int dimension() {
        return board.length;
    }

    // number of tiles out of place
    public int hamming() {
        return hammingDistance;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattanDistance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return isGoalBoard;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        int dimension = board.length;
        int[] blank = new int[2];
        Queue<Board> neighbors = new Queue<>();
        int[][] copy = new int[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                copy[i][j] = board[i][j];
                if (copy[i][j] == 0) {
                    blank[0] = i;
                    blank[1] = j;
                }
            }
        }
        if (blank[0] > 0) {
            exch(copy, blank[0], blank[1], blank[0] - 1, blank[1]);
            neighbors.enqueue(new Board(copy));
            exch(copy, blank[0] - 1, blank[1], blank[0], blank[1]);
        }
        if (blank[0] < dimension - 1) {
            exch(copy, blank[0], blank[1], blank[0] + 1, blank[1]);
            neighbors.enqueue(new Board(copy));
            exch(copy, blank[0] + 1, blank[1], blank[0], blank[1]);
        }
        if (blank[1] > 0) {
            exch(copy, blank[0], blank[1], blank[0], blank[1] - 1);
            neighbors.enqueue(new Board(copy));
            exch(copy, blank[0], blank[1] - 1, blank[0], blank[1]);
        }
        if (blank[1] < dimension - 1) {
            exch(copy, blank[0], blank[1], blank[0], blank[1] + 1);
            neighbors.enqueue(new Board(copy));
            exch(copy, blank[0], blank[1] + 1, blank[0], blank[1]);
        }
        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int dimension = board.length;
        int[] blank = new int[2];
        int[][] copy = new int[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                copy[i][j] = board[i][j];
                if (copy[i][j] == 0) {
                    blank[0] = i;
                    blank[1] = j;
                }
            }
        }
        if (blank[1] == 0) {
            exch(copy, 0, 1, 1, 1);
        }
        else {
            exch(copy, 0, 0, 1, 0);
        }
        return new Board(copy);
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null) return false;
        if (this.getClass() != y.getClass()) return false;
        Board that = (Board) y;
        int dimension = board.length;
        if (dimension != that.board.length) return false;
        for (int i = 0; i < dimension; i++) {
            if (dimension != that.board[i].length) return false;
            for (int j = 0; j < dimension; j++) {
                if (this.board[i][j] != that.board[i][j])
                    return false;
            }
        }
        return true;
    }

    // string representation of this board
    public String toString() {
        StringBuilder sBoard = new StringBuilder();
        int dimension = board.length;
        sBoard.append(dimension + "\n");
        for (short[] row : board) {
            for (short tile : row) {
                if (dimension < 100) {
                    sBoard.append(String.format("%2d ", tile));
                }
                else {
                    sBoard.append(String.format("%3d ", tile));
                }
            }
            sBoard.append("\n");
        }
        return sBoard.toString();
    }

    /* ************************************************************************
                              PRIVATE HELPER METHODS
     **************************************************************************/

    private void exch(int[][] puzzle, int row1, int col1, int row2, int col2) {
        int buffer = puzzle[row1][col1];
        puzzle[row1][col1] = puzzle[row2][col2];
        puzzle[row2][col2] = buffer;
    }

    /* ************************************************************************
                              UNIT TESTING
     **************************************************************************/
    public static void main(String[] args) {

        In file = new In("puzzle00.txt");
        int dim = file.readInt();
        int[][] tiles = new int[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                tiles[i][j] = file.readInt();
            }
        }

        Board puzzle = new Board(tiles);
        System.out.print(puzzle);
        System.out.println("Is this the goal board? " + puzzle.isGoal());
        System.out.println("Hamming distance from the goal board: " + puzzle.hamming());
        System.out.println("Manhattan distance from the goal board: " + puzzle.manhattan());
        Board twin = puzzle.twin();
        System.out.print("Twin: \n" + twin);
        System.out.println("Neighbors:");
        for (Board b : puzzle.neighbors()) {
            System.out.print(b);
        }
    }
}
