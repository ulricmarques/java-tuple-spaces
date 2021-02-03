package UI;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Ulric
 */
public class MainScreen {
   
    public final JPanel panelMain;

    private final JLabel labelServerPort;
    
    protected GUI parentGUI;

    public MainScreen(GUI parentGUI)  {
        
        this.parentGUI = parentGUI;
        
        panelMain = new JPanel();
        panelMain.setLayout(null); 

        labelServerPort = new JLabel("O servidor foi iniciado!");
        labelServerPort.setBounds(115, 150, 140, 50);

        panelMain.add(labelServerPort);
       
    }
   
}