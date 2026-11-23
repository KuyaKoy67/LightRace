package carl.lightrace;

import javafx.geometry.Point2D;

import java.util.ArrayList;
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

        List<Mirror> allMirrors = new ArrayList<>(level.getInitialMirrors());
        allMirrors.addAll(placedMirrors);
        for (Mirror other : allMirrors) {
            if (segmentDistance(a, b, other.getStart(), other.getEnd()) < MIN_GAP) {
            }
        }

        List<ElementBlock> allBlocks = new ArrayList<>(level.getInitialBlocks());
        allBlocks.addAll(placedBlocks);
        for (ElementBlock block : allBlocks) {
            if (segmentTouchesRect(a, b, rect(block))) {
                return "The mirror overlaps a water/glass block.";
            }
        }
        return null;
    }

    public static String validateBlock(ElementBlock candidate, Level level,
                                       List<Mirror> placedMirrors, List<ElementBlock> placedBlocks) {
        double[] r = rect(candidate);

        if (r[0] < 0 || r[1] < 0 || r[2] > GRID_SIZE || r[3] > GRID_SIZE) {
            return "The block must stay inside the grid.";
        }

        Point2D source = new Point2D(level.getSourceX(), level.getSourceY());
        Point2D goal = new Point2D(level.getGoalX(), level.getGoalY());
        if (circleTouchesRect(source, SOURCE_CLEARANCE, r)) {
            return "The block cannot cover the laser source.";
        }
        if (circleTouchesRect(goal, level.getGoalRad(), r)) {
            return "The block cannot cover the goal.";
        }

        return ""; //unfinished
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

    private static double[] rect(ElementBlock b) {
        double halfW = b.getWidth() / 2.0;
        double halfH = b.getHeight() / 2.0;
        double[] r = new double[4];
        r[0] = b.getPosition().getX() - halfW;
        r[1] = b.getPosition().getY() - halfH;
        r[2] = b.getPosition().getX() + halfW;
        r[3] = b.getPosition().getY() + halfH;
        return r;
    }

    private static boolean insideRect(Point2D p, double[] r) {
        return p.getX() >= r[0] && p.getX() <= r[2] && p.getY() >= r[1] && p.getY() <= r[3];
    }

    private static boolean segmentTouchesRect(Point2D a, Point2D b, double[] r) {
        if (insideRect(a, r) || insideRect(b, r)) {
            return true;
        }
        Point2D topLeft = new Point2D(r[0], r[1]);
        Point2D topRight = new Point2D(r[2], r[1]);
        Point2D bottomRight = new Point2D(r[2], r[3]);
        Point2D bottomLeft = new Point2D(r[0], r[3]);
        return crosses(a, b, topLeft, topRight) || crosses(a, b, topRight, bottomRight)
                || crosses(a, b, bottomRight, bottomLeft) || crosses(a, b, bottomLeft, topLeft);
    }

    private static double segmentDistance(Point2D a, Point2D b, Point2D c, Point2D d) {
        if (crosses(a, b, c, d)) {
            return 0.0;
        }
        double d1 = Math.min(pointSegmentDistance(a, c, d), pointSegmentDistance(b, c, d));
        double d2 = Math.min(pointSegmentDistance(c, a, b), pointSegmentDistance(d, a, b));
        return Math.min(d1, d2);
    }

    private static double cross(Point2D o, Point2D a, Point2D b) {
        return (a.getX() - o.getX()) * (b.getY() - o.getY())
                - (a.getY() - o.getY()) * (b.getX() - o.getX());
    }

    private static boolean crosses(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
        double d1 = cross(p3, p4, p1);
        double d2 = cross(p3, p4, p2);
        double d3 = cross(p1, p2, p3);
        double d4 = cross(p1, p2, p4);
        return ((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0))
                && ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0));
    }

    private static boolean circleTouchesRect(Point2D c, double radius, double[] r) {
        double cx = Math.max(r[0], Math.min(c.getX(), r[2]));
        double cy = Math.max(r[1], Math.min(c.getY(), r[3]));
        return c.distance(cx, cy) <= radius;
    }
}
