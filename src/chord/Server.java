/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chord;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hillold
 */
public class Server<T extends Remote> {
    Registry register;
    private T t;
    String ip;
    String key;
    int port;
    public Server(String ip, int port,String key){
        this.ip=ip;
        this.port=port;
        this.key=key;
    }
    public void listen(Remote objRemote) {
        System.setProperty("java.security.policy","./security.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        System.setProperty("java.rmi.server.hostname", this.ip);

        try {
            t = (T) UnicastRemoteObject.exportObject(objRemote, 
                    Registry.REGISTRY_PORT);
            register = LocateRegistry.createRegistry(this.port);
            try {
                register.rebind(key, t);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                        "Registry bind"
                        + " done successfully.");
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"Registry "
                        + "bind FAILED.");
                e.printStackTrace();
            }

        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,e.getMessage());
            e.printStackTrace();
        }
    }
}