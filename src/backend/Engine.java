package backend;

import backend.exception.InvalidName;
import backend.shapes.AbstractShapeClass;
import backend.shapes.DrawingEngine;
import backend.shapes.Shape;
import backend.shapes.drawable.LineSegment;
import org.json.JSONArray;

import static backend.constants.Properties.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Engine extends JPanel implements DrawingEngine, MouseListener, MouseMotionListener {

    private final ArrayList<Shape> shapes;
    private final JComboBox comboBox;
    private int selectedIndex =-1;
    public Engine(JComboBox comboBox) {
        super();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        shapes = new ArrayList<>();
        this.comboBox=comboBox;
    }

    @Override
    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    @Override
    public void removeShape(Shape shape) {
        shapes.remove(shape);
    }

    @Override
    public Shape[] getShapes() {
        return shapes.toArray(new Shape[0]);
    }

    @Override
    public void refresh(Graphics canvas) {
        repaint();
        refreshComboBox();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape s : getShapes()) {
            s.draw(g);
        }
    }


    public void changeColor(Shape shape, Color color, Color fillColor, boolean isBorder, boolean isFill) {
        if (color != null) shape.setColor(color);
        if (fillColor != null) shape.setFillColor(fillColor);
        shape.addProperties(SET_BORDER_KEY, String.valueOf(isBorder));
        shape.addProperties(SET_FILL_KEY, String.valueOf(isFill));
    }

    public void renameShape(Shape shape, String name) {
        shape.addProperties(NAME_KEY, name);
    }

    public Shape getShape(int index) {
        return shapes.get(index);
    }

    public Shape getShape(Shape shape) {
        for (Shape s : shapes) {
            if (shape.getProperties().get(NAME_KEY).equals(s.getProperties().get(NAME_KEY)))
                return s;
        }
        return null;

    }

    public void refreshComboBox() {
        if (comboBox == null) return;

        comboBox.removeAllItems();
        Shape[] shapes = getShapes();
        comboBox.addItem("Select Shape");
        for (Shape shape : shapes) {
            comboBox.addItem(shape.getProperties().get("name"));
        }
    }

    public void checkShapeName(String shapeName) throws InvalidName {
        for (Shape s : getShapes()) {
            if (s.getProperties().get("name").equals(shapeName)) {
                throw new InvalidName();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed (MouseEvent e){

        Point p = new Point(e.getX(), e.getY());
        selectedIndex = -1;
        for(int i=shapes.size()-1 ;i>=0;i--) {
            if (((AbstractShapeClass)shapes.get(i)).contains(p)) {
                ((AbstractShapeClass) shapes.get(i)).setDraggingPoint(p);
                selectedIndex = i;
                break;
            }
        }

        if (selectedIndex != -1) {
            comboBox.setSelectedIndex(selectedIndex + 1);
        } else {
            comboBox.setSelectedIndex(0);
            selectedIndex = -1;
        }
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        if (selectedIndex != -1)
            ((AbstractShapeClass) shapes.get(selectedIndex)).setDraggingPoint(null);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

        Point p = new Point(e.getX(), e.getY());
        if (p.x >= 0 && p.x <= this.getWidth() && p.y >= 0 && p.y <= this.getHeight())
            if (selectedIndex != -1) {
                ((AbstractShapeClass) shapes.get(selectedIndex)).moveTo(p);
                repaint();
            }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void exportImage(JFrame frame) {
        String path;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                return f.getName().toLowerCase().endsWith(".jpg");
            }

            @Override
            public String getDescription() {
                return "JPG file (*.jpg)";
            }
        });

        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showDialog(frame, "Export Drawing") == JFileChooser.APPROVE_OPTION) {
            path = fileChooser.getSelectedFile().getPath();
        } else {
            return;
        }

        BufferedImage image = new BufferedImage(this.getWidth(),this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        this.paint(g2);

        try{
            ImageIO.write(image, "jpg", new File(path + ".jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDrawing (JFrame frame)  {
        String path;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                return f.getName().toLowerCase().endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "JSON file (*.json)";
            }
        });

        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showDialog(frame, "Save Drawing") == JFileChooser.APPROVE_OPTION) {
            path = fileChooser.getSelectedFile().getPath();
        } else {
            return;
        }
//        LineSegment l=new LineSegment(new Point(0,0),new Point(100,100));
//        Map<String, String> p = new HashMap<>();
//        p.put(NAME_KEY,"Abdallah");
//        l.setProperties(p);
        JSONArray jsonShapes = new JSONArray();
        for (Shape s : getShapes()) {
            jsonShapes.put(s.toJSON());
        }

        FileWriter file = null;
        try {
            file = new FileWriter(path);
            file.write(jsonShapes.toString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
