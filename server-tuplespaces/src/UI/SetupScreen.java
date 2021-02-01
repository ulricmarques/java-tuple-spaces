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
    
    private final JTextField inputServerPort;

    private final JButton runHost;
   
    private final JLabel labelServerPort;
    
    protected GUI parentGUI;


    public SetupScreen(GUI parentGUI)  {
        
        this.parentGUI = parentGUI;
        
        // Hospedar
        panelHost = new JPanel();
        panelHost.setLayout(null); 
        panelHost.setSize(300, 400);

        inputServerPort = new JTextField();
        inputServerPort.setBounds(170, 50, 100, 40);
        inputServerPort.addActionListener(this);
        inputServerPort.setText("5000");
        labelServerPort = new JLabel("Digite a porta:");
        labelServerPort.setBounds(50, 50, 140, 40);
        
        runHost = new JButton("Iniciar servidor");
        runHost.setBounds(70, 100, 140, 40);
        runHost.addActionListener(this);
        
        panelHost.add(runHost);
        panelHost.add(inputServerPort);
        panelHost.add(labelServerPort);

        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        CardLayout changePages = (CardLayout) (parentGUI.switchPanels.getLayout());
        
        if (e.getSource() == runHost && (inputServerPort.getText().length() < 1)) {
            JOptionPane.showMessageDialog(null, "Campo porta não foi preenchido");
        }

        if (e.getSource() == runHost && inputServerPort.getText().length() > 0) {
            int portNumber = Integer.parseInt(inputServerPort.getText());
 
            parentGUI.server = new Server(portNumber);
            boolean serverCreated = parentGUI.server.initializeServer();
            
            if(!serverCreated){
                JOptionPane.showMessageDialog(null, "Não foi possível criar o servidor. "
                        + "A porta digitada pode estar ocupada.","Falha ao criar servidor", JOptionPane.ERROR_MESSAGE);
            }
            else {
                changePages.show(parentGUI.switchPanels, "main");
                new Thread(parentGUI.server).start();
            }
        }

    }   
}