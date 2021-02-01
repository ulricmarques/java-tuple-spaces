/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package River.Tuples;

import java.util.ArrayList;
import net.jini.core.entry.Entry;

/**
 *
 * @author Ulric
 */
public class VirtualMachine implements Entry {
    
    public String name;
    public ArrayList<Process> processList;
    public Host parent;
    
    public VirtualMachine(){
        
    }
    
    
}
