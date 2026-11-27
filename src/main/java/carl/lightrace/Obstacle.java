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
        double minX = position.getX() - width / 2.0;
        double maxX = position.getX() + width / 2.0;
        double minY = position.getY() - height / 2.0;
        double maxY = position.getY() + height / 2.0;

        double entryDistance = Double.NEGATIVE_INFINITY;
        double exitDistance = Double.POSITIVE_INFINITY;

        if (Math.abs(direction.getX()) < tolerance) {
            if (origin.getX() < minX || origin.getX() > maxX) {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            double distanceToLeft = (minX - origin.getX()) / direction.getX();
            double distanceToRight = (maxX - origin.getX()) / direction.getX();
            entryDistance = Math.max(entryDistance, Math.min(distanceToLeft, distanceToRight));
            exitDistance = Math.min(exitDistance, Math.max(distanceToLeft, distanceToRight));
        }

        if (Math.abs(direction.getY()) < tolerance) {
            if (origin.getY() < minY || origin.getY() > maxY) {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            double distanceToTop = (minY - origin.getY()) / direction.getY();
            double distanceToBottom = (maxY - origin.getY()) / direction.getY();
            entryDistance = Math.max(entryDistance, Math.min(distanceToTop, distanceToBottom));
            exitDistance = Math.min(exitDistance, Math.max(distanceToTop, distanceToBottom));
        }

        if (exitDistance < 0 || entryDistance > exitDistance) {
            return Double.POSITIVE_INFINITY;
        }
        return entryDistance > tolerance ? entryDistance : (exitDistance > tolerance ? exitDistance : Double.POSITIVE_INFINITY);
    }
}
