/* ****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date:  22/05/2020
 *
 * Description:
 * PointSET.java represents a set of points in the unit square by using a
 * red–black BST
 *
 * Corner cases.
 * Throws an IllegalArgumentException if any argument is null.
 *
 * Performance requirements.
 * The implementation should support insert() and contains() in time
 * proportional to the logarithm of the number of points in the set in the
 * worst case; it should support nearest() and range() in time proportional to
 * the number of points in the set.
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.TreeSet;

public class PointSET {

    private TreeSet<Point2D> points;

    // construct an empty set of points
    public PointSET() {
        points = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException();

        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : points)
            StdDraw.point(p.x(), p.y());
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException();

        Queue<Point2D> pointsInside = new Queue<>();

        for (Point2D p : points) {
            if (rect.contains(p))
                pointsInside.enqueue(p);
        }
        return pointsInside;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D queryP) {
        if (queryP == null)
            throw new IllegalArgumentException();
        if (points.isEmpty())
            return null;

        Point2D nearest = null;
        double nearestDistance = Double.POSITIVE_INFINITY;

        for (Point2D p : points) {
            double distance = p.distanceSquaredTo(queryP);
            if (distance < nearestDistance) {
                nearest = p;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    // unit testing of the methods
    public static void main(String[] args) {

    }
}
