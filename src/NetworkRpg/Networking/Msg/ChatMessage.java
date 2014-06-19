/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.Networking.Msg;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author hexmare
 */
@Serializable
public class ChatMessage extends AbstractMessage
{

    public ChatMessage() {
    }

    private String message;

    public ChatMessage(String value) {
        this.message = value;
    }
    public String getMessage() { return this.message; }
    
}
