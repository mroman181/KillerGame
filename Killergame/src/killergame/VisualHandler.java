/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VisualHandler extends Thread {

    private Socket clientSock;
    private String cliAddr;
    private KillerGame kg;
    private BufferedReader in; // i/o for the client
    private PrintWriter out;
    private boolean closed = true;
    private boolean next; //Conecta con el next/Conecta con el previous
    private KillerClient client;

    public VisualHandler(KillerGame kg, boolean next, int myport) {

        this.kg = kg;
        this.next = next;
        this.client = new KillerClient(this, this.kg, myport);
        new Thread(client).start();
    }

    public VisualHandler(Socket sock, String cliAddr, KillerGame kg, boolean next, int myport) {

        this.clientSock = sock;
        this.cliAddr = cliAddr;
        this.kg = kg;
        this.next = next;

// Get I/O streams from the socket
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            this.out = new PrintWriter(clientSock.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(VisualHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.client = new KillerClient(cliAddr, sock.getPort(), this, this.kg, myport);
        new Thread(client).start();
    }

    public void run() {

        while (true) {

            while (closed) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }

            while (!closed) {
                try {
                    processClient(in, out);
                    Thread.sleep(5);
                } catch (Exception e) {
                    System.out.println("Error visual handler");
                }
            }
            System.out.println("Desconectado");
            if (next) {
                this.kg.getConnectPanel().setTextTextns("No conectado");
            } else {

                this.kg.getConnectPanel().setTextTextps("No conectado");
            }
        }

    }

    public void closeLink() {
        try {
            closed = true;
            out.println("bye"); // tell server
            clientSock.close();
            //System.out.println("Socket cerrado");
        } catch (Exception e) {
            //   System.out.println("Error: close link");
        }
        //System.exit(0);
    }

    private void doRequest(String line, PrintWriter out) {

        if (line.substring(0, 5).equals("ready")) {
            
            String id = line.substring(5);
            //S'ha de comprovar que previousServer existesqui Sino el thread peta i es romp la connexio
            if (this.kg.getNextServer() != null) {

                if (id.equals(this.kg.getId())) {
                    this.kg.getNextServer().sendStart(this.kg.getId());
                    this.kg.start();
                } else {
                    this.kg.getNextServer().sendCommand(line);
                }

            }
        }

        if (line.substring(0, 5).equals("start") || line.substring(0, 5).equals("final")) {

            String id = line.substring(5);

            if (!id.equals(this.kg.getId())) {
                this.kg.getNextServer().sendCommand(line);
                if (line.substring(0, 5).equals("start")) {
                    this.kg.start();
                } else {
                    this.kg.resetGame();
                }
            }
        }

        if (line.substring(0, 5).equals("auton") || line.substring(0, 5).equals("contr")) {
            this.transferedObject(line);
        }

        if (line.substring(0, 5).equals("relay")) {
            int id = Integer.parseInt(line.substring(5, line.indexOf("&")));

            if (this.kg.findPad(id) == null) {
                //nave no pertenece a un pad del servidor
                Controlled c = this.kg.findNave(id);
                if (c == null) {
                    this.kg.getNextServer().sendCommand(line);
                } else {
                    String command = line.substring(line.indexOf("&") + 1);
                    if (command.equals("shoot")) {
                        c.shoot();
                    } else {
                        c.setVelocidades(command);
                    }
                }
            } else {
                //el mensaje ha dado la vuelta y no se ha encontrado la nave
                System.out.println("NAVE PERDIDA");
            }
        }

        //Informar de la meurte
        if (line.substring(0, 4).equals("dead")) {            
            System.out.println("Client msg: " + line);
            String idkg = line.substring(4, line.indexOf("&"));
            int idNave = Integer.parseInt(line.substring(line.indexOf("&") + 1));
            if (!idkg.equals(this.kg.getId())) {
                
                //actuar por la muerte
                KillerPad pad = this.kg.findPad(idNave);
                if(pad!=null){
                    pad.sendCommand("dead");
                }
                //informar al resto
                this.kg.getNextServer().sendCommand(line);
                
            } else {
                //Me ha vuelto el comando
            }

        }

        if (line.substring(0, 4).equals("quit")) {
            String idkg = line.substring(4, line.indexOf("&"));
            int idNave = Integer.parseInt(line.substring(line.indexOf("&") + 1));
            if (!idkg.equals(this.kg.getId())) {
                //actuar por quit
                Controlled nave = this.kg.findNave(idNave);

                if (nave != null) {
                    nave.clear();
                    this.kg.removeControlled(idNave);
                } else {
                    this.kg.getNextServer().sendCommand(line);
                }

            } else {
                //Me ha vuelto el comando

            }

        }

        if (line.substring(0, 5).equals("comun")) {
            this.sendCommand("comok");
        }

        if (line.substring(0, 5).equals("comok")) {
            this.client.resetTimer();
        }

    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isNext() {
        return next;
    }

    public KillerClient getClient() {
        return this.client;
    }

    private void processClient(BufferedReader in, PrintWriter out) {
        String line;
        boolean done = false;
        try {
            while (!done) {
                Thread.sleep(5);
                if ((line = in.readLine()) == null) {
                    done = true;
                } else {
                    if (line.trim().equals("bye")) {
                        done = true;
                        this.client.setSocket("", 10101);
                        this.closeLink();
                    } else {
                        doRequest(line, out);
                    }
                }
            }
        } catch (IOException e) {
            closed = true;
        } catch (InterruptedException ex) {
        }
    } // end of processClient( )

    private void transferedObject(String line) {
        //COmprobamos si es controlled o autonomous
        boolean controlled = line.substring(0, 5).equals("contr");

        String id = line.substring(5, line.indexOf('&'));

        int index = line.indexOf("&") + 1;
        String data = line.substring(index);
        String posY = data.substring(0, data.indexOf('&'));

        index = data.indexOf("&") + 1;
        data = data.substring(index);
        String velx = data.substring(0, data.indexOf('&'));

        index = data.indexOf("&") + 1;
        data = data.substring(index);
        String vely = data.substring(0, data.indexOf('&'));

        index = data.indexOf("&") + 1;
        data = data.substring(index);
        String width = data.substring(0, data.indexOf('&'));

        index = data.indexOf("&") + 1;
        data = data.substring(index);
        String heigth = data.substring(0, data.indexOf('&'));

        index = data.indexOf("&") + 1;
        data = data.substring(index);
        String red = data.substring(0, data.indexOf('&'));

        index = data.indexOf("&") + 1;
        data = data.substring(index);
        String green = data.substring(0, data.indexOf('&'));

        index = data.indexOf("&") + 1;
        data = data.substring(index);
        String blue = data;

   /*     System.out.println("Object received: id: " + id + " posY: " + posY
                + " Width: " + width + " Heigth  " + heigth + " Blue: " + blue
                + " Green:" + green + " Red: " + red);*/
        int ID = 0;
        int y = 0;
        int vx = 0;
        int vy = 0;
        int w = 0;
        int h = 0;
        int r = 0;
        int g = 0;
        int b = 0;

        try {
            ID = Integer.parseInt(id);
            y = Integer.parseInt(posY);
            y = (y * (this.kg.getViewer().getHeight())) / 100;
            vx = Integer.parseInt(velx);
            vy = Integer.parseInt(vely);
            w = Integer.parseInt(width);
            h = Integer.parseInt(heigth);
            r = Integer.parseInt(red);
            g = Integer.parseInt(green);
            b = Integer.parseInt(blue);
            //  public Controlled crearControlled(int id, int x, int y, int vx, int vy, int width, int heigth, Color color) {
            int x;
            if (vx > 0) {
                x = 0;
            } else {
                x = this.kg.getViewer().getWidth() - w;
            }

            if (controlled) {

                Controlled c = this.kg.crearControlled(ID, x, y, vx, vy, w, h, new Color(r, g, b));

                KillerPad pad = this.kg.findPad(ID);
                if (pad != null) {
                    pad.setNave(c);
                }
            } else {
                this.kg.crearAutonomous(ID, x, y, vx, vy, w, h, new Color(r, g, b));
            }
        } catch (Exception e) {
            System.out.println("Error al parsear trama recibida");
        }

    }

    public void sendObject(Alive object) {

        int posY = object.getPosY() * 100 / this.kg.getViewer().getHeight(); //POSY en porcentaje
        int vx = object.getVx();
        int vy = object.getVy();
        int width = object.getWidth();
        int heigth = object.getHeigth();
        int red = object.getColor().getRed();
        int green = object.getColor().getGreen();
        int blue = object.getColor().getBlue();

        String comand;

        if (object instanceof Controlled) {
            comand = "contr";
        } else {
            comand = "auton";
        }

        comand = comand + object.getId() + "&"
                + posY + "&"
                + vx + "&"
                + vy + "&"
                + width + "&"
                + heigth + "&"
                + red + "&"
                + green + "&"
                + blue;

        try {
            out.println(comand);
           // System.out.println(comand + "sended");
        } catch (Exception ex) {
            System.out.println("Problem sending object\n");
        }

    }

    public void sendReady(String id) {

        String command = "ready" + id;
        sendCommand(command);

    }

    private void sendStart(String id) {
        String command = "start" + id;
        sendCommand(command);
    }

    public void sendCommand(String command) {
        try {
            out.println(command);
           // System.out.println(command + "sended");
        } catch (Exception ex) {
            System.out.println("Problem sending startn");
        }
    }

    public void setSocket(Socket sock, String adress) {
        if (clientSock != null) {
            this.closeLink();
        }
        this.clientSock = sock;
        this.cliAddr = adress;
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            this.out = new PrintWriter(clientSock.getOutputStream(), true);
            this.closed = false;

            ConnectPanel cp = this.kg.getConnectPanel();
            if (next) {
                cp.setTextIpNS(this.cliAddr);
                cp.setTextPortNS(this.clientSock.getPort());
            } else {
                cp.setTextIpPS(this.cliAddr);
                cp.setTextPortPS(this.clientSock.getPort());
            }
        } catch (IOException ex) {

        }

    }

    public void setSocket(Socket sock, String adress, int cliPort) {
        if (clientSock != null) {
            this.closeLink();
        }
        this.client.setSocket(adress, cliPort);
        this.clientSock = sock;
        this.cliAddr = adress;
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            this.out = new PrintWriter(clientSock.getOutputStream(), true);
            this.closed = false;

            ConnectPanel cp = this.kg.getConnectPanel();
            if (next) {
                cp.setTextIpNS(this.cliAddr);
                cp.setTextPortNS(cliPort);
            } else {
                cp.setTextIpPS(this.cliAddr);
                cp.setTextPortPS(cliPort);
            }
        } catch (IOException ex) {

        }

    }

    public void conectionFall() {
        try {
            this.clientSock.close();
            this.closed = true;
        } catch (IOException ex) {

        }
    }
}
