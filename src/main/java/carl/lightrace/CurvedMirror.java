package carl.lightrace;

import javafx.geometry.Point2D;

public class CurvedMirror extends Mirror {
    private double curvatureRadius; // positive for convex, negative for concave
    private Point2D centerOfCurvature;

    public CurvedMirror(Point2D position, double angle, double length, double curvatureRadius) {
        super(position, angle, length);
        this.curvatureRadius = curvatureRadius;

        // Compute the center of curvature based on position, angle, and radius
        Point2D normal = new Point2D(Math.cos(angle + Math.PI / 2), Math.sin(angle + Math.PI / 2));
        this.centerOfCurvature = position.add(normal.multiply(curvatureRadius));
    }

    /**
     * Ray-circle intersection adapted for a curved mirror arc.
     */
    @Override
    public double intersect(Point2D origin, Point2D direction) {
        Point2D l = origin.subtract(centerOfCurvature);

        double b = 2.0 * direction.dotProduct(l);
        double c = l.dotProduct(l) - (curvatureRadius * curvatureRadius);

        double discriminant = (b * b) - (4.0 * c);
        if (discriminant < 0) {
            return Double.POSITIVE_INFINITY;
        }

        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = (-b - sqrtDisc) / 2.0;
        double t2 = (-b + sqrtDisc) / 2.0;

        double t = Double.POSITIVE_INFINITY;
        if (t1 > 1e-9) t = t1;
        else if (t2 > 1e-9) t = t2;

        if (t == Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        }

        // Verify that the intersection point actually lies within the mirror's physical length/span
        Point2D hitPoint = origin.add(direction.multiply(t));
        if (hitPoint.distance(position) > length / 2.0) {
            return Double.POSITIVE_INFINITY;
        }

        return t;
    }

    /**
     * Computes the local surface normal at the exact hit point..
     */
    @Override
    public Point2D reflect(Point2D direction, Point2D hitPoint) {
        // 1. Compute local normal at the contact point (from center of curvature to hitPoint)
        Point2D normal = hitPoint.subtract(centerOfCurvature).normalize();

        // 2. Ensure the normal faces opposing the incoming beam direction
        if (direction.dotProduct(normal) > 0) {
            normal = normal.multiply(-1.0);
        }

        // 3. Apply vector reflection formula: r = d - 2(d * n)n
        double dot = direction.dotProduct(normal);
        Point2D scaledNormal = normal.multiply(2.0 * dot);
        return direction.subtract(scaledNormal);
    }
}
