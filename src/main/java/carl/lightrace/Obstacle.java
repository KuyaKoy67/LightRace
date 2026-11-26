package carl.lightrace;

import javafx.geometry.Point2D;
import lombok.Getter;

@Getter
public class Obstacle {
    private final Point2D position; // center of the wall
    private final double width;
    private final double height;
    private static final double tolerance = 0.000001;

    public Obstacle(Point2D position, double width, double height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }
}
