package carl.lightrace;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import java.util.List;

public class BeamAnimator {

    public static final double BEAM_SPEED = 300.0;
    private static final double GRID_SIZE = 600.0;
    private static final double MAX_FRAME_SECONDS = 0.05;

    public interface Listener {
        void onFrame(List<Point2D> drawn, double elapsed);
        void onFinished(boolean goalReached, double elapsed);
    }

    private final List<Point2D> path;
    private final double[] segmentLength;
    private final double[] segmentIndex;

    private final Point2D goal;
    private final double goalRadius;
    private final Listener listener;
    private final AnimationTimer timer;

    private long lastNanos = 0;
    private double elapsed = 0;
    private int segment = 0;
    private double segmentDistance = 0;
}
