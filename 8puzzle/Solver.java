/* *****************************************************************************
 * Name: Spyros Dellas
 * Date: 15/05/2020
 *
 * Description:
 * We implement an 8-puzzle problem solver using the A* search algorithm. We
 * define a search node of the game to be a board, the number of moves made to
 * reach the board, and the previous search node. First, we insert the initial
 * search node (the initial board, 0 moves, and a null previous search node)
 * into a priority queue. Then, we delete from the priority queue the search
 * node with the minimum priority, and insert onto the priority queue all
 * neighboring search nodes (those that can be reached in one move from the
 * dequeued search node). We repeat this procedure until the search node
 * dequeued corresponds to the goal board.
 * The efficacy of this approach hinges on the choice of priority function for
 * a search node. We consider two priority functions:
 * - The Hamming priority function is the Hamming distance of a board plus the
 *   number of moves made so far to get to the search node. Intuitively, a
 *   search node with a small number of tiles in the wrong position is close to
 *   the goal, and we prefer a search node if has been reached using a small
 *   number of moves.
 * - The Manhattan priority function is the Manhattan distance of a board plus
 *   the number of moves made so far to get to the search node.
 * To solve the puzzle from a given search node on the priority queue, the total
 * number of moves we need to make (including those already made) is at least
 * its priority, using either the Hamming or Manhattan priority function.
 * Consequently, when the goal board is dequeued, we have discovered not only a
 * sequence of moves from the initial board to the goal board, but one that
 * makes the fewest moves.
 *
 * Optimisation 1:
 * A* search has one annoying feature: search nodes corresponding to the same
 * board are enqueued on the priority queue many times. To reduce unnecessary
 * exploration of useless search nodes, when considering the neighbors of a
 * search node, we don't enqueue a neighbor if its board is the same as the
 * board of the previous search node in the game tree.
 *
 * Optimisation 2:
 * To avoid recomputing the Manhattan priority of a search node from scratch
 * each time during various priority queue operations, we pre-compute its value
 * when we construct the search node and save it in an instance variable.
 *
 * Detecting unsolvable boards:
 * Not all initial boards can lead to the goal board by a sequence of moves. To
 * detect such situations, we use the fact that boards are divided into two
 * equivalence classes with respect to reachability:
 *  - Those that can lead to the goal board
 *  - Those that can lead to the goal board if we modify the initial board by
 *    swapping any pair of tiles (the blank square is not a tile).
 * To apply the fact, we run the A* algorithm on two puzzle instances - one with
 * the initial board and one with the initial board modified by swapping a pair
 * of tiles—in lockstep (alternating back and forth between exploring search
 * nodes in each of the two game trees). Exactly one of the two will lead to
 * the goal board.
 *
 * Corner case:
 * Throws an IllegalArgumentException in the constructor if the argument is null
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private final Stack<Board> solution;
    private final int moves;
    private final boolean isSolvable;

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
    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException("Null puzzle provided");
        MinPQ<Node> gameTree = new MinPQ<>();
        MinPQ<Node> twinTree = new MinPQ<>();
        gameTree.insert(new Node(initial, null, 0));
        twinTree.insert(new Node(initial.twin(), null, 0));

        Node solutionNode = null;
        while (true) {
            Node minGameNode = gameTree.delMin();
            Node minTwinNode = twinTree.delMin();
            if (minGameNode.board.isGoal()) {
                isSolvable = true;
                solutionNode = minGameNode;
                break;
            }
            else if (minTwinNode.board.isGoal()) {
                isSolvable = false;
                break;
            }
            for (Board b : minGameNode.board.neighbors()) {
                if (minGameNode.previousNode == null) {
                    gameTree.insert(new Node(b, minGameNode, minGameNode.movesFromStart + 1));
                }
                else if (!b.equals(minGameNode.previousNode.board)) {
                    gameTree.insert(new Node(b, minGameNode, minGameNode.movesFromStart + 1));
                }
            }
            for (Board b : minTwinNode.board.neighbors()) {
                if (minTwinNode.previousNode == null) {
                    twinTree.insert(new Node(b, minTwinNode, minTwinNode.movesFromStart + 1));
                }
                else if (!b.equals(minTwinNode.previousNode.board)) {
                    twinTree.insert(new Node(b, minTwinNode, minTwinNode.movesFromStart + 1));
                }
            }
        }

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
        Solver solver = new Solver(initial);

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
