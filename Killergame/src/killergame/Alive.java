package killergame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Alive extends VisibleObject implements Runnable {

    private int vx;
    private int vy;
    private double dposx;
    private double dposy;
    private int posXprevious;
    private int posYprevious;
    private int id;     

    public Alive(int id, Viewer v, int x, int y, int vx, int vy, int width, int heigth, Color color, KillerGame kg) {
        super(v, x, y, width, heigth, color, kg);
        this.dposx = (double) x;
        this.dposy = (double) y;
        this.posXprevious = x;
        this.posYprevious = y;
        this.id = id;
        this.vx = vx;
        this.vy = vy;
    }

    @Override
    public void run() {
        
    }
    
    @Override
    public void paint() {

        

        int x = this.getPosX();
        int y = this.getPosY();

        this.setPosXprevious(x);
        this.setPosYprevious(y);

        BufferedImage image = this.getViewer().getImage();
        Graphics g = image.getGraphics();
        g.setColor(this.getColor());
        g.fillOval(x, y, this.getWidth(), this.getHeigth());
    }

    @Override
    public void clear() {
        BufferedImage image = this.getViewer().getImage();
        Graphics g = image.getGraphics();
        g.setColor(this.getViewer().getColor());
        g.fillOval(this.getPosXprevious(), this.getPosYprevious(), this.getWidth(), this.getHeigth());
    }
    
    public void bound(){
        this.vx = -vx;
        this.vy = -vy;
    }
    
    public int getId() {
        return id;
    }
    
    public int getVx() {
        return vx;
    }

    public void setVx(int vx) {
        this.vx = vx;
    }

    public int getVy() {
        return vy;
    }

    public void setVy(int vy) {
        this.vy = vy;
    }

    public double getDposx() {
        return dposx;
    }

    public void setDposx(double dposx) {
        this.dposx = dposx;
    }

    public double getDposy() {
        return dposy;
    }

    public void setDposy(double dposy) {
        this.dposy = dposy;
    }
    
    void setId(int id) {
       this.id = id;
    }

    public void updatePosition() {

    }

    public int getPosXprevious() {
        return posXprevious;
    }

    public void setPosXprevious(int posXprevious) {
        this.posXprevious = posXprevious;
    }

    public int getPosYprevious() {
        return posYprevious;
    }

    public void setPosYprevious(int posYprevious) {
        this.posYprevious = posYprevious;
    }

}
