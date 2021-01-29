package UI;

import River.Tuples.Cloud;
import River.Tuples.Host;
import River.Tuples.Root;
import River.Tuples.Process;
import River.Tuples.VirtualMachine;
import java.awt.Component;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.JMenuItem;
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

    protected GUI parentGUI;
    
    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    
    private DefaultMutableTreeNode selectedNode;

    public MainScreen(GUI parentGUI)  {
        
        this.parentGUI = parentGUI;
        
        panelMain = new JPanel();
        panelMain.setLayout(null); 
        panelMain.setSize(800, 600);
        
        rootNode = new DefaultMutableTreeNode("Raíz");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new CustomTreeCellRenderer());
        tree.setComponentPopupMenu(getPopUpMenu());
        tree.addMouseListener(getMouseListener());

        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setBounds(20, 20, 600, 400);
        
        panelMain.add(treeScrollPane);
       
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
                    } else{
                        selectedNode = null;
                    }

                }
                super.mousePressed(arg0);
            }
        };
    }
    
    private JPopupMenu getPopUpMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("edit");
        item.addActionListener(getEditActionListener());
        menu.add(item);

        JMenuItem item2 = new JMenuItem("add");
        item2.addActionListener(getAddActionListener());
        menu.add(item2);

        return menu;
    }

    private ActionListener getAddActionListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(selectedNode != null){
                    System.out.println("pressed" + selectedNode);
                    DefaultMutableTreeNode n = new DefaultMutableTreeNode("added");
                    selectedNode.add(n);
                    tree.repaint();
                    tree.updateUI();
                }
            }
        };
    }

    private ActionListener getEditActionListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if(selectedNode != null){
                    //edit here
                    System.out.println("pressed" + selectedNode);
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
            } else if (o instanceof Cloud) {
                Cloud node = (Cloud) o;
                URL imageUrl = getClass().getClassLoader().getResource("resources/cloud.png");
                if (imageUrl != null) {
                    label.setIcon(new ImageIcon(imageUrl));
                }
                label.setText(node.name);
            } else if (o instanceof Host) {
                Host node = (Host) o;
                URL imageUrl = getClass().getClassLoader().getResource("resources/host.png");
                if (imageUrl != null) {
                    label.setIcon(new ImageIcon(imageUrl));
                }
                label.setText(node.name);
            } else if (o instanceof VirtualMachine) {
                VirtualMachine node = (VirtualMachine) o;
                URL imageUrl = getClass().getClassLoader().getResource("resources/virtual-machine.png");
                if (imageUrl != null) {
                    label.setIcon(new ImageIcon(imageUrl));
                }
                label.setText(node.name);
            } else if (o instanceof Process) {
                Process node = (Process) o;
                URL imageUrl = getClass().getClassLoader().getResource("resources/process.png");
                if (imageUrl != null) {
                    label.setIcon(new ImageIcon(imageUrl));
                }
                label.setText(node.name);
                
            } 
            else {
                label.setIcon(null);
                label.setText("Espaço de tuplas");
            }
            return label;
        }
    }
}