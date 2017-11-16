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
public class NodeInfo {
    public String ip;
    public int id;
    public int port;
    NodeInfo(int id,String ip,int port){
        this.id=id;
        this.ip=ip;
        this.port=port;
    } 
    
}
