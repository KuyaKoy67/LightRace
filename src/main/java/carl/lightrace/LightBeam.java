/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package carl.lightrace;

import java.util.ArrayList;
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
    }
}
