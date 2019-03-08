/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package killergame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dam2a21
 */
public class ConnectionHandler implements Runnable {

    private KillerGame kg;
    private int padsId[];
    private int jugadores = 0;
    private Socket clientSock;

    public ConnectionHandler(KillerGame kg, Socket sock) {
        this.kg = kg;
        this.padsId = new int[4];
        this.clientSock = sock;
    }

    private void connect() throws IOException {

        String cliAddr = clientSock.getInetAddress().getHostAddress();

        PrintWriter out = null;
        BufferedReader in = null;
        try {
            out = new PrintWriter(clientSock.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error metodo connect server");
        }
        String line;
        boolean done = false;
        try {
            while (!done) {
                if ((line = in.readLine()) == null) {
                    done = true;
                } else {
                    done = true;
                    System.out.println("Client msg: " + line);
                    if (line.length() > 5) {
                        String sport = line.substring(8);
                        int port = Integer.parseInt(sport);
                        if (line.substring(0, 8).equals("connectn")) {
                            this.kg.getPreviousServer().setSocket(clientSock, cliAddr, port);
                            this.kg.getConnectPanel().setTextTextps("Conectado");
                        }
                        if (line.substring(0, 8).equals("connectp")) {
                            this.kg.getNextServer().setSocket(clientSock, cliAddr, port);
                            this.kg.getConnectPanel().setTextTextns("Conectado");
                        }

                    }
                    if (line.trim().equals("mcone")) {
                        
                        if (this.kg.getSizePads() < 4 && !this.kg.isStarted()) {
                            String lastIP = cliAddr;
                            for (int i = 0; i < 3; i++) {
                                lastIP = lastIP.substring(lastIP.indexOf('.') + 1);
                            }
                            int id = Integer.parseInt(lastIP);
                            this.kg.crearKillerPad(clientSock, cliAddr, id);
                        }else{
                            out.println("full");
                        }
                    }

                }
            }
        } catch (IOException e) {
            System.out.println("error connecting");
        }

    }

    @Override
    public void run() {
        try {
            this.connect();
        } catch (IOException ex) {

        }
    }
}
