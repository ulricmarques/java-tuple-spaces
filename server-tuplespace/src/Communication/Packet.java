/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Communication;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ulric
 */
public class Packet implements Serializable {
    
    private String name;
    private Object content;
    private Action action;
    private Set<String> setOnlines = new HashSet<String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Set<String> getSetOnlines() {
        return setOnlines;
    }

    public void setSetOnlines(Set<String> setOnlines) {
        this.setOnlines = setOnlines;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
        
    public enum Action {
        CONNECT, DISCONNECT, UPDATE, SEND_ONE, SEND_ALL, USERS_ONLINE
    }
}
