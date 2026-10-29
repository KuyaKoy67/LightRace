package carl.lightrace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Point2D;

/**
 *
 * @author User
 */
public class LightBeam {
    private final Point2D startPosition;
    private final Point2D startDirection;
    private Point2D currentDirection;
    private final List<Point2D> path = new ArrayList<>();

    public LightBeam(Point2D startPosition, Point2D direction) {
        this.startPosition = startPosition;
        this.startDirection = direction.normalize();
        reset();
    }
    
    /**
     * Clears the path and restore the path to its starting state.
     */
    public void reset() {
        path.clear();
        path.add(startPosition);
        currentDirection = startDirection;
    }

    /**
     * Gets the start position.
     * @return the start position.
     */
    public Point2D getStartPostion() {
        return startPosition;
    }
    
    /**
     * Gets the current direction.
     * @return the current direction.
     */
    public Point2D getCurrentDirection() {
        return currentDirection;
    }
    
    /**
     * Sets the current direction.
     * @param direction the new direction.
     */
    public void setCurrentDirection(Point2D direction) {
        this.currentDirection = direction.normalize();
    }
    
    /**
     * Gets the current position.
     * @return the current position.
     */
    public Point2D getCurrentPosition() {
        return path.get(path.size() - 1);
    }

    public void addPoint(Point2D point) {
        path.add(point);
    }

    /**
     * Makes the path unmodifiable (only read, loop and size).
     * @return makes the path unmodifiable.
     */
    public List<Point2D> getPath() {
        return Collections.unmodifiableList(path);
    }
    
    /**
     * Counts the amount of times the path changed direction.
     * @return the number of times the path changed direction.
     */
    public int getBounceCount() {
        return path.size() - 1;
    }
}