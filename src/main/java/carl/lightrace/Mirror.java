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
public abstract class Mirror {
    protected Point2D position;
    protected double angle;
    protected double length;
    
    public Mirror(Point2D position, double angle, double length) {
        this.position = position;
        this.angle = angle;
        this.length = length;
    }

    /**
     * helper method that allows us to get the surface direction
     * @return the surface direction of the mirror in Point2D
     */
    protected Point2D getSurfaceDirection() {
        return new Point2D(Math.cos(angle), Math.sin(angle));
    }

    /**
     * helper method that gets the start of a mirror
     * @return the start of the mirror in Point2D
     */
    public Point2D getStart() {
        return position.subtract(getSurfaceDirection().multiply(length / 2));
    }

    /**
     * helper method that gets the end of a mirror
     * @return the end of the mirror in Point2D
     */
    public Point2D getEnd() {
        return position.add(getSurfaceDirection().multiply(length / 2));
    }
    
    /**
     * Receives the unit vector direction of the light beam and 
     * returns the reflected direction in Point2D form.
     * @param direction unit vector of the beam's direction
     * @return reflected unit vector of the beam's direction
     */
    public abstract Point2D reflect(Point2D direction);
}
