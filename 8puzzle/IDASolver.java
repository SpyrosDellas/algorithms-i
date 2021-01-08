/* *****************************************************************************
 * Name: Spyros Dellas
 * Date: 15/05/2020
 *
 * Description:
 * Implementation of the IDA* search algorithm to improve the
 * memory efficiency of the A* algorithm
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class IDASolver {

    private final Stack<Board> solution;
    private final int moves;
    private boolean isSolvable;

    private static class Node implements Comparable<Node> {
        private final Board board;
        private final Node previousNode;
        private final int movesFromStart;
        private final int priority;

        public Node(Board board, Node previousNode, int movesFromStart) {
            this.board = board;
            this.previousNode = previousNode;
            this.movesFromStart = movesFromStart;
            this.priority = movesFromStart + board.manhattan();
        }

        public int compareTo(Node that) {
            if (this.priority == that.priority) return 0;
            return (this.priority > that.priority) ? 1 : -1;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public IDASolver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException("Null puzzle provided");

        Node root = new Node(initial, null, 0);
        int bestEstimate = initial.manhattan();
        Node solutionNode = null;
        System.out.println(
                "Starting IDA* solver with initial Manhattan distance estimate = "
                        + bestEstimate + " moves");
        double start = System.currentTimeMillis();
        while (true) {
            solutionNode = solve(root, bestEstimate);
            if (solutionNode != null) {
                isSolvable = true;
                break;
            }
            else {
                bestEstimate += 1;
                System.out.println(
                        "Estimate increased to " + bestEstimate + " moves");
            }
        }
        double end = System.currentTimeMillis();
        System.out.println("Total time to solve: " + (end - start) / 1000 + " secs");
        if (!isSolvable) {
            solution = null;
            moves = -1;
            return;
        }
        moves = solutionNode.movesFromStart;
        solution = new Stack<>();
        while (solutionNode != null) {
            solution.push(solutionNode.board);
            solutionNode = solutionNode.previousNode;
        }
    }

    private Node solve(Node root, int bestEstimate) {

        if (root.board.isGoal()) return root;

        if (root.priority > bestEstimate) return null;

        Node solutionNode = null;
        for (Board neighbor : root.board.neighbors()) {
            if (root.previousNode == null) {
                solutionNode = solve(new Node(neighbor, root, root.movesFromStart + 1),
                                     bestEstimate);
            }
            else if (!neighbor.equals(root.previousNode.board)) {
                solutionNode = solve(new Node(neighbor, root, root.movesFromStart + 1),
                                     bestEstimate);
            }
            if (solutionNode != null) return solutionNode;
        }
        return null;
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        return solution;
    }

    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        IDASolver solver = new IDASolver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
