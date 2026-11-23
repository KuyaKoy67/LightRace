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
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class      Level1 implements Initializable {
    @FXML private Canvas canvas;
    @FXML private Circle photon;
    @FXML private Label TimeLabel;
    @FXML private Label LevelLabel;
    @FXML private Button PlayButton;
    @FXML private Slider angleSlider;
    @FXML private Label AngleLabel;

    private static final double SCALE = 350.0 / 600.0;

    private static final LevelSystem levelSystem = new LevelSystem();
    private final PhysicsEngine physicsEngine = new PhysicsEngine();

    private List<Point2D> beamPath = null;
    private BeamAnimator animator;
    private double laserAngle;

    public static void setLevel(int index) {
        levelSystem.setCurrentLevelIndex(index);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.setOnMousePressed(e -> aimLaser(e));
        canvas.setOnMouseDragged(e -> aimLaser(e));
        angleSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (Math.abs(newValue.doubleValue() - laserAngle) > 1e-9) {
                laserAngle = newValue.doubleValue();
                beamPath = null;
                photon.setVisible(false);
                TimeLabel.setText("Time : 0.00s");
                updateAngleDisplay();
                redraw();
            }
        });
        resetAngle();
        loadLevelUI();
    }

    private void updateAngleDisplay() {
        AngleLabel.setText(String.format("%.1f°", laserAngle));
        angleSlider.setValue(laserAngle);
    }

    private void resetAngle() {
        Level level = levelSystem.getCurrentLevel();
        laserAngle = Math.toDegrees(Math.atan2(-level.getSourceDirY(), level.getSourceDirX()));
        if (laserAngle < 0) {
            laserAngle += 360;
        }
    }

    private void aimLaser(MouseEvent e) {
        if (PlayButton.isDisable()) {
            return;
        }
        Level level = levelSystem.getCurrentLevel();
        double dx = e.getX() / SCALE - level.getSourceX();
        double dy = e.getY() / SCALE - level.getSourceY();
        if (Math.hypot(dx, dy) < 1) {
            return;
        }
        laserAngle = Math.toDegrees(Math.atan2(-dy, dx));
        if (laserAngle < 0) {
            laserAngle += 360;
        }
        beamPath = null;
        photon.setVisible(false);
        TimeLabel.setText("Time : 0.00s");
        updateAngleDisplay();
        redraw();
    }

    private void loadLevelUI() {
        if (animator != null) {
            animator.stop();
            animator = null;
        }
        beamPath = null;
        photon.setVisible(false);
        PlayButton.setDisable(false);
        angleSlider.setDisable(false);
        TimeLabel.setText("Time : 0.00s");
        LevelLabel.setText("Level : " + levelSystem.getCurrentLevel().getLevelNumber());
        updateAngleDisplay();
        redraw();
    }

    @FXML
    void startLight(ActionEvent e) {
        loadLevelUI();

        Level level = levelSystem.getCurrentLevel();
        Point2D start = new Point2D(level.getSourceX(), level.getSourceY());
        double radians = Math.toRadians(laserAngle);
        Point2D direction = new Point2D(Math.cos(radians), -Math.sin(radians));
        Point2D goal = new Point2D(level.getGoalX(), level.getGoalY());

        List<Point2D> fullPath = physicsEngine.simulate(
                start,
                direction,
                level.getInitialMirrors(),
                level.getInitialBlocks()
        );

        String bounceError = PlacementValidator.validateBounces(physicsEngine);

        PlayButton.setDisable(true);
        angleSlider.setDisable(true);
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
                        angleSlider.setDisable(false);
                        if (goalReached) {
                            TimeLabel.setText(String.format("Goal! %.2fs", elapsed));
                        } else if (bounceError != null) {
                            showMessage(bounceError);
                        } else {
                            TimeLabel.setText(String.format("Missed %.2fs", elapsed));
                        }
                    }
                });
        animator.start();
    }

    @FXML
    void resetArea(ActionEvent e) {
        resetAngle();
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

        if (beamPath == null) {
            double radians = Math.toRadians(laserAngle);
            double endX = level.getSourceX() + 70 * Math.cos(radians);
            double endY = level.getSourceY() - 70 * Math.sin(radians);
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(3);
            gc.strokeLine(level.getSourceX(), level.getSourceY(), endX, endY);

            double textX = Math.max(10, Math.min(level.getSourceX() + 20, 420));
            double textY = Math.max(30, Math.min(level.getSourceY() - 20, 590));
            gc.setFill(Color.BLACK);
            gc.setFont(new Font(24));
            gc.fillText(String.format("Angle: %.1f°", laserAngle), textX, textY);
        }

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