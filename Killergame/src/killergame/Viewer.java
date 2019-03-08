package killergame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Viewer extends Canvas implements Runnable {

    private MyBufferedImage image;
    private MyBufferedImage fondo;
    private ArrayList<VisibleObject> objects;
    private Color color;
    
    public Viewer(ArrayList<VisibleObject> objects, int width, int heigth) {
        super();
        setSize(width, heigth);
        color= new Color(255,255,255);
        setBackground(color);
        this.objects = objects;
        try {
            this.fondo = new MyBufferedImage(ImageIO.read(new File("image.jpg")),  this);
            this.image = new MyBufferedImage(ImageIO.read(new File("image.jpg")),  this);
        } catch (Exception e) {

        }
    }

    @Override
    public void run() {
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Canvas running");
        while (true) {
            for (int i = 0; i < this.objects.size(); i++) {               
               this.objects.get(i).paint();
            }
            this.getGraphics().drawImage(this.image, 0, 0, this);
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);     
    }

    public Color getColor() {
        return color;
    }  
    
    public MyBufferedImage getImage() {
        return this.image;
    }
    
    public void clearCanvas(){
        try {
            this.image = new MyBufferedImage(ImageIO.read(new File("image.jpg")),  this);
        } catch (IOException ex) {
            Logger.getLogger(Viewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
