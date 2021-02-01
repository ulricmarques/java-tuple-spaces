package Communication;

import Communication.Packet.Action;
import River.SpaceController;
import River.Tuples.Root;
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
    
    public SpaceController spaceController;
    
    public Server(int port) {
        this.portNumber = port;
        spaceController = new SpaceController();
        spaceController.AddTuple();
        spaceController.AddTuple("nuvem1");
        spaceController.AddTuple("nuvem1", "host1");
        spaceController.AddTuple("nuvem1", "host1", "vm1");
        spaceController.AddTuple("nuvem1", "host1", "vm1", "p1");
        spaceController.AddTuple("nuvem2");
        spaceController.AddTuple("nuvem2", "host1");
        spaceController.AddTuple("nuvem2", "host1", "vm1");
        spaceController.AddTuple("nuvem1", "host2");
        
        System.out.println("Before");
        spaceController.printSpace();
        
        spaceController.RemoveTuple("nuvem1", "host2");
        
        System.out.println("After");
        spaceController.printSpace();
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
            Packet packet = null;
            try {
                while ((packet = (Packet) input.readObject()) != null) {
                    Action action = packet.getAction();
                    System.out.println("Recebeu action: " + action);
                    if (action.equals(Action.CONNECT)) {
                        boolean isConnected = connect(packet, output);
                        if (isConnected) {
                            mapOnlines.put(packet.getName(), output);
                            System.out.println("Cliente conectado: " + mapOnlines.size());
                            sendOnlines();
                        }
                    } else if (action.equals(Action.DISCONNECT)) {
                        disconnect(packet, output);
                        sendOnlines();
                        return;
                    } else if (action.equals(Action.SEND_ALL)) {
                        sendAll(packet);
                    } else if (action.equals(Action.UPDATE)) {
                        Root currentRoot = spaceController.getCloudList();
                        
                        Packet newPacket = new Packet();
                        packet.setAction(Action.UPDATE);
                        packet.setContent(currentRoot);                                                                                 
                        sendAll(packet);
                    }
                } 
            } catch (ClassNotFoundException | IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean connect(Packet packet, ObjectOutputStream output) {
        if (mapOnlines.size() == 0) {
            packet.setContent("YES");
            send(packet, output);
            System.out.println("map size: " + mapOnlines.size());
            return true; 
        }
        
        //Checa se o nome já está na lista de clientes
        if (mapOnlines.containsKey(packet.getName())) {
            packet.setContent("NO");
            send(packet, output);
            return false;
        } else {
            packet.setContent("YES");
            send(packet, output);
            return true;
        }
        
        
    }

    public void disconnect(Packet packet, ObjectOutputStream output) {
        mapOnlines.remove(packet.getName());

        packet.setContent("até logo!");
        sendAll(packet);
        System.out.println("User " + packet.getName() + " desconectou");
    }

    public void send(Object packet, ObjectOutputStream output) {
        try {
            output.writeObject(packet);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void sendAll(Packet packet) {
        System.out.println("Chegou no sendAll: " + mapOnlines.size());
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (!kv.getKey().equals(packet.getName())) {
                try {
                    kv.getValue().writeObject(packet);
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

        Packet packet = new Packet();
        packet.setAction(Action.USERS_ONLINE);
        packet.setSetOnlines(setNames);

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            packet.setName(kv.getKey());
            try {
                kv.getValue().writeObject(packet);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
