/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.AppStates;

//import SurviveES.Networking.ClientMain;
import NetworkRpg.GameGuiController;
import NetworkRpg.Main;
import NetworkRpg.Networking.Util;
import com.jme3.app.Application;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.PopupBuilder;
import de.lessvoid.nifty.controls.console.builder.ConsoleBuilder;
import de.lessvoid.nifty.examples.controls.chatcontrol.ChatControlDialogDefinition;
import de.lessvoid.nifty.examples.controls.common.DialogPanelControlDefinition;
import org.lwjgl.opengl.Display;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.extras.SpriteElement;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author hexmare
 */
public class MainMenuState extends AbstractAppState{
    public  Main app;
    public  MainMenuState               GUI;
    public  AssetManager      assetManager;   
    
    private AppStateManager   stateManager;
    private BulletAppState    physics;
    private FlyByCamera       flyCam;
    
    public  Screen            screen;
    public  Window            startMenu;
    public  Window            inventoryMenu;
    public  Window            handMenu;
    public  Window            HUD;
    public  Window            EndMenu;
    private String            inventoryCount;
    private Element           killDisplay;
    private Element           healthBar;
    private Element           ammoDisplay;
    
    private Element           finalKills;
    private Element           finalAccuracy;
    private SpriteElement              heartSprite;
    
    private float             measure;
    private boolean           firstTime;
    
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private GameGuiController gameGuiController;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); 
        this.app          = (Main) app;
        this.GUI = new MainMenuState();
        this.stateManager = this.app.getStateManager();
        niftyDisplay = new NiftyJmeDisplay(this.app.getAssetManager(), this.app.getInputManager(), this.app.getAudioRenderer(), this.app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        app.getGuiViewPort().addProcessor(niftyDisplay);
        nifty.loadControlFile("nifty-default-controls.xml");
        ChatControlDialogDefinition.register(nifty);
        DialogPanelControlDefinition.register(nifty);
        this.app.getFlyByCamera().setEnabled(false);
        niftyStartMenu();
//        startMenu();
        
        //((Main)this.app).startNetworkingClient();
             //((ClientMain)this.app).startWorldManager();
    }
    
    public final AppStateManager getStateManager() {
        return app.getStateManager();
    }
    
    public void niftyStartMenu(){
        gameGuiController = new GameGuiController(app.getStateManager(),app);
        registerConsolePopup(nifty);
        nifty.fromXml("Interface/Nifty/hud.xml", "start", gameGuiController);
    }
    
    public Nifty getNifty()
    {
        return nifty;
    }
    
    private static void registerConsolePopup(Nifty nifty) {
    new PopupBuilder("consolePopup") {

      {
        childLayoutAbsolute();
        panel(new PanelBuilder() {

          {
            childLayoutCenter();
            width("100%");
            height("100%");
            alignCenter();
            valignCenter();
            control(new ConsoleBuilder("console") {

              {
                width("80%");
                lines(25);
                alignCenter();
                valignCenter();
                onStartScreenEffect(new EffectBuilder("move") {

                  {
                    length(150);
                    inherit();
                    neverStopRendering(true);
                    effectParameter("mode", "in");
                    effectParameter("direction", "top");
                  }
                });
                onEndScreenEffect(new EffectBuilder("move") {

                  {
                    length(150);
                    inherit();
                    neverStopRendering(true);
                    effectParameter("mode", "out");
                    effectParameter("direction", "top");
                  }
                });
              }
            });
          }
        });
      }
    }.registerPopup(nifty);
  }
    
    public void connectToServer() {
        System.out.println("Called Connect to Server");
        try {
                String player = "hexmare";
                String host = "127.0.0.1";
                int port = Util.tcpPort; 
                ConnectionState cs = new ConnectionState(host, port, player);
                //startSound.playInstance();
                getStateManager().attach(cs);
                ((Main) getStateManager().getApplication()).getFlyByCamera().setEnabled(true);
                setEnabled(false);
            } catch( Exception e ) {
                //log.error("Connection Error", e);
                
                // Play the error sound and pop-up an error window
                //errorSound.playInstance();
                ErrorState error = new ErrorState("Connection Error", e.getMessage(), "dungeon");
                getStateManager().attach(error);                
            }
    }
    
    public void startMenu(){  
        GUI.screen = new Screen(app);
        this.app.getGuiNode().addControl(GUI.screen);
        app.getFlyByCamera().setEnabled(false);
        /*
        Node tempNode = new Node("overheadNode");
        tempNode.addControl(new BillboardControl());
        Quad q = new Quad(1, 1);
        Geometry g = new Geometry("Quad", q);
        BitmapFont font = this.app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        BitmapText text = new BitmapText(font, false);
        text.setLocalTranslation(0.0F, 0.0f, 0.0F);
        text.setLocalScale(.01f);

        text.setText("Text");
        tempNode.attachChild(text);
        Material mat = new Material(this.app.getAssetManager(),  "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        g.setMaterial(mat);

        tempNode.attachChild(g);
        this.app.getRootNode().attachChild(tempNode);
        */

        
        
        
        startMenu = new Window(GUI.screen, "MainWindow", new Vector2f(15f, 15f));
        //heartSprite = new SpriteElement(GUI.screen, new Vector2f(0f, 0f), new Vector2f(50f, 50f), new Vector4f(1,1,1,1),"Interface/Images/Heart.png");
        
        //GUI.screen.addElement(heartSprite);
        startMenu.setWindowTitle("Main Windows");
        startMenu.setMinDimensions(new Vector2f(130, 200));
        startMenu.setWidth(new Float(50));
        startMenu.setHeight(new Float (15));
        startMenu.setIgnoreMouse(true);
        startMenu.setWindowIsMovable(false);
        
        //this.app.getStateManager().getState(FlyCamAppState.class).getCamera().setDragToRotate(true);
        
        // create buttons
        ButtonAdapter makeWindow = new ButtonAdapter( GUI.screen, "Btn1", new Vector2f(15, 15) ) {
         @Override
           public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
            //gameStart();
                //((Main)this.app).startNetworkingClient();
             //((Main)this.app).startWorldManager();
             
//             try {
//                String player = "hexmare";
//                String host = "127.0.0.1";
//                int port = Util.tcpPort; 
//                ConnectionState cs = new ConnectionState(host, port, player);
//                //startSound.playInstance();
//                getStateManager().attach(cs);
//                ((Main) getStateManager().getApplication()).getFlyByCamera().setEnabled(true);
//                setEnabled(false);
//            } catch( Exception e ) {
//                //log.error("Connection Error", e);
//                
//                // Play the error sound and pop-up an error window
//                //errorSound.playInstance();
//                ErrorState error = new ErrorState("Connection Error", e.getMessage(), "dungeon");
//                getStateManager().attach(error);                
//            }
             connectToServer();
             
             
             startMenu.hideWindow();
             //startMenu.removeFromParent();
             
            }  
          };
        makeWindow.setText("Start Game");
 
        startMenu.addChild(makeWindow);
        
        ButtonAdapter serverConnect = new ButtonAdapter( GUI.screen, "Btn2", new Vector2f(15,60)){
            @Override
           public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                //((Main)this.app).startNetworkingClient();
            } 
        };
        serverConnect.setText("Connect");
        startMenu.addChild(serverConnect);
        
        ButtonAdapter serverDisConnect = new ButtonAdapter( GUI.screen, "Btn3", new Vector2f(15,105)){
            @Override
           public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                //((Main)this.app).disconnectNetworkingClient();
            } 
        };
        serverDisConnect.setText("DisConnect");
        startMenu.addChild(serverDisConnect);
        
        
        GUI.screen.addElement(startMenu);
        startMenu.setLocalTranslation(Display.getWidth() / 2 - startMenu.getWidth()/2, Display.getHeight() / 2 + startMenu.getHeight()/2, 0);
        
        
        
        
        
     }
    
}
