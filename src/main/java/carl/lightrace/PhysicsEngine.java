package carl.lightrace;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEngine {

    private static final int MAX_BOUNCES = 50;
    private static final double GRID_EDGE_DISTANCE = 600.0;
    private static final double C = 1.0;

    /**
     * Simulates the light beam's path through the level, handling both mirrors and element blocks.
     *
     * @param start starting coordinate of the beam
     * @param direction initial direction unit vector
     * @param mirrors list of mirrors in the level
     * @param blocks list of water/glass element blocks in the level
     * @return list of points representing the full path of the beam
     */
    public List<Point2D> simulate(Point2D start, Point2D direction, List<Mirror> mirrors, List<ElementBlock> blocks) {
        List<Point2D> path = new ArrayList<>();
        path.add(start);

        Point2D origin = start;
        Point2D dir = direction.normalize();

        for (int i = 0; i < MAX_BOUNCES; i++) {
            double closestT = Double.POSITIVE_INFINITY;
            Mirror closestMirror = null;
            ElementBlock closestBlock = null;

            if (mirrors != null) {
                for (Mirror mirror : mirrors) {
                    double t = mirror.intersect(origin, dir);
                    if (t < closestT) {
                        closestT = t;
                        closestMirror = mirror;
                        closestBlock = null;
                    }
                }
            }

            // 2. Check intersection with all element blocks (water/glass)
            if (blocks != null) {
                for (ElementBlock block : blocks) {
                    double t = block.intersect(origin, dir);
                    if (t < closestT) {
                        closestT = t;
                        closestBlock = block;
                        closestMirror = null; // Clear mirror if block is closer
                    }
                }
            }

            if (closestMirror == null && closestBlock == null) {
                path.add(origin.add(dir.multiply(GRID_EDGE_DISTANCE)));
                break;
            }

            Point2D hit = origin.add(dir.multiply(closestT));
            path.add(hit);

            if (closestMirror != null) {
                dir = closestMirror.reflect(dir, hit);
            } else if (closestBlock != null) {
                dir = closestBlock.refract(dir, hit);
            }

            origin = hit;
        }

        return path;
    }

    public double getTotalDistance(List<Point2D> path) {
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += path.get(i).distance(path.get(i + 1));
        }
        return total;
    }

    public double getTravelTime(List<Point2D> path) {
        return getTotalDistance(path) / C;
    }
}