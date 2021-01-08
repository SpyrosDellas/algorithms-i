/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 01/05/2020
 *
 * Description:
 * Remarkably, it is possible to solve the collinearity problem much faster
 * than the brute-force solution described in BruteCollinearPoints.java.
 *
 * Given a point p, the following method determines whether p participates in a
 * set of 4 or more collinear points:
 * - Think of p as the origin
 * - For each other point q, determine the slope it makes with p
 * - Sort the points according to the slopes they make with p
 * - Check if any 3 (or more) adjacent points in the sorted order have equal
 *   slopes with respect to p. If so, these points, together with p, are
 *   collinear
 *
 * Applying this method for each of the n points in turn yields an efficient
 * algorithm to the problem. The algorithm solves the problem because points
 * that have equal slopes with respect to p are collinear, and sorting brings
 * such points together. The algorithm is fast because the bottleneck operation
 * is sorting.
 *
 * The method segments() includes each maximal line segment containing 4 (or
 * more) points exactly once. For example, if 5 points appear on a line segment
 * in the order p→q→r→s→t, then it does not include the subsegments p→s or q→t.
 *
 * Corner cases.
 * Throws an IllegalArgumentException if the argument to the constructor is
 * null, if any point in the array is null, or if the argument to the
 * constructor contains a repeated point.
 *
 * Performance requirement.
 * The order of growth of the running time is n^2 * log(n) in the worst case
 * and uses space proportional to n plus the number of line segments returned.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Arrays;
import java.util.Comparator;

public class FastCollinearPoints {

    private int size = 0;   // Number of collinear segments
    private Point[] p;      // Copy of the input points
    private LineSegment[] result;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {

        validateInput(points);
        int n = p.length;

        // Corner case, 3 or less points
        if (n < 4) {
            result = new LineSegment[0];
            return;
        }

        // Resizing array that stores the line segments found during the search
        result = new LineSegment[2];

        // Search for 4 or more collinear points
        for (int i = 0; i < n; i++) {

            // Foe each point i, sort the rest of the points in order of their
            // slope with points[i]:
            // A point is less than another point if it's slope with points[i]
            // is less than the other point's slope with p[i]
            Comparator<Point> compareToThis = points[i].slopeOrder();

            // Arrays.sort(p, compareToThis);
            BUMergeSortX.sort(p, compareToThis);

            // Find any sets of 3 or more points that have equal slope with points[i]
            // The first point in the sorted array is always points[i], as the
            // slope with itsself is NEGATIVE_INFINITY by convention
            int counter = 0;
            for (int k = 1; k < n - 1; k++) {

                // Points p[i], p[k] and p[k+1] are collinear
                if (compareToThis.compare(p[k], p[k + 1]) == 0) {
                    counter++;
                    if (counter < 2) continue;
                }
                // Points p[i], p[k] and p[k+1] are not collinear
                // If less than 4 collinear points in current set, reset counter
                // and continue with next set of points
                else if (counter < 2) {
                    counter = 0;
                    continue;
                }

                // 4 or more collinear points found
                if (k + 1 == n - 1 || compareToThis.compare(p[k], p[k + 1]) != 0) {
                    // Find the two endpoints in order smallest -> largest
                    int lo = k - counter;
                    int hi = k + 1;
                    // If we've reached the end of the array, increment lo and hi
                    if (hi == n - 1 && compareToThis.compare(p[k], p[k + 1]) == 0) {
                        lo++;
                        hi++;
                    }
                    // Arrays.sort(p, lo, hi);
                    BUMergeSortX.sort(p, lo, hi);

                    // Add the line segment found only if the current point is
                    // the starting point, thus avoiding any duplicates
                    if (p[0].compareTo(p[lo]) < 0) {
                        // Resize the array if full
                        if (size == result.length) resize(2 * size);
                        // Add the newly found line segment
                        LineSegment newPair = new LineSegment(p[0], p[hi - 1]);
                        result[size++] = newPair;
                    }
                    // Reset counter
                    counter = 0;
                }
            }
        }
        // Resize the result array to the final length required to store the
        // line segments found
        resize(size);
    }

    // Helper method that validates the input
    private void validateInput(Point[] points) {

        if (points == null || points.length == 0)
            throw new IllegalArgumentException("At least 1 points must be provided");

        // Copy the input points, checking for null points
        int n = points.length;
        p = new Point[n];
        for (int i = 0; i < n; i++) {
            if (points[i] == null)
                throw new IllegalArgumentException("Point points[" + i + "] is null");
            p[i] = points[i];
        }

        // Check for duplicate points
        // Arrays.sort(p);
        BUMergeSortX.sort(p);
        for (int i = 0; i < n - 1; i++) {
            if (p[i].compareTo(p[i + 1]) == 0)
                throw new IllegalArgumentException("Duplicate points provided");
        }
    }

    // Resize the array of line segments
    private void resize(int newSize) {
        LineSegment[] copy = new LineSegment[newSize];
        for (int i = 0; i < size; i++) {
            copy[i] = result[i];
        }
        result = copy;
    }

    // the number of line segments
    public int numberOfSegments() {
        return size;
    }

    // the line segments
    public LineSegment[] segments() {
        return Arrays.copyOf(result, result.length);
    }

    // Unit testing
    public static void main(String[] args) {
        // Get the input points
        In pointsFile = new In(args[0]);
        Point[] points = new Point[pointsFile.readInt()];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(pointsFile.readInt(), pointsFile.readInt());
        }
        // Draw the input points
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(900, 900);
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.setXscale(-500, 33000);
        StdDraw.setYscale(-500, 33000);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();
        // Find the line segments containing 4 or more collinear points
        FastCollinearPoints lines = new FastCollinearPoints(points);
        // Draw the line segments
        StdDraw.setPenRadius(0.001);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (LineSegment line : lines.segments()) {
            System.out.println(line);
            line.draw();
        }
        System.out.println("Total lines found: " + lines.size);
        StdDraw.show();
    }
}
