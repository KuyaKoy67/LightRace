/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package carl.lightrace;

import javafx.geometry.Point2D;
/**
 *
 * @author carln
 */
public class StraightMirror extends Mirror {
    
    public StraightMirror(Point2D position, double angle) {
        super(position, angle);
    }
    
    /**
     * Helper method that gets the mirror's unit normal, computed from
     * the angle. For now, this will be used for straight mirrors only.
     * @return the unit vector of the normal
     */
    private Point2D getNormal() {
        double normalAngle = angle + Math.PI / 2;
        return new Point2D(Math.cos(normalAngle), Math.sin(normalAngle));
    }

    /**
     * reflect method for straight mirrors. It uses the physics formula 
     * r = d - 2(d * n)n using the methods from Point2D.
     * @param direction the direction vector unit in Point2D
     * @return the reflected direction vector unit in Point2D
     */
    @Override
    public Point2D reflect(Point2D direction) {
        Point2D normal = getNormal();
        double dot = direction.dotProduct(normal);
        Point2D scaledNormal = normal.multiply(2 * dot);
        return direction.subtract(scaledNormal);
    }
    
}
