/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chord;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hillold
 */
public class Chord implements ChordInterface {
    public Node node;
    
    public static int NO_OF_BITS=3;
    Chord (Node node){
        this.node=node;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException {
        String ip="127.0.0.1";
        int port=Integer.parseInt(args[2]);
        int id=Integer.parseInt(args[0]);
        Node node=new Node(new NodeInfo(id,ip,port));
        Chord ch=new Chord(node);
        Server<ChordInterface> server = new Server<ChordInterface>(ip,port, "node");
        
        server.listen(ch);
        
        Connection<Chord> conn = new Connection<Chord>("127.0.0.1","node");
        Chord stub=null;
        try {
            stub=conn.getStub();
        } catch (NotBoundException ex) {
            Logger.getLogger(Chord.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(stub!=null){
            stub.join(new NodeInfo(1,"127.0.0.1",5001));
            
        }   
        
        while(true){
            ch.displayFingerTable();
            
        }
        
        // TODO code application logic here
    }

    @Override
    public boolean doesBelong(int id, int start, int end) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void join(NodeInfo ninfo) throws RemoteException {
	/* 
	* When it is the first node in Network i.e NO known node exists 
	* And passed parameters belong to this node itself
	*/
	if(this.node.getNodeinfo().id==ninfo.id)
	{       int id=ninfo.id;
                //FingerTable[] fTable=node.fTable;
		for(int i=0;i<NO_OF_BITS;i++)
		{
			this.node.fTable[i].intervalStart=(id + (2<<(i-1)))%(2<<(NO_OF_BITS-1));
			if(i==0)
				this.node.fTable[i].intervalStart=(id + 1)%(2<<(NO_OF_BITS-1));
			node.fTable[i].intervalEnd=(id + (2<<(i)))%(2<<(NO_OF_BITS-1));
			node.fTable[i].nodeinfo=new NodeInfo(id,ninfo.ip,ninfo.port);
			
		}
		node.setPredecessor(ninfo);
		
	}
	/*
	* When there exist a node in the network which is known to this Node
	* Methods are defined later
	*/
	else
	{
		initFingerTable(ninfo);
		updateOthers();
		
	}
    }

    @Override
    public void initFingerTable(NodeInfo ninfo) throws RemoteException {
        node.fTable[0].intervalStart=(ninfo.id + 1)%(2<<(NO_OF_BITS-1));
	node.fTable[0].intervalEnd=(ninfo.id + (2<<(0)))%(2<<(NO_OF_BITS-1));
        Connection<Chord> conn = new Connection<Chord>(ninfo.ip,"node");
        Chord stub=null;
        try {
            stub=conn.getStub();
        } catch (NotBoundException ex) {
            Logger.getLogger(Chord.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(stub!=null){
            node.fTable[0].nodeinfo=stub.findSuccesor(node.fTable[0].intervalStart);
            node.setPredecessor(node.fTable[0].nodeinfo);
            
        }   
        int id=node.getNodeinfo().id;
        for(int i=0;i<NO_OF_BITS-1;i++){
		
		node.fTable[i].intervalStart=(id + (2<<(i-1)))%(2<<(NO_OF_BITS-1));
		node.fTable[i].intervalEnd=(id + (2<<(i)))%(2<<(NO_OF_BITS-1));
		/*
		* IF i'th intervalStart belongs between Current_Node_ID and Previous_Finger_Node
		* THEN No need to find i'th Node since this Previous_Finger_Node will be Node
		* of current finger as well.
		*/
		if(doesBelong(node.fTable[i].intervalStart,id,node.fTable[i-1].nodeinfo.id-1))
		{
			node.fTable[i].nodeinfo=node.fTable[i-1].nodeinfo;
			
		}
		/*
		* IF i'th intervalStart does not reside between Current_Node_ID & Previous_Finger_Node
		* THEN query Existing Known Node to fill up details
		*/
		else
		{       
                    Connection<Chord> conn2 = new Connection<Chord>(ninfo.ip,"node");
                    Chord stub2=null;
                    try {
                        stub=conn2.getStub();
                    } catch (NotBoundException ex) {
                        Logger.getLogger(Chord.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if(stub2!=null){
                     node.fTable[i].nodeinfo=stub2.findSuccesor(node.fTable[i].intervalStart);
                    }    
		}
	}
        
    }

    @Override
    public void updateOthers() throws RemoteException {
        int id=this.node.getNodeinfo().id;
        for(int i=0;i<NO_OF_BITS;i++){
            NodeInfo p=findPredecessor((id-(2<<(i-1))+(2<<NO_OF_BITS))%(2<<NO_OF_BITS));
            Connection<Chord> conn = new Connection<Chord>(p.ip,"node");
            Chord stub=null;
            try {
                stub=conn.getStub();
            } catch (NotBoundException ex) {
                Logger.getLogger(Chord.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(stub!=null){
                stub.updateFingerTable(this.node.getNodeinfo(),i);
            }   
        }
    }

    @Override
    public void displayFingerTable() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updatePredecessor() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateFingerTable(NodeInfo s,int i) throws RemoteException {
        int id=this.node.getNodeinfo().id;
        if(doesBelong(s.id, id, node.fTable[i].nodeinfo.id-1)){
            node.fTable[i].nodeinfo=s;
            NodeInfo pre=getPredecessor();
            Connection<Chord> conn = new Connection<Chord>(pre.ip,"node");
            Chord stub=null;
            try {
                stub=conn.getStub();
            } catch (NotBoundException ex) {
                Logger.getLogger(Chord.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(stub!=null){
                stub.updateFingerTable(this.node.getNodeinfo(),i);
            }   
            
        }
            
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeInfo findSuccesor(int id) throws RemoteException {
       NodeInfo ninfo=findPredecessor(id);
       NodeInfo successor = null;
       Connection<Chord> conn = new Connection<Chord>(ninfo.ip,"node");
        Chord stub=null;
        try {
            stub=conn.getStub();
        } catch (NotBoundException ex) {
            Logger.getLogger(Chord.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(stub!=null){
            successor = stub.getSuccessor();
           
            
        }   
        return successor;
    }

    @Override
    public NodeInfo findPredecessor(int id) throws RemoteException {
        NodeInfo n1=node.getNodeinfo();
        Connection<Chord> conn = new Connection<Chord>(n1.ip,"node");
        Chord stub=null;
        try {
            stub=conn.getStub();
        } catch (NotBoundException ex) {
            Logger.getLogger(Chord.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(!doesBelong(id, n1.id, stub.getSuccessor().id)){
            Connection<Chord> conn2 = new Connection<Chord>(n1.ip,"node");
            Chord stub2=null;
            try {
                stub=conn.getStub();
            } catch (NotBoundException ex) {
                Logger.getLogger(Chord.class.getName()).log(Level.SEVERE, null, ex);
            }
            n1=stub2.closestPrecedingFinger(id);
        }
        return n1;
    }

    @Override
    public NodeInfo getSuccessor() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeInfo getPredecessor() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NodeInfo closestPrecedingFinger(int id) throws RemoteException {
        int nid=this.node.getNodeinfo().id;
        FingerTable[] ft=node.fTable;
        NodeInfo cpf=null;
        for(int i=NO_OF_BITS;i>=0;i--){
            if(doesBelong(ft[i].nodeinfo.id,nid,id)){
                cpf=ft[i].nodeinfo;
                
            }
        }
        return cpf;
    }
    
}
