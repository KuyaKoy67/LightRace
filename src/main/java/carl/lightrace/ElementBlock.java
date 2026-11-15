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

    /**
     * calculates where the ray intersects the bounding box of the block.
     *
     * @param origin ray origin point
     * @param direction normalized ray direction vector
     * @return distance t to the entry hit point, or Double.POSITIVE_INFINITY if no intersection
     */
    public double intersect(Point2D origin, Point2D direction) {
        double minX = position.getX() - width / 2.0;
        double maxX = position.getX() + width / 2.0;
        double minY = position.getY() - height / 2.0;
        double maxY = position.getY() + height / 2.0;

        // X-slab intersection distances
        double t1 = (minX - origin.getX()) / direction.getX();
        double t2 = (maxX - origin.getX()) / direction.getX();
        double tmin = Math.min(t1, t2);
        double tmax = Math.max(t1, t2);

        // Y-slab intersection distances
        double t3 = (minY - origin.getY()) / direction.getY();
        double t4 = (maxY - origin.getY()) / direction.getY();
        tmin = Math.max(tmin, Math.min(t3, t4));
        tmax = Math.min(tmax, Math.max(t3, t4));

        // If tmax < 0, the box is behind the ray. If tmin > tmax, the ray misses the box.
        if (tmax < 0 || tmin > tmax) {
            return Double.POSITIVE_INFINITY;
        }

        return tmin > 1e-9 ? tmin : (tmax > 1e-9 ? tmax : Double.POSITIVE_INFINITY);
    }
}
