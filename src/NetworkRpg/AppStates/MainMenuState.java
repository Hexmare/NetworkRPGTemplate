/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.AppStates;

//import SurviveES.Networking.ClientMain;
import NetworkRpg.GameGuiController;
import NetworkRpg.Main;
import NetworkRpg.Networking.Msg.ChatMessage;
import NetworkRpg.Networking.Util;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.PopupBuilder;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.Window;
import de.lessvoid.nifty.controls.console.builder.ConsoleBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.examples.controls.chatcontrol.ChatControlDialogDefinition;
import de.lessvoid.nifty.examples.controls.common.DialogPanelControlDefinition;
import de.lessvoid.nifty.screen.Screen;


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
    //private SpriteElement              heartSprite;
    private String playerName;
    private String serverHost;
    
    
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
        nifty.getScreen("start").findNiftyControl("userName", TextField.class).setText(Long.toHexString(Double.doubleToLongBits(Math.random())));
        
    }
    
    public void tester(String test)
    {
        System.out.println(test);
    }
    
    public void processChatMessage(ChatMessage cm)
    {
        ListBox chatDisplay = nifty.getCurrentScreen().findNiftyControl("chat_display", ListBox.class);
        chatDisplay.addItem(cm.getMessage());
    }
    
    public Nifty getNifty()
    {
        return nifty;
    }
    
    public void setPlayerName(String value)
    {
        this.playerName = value;
    }
    
    public String  getPlayerName() { return this.playerName; }
    
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
    
    public void connectToServer(String playerName,String hostName,String passWord) {
        System.out.println("Called Connect to Server");
        try {
                String player = playerName;
                String host = hostName;
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
    
    
}
