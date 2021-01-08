/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 01/05/2020
 *
 * Description:
 * BruteCollinearPoints.java examines 4 points at a time and checks whether
 * they all lie on the same line segment, returning all such line segments.
 * To check whether the 4 points p, q, r, and s are collinear, we check whether
 * the three slopes between p and q, between p and r, and between p and s
 * are all equal.
 *
 * The method segments() includes each line segment containing 4 points exactly
 * once. If 4 points appear on a line segment in the order p→q→r→s, then it
 * includes either the line segment p→s or s→p (but not both) and it does not
 * include subsegments such as p→r or q→r. For simplicity, we assume no input to
 * BruteCollinearPoints that has 5 or more collinear points.
 *
 * Corner cases.
 * Throws an IllegalArgumentException if the argument to the constructor is
 * null, if any point in the array is null, or if the argument to the
 * constructor contains a repeated point.
 *
 * Performance requirement.
 * The order of growth of the running time should be n^4 in the worst case and
 * it should use space proportional to n plus the number of line segments
 * returned.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Arrays;

public class BruteCollinearPoints {

    private int size = 0;
    private Point[] p;
    private PointPair[] pointPairs;
    private final LineSegment[] result;


    private class PointPair implements Comparable<PointPair> {
        private final Point[] pPair;

        public PointPair(Point a, Point b) {
            pPair = new Point[] { a, b };
        }

        public LineSegment toLineSegment() {
            return new LineSegment(pPair[0], pPair[1]);
        }

        public int compareTo(PointPair that) {
            if (this.pPair[0].compareTo(that.pPair[0]) < 0) return -1;
            else if (this.pPair[0].compareTo(that.pPair[0]) > 0) return 1;
            else if (this.pPair[1].compareTo(that.pPair[1]) < 0) return -1;
            else if (this.pPair[1].compareTo(that.pPair[1]) > 0) return 1;
            else return 0;
        }
    }

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {

        validateInput(points);
        int n = p.length;

        // Corner case, 3 or less points
        if (n < 4) {
            result = new LineSegment[0];
            return;
        }

        pointPairs = new PointPair[2];

        for (int i = 0; i < n - 3; i++) {
            Point a = p[i];

            for (int j = i + 1; j < n - 2; j++) {
                Point b = p[j];
                double slopeAB = a.slopeTo(b);

                for (int k = j + 1; k < n - 1; k++) {
                    Point c = p[k];
                    double slopeBC = b.slopeTo(c);
                    // The 3 points aren't collinear
                    if (slopeAB != slopeBC) continue;

                    for (int m = k + 1; m < n; m++) {
                        Point d = p[m];
                        double slopeCD = c.slopeTo(d);
                        // The 4 points aren't collinear
                        if (slopeBC != slopeCD) continue;

                        // The 4 points are collinear
                        PointPair newPair = segment(a, b, c, d);
                        // Step 1: resize the array if full
                        if (size == pointPairs.length) resize(2 * size);
                        // Step 2: Add the newly found line segment
                        pointPairs[size++] = newPair;
                    }
                }
            }
        }

        // Remove duplicate elements and update size
        if (size >= 2) {
            Arrays.sort(pointPairs, 0, size);
            int uniqueLines = 1;
            for (int i = 0; i < size - 1; i++) {
                if (pointPairs[i].compareTo(pointPairs[i + 1]) != 0) {
                    uniqueLines++;
                }
                else {
                    pointPairs[i] = null;
                }
            }
            size = uniqueLines;
        }
        result = new LineSegment[size];
        int j = 0;
        for (int i = 0; i < pointPairs.length; i++) {
            if (pointPairs[i] == null) continue;
            result[j++] = pointPairs[i].toLineSegment();
        }
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
        Arrays.sort(p);
        for (int i = 0; i < n - 1; i++) {
            if (p[i].compareTo(p[i + 1]) == 0)
                throw new IllegalArgumentException("Duplicate points provided");
        }
    }

    // Returns the start and end points of a segment of 4 collinear points
    private PointPair segment(Point a, Point b, Point c, Point d) {
        Point[] abcd = { a, b, c, d };
        Arrays.sort(abcd);
        return new PointPair(abcd[0], abcd[3]);
    }

    // Resize the array of line segments
    private void resize(int newSize) {
        PointPair[] copy = new PointPair[newSize];
        for (int i = 0; i < size; i++) {
            copy[i] = pointPairs[i];
        }
        pointPairs = copy;
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
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.setXscale(-500, 33000);
        StdDraw.setYscale(-500, 33000);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();
        // Find the line segments containing 4 or more collinear points
        BruteCollinearPoints lines = new BruteCollinearPoints(points);
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
