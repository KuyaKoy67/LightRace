package carl.lightrace;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class Level1 implements Initializable {
    @FXML private Canvas canvas;
    @FXML private Circle photon;
    @FXML private Label timeLabel;
    @FXML private Label levelLabel;
    @FXML private Button playButton;
    @FXML private Button resetButton;
    @FXML private Button returnButton;
    @FXML private Slider angleSlider;
    @FXML private Label angleLabel;
    @FXML private ListView<String> ToolBoxListView;
    @FXML private Rectangle mirror;
    @FXML private Rectangle obstacle;
    @FXML private Pane placementPane;

    private static final double SCALE = 350.0 / 600.0;
    private static final String mirrorTool = "StraightMirror";

    private static final LevelSystem levelSystem = new LevelSystem();
    private final PhysicsEngine physicsEngine = new PhysicsEngine();

    private final List<Mirror> mirrors = new ArrayList<>();
    private Obstacle obstacleObject;
    private Mirror dragged;
    private Point2D grabOffset;

    private List<Point2D> beamPath = null;
    private BeamAnimator animator;
    private double laserAngle;

    public static void setLevel(int index) {
        levelSystem.setCurrentLevelIndex(index);
    }

    public void enterPlayButton(MouseEvent mouseEvent) throws IOException {
        playButton.setStyle("-fx-background-color: #b9f0c3");
    }

    public void exitPlayButton(MouseEvent mouseEvent) throws IOException {
        playButton.setStyle("-fx-background-color: #76d166");
    }

    public void enterResetButton(MouseEvent mouseEvent) throws IOException {
        resetButton.setStyle("-fx-background-color: #e66060");
    }

    public void exitResetButton(MouseEvent mouseEvent) throws IOException {
        resetButton.setStyle("-fx-background-color: #e33030");
    }

    public void enterReturnButton(MouseEvent mouseEvent) throws IOException {
        returnButton.setStyle("-fx-background-color: #afd8f0");
    }

    public void exitReturnButton(MouseEvent mouseEvent) throws IOException {
        returnButton.setStyle("-fx-background-color: #82a7dc");
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
                timeLabel.setText("Time : 0.00s");
                updateAngleDisplay();
                redraw();
            }
        });
        resetAngle();
        loadLevelUI();
    }

    private void placeMirror(double x, double y) {
        if (playButton.isDisable()) {
            return;
        }

        Mirror m = new StraightMirror(new Point2D(x, y), 0, 100);
        String error = PlacementValidator.validateToolAvailable(
                levelSystem.getCurrentLevel(), mirrorTool, mirrors.size());

        if (error == null) {
            error = placementError(m, null);
        }

        if (error != null) {
            showMessage(error);
            return;
        }

        mirrors.add(m);
        changed();
    }

    private void replace(Mirror old, Mirror updated) {
        if (placementError(updated, old) != null) {
            return;
        }
        mirrors.set(mirrors.indexOf(old), updated);

        if (dragged == old) {
            dragged = updated;
        }
        changed();
    }

    private String placementError(Mirror m, Mirror ignore) {
        List<Mirror> others = new ArrayList<>(mirrors);
        others.remove(ignore);

        String error = PlacementValidator.validateMirror(m, levelSystem.getCurrentLevel(), others, List.of());
        if (error == null && obstacleObject != null) {
            for (int i = 0; i <= 10; i++) {   // sample points along the mirror
                Point2D p = m.getStart().add(m.getEnd().subtract(m.getStart()).multiply(i / 10.0));
                if (Math.abs(p.getX() - obstacleObject.getPosition().getX()) <= obstacleObject.getWidth() / 2
                        && Math.abs(p.getY() - obstacleObject.getPosition().getY()) <= obstacleObject.getHeight() / 2) {
                    return "The mirror cannot overlap the obstacle.";
                }
            }
        }
        return error;
    }

    private Mirror mirrorAt(Point2D p) {
        for (Mirror m : mirrors) {
            Point2D a = m.getStart();
            Point2D ab = m.getEnd().subtract(a);
            double t = Math.max(0, Math.min(1, p.subtract(a).dotProduct(ab) / ab.dotProduct(ab)));
            if (p.distance(a.add(ab.multiply(t))) < 12) {
                return m;
            }
        }
        return null;
    }

    private List<Mirror> allMirrors() {
        List<Mirror> all = new ArrayList<>(levelSystem.getCurrentLevel().getInitialMirrors());
        all.addAll(mirrors);
        return all;
    }

    private void changed() {
        beamPath = null;
        photon.setVisible(false);
        timeLabel.setText("Time : 0.00s");
        redraw();
    }

    private void updateAngleDisplay() {
        angleLabel.setText(String.format("%.1f°", laserAngle));
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
        if (playButton.isDisable()) {
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
        timeLabel.setText("Time : 0.00s");
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
        playButton.setDisable(false);
        angleSlider.setDisable(false);
        timeLabel.setText("Time : 0.00s");
        levelLabel.setText("Level : " + levelSystem.getCurrentLevel().getLevelNumber());
        updateAngleDisplay();
        redraw();
    }

    private List<Point2D> clipAtWall(List<Point2D> path) {
        if (obstacleObject == null) {
            return path;
        }
        for (int i = 0; i < path.size() - 1; i++) {
            Point2D a = path.get(i);
            double len = a.distance(path.get(i + 1));
            if (len == 0) {
                continue;
            }
            Point2D dir = path.get(i + 1).subtract(a).multiply(1 / len);
            double t = obstacleObject.intersect(a, dir);
            if (t < len) {
                List<Point2D> clipped = new ArrayList<>(path.subList(0, i + 1));
                clipped.add(a.add(dir.multiply(t)));
                return clipped;
            }
        }
        return path;
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

        playButton.setDisable(true);
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
                        timeLabel.setText(String.format("Time : %.2fs", elapsed));
                        redraw();
                    }

                    @Override
                    public void onFinished(boolean goalReached, double elapsed) {
                        playButton.setDisable(false);
                        angleSlider.setDisable(false);
                        if (goalReached) {
                            timeLabel.setText(String.format("Goal! %.2fs", elapsed));
                        } else if (bounceError != null) {
                            showMessage(bounceError);
                        } else {
                            timeLabel.setText(String.format("Missed %.2fs", elapsed));
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

    @FXML
    void returnToLevelSelect(MouseEvent e) throws IOException {
        App.setRoot("LevelSelectWindow");
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