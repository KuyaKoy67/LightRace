package carl.lightrace;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;


public class Level1 implements Initializable {

    @FXML private Canvas canvas;

    @FXML private Circle photon;

    @FXML private Label LevelLabel;
    @FXML private Label TimeLabel;

    @FXML private Button PlayButton;

    private static final double SCALE = 350.0 / 600.0;

    private static final double SPEED = 200.0;

    private static final LevelSystem levelSystem = new LevelSystem();

    private final PhysicsEngine physicsEngine = new PhysicsEngine();

    private List<Point2D> beamPath = null;

    private PathTransition animation;

    private boolean running = false;

    private boolean won = false;

    public static void setLevel(int index) {
        levelSystem.setCurrentLevelIndex(index);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadLevelUI();
    }

    private void loadLevelUI() {
        Level level = levelSystem.getCurrentLevel();
        LevelLabel.setText("Lvl " + level.getLevelNumber() + ": " + level.getTitle());
        TimeLabel.setText("Time : 0.0s");
        beamPath = null;
        photon.setVisible(false);
        redraw();
    }

    /**
     * Follows the laser from the source and stops it when it touches the goal.
     * The path from the physics engine is walked in steps of 5 units. At each
     * step we check if the point is inside the goal circle. The beam also stops
     * at the edge of the grid so it does not run forever.
     * @param level the level being played
     * @return the points of the beam, ending at the goal if it was reached
     */
    private List<Point2D> traceBeam(Level level) {
        Point2D start = new Point2D(level.getSourceX(), level.getSourceY());
        Point2D direction = new Point2D(level.getSourceDirX(), level.getSourceDirY());
        Point2D goal = new Point2D(level.getGoalX(), level.getGoalY());

        List<Mirror> mirrors = new ArrayList<>(level.getInitialMirrors());
        List<Point2D> raw = physicsEngine.simulate(start, direction, mirrors);

        List<Point2D> path = new ArrayList<>();
        path.add(start);
        won = false;
        boolean done = false;

        for (int i = 0; i < raw.size() - 1 && !done; i++) {
            Point2D a = raw.get(i);
            Point2D b = raw.get(i + 1);
            double length = a.distance(b);

            for (double d = 5; d <= length && !done; d += 5) {
                Point2D p = a.add(b.subtract(a).multiply(d / length));

                if (p.getX() < 0 || p.getX() > 600 || p.getY() < 0 || p.getY() > 600) {
                    done = true;
                } else {
                    path.add(p);
                    if (p.distance(goal) <= level.getGoalRad()) {
                        won = true;
                        done = true;
                    }
                }
            }

            if (!done) {
                path.add(b);
            }
        }
        return path;
    }
}