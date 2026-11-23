package carl.lightrace;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
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

public class Level1 implements Initializable {
    @FXML private Canvas canvas;
    @FXML private Circle photon;
    @FXML private Label TimeLabel;
    @FXML private Label LevelLabel;
    @FXML private Button PlayButton;

    private static final double SCALE = 350.0 / 600.0;

    private static final LevelSystem levelSystem = new LevelSystem();
    private final PhysicsEngine physicsEngine = new PhysicsEngine();

    private List<Point2D> beamPath = null;
    private BeamAnimator animator;

    public static void setLevel(int index) {
        levelSystem.setCurrentLevelIndex(index);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadLevelUI();
    }

    private void loadLevelUI() {
        if (animator != null) {
            animator.stop();
            animator = null;
        }
        beamPath = null;
        photon.setVisible(false);
        PlayButton.setDisable(false);
        TimeLabel.setText("Time : 0.00s");
        LevelLabel.setText("Level : " + levelSystem.getCurrentLevel().getLevelNumber());
        redraw();
    }

    @FXML
    void startLight(ActionEvent e) {
        loadLevelUI();

        Level level = levelSystem.getCurrentLevel();
        Point2D start = new Point2D(level.getSourceX(), level.getSourceY());
        Point2D direction = new Point2D(level.getSourceDirX(), level.getSourceDirY());
        Point2D goal = new Point2D(level.getGoalX(), level.getGoalY());

        List<Point2D> fullPath = physicsEngine.simulate(
                start,
                direction,
                level.getInitialMirrors(),
                level.getInitialBlocks()
        );

        String bounceError = PlacementValidator.validateBounces(physicsEngine);

        PlayButton.setDisable(true);
        photon.setVisible(true);

        animator = new BeamAnimator(fullPath, level.getInitialBlocks(), goal, level.getGoalRad(),
                new BeamAnimator.Listener() {
                    @Override
                    public void onFrame(List<Point2D> drawn, double elapsed) {
                        beamPath = drawn;
                        Point2D head = drawn.get(drawn.size() - 1);
                        photon.setCenterX(head.getX() * SCALE);
                        photon.setCenterY(head.getY() * SCALE);
                        TimeLabel.setText(String.format("Time : %.2fs", elapsed));
                        redraw();
                    }

                    @Override
                    public void onFinished(boolean goalReached, double elapsed) {
                        PlayButton.setDisable(false);
                        if (goalReached) {
                            TimeLabel.setText(String.format("Time : %.2fs - Goal reached!", elapsed));
                        } else if (bounceError != null) {
                            showMessage(bounceError);
                        } else {
                            TimeLabel.setText(String.format("Time : %.2fs - Missed", elapsed));
                        }
                    }
                });
        animator.start();
    }

    @FXML
    void resetArea(ActionEvent e) {
        loadLevelUI();
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

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