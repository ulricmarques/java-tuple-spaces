/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package River;

import River.Tuples.ClientList;
import River.Tuples.Cloud;
import River.Tuples.Root;
import River.Tuples.ServerMessage;
import River.Tuples.ServerMessage.Action;
import River.Tuples.Host;
import River.Tuples.Process;
import River.Tuples.ProcessMessage;
import River.Tuples.VirtualMachine;
import java.rmi.RemoteException;
import java.util.ArrayList;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

/**
 *
 * @author Ulric
 */
public class SpaceController {
    
    public JavaSpace space;
    
    public SpaceController(){
        System.out.println("Procurando pelo servico JavaSpace...");
        Lookup finder = new Lookup(JavaSpace.class);
        space = (JavaSpace) finder.getService();
        if (space == null) {
                System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
                System.exit(-1);
        } 
        System.out.println("O servico JavaSpace foi encontrado.");
        System.out.println(space);
    }
    
    public void createClientList() {
    	ClientList list = new ClientList();
    	list.clients = new ArrayList<>();
    	
    	try {
			space.write(list, null, Lease.FOREVER);
		} catch (RemoteException | TransactionException e) {
			System.out.println("Error: " + e);
		}
    }
    
    public void addClient(String name) {
    	ClientList template = new ClientList();  	
		try {
			ClientList list = (ClientList) space.take(template, null, 60 * 1000);
			list.clients.add(name);
			space.write(list, null, Lease.FOREVER);
		} catch (RemoteException | UnusableEntryException | TransactionException | InterruptedException e) {
			System.out.println("Error: " + e);
		}    	
    }
    
    public void sendToClient(String clientName, Object content, Action action) {
    	ServerMessage serverMessage = new ServerMessage(clientName, content, action);
    	try {
            space.write(serverMessage, null, Long.MAX_VALUE);
        } catch (TransactionException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
    	
    }
       
    public void addTuple(){
        Root root = new Root();
        root.cloudList = new ArrayList<>();
        try {
            space.write(root, null, Long.MAX_VALUE);
        } catch (TransactionException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
    }
    
    public void addTuple(String cloudName){
        Root template = new Root();
        
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            Cloud newCloud = new Cloud();
            newCloud.name = cloudName;
            newCloud.parent = root;
            newCloud.hostList = new ArrayList<>();
            
            root.cloudList.add(newCloud);
            this.space.write(root, null, Long.MAX_VALUE);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
          
    }
    
    public void addTuple(String cloudName, String hostName){
        Root template = new Root();
        
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            root.cloudList.forEach(cloud->{
                if(cloud.name.equals(cloudName)){
                    Host newHost = new Host();
                    newHost.name = hostName;
                    newHost.parent = cloud;
                    newHost.virtualMachineList = new ArrayList<>();
                    cloud.hostList.add(newHost);
                }  
            });
            
            this.space.write(root, null, Long.MAX_VALUE);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
    }
    
    public void addTuple(String cloudName, String hostName, String virtualMachineName){
        Root template = new Root();
        
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            root.cloudList.forEach(cloud->{
                if(cloud.name.equals(cloudName)){
                    cloud.hostList.forEach(host->{
                        if(host.name.equals(hostName)){
                            VirtualMachine newVirtualMachine = new VirtualMachine();
                            newVirtualMachine.name = virtualMachineName;
                            newVirtualMachine.parent = host;
                            newVirtualMachine.processList = new ArrayList<>();
                            host.virtualMachineList.add(newVirtualMachine);
                        }
                    });
                }  
            });
            
            this.space.write(root, null, Long.MAX_VALUE);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
    }
    
    public void addTuple(String cloudName, String hostName, String virtualMachineName, String processName){
        Root template = new Root();
        
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            root.cloudList.forEach(cloud->{
                if(cloud.name.equals(cloudName)){
                    cloud.hostList.forEach(host->{
                        if(host.name.equals(hostName)){
                            host.virtualMachineList.forEach(virtualMachine ->{
                                if(virtualMachine.name.equals(virtualMachineName)){
                                        Process newProcess = new Process();
                                        newProcess.name = processName;
                                        newProcess.parent = virtualMachine;
                                        virtualMachine.processList.add(newProcess);
                                }
                            });
                        }
                    });
                }  
            });
            
            this.space.write(root, null, Long.MAX_VALUE);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
    }
    
    public void removeTuple(String cloudName){
        Root template = new Root();;       
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            Cloud temp = null;
            for(Cloud cloud : root.cloudList){
                if(cloud.name.equals(cloudName) && cloud.hostList.isEmpty()){
                    temp = cloud;
                }     
            }
            
            root.cloudList.remove(temp);
            this.space.write(root, null, Long.MAX_VALUE);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
        
          
    }
    
    public void removeTuple(String cloudName, String hostName){
        Root template = new Root();
        
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            Host temp = null;
            for(Cloud cloud : root.cloudList){
                if(cloud.name.equals(cloudName)){
                    for(Host host : cloud.hostList){
                        if(host.name.equals(hostName) && host.virtualMachineList.isEmpty()){
                            temp = host;
                        }
                    }                 
                    cloud.hostList.remove(temp);
                }     
            }
            
            this.space.write(root, null, Long.MAX_VALUE);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }        
    }
    
    public void removeTuple(String cloudName, String hostName, String virtualMachineName){
        Root template = new Root();
        
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            VirtualMachine temp = null;
            for(Cloud cloud : root.cloudList){
                if(cloud.name.equals(cloudName)){
                    for(Host host : cloud.hostList){
                        if(host.name.equals(hostName)){
                            for(VirtualMachine virtualMachine : host.virtualMachineList){
                                if(virtualMachine.name.equals(virtualMachineName) && virtualMachine.processList.isEmpty()){
                                    temp = virtualMachine;
                                }
                            }
                            
                            host.virtualMachineList.remove(temp);
                        }
                    }
                }     
            }
            this.space.write(root, null, Long.MAX_VALUE);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
        
    }
    
    public void removeTuple(String cloudName, String hostName, String virtualMachineName, String processName){
        Root template = new Root();
        
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            Process temp = null;
            for(Cloud cloud : root.cloudList){
                if(cloud.name.equals(cloudName)){
                    for(Host host : cloud.hostList){
                        if(host.name.equals(hostName)){
                            for(VirtualMachine virtualMachine : host.virtualMachineList){
                                if(virtualMachine.name.equals(virtualMachineName)){
                                    for(Process process : virtualMachine.processList){
                                        if(process.name.equals(processName)){
                                            temp = process;                                           
                                        }
                                    }                               
                                    virtualMachine.processList.remove(temp);
                                }
                            }                                                      
                        }
                    }
                }     
            }
            this.space.write(root, null, Long.MAX_VALUE);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }       
    }
    
    public void migrateHost(Cloud cloudObject, Host hostObject, String originalName){
        Root template = new Root();
        
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            Host temp = null;
            for(Cloud cloud : root.cloudList){
                if(cloud.name.equals(hostObject.parent.name)){
                    for(Host host : cloud.hostList){
                        if(host.name.equals(originalName)){
                            temp = host;
                        }
                    }                 
                    cloud.hostList.remove(temp);
                }     
            }
            
            for(Cloud cloud: root.cloudList){
                if(cloud.name.equals(cloudObject.name)){
                    if(temp != null){
                    	temp.name = hostObject.name;
                        cloud.hostList.add(temp);
                    }
                }
            }
            
            this.space.write(root, null, Long.MAX_VALUE);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }        
    }
    
    public void migrateVirtualMachine(Host hostObject, VirtualMachine virtualMachineObject, String originalName){
        Root template = new Root();
        
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            VirtualMachine temp = null;
            for(Cloud cloud : root.cloudList){
                if(cloud.name.equals(virtualMachineObject.parent.parent.name)){
                    for(Host host : cloud.hostList){
                        if(host.name.equals(virtualMachineObject.parent.name)){
                        	for(VirtualMachine virtualMachine : host.virtualMachineList){
                                if(virtualMachine.name.equals(originalName)){
                                	temp = virtualMachine;
                                }       
                        	}                       	
                        	host.virtualMachineList.remove(temp);
                        }
                    }                 
                }     
            }
            
            for(Cloud cloud: root.cloudList){
                if(cloud.name.equals(hostObject.parent.name)){
                	for(Host host : cloud.hostList){
                        if(host.name.equals(hostObject.name)){
                        	if(temp != null){
                        		temp.name = virtualMachineObject.name;
                                host.virtualMachineList.add(temp);
                            }
                        }
                	}                    
                }
            }            
            this.space.write(root, null, Long.MAX_VALUE);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }        
    }
    
    public void migrateProcess(VirtualMachine virtualMachineObject, Process processObject, String originalName){
        Root template = new Root();
        
        try {
    		Root root = (Root) space.take(template, null, 60 * 1000);
            
            Process temp = null;
            for(Cloud cloud : root.cloudList){
                if(cloud.name.equals(processObject.parent.parent.parent.name)){
                    for(Host host : cloud.hostList){
                        if(host.name.equals(processObject.parent.parent.name)){
                        	for(VirtualMachine virtualMachine : host.virtualMachineList){
                                if(virtualMachine.name.equals(processObject.parent.name)){
                                	for(Process process : virtualMachine.processList){
                                        if(process.name.equals(originalName)){
                                        	temp = process;
                                        }
                                	}   
                                	virtualMachine.processList.remove(temp);
                                }       
                        	}                       	                       	
                        }
                    }                 
                }     
            }
            
            for(Cloud cloud: root.cloudList){
                if(cloud.name.equals(virtualMachineObject.parent.parent.name)){
                	for(Host host : cloud.hostList){
                        if(host.name.equals(virtualMachineObject.parent.name)){
                        	for(VirtualMachine virtualMachine : host.virtualMachineList){
                                if(virtualMachine.name.equals(virtualMachineObject.name)){
                                	if(temp != null){
                                		temp.name = processObject.name;
                                        virtualMachine.processList.add(temp);
                                    }
                                }
                        	}	
                        }
                	}                    
                }
            } 
            
            this.space.write(root, null, Long.MAX_VALUE);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }        
    }
    
    
    
    public Root getCloudList(){
        Root template = new Root();
        
        Root root = null;
        
        try {
            root = (Root) space.read(template, null, 60 * 1000);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
        
        return root;   
    }
    
    
    public void sendMessage(ProcessMessage processMessage) {
    	try {
            space.write(processMessage, null, Lease.FOREVER);
        } catch (TransactionException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
    }
    
    
    public void printSpace(){
        Root template = new Root();
        
        try {
            Root root = (Root) space.read(template, null, 60 * 1000);
            
            root.cloudList.forEach(cloud->{
                System.out.println("(Cloud): " + cloud.name);
                cloud.hostList.forEach(host->{
                    System.out.println("\t(Host): " + host.name);
                    host.virtualMachineList.forEach(virtualMachine ->{
                        System.out.println("\t\t(Virtual Machine): " + virtualMachine.name);
                        virtualMachine.processList.forEach(process ->{
                            System.out.println("\t\t\t(Process): " + process.name);
                        });     
                    });
                });  
            });           
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }  
    }   
    
    public void printClients(){
        ClientList template = new ClientList();
        
        try {
        	ClientList list = (ClientList) space.read(template, null, 60 * 1000);
            
            list.clients.forEach(client->{
                System.out.println("Cliente: " + client);               
            });           
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }  
    }   
}
