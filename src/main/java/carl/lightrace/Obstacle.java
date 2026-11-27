package carl.lightrace;

import javafx.geometry.Point2D;
import lombok.Getter;

@Getter
public class Obstacle {
    private final Point2D position; // center of the wall
    private final double width;
    private final double height;
    private static final double tolerance = 0.000001;

    public Obstacle(Point2D position, double width, double height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    public double intersect(Point2D origin, Point2D direction) {
        double left   = position.getX() - width / 2.0;
        double right  = position.getX() + width / 2.0;
        double top    = position.getY() - height / 2.0;
        double bottom = position.getY() + height / 2.0;

        double entryDistance = Double.NEGATIVE_INFINITY;
        double exitDistance  = Double.POSITIVE_INFINITY;

        if (Math.abs(direction.getX()) < tolerance) {
            if (origin.getX() < left || origin.getX() > right) {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            double distToLeft  = (left  - origin.getX()) / direction.getX();
            double distToRight = (right - origin.getX()) / direction.getX();
            entryDistance = Math.max(entryDistance, Math.min(distToLeft, distToRight));
            exitDistance  = Math.min(exitDistance,  Math.max(distToLeft, distToRight));
        }

        if (Math.abs(direction.getY()) < tolerance) {
            if (origin.getY() < top || origin.getY() > bottom) {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            double distToTop    = (top    - origin.getY()) / direction.getY();
            double distToBottom = (bottom - origin.getY()) / direction.getY();
            entryDistance = Math.max(entryDistance, Math.min(distToTop, distToBottom));
            exitDistance  = Math.min(exitDistance,  Math.max(distToTop, distToBottom));
        }

        if (exitDistance < 0 || entryDistance > exitDistance) {
            return Double.POSITIVE_INFINITY;
        }

        return entryDistance > tolerance ? entryDistance
                : (exitDistance > tolerance ? exitDistance : Double.POSITIVE_INFINITY);
    }
}
