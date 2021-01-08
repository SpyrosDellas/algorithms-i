/* ****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date:  22/05/2020
 *
 * Description:
 * KdTree.java uses a 2d-tree to implement the same API with PointSET.
 * A 2d-tree is a generalization of a BST to two-dimensional keys. The idea is
 * to build a BST with points in the nodes, using the x- and y-coordinates of
 * the points as keys in strictly alternating sequence.
 *
 * The prime advantage of a 2d-tree over a BST is that it supports efficient
 * implementation of range search and nearest-neighbor search. Each node
 * corresponds to an axis-aligned rectangle in the unit square, which encloses
 * all of the points in its subtree.
 * The root corresponds to the unit square; the left and right children of the
 * root corresponds to the two rectangles split by the x-coordinate of the
 * point at the root; and so forth.
 *
 * Corner cases.
 * Throws an IllegalArgumentException if any argument is null.
 *
 * Draw.
 * A 2d-tree divides the unit square in a simple way: all the points to the
 * left of the root go in the left subtree; all those to the right go in the
 * right subtree; and so forth, recursively. draw() draws all of the points to
 * standard draw in black and the subdivisions in red (for vertical splits)
 * and blue (for horizontal splits).
 * This method is not efficient — it is primarily for debugging.
 *
 **************************************************************************** */


import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;


public class KdTree {

    private Node root;
    private int size;

    private static class Node {

        private Point2D point;
        private Node left;
        private Node right;

        public Node(Point2D point) {
            this.point = point;
            this.left = null;
            this.right = null;
        }
    }

    private static class PointDistance {

        private final Point2D point;
        private final double distance;

        public PointDistance(Point2D point, double distance) {
            this.point = point;
            this.distance = distance;
        }
    }

    // construct an empty set of points
    public KdTree() {
        root = null;
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {

        if (p == null)
            throw new IllegalArgumentException();

        root = insert(root, p, 0);
    }

    private Node insert(Node x, Point2D p, int level) {


        if (x == null) {
            size++;
            return new Node(p);
        }

        if (p.compareTo(x.point) == 0)
            return x;

        if (level % 2 == 0) {
            if (p.x() < x.point.x())
                x.left = insert(x.left, p, level + 1);
            else
                x.right = insert(x.right, p, level + 1);
        }
        else {
            if (p.y() < x.point.y())
                x.left = insert(x.left, p, level + 1);
            else
                x.right = insert(x.right, p, level + 1);
        }

        return x;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        return contains(root, p, 0);
    }

    private boolean contains(Node x, Point2D p, int level) {

        if (x == null)
            return false;

        if (p.compareTo(x.point) == 0)
            return true;

        if (level % 2 == 0) {
            if (p.x() < x.point.x())
                return contains(x.left, p, level + 1);
            else
                return contains(x.right, p, level + 1);
        }
        else {
            if (p.y() < x.point.y())
                return contains(x.left, p, level + 1);
            else
                return contains(x.right, p, level + 1);
        }
    }

    // draw all points to standard draw
    public void draw() {
        draw(root, 0, 0.0, 1.0, 0.0, 1.0);
    }

    private void draw(Node x, int level, double xMin, double xMax, double yMin, double yMax) {

        if (x == null)
            return;

        if (level % 2 == 0) {
            draw(x.left, level + 1, xMin, x.point.x(), yMin, yMax);
        }
        else {
            draw(x.left, level + 1, xMin, xMax, yMin, x.point.y());
        }

        StdDraw.setPenRadius(0.01);
        StdDraw.point(x.point.x(), x.point.y());
        StdDraw.setPenRadius(0.002);

        if (level % 2 == 0) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(x.point.x(), yMin, x.point.x(), yMax);
            StdDraw.setPenColor(StdDraw.BLACK);
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(xMin, x.point.y(), xMax, x.point.y());
            StdDraw.setPenColor(StdDraw.BLACK);
        }

        if (level % 2 == 0) {
            draw(x.right, level + 1, x.point.x(), xMax, yMin, yMax);
        }
        else {
            draw(x.right, level + 1, xMin, xMax, x.point.y(), yMax);
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {

        if (rect == null)
            throw new IllegalArgumentException();

        Queue<Point2D> pointsInside = new Queue<>();

        range(root, rect, pointsInside, 0);
        return pointsInside;
    }

    private void range(Node x, RectHV rect, Queue<Point2D> pointsInside, int level) {

        if (x == null)
            return;

        if (level % 2 == 0) {
            if (rect.xmin() < x.point.x()) {
                range(x.left, rect, pointsInside, level + 1);
            }
        }
        else {
            if (rect.ymin() < x.point.y()) {
                range(x.left, rect, pointsInside, level + 1);
            }
        }

        if (rect.contains(x.point))
            pointsInside.enqueue(x.point);

        if (level % 2 == 0) {
            if (rect.xmax() >= x.point.x()) {
                range(x.right, rect, pointsInside, level + 1);
            }
        }
        else {
            if (rect.ymax() >= x.point.y()) {
                range(x.right, rect, pointsInside, level + 1);
            }
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D queryP) {

        if (queryP == null)
            throw new IllegalArgumentException();

        if (isEmpty())
            return null;

        PointDistance nearest = new PointDistance(root.point,
                                                  queryP.distanceSquaredTo(root.point));
        RectHV subdivision = new RectHV(0.0, 0.0, 1.0, 1.0);
        return nearestEven(root, queryP, nearest, subdivision).point;
    }

    private PointDistance nearestEven(Node x, Point2D queryP, PointDistance nearest,
                                      RectHV subdivision) {

        if (x == null)
            return nearest;

        // System.out.println("Visiting node: " + x.point +
        //                           " -> subdivision = " + subdivision.toString());

        double distanceToThisNode = queryP.distanceSquaredTo(x.point);
        if (distanceToThisNode < nearest.distance) {
            nearest = new PointDistance(x.point, distanceToThisNode);
        }

        double thisX = x.point.x();

        double xMin = subdivision.xmin();
        double yMin = subdivision.ymin();
        double xMax = subdivision.xmax();
        double yMax = subdivision.ymax();
        RectHV leftSubdivision = new RectHV(xMin, yMin, thisX, yMax);
        RectHV rightSubdivision = new RectHV(thisX, yMin, xMax, yMax);

        if (queryP.x() < thisX) {
            nearest = nearestOdd(x.left, queryP, nearest, leftSubdivision);
            double distanceToBoundary;
            if (!subdivision.contains(queryP)) {
                distanceToBoundary = rightSubdivision.distanceSquaredTo(queryP);
            }
            else {
                distanceToBoundary = thisX - queryP.x();
                distanceToBoundary *= distanceToBoundary;
            }
            if (distanceToBoundary < nearest.distance) {
                nearest = nearestOdd(x.right, queryP, nearest, rightSubdivision);
            }
        }

        else {
            nearest = nearestOdd(x.right, queryP, nearest, rightSubdivision);
            double distanceToBoundary;
            if (!subdivision.contains(queryP)) {
                distanceToBoundary = leftSubdivision.distanceSquaredTo(queryP);
            }
            else {
                distanceToBoundary = thisX - queryP.x();
                distanceToBoundary *= distanceToBoundary;
            }
            if (distanceToBoundary < nearest.distance) {
                nearest = nearestOdd(x.left, queryP, nearest, leftSubdivision);
            }
        }

        return nearest;
    }

    private PointDistance nearestOdd(Node x, Point2D queryP, PointDistance nearest,
                                     RectHV subdivision) {

        if (x == null)
            return nearest;

        // System.out.println("Visiting node: " + x.point +
        //                           " -> subdivision = " + subdivision.toString());

        double thisY = x.point.y();

        double xMin = subdivision.xmin();
        double yMin = subdivision.ymin();
        double xMax = subdivision.xmax();
        double yMax = subdivision.ymax();
        RectHV leftSubdivision = new RectHV(xMin, yMin, xMax, thisY);
        RectHV rightSubdivision = new RectHV(xMin, thisY, xMax, yMax);

        double distanceToThisNode = queryP.distanceSquaredTo(x.point);
        if (distanceToThisNode < nearest.distance) {
            nearest = new PointDistance(x.point, distanceToThisNode);
        }

        if (queryP.y() < thisY) {
            nearest = nearestEven(x.left, queryP, nearest, leftSubdivision);
            double distanceToBoundary;
            if (!subdivision.contains(queryP)) {
                distanceToBoundary = rightSubdivision.distanceSquaredTo(queryP);
            }
            else {
                distanceToBoundary = thisY - queryP.y();
                distanceToBoundary *= distanceToBoundary;
            }
            if (distanceToBoundary < nearest.distance) {
                nearest = nearestEven(x.right, queryP, nearest, rightSubdivision);
            }
        }

        else {
            nearest = nearestEven(x.right, queryP, nearest, rightSubdivision);
            double distanceToBoundary;
            if (!subdivision.contains(queryP)) {
                distanceToBoundary = leftSubdivision.distanceSquaredTo(queryP);
            }
            else {
                distanceToBoundary = thisY - queryP.y();
                distanceToBoundary *= distanceToBoundary;
            }
            if (distanceToBoundary < nearest.distance) {
                nearest = nearestEven(x.left, queryP, nearest, leftSubdivision);
            }
        }

        return nearest;
    }

    private static void checkNearest(String file, int trials) {

        In in = new In(file);
        KdTree kdtree = new KdTree();
        PointSET brute = new PointSET();

        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
            brute.insert(p);
        }

        // Check correctness of nearest(), trials random points in the unit square
        System.out.println(
                "Checking correctness of KdTree.nearest() for " + trials
                        + " random points in the unit square against the brute force implementation...");
        for (int i = 0; i < trials; i++) {
            double x = StdRandom.uniform();
            double y = StdRandom.uniform();
            Point2D q = new Point2D(x, y);
            Point2D bruteNearest = brute.nearest(q);
            Point2D kdNearest = kdtree.nearest(q);
            if (bruteNearest.compareTo(kdNearest) != 0)
                throw new RuntimeException("Correctness check failed.");
        }
        System.out.println("Correctness of KdTree.nearest() verified.");

    }

    // unit testing of the methods
    public static void main(String[] args) {

        String file1 = "circle10000.txt";
        checkNearest(file1, 5000);

        String file2 = "input1M.txt";
        In in = new In(file2);
        KdTree kdtree = new KdTree();
        PointSET brute = new PointSET();
        System.out.println("\nChecking performance of nearest() for file " + file2 + "...");

        System.out.println("Building PointSET and KdTree...");
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
            brute.insert(p);
        }

        int trials = 3000000;
        Point2D[] points = new Point2D[trials];
        System.out.println("Generating " + trials + " random points...");
        for (int i = 0; i < trials; i++) {
            double x = StdRandom.uniform();
            double y = StdRandom.uniform();
            points[i] = new Point2D(x, y);
        }

        System.out.println("Calculating performance...");
        double start = System.currentTimeMillis();
        for (int i = 0; i < trials; i++) {
            Point2D kdNearest = kdtree.nearest(points[i]);
        }
        double end = System.currentTimeMillis();
        int pointsPerSec = (int) (trials / ((end - start) / 1000));
        System.out.println("KdTree.nearest() performance: " + pointsPerSec + " points/sec");

        double start1 = System.currentTimeMillis();
        int trials1 = 200;
        for (int i = 0; i < trials1; i++) {
            Point2D bruteNearest = brute.nearest(points[i]);
        }
        double end1 = System.currentTimeMillis();
        int pointsPerSec1 = (int) (trials1 / ((end1 - start1) / 1000));
        System.out.println("PointSET.nearest() performance: " + pointsPerSec1 + " points/sec");

        System.out.println("Outcome: KdTree is " + pointsPerSec / pointsPerSec1
                                   + " times faster than the brute force implementation");

    }

}
