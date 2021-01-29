package UI;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import Communication.Client;
import Communication.Packet;
import Communication.Packet.Action;

/**
 *
 * @author Ulric
 */
public class SetupScreen implements ActionListener{
   
    protected GUI parentGUI;
    
    public final JPanel panelJoin;
    
    private final JTextField inputHost;
    private final JTextField inputNameJoin;
    private final JTextField inputClientPort;

    private final JButton runJoin;
    
    private final JLabel labelIP;
    private final JLabel labelClientPort;
    private final JLabel labelNameJoin;
    
    public SetupScreen(GUI parentGUI)  {
        this.parentGUI = parentGUI;
        
        // Entrar
        panelJoin = new JPanel();
        panelJoin.setLayout(null); 
        panelJoin.setSize(300, 400);
        
        inputNameJoin = new JTextField();
        inputNameJoin.setText("Cliente");
        inputNameJoin.setBounds(60, 60, 400, 40);
        inputNameJoin.addActionListener(this);
        labelNameJoin = new JLabel("Digite seu nome:");
        labelNameJoin.setBounds(60, 30, 400, 40);

        inputHost = new JTextField();
        inputHost.setBounds(60, 130, 400, 40);
        inputHost.setText("localhost");
        inputHost.addActionListener(this);
        labelIP = new JLabel("Digite o IP");
        labelIP.setBounds(60, 100, 300, 40);

        inputClientPort = new JTextField();
        inputClientPort.setBounds(60, 200, 400, 40);
        inputClientPort.addActionListener(this);
        inputClientPort.setText("5000");
        labelClientPort = new JLabel("Digite a porta");
        labelClientPort.setBounds(60, 170, 300, 40);
        
        runJoin = new JButton("Iniciar cliente");
        runJoin.setBounds(200, 250, 120, 50);
        runJoin.addActionListener(this);
        
        panelJoin.add(runJoin);
        panelJoin.add(labelIP);
        panelJoin.add(inputHost);
        panelJoin.add(labelClientPort);
        panelJoin.add(inputClientPort);
        panelJoin.add(labelNameJoin);
        panelJoin.add(inputNameJoin);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        CardLayout changePages = (CardLayout) (parentGUI.switchPanels.getLayout());
        
        if (e.getSource() == runJoin  && (inputClientPort.getText().length() < 1 || inputNameJoin.getText().length() < 1)) {

            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos.");

        }
        if (e.getSource() == runJoin  && inputClientPort.getText().length() > 0 && inputNameJoin.getText().length() > 0) {
            
            int portNumber = Integer.parseInt(inputClientPort.getText());
            String hostNumber = inputHost.getText();
            String playerName = inputNameJoin.getText();
      
            parentGUI.client = new Client(hostNumber, portNumber, playerName, parentGUI);
            boolean connectionAccepted = parentGUI.client.connect();
            
            if(!connectionAccepted){
                JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor. "
                        + "Verifique os dados digitados e tente novamente.", "Falha ao conectar", JOptionPane.ERROR_MESSAGE);
            }
            else{
                changePages.show(parentGUI.switchPanels, "main");
                parentGUI.client.startListener();
                Packet msg = new Packet();
                msg.setAction(Action.CONNECT);
                msg.setName(playerName);
                parentGUI.socket = parentGUI.client.getSocket();
                parentGUI.client.send(msg);
                
                Packet msg2 = new Packet();
                msg2.setAction(Action.UPDATE);
                parentGUI.client.send(msg2);
 
            }
        }
    }   
}