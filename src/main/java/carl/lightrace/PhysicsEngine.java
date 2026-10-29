package carl.lightrace;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {

    private static final double EPS = 1e-9;
    private static final int MAX_BOUNCES = 50;
    private static final double GRID_EDGE_DISTANCE = 600.0;
    private static final double C = 1.0;

    /**
     * it does the math and returns a list of points.
     * @param start the start coordinate
     * @param direction the direction unit vector
     * @param mirrors the list of mirrors detected
     * @return the list of points
     */
    public List<Point2D> simulate(Point2D start, Point2D direction, List<Mirror> mirrors) {
        List<Point2D> path = new ArrayList<>();
        path.add(start);

        Point2D origin = start;
        Point2D dir = direction.normalize();

        for (int i = 0; i < MAX_BOUNCES; i++) {
            double closestT = Double.POSITIVE_INFINITY;
            Mirror closestMirror = null;

            for (Mirror mirror : mirrors) {
                double t = intersect(origin, dir, mirror);
                if (t < closestT) {
                    closestT = t;
                    closestMirror = mirror;
                }
            }

            if (closestMirror == null) {
                path.add(origin.add(dir.multiply(GRID_EDGE_DISTANCE)));
                break;
            }

            Point2D hit = origin.add(dir.multiply(closestT));
            path.add(hit);
            dir = closestMirror.reflect(dir);
            origin = hit;
        }

        return path;
    }

    /**
     * gets total distance of the beam's path
     * @param path the beam path
     * @return the total distance
     */
    public double getTotalDistance(List<Point2D> path) {
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += path.get(i).distance(path.get(i + 1));
        }
        return total;
    }

    /**
     * gets travel time of the beam
     * @param path the path
     * @return the travel time of the beam
     */
    public double getTravelTime(List<Point2D> path) {
        return getTotalDistance(path) / C;
    }

    /**
     * Finds how far a beam travels before hitting a mirror
     * @param origin where the beam starts
     * @param direction unit vector of the beam
     * @param mirror the mirror to test
     * @return distance t to the hit, or Double.POSITIVE_INFINITY if no hit
     */
    public double intersect(Point2D origin, Point2D direction, Mirror mirror) {
        Point2D a = mirror.getStart();
        Point2D e = mirror.getEnd().subtract(a);
        Point2D w = a.subtract(origin);

        double denominator = cross(direction, e);
        if (Math.abs(denominator) < EPS) {
            return Double.POSITIVE_INFINITY;
        }

        double t = cross(w, e) / denominator;
        double s = cross(w, direction) / denominator;

        if (t < EPS || s < 0 || s > 1) {
            return Double.POSITIVE_INFINITY;
        }
        return t;
    }

    /**
     * helper method that crosses two vectors and returns a cross product. This is useful in the case of calculating
     * unknown variables in vectors.
     * @param a the first vector
     * @param b the second vector
     * @return the cross product
     */
    private double cross(Point2D a, Point2D b) {
        return a.getX() * b.getY() - a.getY() * b.getX();
    }
}