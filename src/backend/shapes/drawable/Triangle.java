package backend.shapes.drawable;

import backend.shapes.AbstractShapeClass;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import static backend.constants.Properties.SET_BORDER_KEY;
import static backend.constants.Properties.SET_FILL_KEY;

public class Triangle extends AbstractShapeClass {
    private Point point2;
    private Point point3;

    public Triangle(Point point1, Point point2, Point point3) {
        super(point1);
        setPoint2(point2);
        setPoint3(point3);
    }

    public Point getPoint2() {
        return point2;
    }

    public void setPoint2(Point point2) {
        this.point2 = point2;
    }

    public Point getPoint3() {
        return point3;
    }

    public void setPoint3(Point point3) {
        this.point3 = point3;
    }

    @Override
    public void draw(Graphics canvas) {
        ((Graphics2D) canvas).setStroke(new BasicStroke(DEF_STROKE_SIZE));

        GeneralPath tri = new GeneralPath(Path2D.WIND_NON_ZERO);
        tri.moveTo(getPosition().x, getPosition().y);
        tri.lineTo(getPoint2().x, getPoint2().y);
        tri.lineTo(getPoint3().x, getPoint3().y);
        tri.lineTo(getPosition().x, getPosition().y);


        if (getProperties().get(SET_BORDER_KEY).equals("true")) {
            canvas.setColor(getColor());
            ((Graphics2D) canvas).draw(tri);
        }

        if (getProperties().get(SET_FILL_KEY).equals("true")) {
            canvas.setColor(getFillColor());
            ((Graphics2D) canvas).fill(tri);
        }
    }

    @Override
    public boolean contains(Point point) {
        return false;
    }

    @Override
    public void moveTo(Point point) {

    }
}
