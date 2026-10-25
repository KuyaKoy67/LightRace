package carl.lightrace;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {
    @Getter private int levelNumber;
    @Getter private double sourceX;
    @Getter private double sourceY;
    @Getter private double sourceDirX;
    @Getter private double sourceDirY;
    @Getter private double goalX;
    @Getter private double goalY;
    @Getter private double goalRad;
    private List<Mirror> initialMirrors;
    private Map<String, Integer> availableTools;

    public Level(int levelNumber, int sourceX, int sourceY, int sourceDirX,
                 int goalX, int sourceDirY, int goalY, int goalRad,
                 List<Mirror> initialMirrors, Map<String, Integer> availableTools) {
        this.levelNumber = levelNumber;
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
}
