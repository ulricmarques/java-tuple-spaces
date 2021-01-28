import River.SpaceController;
import UI.GUI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ulric
 */
public class Main {

    public static void main(String[] args) {
        new GUI();
        
        SpaceController spaceController = new SpaceController();
        spaceController.AddTuple();
        spaceController.AddTuple("nuvem1");
        spaceController.AddTuple("nuvem1", "host1");
        spaceController.AddTuple("nuvem1", "host1", "vm1");
        spaceController.AddTuple("nuvem2");
        spaceController.AddTuple("nuvem2", "host1");
        spaceController.AddTuple("nuvem2", "host1", "vm1");
        spaceController.AddTuple("nuvem1", "host2");
        
        System.out.println("Before");
        spaceController.list();
        
        spaceController.RemoveTuple("nuvem1", "host2");
        
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("After");
        spaceController.list();
    }

}