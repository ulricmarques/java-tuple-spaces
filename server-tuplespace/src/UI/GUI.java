package UI;

import Communication.Server;
import java.awt.CardLayout;
import java.io.Serializable;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Ulric
 */
public class GUI implements Serializable{
        
    protected JFrame window;
    
    protected JPanel switchPanels; 
   
    public Server server;
    
    public SetupScreen setupScreen;
    
    public MainScreen mainScreen;
    
   
    public GUI() {
        window = new JFrame("Servidor - Espaço de Tuplas");
        
        switchPanels = new JPanel(new CardLayout());
        
        setupScreen = new SetupScreen(this);
        mainScreen = new MainScreen(this);
        switchPanels.add(this.setupScreen.panelHost, "setup");
        switchPanels.add(this.mainScreen.panelMain, "main");
        
        window.add(switchPanels);
        window.setResizable(false); 
        window.setSize(300, 400);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true); 
    }

}
