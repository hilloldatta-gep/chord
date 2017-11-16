/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chord;

/**
 *
 * @author hillold
 */
public class Node {
    private NodeInfo nodeinfo;
    private NodeInfo successor;
    private NodeInfo predecessor;
    public FingerTable[] fTable;
    
    Node(NodeInfo nodeinfo){
        this.nodeinfo=nodeinfo;
    }
    
    public NodeInfo getNodeinfo(){
        return this.nodeinfo;
    }
    public NodeInfo getSuccessor(){
        return this.successor;
    }
    
    public NodeInfo  getPredecesor(){
        return this.predecessor;
    }
    
    public void setSuccessor(NodeInfo ninfo){
        this.successor=ninfo;
    }
    public void setPredecessor(NodeInfo ninfo){
        this.predecessor=ninfo;
    }
}
