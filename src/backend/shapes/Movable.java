package backend.shapes;

import java.awt.*;

public interface Movable {
    void setDraggingPoint(Point point);
    Point getDraggingPoint();
    boolean contains(Point point);
    void moveTo(Point point);
}
