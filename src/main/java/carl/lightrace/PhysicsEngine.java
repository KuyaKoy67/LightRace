package carl.lightrace;

import javafx.geometry.Point2D;

public class PhysicsEngine {


    /**
     * helper method that crosses two vectors and returns a cross product. This is useful in the case of calculating
     * unknown variables in vectors.
     * @param a the first vector
     * @param b the second vector
     * @return the cross product
     */
    private double cross(Point2D a, Point2D b) {
        return a.getX() * b.getY() - a.getY() * b.getX();
    }
}
