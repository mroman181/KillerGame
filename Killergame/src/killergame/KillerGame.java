package killergame;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class KillerGame extends JFrame {

    private ArrayList<VisibleObject> objects;
    private ArrayList<VisibleObject> muertos;
    private ArrayList<KillerPad> pads;
    private KillerRules rules;
    private Viewer viewer;
    private KillerServer server;
    private VisualHandler ps;
    private VisualHandler ns;
    private ConnectPanel cp;
    private KillerRules kr;

    private int width;
    private int heigth;
    private boolean started = false;

    private String id = "1";
    private GamePanel gp;

    public KillerGame() {
        super("KillerGame - Port 1234");

        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
            this.setId(inetAddress.getHostAddress() + "i" + KillerServer.getPort());
        } catch (UnknownHostException ex) {
        }

        this.objects = new ArrayList();
        this.muertos = new ArrayList();
        this.pads = new ArrayList();
        this.kr = new KillerRules();
        this.crearInterfaz();

        this.crearPreviousServer();
        this.crearNextServer();
        this.server = new KillerServer(this);
        this.crearConnectPanel();
        this.setVisible(true);
        new Thread(server).start();

        this.crearViewer();
    }

    public ConnectPanel getConnectPanel() {
        return this.cp;
    }

    public String getId() {
        return id;
    }

    public Viewer getViewer() {
        return viewer;
    }

    public KillerServer getServer() {
        return server;
    }

    public VisualHandler getPreviousServer() {
        return ps;
    }

    public VisualHandler getNextServer() {
        return ns;
    }

    public int getHeigth() {
        return heigth;
    }

    public int getSizePads() {
        return this.pads.size();
    }

    public boolean addPad(KillerPad pad) {
        if (this.pads.size() < 4) {
            this.pads.add(pad);
            return true;
        }
        return false;
    }

    private void crearConnectPanel() {

        this.cp = new ConnectPanel(this);

    }

    public void crearAutonomous(int id, int x, int y, int vx, int vy, int width, int heigth, Color color) {

        Autonomous a = new Autonomous(id, this.viewer, this, x, y, vx, vy, width, heigth, color);
        this.objects.add(a);
        new Thread(a).start();

    }

    public Controlled crearControlled(int id, int x, int y, int vx, int vy, int width, int heigth, Color color) {

        Controlled a = new Controlled(id, this.viewer, this, x, y, vx, vy, width, heigth, color);
        this.objects.add(a);
        new Thread(a).start();
        return a;
    }

    public void crearKillerPad(Socket clientSock, String cliAddr, int id) {
        if (this.pads.size() < 4) {
            KillerPad pad = new KillerPad(clientSock, cliAddr, this, id);
            this.pads.add(pad);
            new Thread(pad).start();
        }
    }

    public void crearPreviousServer() {
        this.ps = new VisualHandler(this, false);
        new Thread(this.ps).start();
    }

    public void crearNextServer() {
        this.ns = new VisualHandler(this, true);
        new Thread(this.ns).start();
    }

    private void crearInterfaz() {
        this.width = 800;
        this.heigth = 600;
        this.setSize(this.width, this.heigth);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void crearStatic(int x, int y, int width, int heigth, Color color) {

        Static s = new Static(viewer, x, y, width, heigth, color, this);
        this.objects.add(s);

    }

    private void crearViewer() {

        this.viewer = new Viewer(this.objects, this.width, this.heigth - 100);
        this.gp = new GamePanel(this, this.viewer);
        this.gp.setVisible(false);
        this.add(gp);
        new Thread(viewer).start();
    }

    public KillerPad findPad(int id) {
        for (KillerPad pad : this.pads) {
            if (pad.getNave().getId() == id) {
                return pad;
            }
        }

        return null;
    }

    public Controlled findNave(int id) {

        for (VisibleObject object : this.objects) {
            if (object instanceof Controlled) {
                Controlled c = (Controlled) object;
                if (c.getId() == id) {
                    return c;
                }
            }
        }

        return null;
    }

    public void killObject(VisibleObject object) {
        this.muertos.add(object);
        this.objects.remove(object);
    }

    public boolean isStarted() {
        return started;
    }

    public void ready() {
        if (!this.ns.isClosed() && !this.ps.isClosed()) {
            this.ns.sendReady(this.id);
        }
    }

    public void removePad(KillerPad pad) {
        this.pads.remove(pad);
        this.cp.setNPads(this.pads.size());
    }

    public void removeControlled(int id) {
        Controlled c = this.findNave(id);
        this.objects.remove(c);
    }

    public void start() {

        this.started = true;
        this.cp.setVisible(false);
        this.gp.setVisible(true);

        this.crearAutonomous(0, 445, 80, 200, 200, 20, 20, new Color(0, 64, 0));
        this.crearStatic(375, 70, 50, 50, new Color(64, 128, 128));
        this.crearStatic(375, 220, 50, 50, new Color(64, 128, 128));
        this.crearStatic(375, 370, 50, 50, new Color(64, 128, 128));
        for (int i = 0; i < this.pads.size(); i++) {
            KillerPad pad = this.pads.get(i);

            int par = 1;
            int impar = 1;

            switch (i) {
                case 0:
                    par = 1;
                    impar = 1;
                    break;
                case 1:
                    par = 3;
                    impar = 1;
                    break;
                case 2:
                    par = 1;
                    impar = 3;
                    break;
                case 3:
                    par = 3;
                    impar = 3;
                    break;
                default:
                    break;
            }

            Controlled c = this.crearControlled(pad.getId(), par * this.width / 4, impar * this.heigth / 4, 0, 0, 30, 30, pad.getColor());
            pad.setNave(c);
        }

    }

    public void setId(String id) {
        this.id = id;
        System.out.println(this.id);
    }

    public void testColision(Alive alive) {

        int centerX = alive.getPosX() + alive.getWidth() / 2;
        int centerY = alive.getPosY() + alive.getHeigth() / 2;
        int centerX2;
        int centerY2;
        int radio1 = alive.getHeigth() / 2;
        int diametro;

        for (int i = 0; i < this.objects.size(); i++) {
            VisibleObject o = this.objects.get(i);
            if (!o.equals(alive)) {
                int radio2 = o.getHeigth() / 2;
                centerX2 = o.getPosX() + o.getWidth() / 2;
                centerY2 = o.getPosY() + o.getHeigth() / 2;
                int x = (int) Math.pow(centerX - centerX2, 2);
                int y = (int) Math.pow(centerY - centerY2, 2);
                int distancia = (int) Math.sqrt(x + y);
                diametro = radio1 + radio2;
                if (distancia <= diametro) {
                    boolean crashed = true;
                    if (o instanceof Controlled && alive instanceof Controlled) {
                        Controlled object = (Controlled) o;
                        if (object.getId() == alive.getId()) {
                            crashed = false;
                        }
                    }
                    if (crashed) {
                        System.out.println("Han chocado");
                        switch (this.kr.crashed(alive, o)) {
                            case 1:
                                //mueren ambas
                                alive.die();
                                o.die();

                                break;
                            case 2:
                                //El alive rebota
                                alive.bound();

                                break;
                            case 3:
                                //mueren el alive
                                alive.die();

                                break;
                            default:

                        }
                    }

                }

            }
        }
    }

    public void resetGame() {
        this.started = false;
        for (int i = 0; i < this.objects.size(); i++) {
            this.objects.get(i).setMuerto(true);
        }
        for (int i = 0; i < this.muertos.size(); i++) {
            this.muertos.get(i).setMuerto(true);
        }

        this.objects.clear();
        this.muertos.clear();
        this.gp.setVisible(false);
        this.cp.resetPanel();
        this.viewer.clearCanvas();

        /*if(!this.ns.isClosed())
            this.ns.closeLink();
        if(!this.ps.isClosed())
            this.ps.closeLink();
         */
    }

    public static void main(String[] args) {
        // TODO code application logic here

        KillerGame kg = new KillerGame();

    }
}
