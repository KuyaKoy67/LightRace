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

    @Override
    public Point2D reflect(Point2D direction) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
