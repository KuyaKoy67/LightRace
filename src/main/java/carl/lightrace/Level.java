package carl.lightrace;

import javafx.geometry.Point2D;
import lombok.Getter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Getter
public class Level {
    private int levelNumber;
    private String title;
    private double sourceX;
    private double sourceY;
    private double sourceDirX;
    private double sourceDirY;
    private double goalX;
    private double goalY;
    private double goalRad;
    private List<Mirror> initialMirrors;
    private Map<String, Integer> availableTools;

    public Level(int levelNumber, String title,
                 double sourceX, double sourceY,
                 double sourceDirX, double sourceDirY,
                 double goalX, double goalY, double goalRadius,
                 List<Mirror> initialMirrors, Map<String, Integer> availableTools) {

        this.levelNumber = levelNumber;
        this.title = title;
        this.sourceX = sourceX;
        this.sourceY = sourceY;

        double length = Math.hypot(sourceDirX, sourceDirY);
        if (length > 0) {
            this.sourceDirX = sourceDirX / length;
            this.sourceDirY = sourceDirY / length;
        } else {
            this.sourceDirX = 1.0;
            this.sourceDirY = 0.0;
        }

        this.goalX = goalX;
        this.goalY = goalY;
        this.goalRad = goalRadius;

        this.initialMirrors = initialMirrors != null ? List.copyOf(initialMirrors) : List.of();
        this.availableTools = availableTools != null ? new HashMap<>(availableTools) : new HashMap<>();
    }

    /**
     * Checks whether any line segment along the light beam's path passes within
     * the goal's radius tolerance.
     */
    public boolean checkGoalDetection(LightBeam beam) {
        if (beam == null || beam.getPath() == null || beam.getPath().isEmpty()) {
            return false;
        }

        List<Point2D> path = beam.getPath();
        Point2D goalCenter = new Point2D(goalX, goalY);

        if (path.size() == 1) {
            return path.get(0).distance(goalCenter) <= goalRad;
        }

        for (int i = 0; i < path.size() - 1; i++) {
            Point2D a = path.get(i);
            Point2D b = path.get(i + 1);

            if (isSegmentNearPoint(a, b, goalCenter, goalRad)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSegmentNearPoint(Point2D a, Point2D b, Point2D p, double radius) {
        double abX = b.getX() - a.getX();
        double abY = b.getY() - a.getY();
        double apX = p.getX() - a.getX();
        double apY = p.getY() - a.getY();

        double abLenSq = abX * abX + abY * abY;
        if (abLenSq == 0.0) {
            return a.distance(p) <= radius;
        }

        double t = (apX * abX + apY * abY) / abLenSq;
        t = Math.max(0.0, Math.min(1.0, t));

        double closestX = a.getX() + t * abX;
        double closestY = a.getY() + t * abY;

        double distanceSq = Math.pow(p.getX() - closestX, 2) + Math.pow(p.getY() - closestY, 2);
        return distanceSq <= (radius * radius);
    }

    public List<Mirror> getInitialMirrors() {
        return Collections.unmodifiableList(initialMirrors);
    }

    public Map<String, Integer> getAvailableTools() {
        return Collections.unmodifiableMap(availableTools);
    }
}