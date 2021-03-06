/*
 * $Id: ModelState.java 1156 2013-09-30 06:17:23Z PSpeed42@gmail.com $
 *
 * Copyright (c) 2013 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package NetworkRpg.AppStates;


import NetworkRpg.Components.ModelType;
import NetworkRpg.Components.Position;
import NetworkRpg.Factories.ModelFactory;
import NetworkRpg.GameClient;
import NetworkRpg.Networking.Msg.CommandSet;
import NetworkRpg.Networking.Msg.ViewDirection;
import NetworkRpg.Objects.Avatar;
import NetworkRpg.ThirdPersonCamera;
import NetworkRpg.TimeProvider;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Name;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *  Watches entities with Position and ModelType components
 *  and creates/destroys Spatials as needed as well as moving
 *  them to the appropriate locations.
 *  Spatials are created with a ModelFactory callback object
 *  that can be game specific.  
 *
 *  @author    Paul Speed
 */
public class ModelState extends BaseAppState {

    static Logger log = LoggerFactory.getLogger( ModelState.class );

    private EntityData ed;
    private EntitySet entities;
    private EntitySet nameSet;
    private TimeProvider time;
    private Map<EntityId, Spatial> models = new HashMap<EntityId, Spatial>();
    private Node modelRoot;
    private ModelFactory factory;
    private BulletAppState bulletAppState;
    private boolean isServer = false;
    private GameClient client;
    private ThirdPersonCamera camera;
    private Avatar playerAvatar;
    private float mouselookSpeed = FastMath.PI;
    
    
    public ModelState( TimeProvider time, ModelFactory factory, EntityData e, boolean IS) {
        this.time = time;
        this.factory = factory;
        this.ed = e;
        this.isServer = IS;
    }

    public Node getModelRoot() {
        return modelRoot;
    }


    
    public Spatial getSpatial( EntityId entity ) {
        // Make sure we are up to date
        refreshModels(0);
        return models.get(entity);
    }

    public Collection<Spatial> spatials() {
        return models.values();
    }

    protected Spatial createSpatial( Entity e ) {
        return factory.createModel(e);
    }

    protected void addModels( Set<Entity> set ) {

        for( Entity e : set ) {
            // See if we already have one
            Spatial s = models.get(e.getId());
            if( s != null ) {
                log.error("Model already exists for added entity:" + e);
                continue;
            }

            s = createSpatial(e);
            models.put(e.getId(), s);
            updateModelSpatial(e, s,0);
            modelRoot.attachChild(s);
            
            if (!isServer) {
                if (e.getId().getId() == client.getPlayer().getId()) {
                //System.out.println("Here is our player");
                playerAvatar = (Avatar)s;
                camera = new ThirdPersonCamera("Camera Node", getApplication().getCamera(), (Node)((Avatar)s).getChild("character node"));
            }
            }
            
        }
    }

    protected void removeModels( Set<Entity> set ) {

        for( Entity e : set ) {
            Spatial s = models.remove(e.getId());
            if( s == null ) {
                log.error("Model not found for removed entity:" + e);
                continue;
            }
            s.removeFromParent();
            getApplication().getStateManager().getState(BulletAppState.class).getPhysicsSpace().remove(((Avatar)s).avatarControl);                   
        }
    }

    protected void updateModelSpatial( Entity e, Spatial s,float tpf) {
        Position p = e.get(Position.class);
    
        
        ModelType mt = e.get(ModelType.class);
        if (mt != null && mt.getType().equalsIgnoreCase("ogre")) {

            Name nm =  e.get(Name.class);

            if (this.isServer == false) {
                Vector3f tLocation = p.getLocation().clone();
                if (tLocation.distance(((Avatar)s).getLocalTranslation()) >= 1) {
                    ((Avatar)s).avatarControl.warp(tLocation.interpolate(((Avatar)s).getLocalTranslation(), tpf));
                }
                
                
                
            }
            
        }
        else
        { 

        }
            

    }
    
    protected void updateNames( Set<Entity> set ) {

        for( Entity e : set ) {
            Spatial s = models.get(e.getId());
            Name nm =  e.get(Name.class);
            if (!isServer) {
                if (nm != null) {
                    ((Avatar)s).setPlayerName(nm.getName());
                    
                }
            }
            
        }
    }
    
    public void setAvatarCommand(CommandSet cs)
    {
        Spatial s = models.get(cs.getEntityId());
        Vector3f wd = new Vector3f();
        boolean walking = false;
        wd.set(0, 0, 0);
        
        Vector3f fd = ((Node)s).getChild(0).getWorldRotation().clone().mult(Vector3f.UNIT_Z);
        Vector3f ld = ((Node)s).getChild(0).getWorldRotation().clone().mult(Vector3f.UNIT_X);

        if (cs.isLeft()) {
            wd.addLocal(ld);
            walking = true;
        }
        if (cs.isRight()) {
            wd.addLocal(ld.negate());
            walking = true;
        }
        if (cs.isForward()) {
            wd.addLocal(fd);
            walking = true;
        }
        if (cs.isBack()) {
            wd.addLocal(fd.negate());
            walking = true;
        }
        if (cs.isJumping())
        {
            if (((Avatar)s).avatarControl.isOnGround()) {
                ((Avatar)s).avatarControl.jump();
                walking = false;
            }  
        }
        ((Avatar)s).avatarControl.setWalkDirection(wd.mult(new Vector3f(3.5f,0,3.5f)));
        if (walking) {
           ((Avatar)s).setAnim("walk");
        }
        else
        {
            ((Avatar)s).setAnim("idle");
        }
    }
    
    public void setAvatarViewDirection(ViewDirection msg)
    {
        Avatar s = (Avatar) models.get(msg.getEid());
        s.avatarControl.setViewDirection(msg.getDirection());   
        Quaternion turn = new Quaternion();
        turn.fromAngleAxis(s.avatarControl.getViewDirection().normalize().angleBetween(s.avatarControl.getViewDirection().normalize()), Vector3f.UNIT_Y);
        s.avatarControl.setWalkDirection(turn.mult(s.avatarControl.getWalkDirection()));
//        s.avatarControl.setWalkDirection(s.avatarControl.getWalkDirection().add(msg.getDirection().normalize()));
    }
    
    public void setAvatarDirection(String dir,float value,float tpf)
    {
        if (dir.equals("TurnLeft"))
	{
            Quaternion turn = new Quaternion();
	    turn.fromAngleAxis(mouselookSpeed*value, Vector3f.UNIT_Y);
	    playerAvatar.avatarControl.setViewDirection(turn.mult(playerAvatar.avatarControl.getViewDirection()));
	}
	else if (dir.equals("TurnRight"))
	{
            Quaternion turn = new Quaternion();
	    turn.fromAngleAxis(-mouselookSpeed*value, Vector3f.UNIT_Y);          
	    playerAvatar.avatarControl.setViewDirection(turn.mult(playerAvatar.avatarControl.getViewDirection()));
	}
	else if (dir.equals("MouselookDown"))
	{
                camera.verticalRotate(mouselookSpeed*value); 
	}
	else if (dir.equals("MouselookUp"))
	{
                camera.verticalRotate(-mouselookSpeed*value);
	}
        Quaternion turn = new Quaternion();
        turn.fromAngleAxis(playerAvatar.avatarControl.getViewDirection().normalize().angleBetween(playerAvatar.avatarControl.getViewDirection().normalize()), Vector3f.UNIT_Y);
        playerAvatar.avatarControl.setWalkDirection(turn.mult(playerAvatar.avatarControl.getWalkDirection()));
        //playerAvatar.avatarControl.setWalkDirection(playerAvatar.avatarControl.getWalkDirection().add(playerAvatar.avatarControl.getViewDirection().normalize()));
        if (!isServer) {   
            ViewDirection cs = new ViewDirection(getApplication().getStateManager().getState(PlayerState.class).getClient().getPlayer(),playerAvatar.avatarControl.getViewDirection());
            getApplication().getStateManager().getState(ConnectionState.class).getClient().send(cs);
        }  
    }

    protected void updateModels( Set<Entity> set ,float tpf) {

        for( Entity e : set ) {
            Spatial s = models.get(e.getId());
            if( s == null ) {
                log.error("Model not found for updated entity:" + e);
                continue;
            }
            updateModelSpatial(e, s,tpf);
        }
    }

    protected void refreshModels(float tpf) {    
        if( entities.applyChanges() ) {
            removeModels(entities.getRemovedEntities());
            addModels(entities.getAddedEntities());
            updateModels(entities.getChangedEntities(),tpf);
        }
        if (nameSet.applyChanges()) {
            updateNames(nameSet.getAddedEntities());
            updateNames(nameSet.getChangedEntities());
        }
    }

    @Override
    protected void initialize( Application app ) {

        factory.setState(this);

        // Grab the set of entities we are interested in
        entities = ed.getEntities(Position.class, ModelType.class);
        nameSet = ed.getEntities(Name.class);
        // Create a root for all of the models we create
        modelRoot = new Node("Model Root");
        bulletAppState = app.getStateManager().getState(BulletAppState.class);
        if (!isServer) {
            client = app.getStateManager().getState(GamePlayState.class).getClient();
        }
        
    }

    @Override
    protected void cleanup( Application app ) {

        entities.release();
        entities = null;
    }

    @Override
    protected void enable() {
        ((SimpleApplication)getApplication()).getRootNode().attachChild(modelRoot);

        entities.applyChanges();
        addModels(entities);
    }

    @Override
    public void update( float tpf ) {
        refreshModels(tpf);
    }

    @Override
    protected void disable() {
        modelRoot.removeFromParent();
        removeModels(entities);
    }

}
