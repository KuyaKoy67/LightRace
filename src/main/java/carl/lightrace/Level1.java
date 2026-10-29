package carl.lightrace;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

public class Level1 implements Initializable {
    @FXML private Canvas canvas;
    @FXML private Circle photon;

    private static final double SCALE = 350.0 / 600.0;

    private static final LevelSystem levelSystem = new LevelSystem();
    private final PhysicsEngine physicsEngine = new PhysicsEngine();

    private List<Point2D> beamPath = null;
    private PathTransition animation;


    public static void setLevel(int index) {
        levelSystem.setCurrentLevelIndex(index);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadLevelUI();
    }

    private void loadLevelUI() {
        if (animation != null) {
            animation.stop();
        }
        beamPath = null;
        photon.setVisible(false);
        redraw();
    }

    /**
     * Handler of the PLAY button. Walks along the beam in steps of 5 units and
     * stops at the goal (or at the edge of the grid). Then the photon is moved
     * along that path.
     * @param e the button click event
     */
    @FXML
    void startLight(ActionEvent e) {
        loadLevelUI();

        Level level = levelSystem.getCurrentLevel();
        Point2D start = new Point2D(level.getSourceX(), level.getSourceY());
        Point2D direction = new Point2D(level.getSourceDirX(), level.getSourceDirY());
        Point2D goal = new Point2D(level.getGoalX(), level.getGoalY());

        List<Point2D> raw = physicsEngine.simulate(start, direction, level.getInitialMirrors());
        Point2D end = raw.get(1);
        double length = start.distance(end);

        beamPath = new ArrayList<>();
        beamPath.add(start);
        for (double d = 5; d <= length; d += 5) {
            Point2D p = start.add(end.subtract(start).multiply(d / length));
            if (p.getX() > 600 || p.getY() > 600 || p.getX() < 0 || p.getY() < 0) {
                break;
            }
            beamPath.add(p);
            if (p.distance(goal) <= level.getGoalRad()) {
                break;
            }
        }

        Polyline track = new Polyline();
        for (Point2D p : beamPath) {
            track.getPoints().addAll(p.getX() * SCALE, p.getY() * SCALE);
        }

        redraw();
        photon.setVisible(true);
        animation = new PathTransition(Duration.seconds(2), track, photon);
        animation.play();
    }

    /**
     * Handler of the RESET button. Puts the level back to its starting state.
     * @param e the button click event
     */
    @FXML
    void resetArea(ActionEvent e) {
        loadLevelUI();
    }

    /**
     * Draws the grid, the goal, the source and the beam (if there is one).
     */
    private void redraw() {
        Level level = levelSystem.getCurrentLevel();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.save();
        gc.scale(SCALE, SCALE);

        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        for (double g = 0; g <= 600; g += 50) {
            gc.strokeLine(g, 0, g, 600);
            gc.strokeLine(0, g, 600, g);
        }

        double r = level.getGoalRad();
        gc.setFill(Color.LIMEGREEN);
        gc.fillOval(level.getGoalX() - r, level.getGoalY() - r, 2 * r, 2 * r);
        gc.setFill(Color.RED);
        gc.fillOval(level.getSourceX() - 12, level.getSourceY() - 12, 24, 24);

        if (beamPath != null) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(3);
            for (int i = 0; i < beamPath.size() - 1; i++) {
                Point2D a = beamPath.get(i);
                Point2D b = beamPath.get(i + 1);
                gc.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
            }
        }

        gc.restore();
    }
}