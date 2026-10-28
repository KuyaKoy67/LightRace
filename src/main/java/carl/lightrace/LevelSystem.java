package carl.lightrace;

import javafx.geometry.Point2D;
import lombok.Getter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelSystem {
    private final List<Level> levels = new ArrayList<>();

    @Getter
    private int currentIndex = 0;

    public LevelSystem() {
        initializeMirrorOnlyLevels();
    }

    /**
     * Helper method to initialize the mirror-only levels for Build 1.
     */
    private void initializeMirrorOnlyLevels() {
        List<Mirror> level1Mirrors = new ArrayList<>();
        Map<String, Integer> level1Tools = new HashMap<>();
        level1Tools.put("StraightMirror", 1);

        Level level1 = new Level(
                1,
                "Straight Shot",
                50.0, 200.0,
                1.0, 0.0,
                550.0, 200.0,
                25.0,
                level1Mirrors,
                level1Tools
        );

        List<Mirror> level2Mirrors = new ArrayList<>();
        Map<String, Integer> level2Tools = new HashMap<>();
        level2Tools.put("StraightMirror", 2);

        Level level2 = new Level(
                2,
                "Corner Turn",
                50.0, 350.0,
                1.0, 0.0,
                550.0, 50.0,
                25.0,
                level2Mirrors,
                level2Tools
        );

        List<Mirror> level3Mirrors = new ArrayList<>();
        level3Mirrors.add(new StraightMirror(new Point2D(300.0, 300.0), Math.PI / 4, 100));

        Map<String, Integer> level3Tools = new HashMap<>();
        level3Tools.put("StraightMirror", 2);

        Level level3 = new Level(
                3,
                "The Preset Obstacle",
                50.0, 50.0,
                0.0, 1.0,
                550.0, 550.0,
                25.0,
                level3Mirrors,
                level3Tools
        );

        levels.add(level1);
        levels.add(level2);
        levels.add(level3);
    }

    /**
     * Returns the currently active Level instance.
     */
    public Level getCurrentLevel() {
        if (levels.isEmpty() || currentIndex < 0 || currentIndex >= levels.size()) {
            return null;
        }
        return levels.get(currentIndex);
    }

    /**
     * Advances to the next level if available.
     */
    public boolean nextLevel() {
        if (currentIndex < levels.size() - 1) {
            currentIndex++;
            return true;
        }
        return false;
    }

    /**
     * Returns to the previous level if available.
     */
    public boolean previousLevel() {
        if (currentIndex > 0) {
            currentIndex--;
            return true;
        }
        return false;
    }

    public void setCurrentLevelIndex(int index) {
        if (index >= 0 && index < levels.size()) {
            currentIndex = index;
        }
    }

    public int getTotalLevels() {
        return levels.size();
    }

    public List<Level> getLevels() {
        return Collections.unmodifiableList(levels);
    }
}