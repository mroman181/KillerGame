package killergame;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KillerPad implements Runnable {

    private Socket clientSock;
    private String cliAddr;
    private KillerGame kg;
    private BufferedReader in; // i/o for the client
    private PrintWriter out;
    private Controlled nave;
    private boolean closed = false;
    private boolean disconnected = false; // si se desconecta y pasan 2 minutos no intentaremos reconectarlo
    private int id;
    private Color color;
    private KillerClient client;

    public KillerPad(Socket sock, String cliAddr, KillerGame kg, int id) {

        this.clientSock = sock;
        this.cliAddr = cliAddr;

        this.kg = kg;

        this.id = id;

        // Get I/O streams from the socket
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            this.out = new PrintWriter(clientSock.getOutputStream(), true);
        } catch (IOException ex) {
            System.out.println("Error creando socket KillerPad");
        }

        this.client = new KillerClient(cliAddr, sock.getPort(), this, this.kg, true, this.kg.getServer().getPort());
        new Thread(client).start();
    }

    @Override
    public void run() {

        try {

            while (!disconnected) {
                int t = 0;
                while (closed && !disconnected) {
                    Thread.sleep(100);
                    t++;
                    if (t > 300) {
                        disconnected = true;
                    }
                }
                if (!disconnected) {
                    System.out.println("Pad Conectado");
                    this.kg.getConnectPanel().updatePads();
                    while (!closed) {
                        processClient(in, out);
                        Thread.sleep(5);
                    }
                    this.kg.removePad(this);
                    System.out.println("Pad Desconectado");
                }
            }

        } catch (Exception e) {
            System.out.println("Error killer Pad");
        }
        System.out.println("Pad Bye");
        if (this.kg.isStarted()) {
            if (!this.nave.isEnviado()) {
                Controlled c = this.kg.findNave(id);
                if (c != null) {
                    c.clear();
                    this.kg.removeControlled(id);
                }

            } else {
                this.kg.getNextServer().sendCommand("quit" + this.kg.getId() + "&" + id);
            }
        }
    }

    private void closeLink() {
        try {
            out.println("bye"); // tell server
            clientSock.close();
            this.kg.removePad(this);
            System.out.println("Socket cerrado");
        } catch (Exception e) {
            System.out.println(e);
        }
        //System.exit(0);
    }

    private void doRequest(String line, PrintWriter out) {

        if (this.kg.isStarted()) {
            if (line.length() == 2) {
                if (!this.nave.isEnviado()) {
                    this.nave.setVelocidades(line);
                } else {
                    //enviar relay a otros ordenadores
                    this.kg.getNextServer().sendCommand("relay" + this.nave.getId() + "&" + line);
                }
            }
            if (line.length() >= 5) {
                if (line.substring(0, 5).equals("shoot")) {
                    if (!this.nave.isEnviado()) {
                        this.nave.shoot();
                    } else {
                        this.kg.getNextServer().sendCommand("relay" + this.nave.getId() + "&" + line);
                    }
                }
            }

        } else {
            if (line.length() >= 5) {
                if (line.substring(0, 5).equals("color")) {
                    int red = Integer.parseInt(line.substring(5, line.indexOf("&")));

                    String text = line.substring(line.indexOf("&") + 1);
                    String sgreen = text.substring(0, text.indexOf("&"));
                    int green = Integer.parseInt(sgreen);

                    String sblue = text.substring(text.indexOf("&") + 1);

                    int blue = Integer.parseInt(sblue);

                    this.color = new Color(red, green, blue);
                }
            }
        }
        if (line.length() >= 5) {
            if (line.substring(0, 5).equals("comok")) {
                this.client.resetTimer();
            }
        }
    }

    public Color getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

    public Controlled getNave() {
        return this.nave;
    }

    public boolean isClosed() {
        return this.closed;
    }

    private void processClient(BufferedReader in, PrintWriter out) {
        String line;
        String response = null;
        boolean done = false;
        try {
            while (!done) {
                if ((line = in.readLine()) == null) { //QUEDA PENJAT
                    done = true;
                } else {
                //    System.out.println("Pad msg: " + line);
                    if (line.trim().equals("bye")) {
                        done = true;
                        this.closed = true;
                        this.disconnected = true;
                        this.closeLink();
                        if (this.kg.isStarted()) {
                            if (!this.nave.isEnviado()) {
                                this.kg.findNave(id).clear();
                                this.kg.removeControlled(id);
                            } else {
                                this.kg.getNextServer().sendCommand("quit" + this.kg.getId() + "&" + id);
                            }
                        }
                    } else {

                        doRequest(line, out);

                    }
                }

            }
        } catch (IOException e) {
            this.closed = true;
        } catch (Exception e) {
            this.closed = true;
        }
    } // end of processClient( )

    public void setNave(Controlled nave) {

        this.nave = nave;

    }

    public void sendCommand(String command) {
        try {
            out.println(command);
        //    System.out.println(command + "sended");
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

        } catch (IOException ex) {

        }

    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    public void conectionFall() {
        try {
            this.clientSock.close();
            this.closed = true;
        } catch (IOException ex) {

        }
    }

}
