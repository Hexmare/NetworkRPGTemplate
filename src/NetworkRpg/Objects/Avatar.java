/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.Objects;

//import SurviveES.Networking.ClientMain;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;

/**
 *
 * @author hexmare
 */
public class Avatar extends Node implements AnimEventListener{

    public Spatial avatarSpatial;
    private Node avatarNode;
    private Node model;
    public BetterCharacterControl avatarControl;
    private SimpleApplication App;
    private BulletAppState bulletAppState;
    private Node rootNode;
    private AnimChannel animChannel;
    private AnimControl animControl;
    private BitmapText label;
    private Node textNode;
    private String idleAnim = "IdleBase";
    private String walkAnim = "RunBase";
    private String attackAnim = "SliceHorizontal";
    private String jumpAnim = "JumpLoop"; //hilarious
    private BitmapFont guiFont;
    private String playerName;
    public Avatar() {
        createAvatar();
    }

    public Avatar(String name) {
        super(name);
        createAvatar();
    }
    
    public Avatar(String name,SimpleApplication app)
    {
        super(name);
        
        App = app;
        bulletAppState = App.getStateManager().getState(BulletAppState.class);
        rootNode = App.getRootNode();
        playerName = "";
        createAvatar();
        
    }
    
    public Avatar(BulletAppState bas)
    {
        bulletAppState = bas;
        createAvatar();
    }
    
    final public void createAvatar(){

        avatarNode = new Node("character node");
        //characterNode.setLocalTranslation(new Vector3f(4, 5, 2));

        // Add a character control to the node so we can add other things and
        // control the model rotation
        avatarControl = new BetterCharacterControl(0.3f, 2f, 8f);
        avatarNode.addControl(avatarControl);
        bulletAppState.getPhysicsSpace().add(avatarControl);
        avatarControl.setGravity(new Vector3f(0f,0f,0f));
        avatarControl.setGravity(new Vector3f(0f,-9.81f,0f));
        // Load model, attach to character node
        model = (Node) App.getAssetManager().loadModel("Models/Sinbad/Sinbad.mesh.xml");
        model.move(0f, 1.25f, 0f);
        model.setLocalScale(.2f);
        avatarNode.attachChild(model);
        this.attachChild(avatarNode);
        
        animControl = model.getControl(AnimControl.class);
        //animControl.addListener(this);
        //for (String anim : animControl.getAnimationNames()) {
        //    System.out.println(anim);
        //}
        animChannel = animControl.createChannel();
        animChannel.setAnim(idleAnim);
        
        // Add character node to the rootNode
        //rootNode.attachChild(this);
        guiFont = App.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        label = new BitmapText( guiFont, false );
        label.setSize( 1f );
        label.setText( playerName );
        float textWidth = label.getLineWidth() + 20;
        float textOffset = textWidth / 2;
        label.setBox( new Rectangle(-textOffset,0, textWidth, label.getHeight()) );
        label.setColor( new ColorRGBA( 0, 1, 1, 1 ) );
        label.setAlignment( BitmapFont.Align.Center );
        label.setQueueBucket( RenderQueue.Bucket.Transparent );
        BillboardControl bc = new BillboardControl();
        bc.setAlignment( BillboardControl.Alignment.Screen );
        label.addControl(bc);



        textNode = new Node( "LabelNode" );
        textNode.setLocalTranslation( 0, label.getHeight() * 5, 0 );
        textNode.attachChild( label );
        model.attachChild(textNode);

       
       }

    public void setPlayerName(String value){
        playerName = value;
        label.setText(playerName);
    }
    
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setAnim(String anim)
    {
        if(anim.equalsIgnoreCase("attack"))
	{
 
	    animChannel.setAnim(attackAnim,.3f);
	    animChannel.setLoopMode(LoopMode.DontLoop);
	    
	}
	else if(anim.equalsIgnoreCase("walk"))
	{
	           if (!animChannel.getAnimationName().equals(walkAnim)) {
                animChannel.setAnim(walkAnim,.3f);
		    animChannel.setLoopMode(LoopMode.Loop);
            }
		    
		
	}
        else
        {
            animChannel.setAnim(idleAnim,.3f);
		    animChannel.setLoopMode(LoopMode.Cycle);
        }
    }
    
    
}
