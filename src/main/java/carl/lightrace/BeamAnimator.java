package carl.lightrace;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;

import java.util.ArrayList;
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

    public BeamAnimator(List<Point2D> path, List<ElementBlock> blocks,
                        Point2D goal, double goalRadius, Listener listener) {
        this.path = path;
        this.goal = goal;
        this.goalRadius = goalRadius;
        this.listener = listener;

        int count = path.size() - 1;
        segmentLength = new double[count];
        segmentIndex = new double[count];
        for (int i = 0; i < count; i++) {
            Point2D a = path.get(i);
            Point2D b = path.get(i + 1);
            segmentLength[i] = a.distance(b);
            segmentIndex[i] = mediumIndexAt(a.midpoint(b), blocks);
        }

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastNanos == 0) {
                    lastNanos = now;
                    return;
                }
                double dt = Math.min((now - lastNanos) / 1e9, MAX_FRAME_SECONDS);
                lastNanos = now;
                step(dt);
            }
        };
    }

    public void start() {
        List<Point2D> first = new ArrayList<>();
        first.add(path.get(0));
        listener.onFrame(first, 0);
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private double mediumIndexAt(Point2D p, List<ElementBlock> blocks) {
        for (ElementBlock b : blocks) {
            double halfW = b.getWidth() / 2.0;
            double halfH = b.getHeight() / 2.0;
            if (Math.abs(p.getX() - b.getPosition().getX()) < halfW
                    && Math.abs(p.getY() - b.getPosition().getY()) < halfH) {
                return b.getRefractiveIndex();
            }
        }
        return 1.0;
    }

    private void step(double dt) {
        double remaining = dt;

        while (remaining > 1e-12 && segment < segmentLength.length) {
            double speed = BEAM_SPEED / segmentIndex[segment];
            double distanceLeft = segmentLength[segment] - segmentDistance;
            double reach = speed * remaining;

            if (reach < distanceLeft) {
                segmentDistance += reach;
                elapsed += remaining;
                remaining = 0;
            } else {
                double timeUsed = distanceLeft / speed;
                elapsed += timeUsed;
                remaining -= timeUsed;
                segment++;
                segmentDistance = 0;
            }
        }
    }
}
