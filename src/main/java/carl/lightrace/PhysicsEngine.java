package carl.lightrace;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {

    private static final int MAX_BOUNCES = 50;
    private static final double GRID_EDGE_DISTANCE = 600.0;
    private static final double C = 1.0;

    /**
     * Simulates the light beam's path through the level.
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
                double t = mirror.intersect(origin, dir);
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
            dir = closestMirror.reflect(dir, hit);
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
}