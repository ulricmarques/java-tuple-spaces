/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package River;

import River.Tuples.Cloud;
import River.Tuples.Root;
import River.Tuples.Host;
import River.Tuples.Process;
import River.Tuples.VirtualMachine;
import java.rmi.RemoteException;
import java.util.ArrayList;
import net.jini.core.entry.UnusableEntryException;
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
       
    public void AddTuple(){
        Root root = new Root();
        root.cloudList = new ArrayList<>();
        try {
            space.write(root, null, 60 * 1000);
        } catch (TransactionException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
    }
    
    public void AddTuple(String cloudName){
        Root template = new Root();
        
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            Cloud newCloud = new Cloud();
            newCloud.name = cloudName;
            newCloud.parent = root;
            newCloud.hostList = new ArrayList<>();
            
            root.cloudList.add(newCloud);
            this.space.write(root, null, 60 * 1000);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
          
    }
    
    public void AddTuple(String cloudName, String hostName){
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
            
            this.space.write(root, null, 60 * 1000);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
    }
    
    public void AddTuple(String cloudName, String hostName, String virtualMachineName){
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
            
            this.space.write(root, null, 60 * 1000);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
    }
    
    public void AddTuple(String cloudName, String hostName, String virtualMachineName, String processName){
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
                                        newProcess.name = virtualMachineName;
                                        newProcess.parent = virtualMachine;
                                        virtualMachine.processList.add(newProcess);
                                }
                            });
                        }
                    });
                }  
            });
            
            this.space.write(root, null, 60 * 1000);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
    }
    
    public boolean RemoveTuple(String cloudName){
        Root template = new Root();
        boolean wasEmpty = false;
        
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            Cloud temp = null;
            for(Cloud cloud : root.cloudList){
                if(cloud.name.equals(cloudName) && cloud.hostList.isEmpty()){
                    temp = cloud;
                    wasEmpty = true;
                }     
            }
            
            root.cloudList.remove(temp);
            this.space.write(root, null, 60 * 1000);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
        
        return wasEmpty;
          
    }
    
    public boolean RemoveTuple(String cloudName, String hostName){
        Root template = new Root();
        boolean wasEmpty = false;
        
        try {
            Root root = (Root) space.take(template, null, 60 * 1000);
            
            Host temp = null;
            for(Cloud cloud : root.cloudList){
                if(cloud.name.equals(cloudName)){
                    for(Host host : cloud.hostList){
                        if(host.name.equals(hostName) && host.virtualMachineList.isEmpty()){
                            temp = host;
                            wasEmpty = true;
                        }
                    }
                    
                    cloud.hostList.remove(temp);
                }     
            }
            
            this.space.write(root, null, 60 * 1000);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
        
        return wasEmpty;
    }
    
    public boolean RemoveTuple(String cloudName, String hostName, String virtualMachineName){
        Root template = new Root();
        boolean wasEmpty = false;
        
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
                                    wasEmpty = true;
                                }
                            }
                            
                            host.virtualMachineList.remove(temp);
                        }
                    }
                }     
            }
            this.space.write(root, null, 60 * 1000);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
        
        return wasEmpty;
    }
    
    public boolean RemoveTuple(String cloudName, String hostName, String virtualMachineName, String processName){
        Root template = new Root();
        boolean wasEmpty = false;
        
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
                                            temp = null;
                                            wasEmpty = true;
                                        }
                                    }
                                    
                                    virtualMachine.processList.remove(temp);
                                }
                            }                                                      
                        }
                    }
                }     
            }
            this.space.write(root, null, 60 * 1000);
        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            System.out.println("Error: " + ex);
        }
        
        return wasEmpty;
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
}
