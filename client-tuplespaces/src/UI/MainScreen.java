package UI;

import River.Tuples.ClientMessage.Action;
import River.Tuples.Cloud;
import River.Tuples.Host;
import River.Tuples.Migration;
import River.Tuples.Root;
import River.Tuples.Process;
import River.Tuples.ProcessMessage;
import River.Tuples.VirtualMachine;
import java.awt.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author Ulric
 */
public class MainScreen {
   
    public final JPanel panelMain;
    protected JTextArea display;
    protected JScrollPane scrollBar;
    protected GUI parentGUI;
    
    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    
    private DefaultMutableTreeNode selectedNode;

    public MainScreen(GUI parentGUI)  {
        
        this.parentGUI = parentGUI;
        
        panelMain = new JPanel();
        panelMain.setLayout(null); 
        panelMain.setSize(350, 600);
        
        display = new JTextArea();
        display.setEditable(false);
        scrollBar = new JScrollPane(display);
        scrollBar.setBounds(20, 430, 300, 130);

        panelMain.add(scrollBar);
        
        rootNode = new DefaultMutableTreeNode("Raíz");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new CustomTreeCellRenderer());
        tree.addMouseListener(getMouseListener());
        tree.setRowHeight(20);
        ToolTipManager.sharedInstance().registerComponent(tree);

        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setBounds(20, 20, 300, 400);
        
        panelMain.add(treeScrollPane);
       
    }
    
    public void setDisplayText(String text) {
    	String temp = this.display.getText();  	
    	this.display.setText(temp + "\n" + text);    	
    }
       
    public void rebuildTree(Root root){
        rootNode.removeAllChildren();
        
        for (Cloud cloud : root.cloudList){
            DefaultMutableTreeNode cloudNode = addNode(null, cloud, true);
            for (Host host : cloud.hostList) {
                DefaultMutableTreeNode hostNode = addNode(cloudNode, host, true);
                for(VirtualMachine virtualMachine : host.virtualMachineList){
                    DefaultMutableTreeNode virtualMachineNode = addNode(hostNode, virtualMachine, true);
                    for(Process process : virtualMachine.processList){
                        addNode(virtualMachineNode, process, true);
                    }
                }
            }
        }
        
        treeModel.reload();
        
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        
    }

    public DefaultMutableTreeNode addNode(DefaultMutableTreeNode parent,
                                            Object child, 
                                            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = 
                new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }
	
	
        treeModel.insertNodeInto(childNode, parent, 
                                 parent.getChildCount());

        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }
    
    private MouseListener getMouseListener() {
        return new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent arg0) {
                if(arg0.getButton() == MouseEvent.BUTTON3){
                    TreePath pathForLocation = tree.getPathForLocation(arg0.getPoint().x, arg0.getPoint().y);
                    if(pathForLocation != null){
                        selectedNode = (DefaultMutableTreeNode) pathForLocation.getLastPathComponent();
                        tree.setComponentPopupMenu(getPopUpMenu());
                    } else {
                        selectedNode = null;
                        JOptionPane.showMessageDialog(panelMain, "Nada foi selecionado.", "Seleção vazia", JOptionPane.WARNING_MESSAGE);
                    }

                }
                super.mousePressed(arg0);
            }
        };
    }
    
    private JPopupMenu getPopUpMenu() {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem addItem = new JMenuItem("Adicionar");
        addItem.addActionListener(getAddActionListener());
        
        if(selectedNode.getLevel() < 4) {       	
        	menu.add(addItem);
        }
        
        JMenuItem removeItem = new JMenuItem("Remover");
        removeItem.addActionListener(getRemoveActionListener());        
        if(selectedNode.getLevel() > 0) {       	
        	menu.add(removeItem);
        }
        
        
        JMenuItem migrateItem = new JMenuItem("Migrar");
        migrateItem.addActionListener(getMigrateActionListener());
        if(selectedNode.getLevel() > 1) {       	
        	menu.add(migrateItem);
        }
                
        JMenuItem messageItem = new JMenuItem("Enviar mensagem");
        messageItem.addActionListener(getMessageActionListener());       
        if(selectedNode.getLevel() == 4) {
        	menu.add(messageItem);
        }
       
        return menu;
    }
    
    public boolean checkNameExistence(Enumeration<TreeNode> children, String name) {
    	boolean nameAlreadyExists = false;
    	
    	while(children.hasMoreElements()) {
    		DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) children.nextElement();
    		String tempName = "";
    		if (tempNode.getUserObject() instanceof Cloud) {
                Cloud tempObject = (Cloud) tempNode.getUserObject();
                tempName = tempObject.name;
            }
    		if (tempNode.getUserObject() instanceof Host) {
                Host tempObject = (Host) tempNode.getUserObject();
                tempName = tempObject.name;
            }
    		if (tempNode.getUserObject() instanceof VirtualMachine) {
                VirtualMachine tempObject = (VirtualMachine) tempNode.getUserObject();
                tempName = tempObject.name;
            }
    		if (tempNode.getUserObject() instanceof Process) {
                Process tempObject = (Process) tempNode.getUserObject();
                tempName = tempObject.name;
            }
    		
    		if(tempName.equals(name)) {
    			nameAlreadyExists = true;
    		}	    		
    	}  	
    	return nameAlreadyExists;  	
    }

    private ActionListener getAddActionListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(selectedNode != null){     
                	System.out.println("SelectedNode level: " + selectedNode.getLevel());
                    if (selectedNode.getLevel() == 0) {                   	
                    	String name = JOptionPane.showInputDialog(panelMain, "Digite o nome da nova nuvem:",
                    			"Adicionar nuvem", JOptionPane.PLAIN_MESSAGE);
                    	if(name != null && name.length() > 0) {
                    		
                    		if(checkNameExistence(selectedNode.children(), name)) {
                    			JOptionPane.showMessageDialog(panelMain, "Nome já existe no container, tente outro.", 
                    					"Nome já existe", JOptionPane.ERROR_MESSAGE);                 			
                    		}
                    		else {
                    			Cloud temp = new Cloud();
                        		temp.name = name;
                            	parentGUI.client.send(temp, Action.ADD); 
                    		}                   		
                    	}                    	                 	
                    } 
                    if (selectedNode.getLevel() == 1) {
                    	String name = JOptionPane.showInputDialog(panelMain, "Digite o nome do novo host:",
                    			"Adicionar host", JOptionPane.PLAIN_MESSAGE);
                    	if(name != null && name.length() > 0) {                   		
                    		if(checkNameExistence(selectedNode.children(), name)) {
                    			JOptionPane.showMessageDialog(panelMain, "Nome já existe no container, tente outro.", 
                    					"Nome já existe", JOptionPane.ERROR_MESSAGE);                 			
                    		}
                    		else {
                    			Host temp = new Host();
                        		temp.name = name;
                        		temp.parent = (Cloud) selectedNode.getUserObject();
                            	parentGUI.client.send(temp, Action.ADD); 
                    		}
                    	}                   	
                    } 
                    if (selectedNode.getLevel() == 2) {
                    	String name = JOptionPane.showInputDialog(panelMain, "Digite o nome da nova máquina virtual:",
                    			"Adicionar máquina virtual", JOptionPane.PLAIN_MESSAGE);
                    	if(name != null && name.length() > 0) {
                    		if(checkNameExistence(selectedNode.children(), name)) {
                    			JOptionPane.showMessageDialog(panelMain, "Nome já existe no container, tente outro.", 
                    					"Nome já existe", JOptionPane.ERROR_MESSAGE);                 			
                    		}
                    		else {
                    			VirtualMachine temp = new VirtualMachine();
                        		temp.name = name;
                        		temp.parent = (Host) selectedNode.getUserObject();
                            	parentGUI.client.send(temp, Action.ADD);                   		
                    		}
                    	}                 	                	
                    } 
                    if (selectedNode.getLevel() == 3) {
                    	String name = JOptionPane.showInputDialog(panelMain, "Digite o nome do novo processo:",
                    			"Adicionar processo", JOptionPane.PLAIN_MESSAGE);
                    	if(name != null && name.length() > 0) {
                    		if(checkNameExistence(selectedNode.children(), name)) {
                    			JOptionPane.showMessageDialog(panelMain, "Nome já existe no container, tente outro.", 
                    					"Nome já existe", JOptionPane.ERROR_MESSAGE);                 			
                    		}
                    		else {
                    			Process temp = new Process();
                        		temp.name = name;
                        		temp.parent = (VirtualMachine) selectedNode.getUserObject();
                            	parentGUI.client.send(temp, Action.ADD);
                    		} 
                    	}                    	               	
                    }               
                }
            }
        };
    }

    private ActionListener getRemoveActionListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
            	if(selectedNode != null){     
                	System.out.println("SelectedNode level: " + selectedNode.getLevel());
                    if (selectedNode.getLevel() == 1) {
                    	if(selectedNode.getChildCount() > 0) {
                    		JOptionPane.showMessageDialog(panelMain, "Essa nuvem não está vazia!", 
                					"Remover nuvem", JOptionPane.ERROR_MESSAGE); 
                    	}
                    	else {
                    		int confirm = JOptionPane.showConfirmDialog(panelMain, "Deseja realmente remover essa nuvem?",
                        			"Remover nuvem", JOptionPane.YES_NO_OPTION);
                        	if(confirm == 0){ 
                        		Cloud temp = (Cloud) selectedNode.getUserObject();
                            	parentGUI.client.send(temp, Action.REMOVE);                    		
                        	}                     		
                    	}               	                    		               		
                	}                    	                 	                    
                    if (selectedNode.getLevel() == 2) {
                    	if(selectedNode.getChildCount() > 0) {
                    		JOptionPane.showMessageDialog(panelMain, "Esse host não está vazio!", 
                					"Remover host", JOptionPane.ERROR_MESSAGE); 
                    	}
                    	else {
                    		int confirm = JOptionPane.showConfirmDialog(panelMain, "Deseja realmente remover esse host?",
                        			"Remover host", JOptionPane.YES_NO_OPTION);
                        	if(confirm == 0){ 
                        		Host temp = (Host) selectedNode.getUserObject();
                            	parentGUI.client.send(temp, Action.REMOVE);                    		
                        	}                     		
                    	}                  	                 	
                    } 
                    if (selectedNode.getLevel() == 3) {
                    	if(selectedNode.getChildCount() > 0) {
                    		JOptionPane.showMessageDialog(panelMain, "Essa máquina virtual não está vazia!", 
                					"Remover máquina virtual", JOptionPane.ERROR_MESSAGE); 
                    	}
                    	else {
                    		int confirm = JOptionPane.showConfirmDialog(panelMain, "Deseja realmente remover essa máquina virtual?",
                        			"Remover máquina virtual", JOptionPane.YES_NO_OPTION);
                        	if(confirm == 0){ 
                        		VirtualMachine temp = (VirtualMachine) selectedNode.getUserObject();
                            	parentGUI.client.send(temp, Action.REMOVE);                    		
                        	}                     		
                    	}           	                	
                    } 
                    if (selectedNode.getLevel() == 4) {                  	
                		int confirm = JOptionPane.showConfirmDialog(panelMain, "Deseja realmente remover esse processo?",
                    			"Remover processo", JOptionPane.YES_NO_OPTION);
                    	if(confirm == 0){ 
                    		Process temp = (Process) selectedNode.getUserObject();
                        	parentGUI.client.send(temp, Action.REMOVE);                    		
                    	}                     		               	          	               	
                    }               
                }
            }
        };
    }
    
    private ActionListener getMigrateActionListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(selectedNode != null){               	
                	if (selectedNode.getLevel() == 2) {
                		ArrayList<String> cloudOptions = new ArrayList<>();
                		ArrayList<DefaultMutableTreeNode> cloudNodes = new ArrayList<>();
                		Host selectedHost = (Host) selectedNode.getUserObject();    
                		String originalName = selectedHost.name;
                		Enumeration<DefaultMutableTreeNode> clouds = 
                				(Enumeration<DefaultMutableTreeNode>) selectedNode.getParent().getParent().children();
                        while(clouds.hasMoreElements()){
                        	DefaultMutableTreeNode tempNode = clouds.nextElement();
                        	Cloud tempCloud = (Cloud) tempNode.getUserObject();
                            String cloudName = tempCloud.name;
                            cloudOptions.add(cloudName);
                            cloudNodes.add(tempNode);                                                 
                        }
                        
                        Object[] options = cloudOptions.toArray();
                        String destinationCloud = (String) JOptionPane.showInputDialog(panelMain,
                                            "Selecione a nuvem de destino:",
                                            "Migrar host",
                                            JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                        
                        if(destinationCloud != null && destinationCloud.length() > 0 ){
                        	int index = cloudOptions.indexOf(destinationCloud);
                        	DefaultMutableTreeNode selectedCloudNode = cloudNodes.get(index);
                        	Cloud selectedCloud = (Cloud) selectedCloudNode.getUserObject();
                        	while(checkNameExistence(selectedCloudNode.children(), selectedHost.name)) {
                    			JOptionPane.showMessageDialog(panelMain, "Nome já existe no container, renomeie o host.", 
                    					"Nome já existe", JOptionPane.ERROR_MESSAGE);  
                    			
                    			String newName = JOptionPane.showInputDialog(panelMain, "Digite o novo nome: ", "Migrar host", JOptionPane.OK_CANCEL_OPTION);
                                if((newName != null) && (newName.length() > 0)){
                                    selectedHost.name = newName;                              
                                }
                                else{
                                    return;
                                }
                    		}
                        	
                        	Migration migration = new Migration(selectedCloud, selectedHost, originalName);
                        	parentGUI.client.send(migration, Action.MIGRATE_HOST);
                        }             		
                	}
                	if (selectedNode.getLevel() == 3) {
                		ArrayList<String> cloudOptions = new ArrayList<>();
                		ArrayList<DefaultMutableTreeNode> cloudNodes = new ArrayList<>();
                		VirtualMachine selectedVirtualMachine = (VirtualMachine) selectedNode.getUserObject();    
                		String originalName = selectedVirtualMachine.name;
                		Enumeration<DefaultMutableTreeNode> clouds = 
                				(Enumeration<DefaultMutableTreeNode>) selectedNode.getParent().getParent().getParent().children();
                        while(clouds.hasMoreElements()){
                        	DefaultMutableTreeNode tempNode = clouds.nextElement();
                        	Cloud tempCloud = (Cloud) tempNode.getUserObject();
                            String cloudName = tempCloud.name;
                            cloudOptions.add(cloudName);
                            cloudNodes.add(tempNode);                                                 
                        }
                        
                        Object[] options = cloudOptions.toArray();
                        String destinationCloud = (String) JOptionPane.showInputDialog(panelMain,
                                            "Selecione a nuvem de destino:",
                                            "Migrar máquina virtual",
                                            JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                        
                        if(destinationCloud != null && destinationCloud.length() > 0 ){
                        	int index = cloudOptions.indexOf(destinationCloud);
                        	DefaultMutableTreeNode selectedCloudNode = cloudNodes.get(index);                        	
                                             	
                        	ArrayList<String> hostOptions = new ArrayList<>();
                    		ArrayList<DefaultMutableTreeNode> hostNodes = new ArrayList<>();
                    		Enumeration<TreeNode> hosts = 
                    				selectedCloudNode.children();
                            while(hosts.hasMoreElements()){
                            	DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) hosts.nextElement();
                            	Host tempHost = (Host) tempNode.getUserObject();
                                String hostName = tempHost.name;
                                hostOptions.add(hostName);
                                hostNodes.add(tempNode);                                                 
                            }
                            
                            Object[] options2 = hostOptions.toArray();
                            String destinationHost = (String) JOptionPane.showInputDialog(panelMain,
                                                "Selecione o host de destino:",
                                                "Migrar máquina virtual",
                                                JOptionPane.PLAIN_MESSAGE, null, options2, options2[0]);
                            if(destinationHost != null && destinationHost.length() > 0 ){
                            	int index2 = hostOptions.indexOf(destinationHost);
                            	DefaultMutableTreeNode selectedHostNode = hostNodes.get(index2);
                            	Host selectedHost = (Host) selectedHostNode.getUserObject();
                            	
                            	String tempName = selectedVirtualMachine.name;
                            	while(checkNameExistence(selectedHostNode.children(), tempName)) {                           		
                        			JOptionPane.showMessageDialog(panelMain, "Nome já existe no container, renomeie a máquina virtual.", 
                        					"Nome já existe", JOptionPane.ERROR_MESSAGE);  
                        			
                        			String newName = JOptionPane.showInputDialog(panelMain, "Digite o novo nome: ",
                        					"Migrar máquina virtual", JOptionPane.OK_CANCEL_OPTION);
                                    if((newName != null) && (newName.length() > 0)){
                                        tempName = newName;                              
                                    }
                                    else{
                                        return;
                                    }
                        		}
                            	
                            	selectedVirtualMachine.name = tempName;
                            	
                            	Migration migration = new Migration(selectedHost, selectedVirtualMachine, originalName);
                            	parentGUI.client.send(migration, Action.MIGRATE_VIRTUAL_MACHINE);
                            }
                        }            		
                	}
                	if (selectedNode.getLevel() == 4) {
                		ArrayList<String> cloudOptions = new ArrayList<>();
                		ArrayList<DefaultMutableTreeNode> cloudNodes = new ArrayList<>();
                		Process selectedProcess = (Process) selectedNode.getUserObject();    
                		String originalName = selectedProcess.name;
                		Enumeration<DefaultMutableTreeNode> clouds = 
                				(Enumeration<DefaultMutableTreeNode>) selectedNode.getParent().getParent().getParent().getParent().children();
                        while(clouds.hasMoreElements()){
                        	DefaultMutableTreeNode tempNode = clouds.nextElement();
                        	Cloud tempCloud = (Cloud) tempNode.getUserObject();
                            String cloudName = tempCloud.name;
                            cloudOptions.add(cloudName);
                            cloudNodes.add(tempNode);                                                 
                        }
                        
                        Object[] options = cloudOptions.toArray();
                        String destinationCloud = (String) JOptionPane.showInputDialog(panelMain,
                                            "Selecione a nuvem de destino:",
                                            "Migrar processo",
                                            JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                        
                        if(destinationCloud != null && destinationCloud.length() > 0 ){
                        	int index = cloudOptions.indexOf(destinationCloud);
                        	DefaultMutableTreeNode selectedCloudNode = cloudNodes.get(index);
                        	
                        	ArrayList<String> hostOptions = new ArrayList<>();
                    		ArrayList<DefaultMutableTreeNode> hostNodes = new ArrayList<>();
                    		Enumeration<TreeNode> hosts = selectedCloudNode.children();
                            while(hosts.hasMoreElements()){
                            	DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) hosts.nextElement();
                            	Host tempHost = (Host) tempNode.getUserObject();
                                String hostName = tempHost.name;
                                hostOptions.add(hostName);
                                hostNodes.add(tempNode);                                                 
                            }
                            
                            Object[] options2 = hostOptions.toArray();
                            String destinationHost = (String) JOptionPane.showInputDialog(panelMain,
                                                "Selecione o host de destino:",
                                                "Migrar processo",
                                                JOptionPane.PLAIN_MESSAGE, null, options2, options2[0]);
                            if(destinationHost != null && destinationHost.length() > 0 ){
                            	int index2 = hostOptions.indexOf(destinationHost);
                            	DefaultMutableTreeNode selectedHostNode = hostNodes.get(index2);
                            	                           	
                            	ArrayList<String> virtualMachineOptions = new ArrayList<>();
                        		ArrayList<DefaultMutableTreeNode> virtualMachineNodes = new ArrayList<>();
                        		Enumeration<TreeNode> virtualMachines = selectedHostNode.children();
                                while(virtualMachines.hasMoreElements()){
                                	DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) virtualMachines.nextElement();
                                	VirtualMachine tempVirtualMachine = (VirtualMachine) tempNode.getUserObject();
                                    String virtualMachineName = tempVirtualMachine.name;
                                    virtualMachineOptions.add(virtualMachineName);
                                    virtualMachineNodes.add(tempNode);                                                 
                                }
                                
                                Object[] options3 = virtualMachineOptions.toArray();
                                String destinationVirtualMachine = (String) JOptionPane.showInputDialog(panelMain,
                                                    "Selecione o host de destino:",
                                                    "Migrar processo",
                                                    JOptionPane.PLAIN_MESSAGE, null, options3, options3[0]);
                                if(destinationVirtualMachine != null && destinationVirtualMachine.length() > 0 ){
                                	int index3 = virtualMachineOptions.indexOf(destinationVirtualMachine);
                                	DefaultMutableTreeNode selectedVirtualMachineNode = virtualMachineNodes.get(index3);
                                	VirtualMachine selectedVirtualMachine = (VirtualMachine) selectedVirtualMachineNode.getUserObject();
                                	
                                	String tempName = selectedProcess.name;
                                	while(checkNameExistence(selectedVirtualMachineNode.children(), tempName)) {
                            			JOptionPane.showMessageDialog(panelMain, "Nome já existe no container, renomeie o processo.", 
                            					"Nome já existe", JOptionPane.ERROR_MESSAGE);  
                            			
                            			String newName = JOptionPane.showInputDialog(panelMain, "Digite o novo nome: ",
                            					"Migrar processo", JOptionPane.OK_CANCEL_OPTION);
                                        if((newName != null) && (newName.length() > 0)){
                                            tempName = newName;                              
                                        }
                                        else{
                                            return;
                                        }
                            		}
                                	
                                	selectedProcess.name = tempName;
                                	
                                	Migration migration = new Migration(selectedVirtualMachine, selectedProcess, originalName);
                                	parentGUI.client.send(migration, Action.MIGRATE_PROCESS);
                                	
                                }                           
                            }
                        }
                	}
                }
            }
        };
    }
    
    private ActionListener getMessageActionListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(selectedNode != null){
                	ArrayList<String> arrayOptions = new ArrayList<>();
                	Process process = (Process) selectedNode.getUserObject();                  
                    Enumeration<DefaultMutableTreeNode> siblings =
                    		(Enumeration<DefaultMutableTreeNode>) selectedNode.getParent().children();
                    while(siblings.hasMoreElements()){
                    	Process temp = (Process) siblings.nextElement().getUserObject();
                        String siblingName = temp.name;
                        if(!siblingName.equals(process.name)){
                            arrayOptions.add(siblingName);
                        }                       
                    }
                    
                    Object[] options = arrayOptions.toArray();
                    String destination = (String) JOptionPane.showInputDialog(panelMain,
                                        "Para qual processo deseja enviar a mensagem?",
                                        "Enviar mensagem para outro processo",
                                        JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    
                    if(destination != null && destination.length() > 0 ){
                    	String message = JOptionPane.showInputDialog(panelMain, "Digite a mensagem:",
                    			"Enviar mensagem para outro processo", JOptionPane.PLAIN_MESSAGE);                 	
                    	if(message != null && message.length() > 0) {
                    		ProcessMessage processMessage = new ProcessMessage(destination, process, message);
                    		parentGUI.client.send(processMessage, Action.MESSAGE);
                    	}
                    } 
                }
            }
        };
    }


    class CustomTreeCellRenderer implements TreeCellRenderer {
        private JLabel label;

        CustomTreeCellRenderer() {
            label = new JLabel();
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            Object o = ((DefaultMutableTreeNode) value).getUserObject();
           
            if (o instanceof Root) {
                Root node = (Root) o;
                URL imageUrl = getClass().getClassLoader().getResource("resources/root.png");
                if (imageUrl != null) {
                    label.setIcon(new ImageIcon(imageUrl));
                }
                label.setText("Espaço de tuplas");
                label.setToolTipText("Raíz do espaço de tuplas");
            } else if (o instanceof Cloud) {
                Cloud node = (Cloud) o;
                URL imageUrl = getClass().getClassLoader().getResource("resources/cloud.png");
                if (imageUrl != null) {
                    label.setIcon(new ImageIcon(imageUrl));
                }
                label.setText(node.name);
                label.setToolTipText("Nuvem: " + node.name);
            } else if (o instanceof Host) {
                Host node = (Host) o;
                URL imageUrl = getClass().getClassLoader().getResource("resources/host.png");
                if (imageUrl != null) {
                    label.setIcon(new ImageIcon(imageUrl));
                }
                label.setText(node.name);
                label.setToolTipText("Host: " + node.name);
            } else if (o instanceof VirtualMachine) {
                VirtualMachine node = (VirtualMachine) o;
                URL imageUrl = getClass().getClassLoader().getResource("resources/virtual-machine.png");
                if (imageUrl != null) {
                    label.setIcon(new ImageIcon(imageUrl));
                }
                label.setText(node.name);
                label.setToolTipText("Máquina Virtual: " + node.name);
            } else if (o instanceof Process) {
                Process node = (Process) o;
                URL imageUrl = getClass().getClassLoader().getResource("resources/process.png");
                if (imageUrl != null) {
                    label.setIcon(new ImageIcon(imageUrl));
                }                
                label.setText(node.name);
                label.setToolTipText("Processo: " + node.name);              
            } 
            else {
                label.setIcon(null);
                label.setText("Espaço de tuplas");
                label.setToolTipText("Raíz do espaço de tuplas");
            }
            return label;
        }
    }
}