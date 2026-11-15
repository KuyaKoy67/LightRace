package carl.lightrace;

import javafx.geometry.Point2D;
import lombok.Getter;

@Getter
public class ElementBlock {
    private Point2D position;
    private double width;
    private double height;
    private double refractiveIndex; // e.g., 1.33 for water, 1.50 for glass
    private String blockType;       // "Water" or "Glass"

    public ElementBlock(Point2D position, double width, double height, double refractiveIndex, String blockType) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.refractiveIndex = refractiveIndex;
        this.blockType = blockType;
    }
}
