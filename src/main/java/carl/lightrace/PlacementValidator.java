package carl.lightrace;

import javafx.geometry.Point2D;

import java.util.List;

public class PlacementValidator {
    public static final double GRID_SIZE = 600.0;

    private static final double MIN_GAP = 2.0;

    private static final double SOURCE_CLEARANCE = 15.0;

    public static String validateToolAvailable(Level level, String toolName, int alreadyPlaced) {
        Integer available = level.getAvailableTools().get(toolName);
        if (available == null || alreadyPlaced >= available) {
            return "No more " + toolName + " pieces left in the toolbox.";
        }
        return null;
    }

    public static String validateMirror(Mirror candidate, Level level,
                                        List<Mirror> placedMirrors, List<ElementBlock> placedBlocks) {
        Point2D a = candidate.getStart();
        Point2D b = candidate.getEnd();

        if (!inGrid(a) || !inGrid(b)) {
            return "The mirror must stay inside the grid.";
        }

        Point2D source = new Point2D(level.getSourceX(), level.getSourceY());
        Point2D goal = new Point2D(level.getGoalX(), level.getGoalY());
        if (pointSegmentDistance(source, a, b) < SOURCE_CLEARANCE) {
            return "The mirror cannot cover the laser source.";
        }
        if (pointSegmentDistance(goal, a, b) < level.getGoalRad()) {
            return "The mirror cannot cover the goal.";
        }
        return "";// incomplete
    }

    private static boolean inGrid(Point2D p) {
        return p.getX() >= 0 && p.getX() <= GRID_SIZE && p.getY() >= 0 && p.getY() <= GRID_SIZE;
    }

    private static double pointSegmentDistance(Point2D p, Point2D a, Point2D b) {
        Point2D ab = b.subtract(a);
        double lenSq = ab.dotProduct(ab);
        if (lenSq == 0.0) {
            return p.distance(a);
        }
        double t = Math.max(0.0, Math.min(1.0, p.subtract(a).dotProduct(ab) / lenSq));
        return p.distance(a.add(ab.multiply(t)));
    }
}
