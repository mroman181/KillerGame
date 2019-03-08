package killergame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Viewer extends Canvas implements Runnable {

    private BufferedImage image;
    private ArrayList<VisibleObject> objects;
    private Color color;

    public Viewer(ArrayList<VisibleObject> objects, int width, int heigth) {
        super();
        setSize(width, heigth);
        color = new Color(255, 255, 255);
        setBackground(color);
        this.objects = objects;
        image = new BufferedImage(this.getWidth(), this.getHeight(), TYPE_3BYTE_BGR);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
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
            image = new BufferedImage(this.getWidth(), this.getHeight(), TYPE_3BYTE_BGR);
            Graphics g = image.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
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

    public BufferedImage getImage() {
        return this.image;
    }

    public void clearCanvas() {
        image = new BufferedImage(this.getWidth(), this.getHeight(), TYPE_3BYTE_BGR);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

}
