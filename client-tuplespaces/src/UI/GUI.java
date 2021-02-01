package UI;

import Communication.Client;
import java.awt.CardLayout;
import java.io.Serializable;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Ulric
 */
public class GUI implements Serializable{
        
    protected JFrame window;
    
    protected JPanel switchPanels; 
   
    public Client client;
    
    public SetupScreen setupScreen;
    
    public MainScreen mainScreen;
    
    public Socket socket;
    
   
    public GUI() {
        window = new JFrame("Cliente - Espaço de Tuplas");
        
        switchPanels = new JPanel(new CardLayout());
        
        setupScreen = new SetupScreen(this);
        mainScreen = new MainScreen(this);
        switchPanels.add(this.setupScreen.panelJoin, "setup");
        switchPanels.add(this.mainScreen.panelMain, "main");
        
        window.add(switchPanels);
        window.setResizable(false); 
        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true); 
    }
    

}
