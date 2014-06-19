package NetworkRpg;

import NetworkRpg.AppStates.ConnectionState;
import NetworkRpg.AppStates.MainMenuState;
import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private AppSettings settings;
    private boolean isFullScreen = false;
    
    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280,1024);
        app.setPauseOnLostFocus(false);
        app.setDisplayStatView(false);
        app.setDisplayFps(false);
        app.start();
    }
    
    public Main(){
        //super(new MainMenuState());
        super();
    }

    public boolean getIsFullScreen(){
        return isFullScreen;
    }
    
    public void setIsFullScreen(boolean value)
    {
        this.isFullScreen = value;
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
