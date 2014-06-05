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
public class LocAndDir extends AbstractMessage
{

    public LocAndDir() {
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public LocAndDir(Vector3f position, Vector3f direction) {
        this.position = position;
        this.direction = direction;
    }
    private Vector3f position;
    private Vector3f direction;
    
}
