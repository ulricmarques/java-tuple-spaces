package River;

import java.rmi.RemoteException;

import River.Tuples.Cloud;
import River.Tuples.Host;
import River.Tuples.Process;
import River.Tuples.ProcessMessage;
import River.Tuples.Root;
import River.Tuples.VirtualMachine;
import River.Tuples.ServerMessage.Action;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;

public class ProcessThread implements Runnable {
	
	public String clientName;
	public Process process;
	public SpaceController spaceController;
	
	public ProcessThread(String clientName, Process process, SpaceController spaceController ) {
		this.clientName = clientName;
		this.process = process;
		this.spaceController = spaceController;
	}
	

	@Override
	public void run() {
		while(true) {
			try {				
				ProcessMessage template = new ProcessMessage();
				template.destination = this.process.name;
				ProcessMessage message = (ProcessMessage) spaceController.space.take(template, null, Long.MAX_VALUE);
				
				boolean sameVirtualMachine = false;
				Root templateRoot = new Root();
				Root root = (Root) spaceController.space.read(templateRoot, null, 60 * 1000);
				for(Cloud cloud : root.cloudList){
	                if(cloud.name.equals(process.parent.parent.parent.name)){
	                    for(Host host : cloud.hostList){
	                        if(host.name.equals(process.parent.parent.name)){
	                            for(VirtualMachine virtualMachine : host.virtualMachineList){
	                                if(virtualMachine.name.equals(process.parent.name)){
	                                    for(Process process : virtualMachine.processList){
	                                        if(process.name.equals(message.origin.name)) {                                           
	                                            sameVirtualMachine = true;
	                                        }
	                                    }                               
	                                }
	                            }                                                      
	                        }
	                    }
	                }     
	            }
				
				if(sameVirtualMachine) {
					System.out.println("Processo está na mesma máquina virtual."
                    		+ "Pode enviar a mensagem");
					spaceController.sendToClient(clientName, message, Action.MESSAGE);
				}
				else {
					System.out.println("Processo não está na mesma máquina virtual."
                    		+ "Devolvendo mensagem ao espaço...");
				    spaceController.space.write(message, null, Lease.FOREVER);
				}
				
			} catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
	            System.out.println("Error: " + ex);
	        } 
		}
		
		
	}

}
