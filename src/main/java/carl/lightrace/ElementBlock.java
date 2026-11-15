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

    /**
     * Determines the outward surface normal at a specific hit point on the block's boundary.
     */
    public Point2D getNormalAt(Point2D hitPoint) {
        double minX = position.getX() - width / 2.0;
        double maxX = position.getX() + width / 2.0;
        double minY = position.getY() - height / 2.0;
        double maxY = position.getY() + height / 2.0;

        double eps = 1e-3;
        if (Math.abs(hitPoint.getX() - minX) < eps) return new Point2D(-1, 0); // Left face
        if (Math.abs(hitPoint.getX() - maxX) < eps) return new Point2D(1, 0);  // Right face
        if (Math.abs(hitPoint.getY() - minY) < eps) return new Point2D(0, -1); // Top face
        if (Math.abs(hitPoint.getY() - maxY) < eps) return new Point2D(0, 1);  // Bottom face

        return new Point2D(0, -1); // Fallback normal
    }

    /**
     * uses Snell's law to refract incoming light beams
     * @param direction incoming unit direction vector of the beam
     * @param hitPoint exact contact point on the block boundary
     * @return refracted unit vector, or reflected vector if TIR occurs
     */
    public Point2D refract(Point2D direction, Point2D hitPoint) {
        Point2D normal = getNormalAt(hitPoint);

        double n1 = 1.0;                  // Starting index (Air)
        double n2 = refractiveIndex;      // Target index (Water = 1.33, Glass = 1.50)

        double cosTheta1 = -direction.dotProduct(normal);

        if (cosTheta1 < 0) {
            cosTheta1 = direction.dotProduct(normal);
            double temp = n1;
            n1 = n2;
            n2 = temp;
            normal = normal.multiply(-1.0);
        }

        double eta = n1 / n2;
        double sin2Theta2 = eta * eta * (1.0 - cosTheta1 * cosTheta1);

        if (sin2Theta2 > 1.0) {
            double dot = direction.dotProduct(normal);
            return direction.subtract(normal.multiply(2.0 * dot)); // Standard reflection formula
        }

        double cosTheta2 = Math.sqrt(1.0 - sin2Theta2);

        Point2D term1 = direction.multiply(eta);
        Point2D term2 = normal.multiply(eta * cosTheta1 - cosTheta2);
        return term1.add(term2).normalize();
    }
}
