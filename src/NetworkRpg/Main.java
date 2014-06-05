package NetworkRpg;

import NetworkRpg.AppStates.ConnectionState;
import NetworkRpg.AppStates.MainMenuState;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.setPauseOnLostFocus(false);
        app.start();
    }
    
    public Main(){
        //super(new MainMenuState());
        super();
    }

    
    
    @Override
    public void simpleInitApp() {
        getStateManager().attach(new MainMenuState());
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    @Override
    public void destroy()
    {
        try {
            getStateManager().getState(ConnectionState.class).cleanup();
        } catch (Exception e) {
        }
        super.destroy();
    }
}
