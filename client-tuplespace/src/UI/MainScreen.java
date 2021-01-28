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
        
        // Hospedar
        panelMain = new JPanel();
        panelMain.setLayout(null); 
        panelMain.setSize(300, 400);

        labelServerPort = new JLabel("Servidor rodando...");
        labelServerPort.setBounds(250, 200, 140, 40);

        panelMain.add(labelServerPort);
       
    }
   
}