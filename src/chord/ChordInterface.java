/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chord;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author hillold
 */
public interface ChordInterface extends Remote{
    public boolean doesBelong(int id,int start, int end)
            throws RemoteException;
    public	void   join(NodeInfo ninfo)
            throws RemoteException;
    public 	void  initFingerTable(NodeInfo ninfo)
                    throws RemoteException;
    public  void   updateOthers()
         throws RemoteException;
    public void   displayFingerTable()
            throws RemoteException;
    public void updatePredecessor()
           throws RemoteException;
    public void   updateFingerTable(NodeInfo s,int i)
         throws RemoteException;
    public NodeInfo  findSuccesor(int id)
         throws RemoteException;
    public NodeInfo findPredecessor(int id)
           throws RemoteException;;
    public NodeInfo getSuccessor()
         throws RemoteException;
    public NodeInfo getPredecessor()
         throws RemoteException;;

    public NodeInfo closestPrecedingFinger(int id)
         throws RemoteException;
	
}
