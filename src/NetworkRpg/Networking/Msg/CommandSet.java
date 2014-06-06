/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.Networking.Msg;


import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityId;

/**
 *
 * @author hexmare
 */
@Serializable
public class CommandSet extends AbstractMessage
{

    public boolean isForward() {
        return forward;
    }

    public boolean isBack() {
        return back;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }
    
    public EntityId getEntityId() {
        return eid;
    }

    private boolean forward =false;
    private boolean back =false;
    private boolean left =false;
    private boolean right =false;
    private EntityId eid;
    
    public CommandSet(EntityId ed,boolean  fwd, boolean rev, boolean lft, boolean rt) {
        this.forward = fwd;
        this.back = rev;
        this.left = lft;
        this.right = rt;
        this.eid = ed;
    }
    
    public CommandSet() {
    }

    
    
}
