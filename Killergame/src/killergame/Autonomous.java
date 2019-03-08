package killergame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Autonomous extends Alive {

    private boolean enviado = false;
    private boolean bounded = false;
    private int tBound = 0;

    public Autonomous(Viewer v, KillerGame kg, int x, int y, int vx, int vy, int heigth, int width, Color color) {
        super(0, v, vx, vy, x, y, width, heigth, color, kg);
    }

    public Autonomous(int id, Viewer v, KillerGame kg, int x, int y, int vx, int vy, int heigth, int width, Color color) {
        super(id, v, x, y, vx, vy, width, heigth, color, kg);
    }

    public void mover() {

        int vx = this.getVx();
        int vy = this.getVy();

        double posx = this.getDposx();
        double posy = this.getDposy();

        double incx = (double) vx * 100;
        double incy = (double) vy * 100;

        incx = Math.round(incx / 200);
        incy = Math.round(incy / 200);
        incx = incx / 100;
        incy = incy / 100;

        this.setDposx(posx + incx);
        this.setDposy(posy + incy);

        this.setPosX((int) posx);
        this.setPosY((int) posy);
        this.isInLimitY();
        this.isInLimitX();

    }

    @Override
    public void run() {

        while (!this.isMuerto()) {

            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
            }

            this.mover();
            this.intersect();

        }

        if (enviado) {
            /*while (this.getPosX() < this.getViewer().getWidth() ) {

                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                }
                this.mover();
            }*/
            this.getKillerGame().killObject(this);
            this.clear();
        }

    }

    public boolean hasId() {
        if (this.getId() == 0) {
            return false;
        }
        return true;
    }

    private void isInLimitY() {

        if (this.getPosY() <= 0 && this.getVy() < 0) {
            this.setVy(-this.getVy());
        }
        if (this.getPosY() + this.getHeigth() >= this.getViewer().getHeight() && this.getVy() > 0) {
            this.setVy(-this.getVy());
        }
    }

    private void isInLimitX() {

        if (!this.isMuerto()) {
            if (this.getPosX() <= 0 && this.getVx() < 0 && !enviado) {

                VisualHandler ps = this.getKillerGame().getPreviousServer();
                ps.sendObject(this);
                enviado = true;
                this.setMuerto(true);
            }

            if (this.getPosX() + this.getWidth() >= this.getViewer().getWidth() && this.getVx() > 0 && !enviado) {

                VisualHandler ns = this.getKillerGame().getNextServer();
                ns.sendObject(this);
                enviado = true;
                this.setMuerto(true);
            }
        }
    }

    @Override
    public void bound() {
        if (!bounded) {
            super.bound();
            bounded = true;
            tBound = 0;
        }
    }

    private void intersect() {
        if (bounded) {
            tBound++;
            if (tBound > 5) {
                bounded = false;
            }
        }
        this.getKillerGame().testColision(this);
    }
}
