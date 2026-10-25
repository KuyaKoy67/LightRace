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
    
    public Mirror(Point2D position, double angle) {
        this.position = position;
        this.angle = angle;
    }
    
    /**
     * Receives the unit vector direction of the light beam and 
     * returns the reflected direction in Point2D form.
     * @param direction unit vector of the beam's direction
     * @return reflected unit vector of the beam's direction
     */
    public abstract Point2D reflect(Point2D direction);
}
