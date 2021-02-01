package Communication;

import Communication.Packet.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ulric
 */
public class Server implements Runnable {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();
    private int portNumber;

    
    public Server(int port) {
        this.portNumber = port;      
    }
    
    public int getPort(){
        return this.portNumber;
    }
    
    public boolean initializeServer(){
        try {
            serverSocket = new ServerSocket(this.portNumber);
            System.out.println("Servidor criado!");
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } 
    }
    
    
    @Override
    public void run() {
        this.startListener();
    }
    
    public void startListener(){
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

            new Thread(new ListenerSocket(socket)).start();
        }
    }

    private class ListenerSocket implements Runnable {

        private ObjectOutputStream output;
        private ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream (socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            Packet message = null;
            try {
                while ((message = (Packet) input.readObject()) != null) {
                    Action action = message.getAction();

                    if (action.equals(Action.CONNECT)) {
                        boolean isConnected = connect(message, output);
                        if (isConnected) {
                            mapOnlines.put(message.getName(), output);
                            System.out.println("Cliente conectado: " + mapOnlines.size());
                            sendOnlines();
                        }
                    } else if (action.equals(Action.DISCONNECT)) {
                        disconnect(message, output);
                        sendOnlines();
                        return;
                    } else if (action.equals(Action.SEND_ALL)) {
                        sendAll(message);
                    }
                } 
            } catch (ClassNotFoundException | IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean connect(Packet message, ObjectOutputStream output) {
        if (mapOnlines.size() == 0) {
            message.setContent("YES");
            send(message, output);
            System.out.println("map size: " + mapOnlines.size());
            return true; 
        }
        
        //Checa se o nome já está na lista de clientes
        if (mapOnlines.containsKey(message.getName())) {
            message.setContent("NO");
            send(message, output);
            return false;
        } else {
            message.setContent("YES");
            send(message, output);
            return true;
        }
        
        
    }

    public void disconnect(Packet message, ObjectOutputStream output) {
        mapOnlines.remove(message.getName());

        message.setContent("até logo!");
        sendAll(message);
        System.out.println("User " + message.getName() + " desconectou");
    }

    public void send(Object message, ObjectOutputStream output) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void sendAll(Packet message) {
        System.out.println("Chegou no sendAll: " + mapOnlines.size());
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (!kv.getKey().equals(message.getName())) {
                message.setAction(Action.SEND_ONE);
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        System.out.println("Enviou");
    }

    private void sendOnlines() {
        Set<String> setNames = new HashSet<String>();
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            setNames.add(kv.getKey());
        }

        Packet message = new Packet();
        message.setAction(Action.USERS_ONLINE);
        message.setSetOnlines(setNames);

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            message.setName(kv.getKey());
            try {
                kv.getValue().writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
