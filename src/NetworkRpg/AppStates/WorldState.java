/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.AppStates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author hexmare
 */
public class WorldState extends BaseAppState {
    public BulletAppState bulletAppState;
    public Node rootNode;
    @Override
    protected void initialize(Application app) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        getStateManager().attach(bulletAppState);
        rootNode = ((SimpleApplication) getApplication()).getRootNode();
        
        
        Spatial sceneModel = getApplication().getAssetManager().loadModel("Scenes/scene1.j3o");     
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) sceneModel);
	RigidBodyControl scene = new RigidBodyControl(sceneShape, 0);
	sceneModel.addControl(scene);
	bulletAppState.getPhysicsSpace().add(scene);
        Node sceneNode = new Node("scene node");
        rootNode.attachChild(sceneNode);
        sceneNode.attachChild(sceneModel);
        Spatial terrain = ((Node)sceneModel).getChild("terrain-scene1");
        terrain.addControl(new RigidBodyControl(0));
        terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        bulletAppState.getPhysicsSpace().addAll(terrain);
        //bulletAppState.setDebugEnabled(true);
        //DirectionalLight sun = new DirectionalLight();
        //sun.setDirection(new Vector3f(-.1f, -.7f, -1f));
        //rootNode.addLight(sun);
        
        //PointLight pl = new PointLight();
        //rootNode.addLight(pl);
        //pl.setPosition(new Vector3f(-6f,2f,-2f));
        
        
    }

    @Override
    protected void cleanup(Application app) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void enable() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void disable() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void update( float tpf ) {
        
    }
    
}
