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

/**
 *
 * @author Ulric
 */
public class SetupScreen implements ActionListener{
   
    protected GUI parentGUI;
    
    public final JPanel panelJoin;
    
    private final JTextField inputNameJoin;

    private final JButton runJoin;
    
    private final JLabel labelNameJoin;
    
    public SetupScreen(GUI parentGUI)  {
        this.parentGUI = parentGUI;
        
        panelJoin = new JPanel();
        panelJoin.setLayout(null); 
        
        inputNameJoin = new JTextField();
        inputNameJoin.setText("Cliente");
        inputNameJoin.setBounds(20, 60, 305, 40);
        inputNameJoin.addActionListener(this);
        labelNameJoin = new JLabel("Digite seu nome:");
        labelNameJoin.setBounds(20, 30, 305, 40);

        
        
        runJoin = new JButton("Iniciar cliente");
        runJoin.setBounds(115, 150, 120, 50);
        runJoin.addActionListener(this);
        
        panelJoin.add(runJoin);
        panelJoin.add(labelNameJoin);
        panelJoin.add(inputNameJoin);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        CardLayout changePages = (CardLayout) (parentGUI.switchPanels.getLayout());
        
        if (e.getSource() == runJoin  &&  inputNameJoin.getText().length() < 1) {

            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos.");

        }
        if (e.getSource() == runJoin  && inputNameJoin.getText().length() > 0) {
            
            String playerName = inputNameJoin.getText();
      
            parentGUI.client = new Client(playerName, parentGUI);            
            changePages.show(parentGUI.switchPanels, "main");
            parentGUI.client.startListener();               
            parentGUI.client.send(null, River.Tuples.ClientMessage.Action.UPDATE);
            
        }
    }   
}