/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.Networking.Msg;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author hexmare
 */
@Serializable
public class ServerKill extends AbstractMessage {
    private String hello;       // custom message data
    public ServerKill() {}    // empty constructor
    public ServerKill(String s) { hello = s; } // custom constructor
    
    public String getMessage(){return hello;};
}
