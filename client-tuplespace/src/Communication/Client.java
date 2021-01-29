
package Communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import Communication.Packet.Action;
import River.Tuples.Root;
import UI.GUI;


/**
 *
 * @author Ulric
 */
public class Client {
    
    private Socket socket;
    public ObjectOutputStream output;
    public ObjectInputStream input;
    private String ip;
    private int portNumber;
    public String clientName;
  
    public GUI parentGUI;
    
    public Client(String host, int port, String name, GUI parentGUI ){
        this.parentGUI = parentGUI;
        this.ip = host;
        this.portNumber = port;
        this.clientName = name;
    }
    
    public boolean connect() {
        try {
            this.socket = new Socket(this.ip, this.portNumber);
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (UnknownHostException ex) {          
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
    
    public Socket getSocket(){
        return socket;
    }
    
    public void send(Packet packet) {
        try {
            output.writeObject(packet);
            System.out.println("Enviou a mensagem: ");
        } catch (IOException ex) {
            System.out.println("Erro no send: " + ex);
        }
    }
    
    public void startListener(){
       new Thread(new ListenerSocket(socket)).start();
    }
    
    private class ListenerSocket implements Runnable {

        public ListenerSocket(Socket socket) {
            try {
                input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            Packet packet = null;
            try {
                while ((packet = (Packet) input.readObject()) != null) {
                    Packet.Action action = packet.getAction();
                    
                    System.out.println("Recebeu action: " + action);

                    if (action.equals(Action.CONNECT)) {
                        System.out.println("Conectado.");
                    } else if (action.equals(Action.DISCONNECT)) {
//                        disconnected();
//                        socket.close();
                    } else if (action.equals(Action.USERS_ONLINE)) {
//                        refreshOnlines(packet);
                    } else if (action.equals(Action.UPDATE)) {
                        Root currentRoot = (Root) packet.getContent();
                        System.out.println("Caiu no update: " + currentRoot.cloudList.get(0).name);
                        parentGUI.mainScreen.rebuildTree(currentRoot);
                    }
                }
            } catch (IOException ex) {
                System.out.println("Erro: " + ex);
            } catch (ClassNotFoundException ex) {
                System.out.println("Erro: " + ex);
            }
        }
    }
}