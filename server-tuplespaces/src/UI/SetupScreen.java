package UI;

import Communication.Server;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Ulric
 */
public class SetupScreen implements ActionListener{
   
    public final JPanel panelHost;
    
    private final JButton runHost;
   
    private final JLabel labelServer;
    
    protected GUI parentGUI;


    public SetupScreen(GUI parentGUI)  {
        
        this.parentGUI = parentGUI;
        
        panelHost = new JPanel();
        panelHost.setLayout(null); 
        panelHost.setSize(300, 400);

        labelServer = new JLabel("Execute o Apache River antes de iniciar o servidor");
        labelServer.setBounds(20, 30, 305, 40);
        
        runHost = new JButton("Iniciar servidor");
        runHost.setBounds(115, 150, 120, 50);
        runHost.addActionListener(this);
        
        panelHost.add(runHost);
        panelHost.add(labelServer);

        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        CardLayout changePages = (CardLayout) (parentGUI.switchPanels.getLayout());
        
        if (e.getSource() == runHost) {
            parentGUI.server = new Server();
            changePages.show(parentGUI.switchPanels, "main");
            new Thread(parentGUI.server).start();
            
        }

    }   
}