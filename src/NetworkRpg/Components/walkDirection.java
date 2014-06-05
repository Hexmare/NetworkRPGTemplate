/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.Components;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author hexmare
 */

public class walkDirection implements EntityComponent {

    public walkDirection() {
    }

    public walkDirection(Vector3f direction) {
        this.direction = direction;
    }
    private Vector3f direction;

    public Vector3f getDirection() {
        return direction;
    }
    
}
