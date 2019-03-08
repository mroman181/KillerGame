package killergame;

import java.net.ServerSocket;
import java.net.Socket;

public class KillerServer implements Runnable {

    private static int PORT = 1234;
    private KillerGame kg;

    public KillerServer(KillerGame kg) {
        this.kg = kg;
    }

    public KillerGame getKillerGame() {
        return kg;
    }

    public static int getPort(){
        return PORT;
    }
    
    @Override
    public void run() {

        try {
                                    
            ServerSocket serverSock = new ServerSocket(PORT);
            Socket clientSock;

            while (true) {
                System.out.println("Waiting for a client...");
                clientSock = serverSock.accept();                
                ConnectionHandler ch = new ConnectionHandler(this.kg, clientSock);
                new Thread(ch).start(); 
                Thread.sleep(50);
            }
        } catch (Exception e) {
            System.out.println("Error Servidor");
        }

    }
}

    
