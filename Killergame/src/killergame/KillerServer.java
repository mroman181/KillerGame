package killergame;

import java.net.InetAddress;
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

    public static int getPort() {
        return PORT;
    }

    @Override
    public void run() {

        ServerSocket serverSock = null;
        InetAddress inetAddress;
        for (int i = 0; i < 100 && serverSock == null; i++) {

            try {
                serverSock = new ServerSocket(PORT + i);

                inetAddress = InetAddress.getLocalHost();
                this.kg.setId(inetAddress.getHostAddress() + "i" + (PORT + i));

            } catch (Exception e) {
                System.out.println("Puerto " + (i + PORT) + " ya en uso");
            }

        }
        Socket clientSock;
        
        if (serverSock != null) {
            while (true) {
                try {
                    System.out.println("Waiting for a client...");
                    clientSock = serverSock.accept();
                    ConnectionHandler ch = new ConnectionHandler(this.kg, clientSock);
                    new Thread(ch).start();
                    Thread.sleep(50);
                } catch (Exception e) {
                    System.out.println("Error Servidor");
                }
            }
        }else{
            System.out.println("No se ha podido usar ninguna dirección como servidor");
        }

    }
}
