package killergame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KillerClient implements Runnable {

    private int PORT; // server details
    private String HOST;
    private Socket sock;
    private BufferedReader in; // i/o for the client
    private PrintWriter out;
    private VisualHandler vh;
    private KillerGame kg;
    private boolean isPad;
    private KillerPad pad;
    private int t = 0;

    public KillerClient(VisualHandler vh, KillerGame kg) {
        this.kg = kg;
        this.vh = vh;
        this.isPad = false;
    }

    public KillerClient(String host, int port, VisualHandler vh, KillerGame kg) {
        this.kg = kg;
        this.PORT = port;
        this.HOST = host;
        this.vh = vh;
        this.isPad = false;
    }

    public KillerClient(String host, int port, KillerPad pad, KillerGame kg, boolean isPad) {
        this.kg = kg;
        this.PORT = 1234;
        this.HOST = host;
        this.pad = pad;
        this.isPad = isPad;
    }

    private void makeContact() throws IOException {

        sock = new Socket(HOST, PORT);
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out = new PrintWriter(sock.getOutputStream(), true);

    }

    @Override
    public void run() {

        if (this.isPad) {

            while (true && !this.pad.isDisconnected()) {
                //try to connect
                while (this.pad.isClosed() && !this.pad.isDisconnected()) {
                    if (this.pad.isClosed()) {
                        if (tryToConnectPad()) {

                            this.pad.setSocket(sock, HOST);

                            if (!this.kg.addPad(pad)) {
                                this.pad.setDisconnected(true);
                            }
                        }
                    }

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {

                    }
                }
                this.t = 0;
                while (!this.pad.isClosed() && !this.pad.isDisconnected()) {
                    //El visual handler esta conectado 
                    try {
                        Thread.sleep(100);
                        this.t++;
                        if (t > 10) {
                            this.pad.conectionFall();

                        } else {
                            this.pad.sendCommand("comu");
                        }
                    } catch (InterruptedException ex) {

                    }
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {

                }
            }

        } else {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {

            }
            while (true) {
                //try to connect
                while (this.vh.isClosed()) {
                    if (this.vh.isClosed()) {
                        if (tryToConnect()) {
                            this.vh.setSocket(sock, HOST);
                        }
                    }

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {

                    }
                }
                this.changeText(this.vh.isNext(), "Conectado");
                this.t = 0;
                while (!this.vh.isClosed()) {
                    //El visual handler esta conectado 
                    try {
                        Thread.sleep(1000);
                        this.t++;
                        if (t > 5) {
                            this.vh.conectionFall();

                        } else {
                            this.vh.sendCommand("comun");
                        }
                    } catch (InterruptedException ex) {

                    }
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {

                }
            }
        }

    }

    public void setSocket(String ip, int port) {

        this.PORT = port;
        this.HOST = ip;

    }

    private boolean tryToConnect() {
        String comand;
        boolean next = this.vh.isNext();
        if (next) {
            comand = "connectn" + KillerServer.getPort();
        } else {
            comand = "connectp" + KillerServer.getPort();
        }
        try {
            makeContact();
            out.println(comand);

            return true;
        } catch (Exception ex) {
            if (this.vh.isClosed()) {
                this.changeText(next, "No Conectado");
            }
        }
        return false;
    }

    private boolean tryToConnectPad() {
        String comand;
        comand = "mcone";
        System.out.println("trying mcone");
        try {
            makeContact();
            out.println(comand);

            return true;
        } catch (Exception ex) {

        }
        return false;
    }

    private void changeText(boolean next, String text) {
        if (next) {
            this.kg.getConnectPanel().setTextTextns(text);
        } else {

            this.kg.getConnectPanel().setTextTextps(text);
        }
    }

    public void resetTimer() {
        this.t = 0;
    }

}
