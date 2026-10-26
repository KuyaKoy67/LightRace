package carl.lightrace;

import javafx.geometry.Point2D;
import lombok.Getter;

import java.util.*;

public class Level {
    @Getter private int levelNumber;
    @Getter private String title;
    @Getter private double sourceX;
    @Getter private double sourceY;
    @Getter private double sourceDirX;
    @Getter private double sourceDirY;
    @Getter private double goalX;
    @Getter private double goalY;
    @Getter private double goalRad;
    private List<Mirror> initialMirrors;
    private Map<String, Integer> availableTools;

    public Level(int levelNumber, String title, double sourceX, double sourceY, double sourceDirX,
                 double goalX, double sourceDirY, double goalY, double goalRad,
                 List<Mirror> initialMirrors, Map<String, Integer> availableTools) {
        this.levelNumber = levelNumber;
        this.title = title;
        this.sourceX = sourceX;
        this.sourceY = sourceY;

        double length = Math.hypot(sourceDirX,  sourceDirY);
        if (length > 0) {
            this.sourceDirX = (sourceDirX / length);
            this.sourceDirY = (sourceDirY / length);
        } else {
            this.sourceDirX = 1.0;
            this.sourceDirY = 0.0;
        }

        this.goalX = goalX;
        this.goalY = goalY;
        this.goalRad = goalRad;

        this.initialMirrors = initialMirrors != null ? List.copyOf(initialMirrors) : List.of();
        this.availableTools = availableTools != null ? new HashMap<>(availableTools) : new HashMap<>();
    }

    /**
     * Checks whether any point along the light beam's path reaches
     * within the goal's radius tolerance.
     */
    public boolean checkGoalDetection(LightBeam beam) {
        if (beam == null || beam.getPath() == null || beam.getPath().isEmpty()) {
            return false;
        }

        for (Point2D point : beam.getPath()) {
            double distance = Math.hypot(point.getX() - goalX, point.getY() - goalY);
            if (distance <= goalRad) {
                return true;
            }
        }
        return false;
    }

    public List<Mirror> getInitialMirrors() {
        return Collections.unmodifiableList(initialMirrors);
    }

    public Map<String, Integer> getAvailableTools() {
        return Collections.unmodifiableMap(availableTools);
    }
}
