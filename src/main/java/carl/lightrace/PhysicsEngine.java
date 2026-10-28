package carl.lightrace;

import javafx.geometry.Point2D;

public class PhysicsEngine {

    private static final double EPS = 1e-9;

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
