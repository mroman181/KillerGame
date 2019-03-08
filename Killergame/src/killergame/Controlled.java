package killergame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controlled extends Alive {

    private boolean enviado = false;
    private static final int velocidad = 150;
    private String direction = "up";
    private int limite; //limite en el eje y que puede alcanzar

    public Controlled(int id, Viewer v, KillerGame kg, int x, int y, int vx, int vy, int width, int heigth, Color color) {
        super(id, v, x, y, vx, vy, width, heigth, color, kg);
        this.limite = v.getHeight() - heigth;

    }

    @Override
    public void run() {

        while (!this.isMuerto() || enviado) {

            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
            }

            this.mover();
            this.getKillerGame().testColision(this);

        }

    }

    @Override
    public void die() {
        if (!this.isMuerto() && !enviado) {
            this.setMuerto(true);
            this.getKillerGame().killObject(this);
            this.clear();
            System.out.println("Jugador " + this.getId() +" ha muerto");
            KillerPad pad = this.getKillerGame().findPad(this.getId());
            if(pad!=null){
                pad.sendCommand("dead");
            }
            //informar al resto de que ha muerto la nave
            this.getKillerGame().getNextServer().sendCommand("dead" + this.getKillerGame().getId() + "&" + this.getId());
        }
    }

    public boolean isEnviado() {
        return enviado;
    }

    private void mover() {

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
        this.setPosX((int) posx);

        int limitY = this.isInLimitY();
        if (limitY == 0) {

            this.setDposy(posy + incy);
            this.setPosY((int) posy);

        } else {
            if (limitY == 1) {
                this.setDposy(0);
                this.setPosY(0);
            } else {
                this.setDposy(this.limite);
                this.setPosY(this.limite);
            }
        }

        this.isInLimitX();

    }

    private int isInLimitY() {

        if (this.getPosY() <= 0 && this.getVy() < 0) {
            return 1;
        }
        if (this.getPosY() + this.getHeigth() >= this.getViewer().getHeight() && this.getVy() > 0) {
            return 2;
        }
        return 0;
    }

    private void isInLimitX() {
        if (!this.isMuerto()) {

            if (this.getPosX() <= 0 && this.getVx() < 0 && !enviado) {
                enviado = true;
                VisualHandler ps = this.getKillerGame().getPreviousServer();
                ps.sendObject(this);

                this.clear();
                this.getKillerGame().killObject(this);
            }
            if (this.getPosX() + this.getWidth() >= this.getViewer().getWidth() && this.getVx() > 0 && !enviado) {
                enviado = true;
                VisualHandler ns = this.getKillerGame().getNextServer();
                ns.sendObject(this);

                this.clear();
                this.getKillerGame().killObject(this);
            }
        }
    }

    public void shoot() {
        KillerGame kg = this.getKillerGame();
        switch (this.direction) {
            case "up":
                kg.crearAutonomous(this.getId(), this.getPosX() + this.getWidth() / 2 - 5, this.getPosY() - 15, 0, -400, 10, 10, this.getColor());
                break;

            case "do":
                kg.crearAutonomous(this.getId(), this.getPosX() + this.getWidth() / 2 - 5, this.getPosY() + this.getHeigth() + 15, 0, 400, 10, 10, this.getColor());
                break;

            case "le":
                kg.crearAutonomous(this.getId(), this.getPosX() - 15, this.getPosY() + this.getHeigth() / 2 - 5, -400, 0, 10, 10, this.getColor());
                break;

            case "ri":
                kg.crearAutonomous(this.getId(), this.getPosX() + this.getWidth() + 15, this.getPosY() + this.getHeigth() / 2 - 5, 400, 0, 10, 10, this.getColor());
                break;

            case "ur":
                kg.crearAutonomous(this.getId(), this.getPosX() + this.getWidth(), this.getPosY() - 15, 282, -282, 10, 10, this.getColor());
                break;

            case "ul":
                kg.crearAutonomous(this.getId(), this.getPosX() - 15, this.getPosY() - 15, -282, -282, 10, 10, this.getColor());
                break;

            case "dr":
                kg.crearAutonomous(this.getId(), this.getPosX() + this.getWidth(), this.getPosY() + this.getHeigth() + 15, 282, 282, 10, 10, this.getColor());

                break;

            case "dl":
                kg.crearAutonomous(this.getId(), this.getPosX() - 15, this.getPosY() + this.getHeigth() + 15, -282, 282, 10, 10, this.getColor());

                break;
            default:
                kg.crearAutonomous(this.getId(), this.getPosX() + this.getWidth() / 2 - 5, this.getPosY() - 15, 0, -400, 10, 10, this.getColor());
        }
    }

    public void setVelocidades(String direccion) {
        this.direction = direccion;
        switch (direccion) {
            case "up":
                this.setVy(-this.velocidad);
                this.setVx(0);
                break;

            case "do":
                this.setVy(this.velocidad);
                this.setVx(0);
                break;

            case "le":
                this.setVx(-this.velocidad);
                this.setVy(0);
                break;

            case "ri":
                this.setVx(this.velocidad);
                this.setVy(0);
                break;

            case "ur":
                this.setVx(70);
                this.setVy(-70);
                break;

            case "ul":
                this.setVx(-70);
                this.setVy(-70);
                break;

            case "dr":
                this.setVx(70);
                this.setVy(70);
                break;

            case "dl":
                this.setVx(-70);
                this.setVy(70);
                break;

            default:
                this.setVy(0);
                this.setVx(0);

        }
    }
}
