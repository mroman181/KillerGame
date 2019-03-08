package killergame;

import java.awt.Color;
import java.awt.Graphics;

public class VisibleObject implements Renderizable {

    private int posX;
    private int posY;
    private boolean muerto = false;
    private Viewer viewer;   
    private int width;
    private int heigth;
    private Color color;
    private KillerGame killerGame;
    
    public VisibleObject(Viewer v, int x, int y, int width, int heigth, Color color, KillerGame kg){
        this.viewer = v;
        this.posX = x;
        this.posY = y;
        this.width = width;
        this.heigth = heigth;
        this.color = color;
        this.killerGame = kg;
    }

    @Override
    public void paint() {  

        MyBufferedImage image = this.getViewer().getImage();
        Graphics g = image.getGraphics();
        g.setColor(this.getColor());
        g.fillOval(posX, posY, this.width, this.heigth);
    }
    
   
    @Override
    public void clear() {
        MyBufferedImage image = this.getViewer().getImage();
        Graphics g = image.getGraphics();
        g.setColor(this.getViewer().getColor());
        g.fillOval(posX, posY, this.width, this.heigth);
    }
    

    public Color getColor() {
        return color;
    }   
    
    public int getWidth() {
        return width;
    }

    public int getHeigth() {
        return heigth;
    }    
    
    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
    
    public KillerGame getKillerGame() {
        return killerGame;
    }

    public void setKillerGame(KillerGame killerGame) {
        this.killerGame = killerGame;
    }

    public boolean isMuerto() {
        return muerto;
    }

    public void setMuerto(boolean muerto) {
        this.muerto = muerto;
    } 

    public Viewer getViewer() {
        return viewer;
    }  

    public void die() {
        if (!this.muerto) {
            this.muerto = true;
            this.killerGame.killObject(this);
            this.clear();
        }
    }
    
}
