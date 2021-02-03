/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package River.Tuples;

import net.jini.core.entry.Entry;

/**
 *
 * @author Ulric
 */
public class ClientMessage implements Entry {
    
    public String name;
    public Object content;
    public Action action;
    
    public ClientMessage(){   
    	
    }
    
    public ClientMessage(String name, Object content, Action action) {    	
    	this.name = name;
    	this.content = content;
    	this.action = action;
    }
    
    public enum Action {
    	CONNECT, UPDATE, ADD, REMOVE,
    	MIGRATE_HOST, MIGRATE_VIRTUAL_MACHINE, 
    	MIGRATE_PROCESS, MESSAGE
    }
    
    
}