package carl.lightrace;

import javafx.geometry.Point2D;

public class CurvedMirror extends Mirror {
    private double curvatureRadius; // positive for convex, negative for concave

    public CurvedMirror(Point2D position, double angle, double length, double curvatureRadius) {
        super(position, angle, length);
        this.curvatureRadius = curvatureRadius;
    }

    @Override
    public Point2D reflect(Point2D direction) {
        return null;
    }
}
