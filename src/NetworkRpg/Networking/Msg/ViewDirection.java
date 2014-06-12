/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.Networking.Msg;


import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityId;

/**
 *
 * @author hexmare
 */
@Serializable
public class ViewDirection extends AbstractMessage
{

    private Vector3f direction;

    public Vector3f getDirection() {
        return direction;
    }

    public EntityId getEid() {
        return eid;
    }
    
    
    private EntityId eid;
    
    public ViewDirection(EntityId ed,Vector3f  dir) {
        this.direction = dir;
        this.eid = ed;
    }
    
    public ViewDirection() {
    }

    
    
}
