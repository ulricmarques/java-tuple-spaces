/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tuples;

import java.util.ArrayList;
import net.jini.core.entry.Entry;

/**
 *
 * @author Ulric
 */
public class Cloud implements Entry {
    
    public String name;
    public ArrayList<Host> hostList;
    public Root parent;
    
    public Cloud(){
        
    }
    
    
}
