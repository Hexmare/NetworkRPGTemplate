package NetworkRpg;
 
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
 
/**
 *
 * @author Berzee
 */
public class ThirdPersonPlayerNode extends Node implements ActionListener, AnalogListener, AnimEventListener
{
    //ThirdPersonCameraNode automatically sets itself up to follow a target
    //object. Check the "onAnalog" function here to see how we do mouselook.
    private ThirdPersonCamera camera;
    private Camera cam;
 
    private Spatial model;
    private CharacterControl characterControl;
    private AnimChannel animChannel;
    private AnimControl animControl;
    private InputManager inputManager;
    private Vector3f walkDirection = new Vector3f();
 
    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;
    private boolean attack;
    private boolean attacking;
    private boolean pan = false;
 
    //These can all be changed according to your whims.
    private float walkSpeed = .15f;
    private float mouselookSpeed = FastMath.PI;
    private float jumpSpeed = 15;
    private float fallSpeed = 20;
    private float gravity = 25;
    private float stepSize = .05f;
 
    //Animation names. These are currently set up for the Ninja.mesh.xml from
    //jme3-test-data.jar (to add that jar to your project, right-click on
    //Libraries in the SDK project explorer, click "Add Library" and choose
    //jme3-test-data.jar from the menu).
    //
    //Alternatively, use any model you like and change these animation names
    //as needed.
    private String idleAnim = "IdleBase";
    private String walkAnim = "RunBase";
    private String attackAnim = "SliceHorizontal";
    private String jumpAnim = "JumpLoop"; //hilarious
 
    public ThirdPersonPlayerNode(Spatial model, InputManager inputManager, Camera cam)
    {
	super();
	this.cam = cam;
	camera = new ThirdPersonCamera("CamNode", cam, this);
 
        this.model = model;
//	this.model.scale(.25f); //Ninja.mesh.xml-specific scale stuff
//	this.model.setLocalTranslation(0f, .35f, 0f); //Ninja-specific
//        Quaternion rotQuat1 = new Quaternion();
//        rotQuat1.fromAngleAxis((float)Math.PI, new Vector3f(0, 1, 0));
//        this.model.setLocalRotation(rotQuat1);
//	this.attachChild(this.model);
        ((Node)this.model).attachChild(this);
 
//	CapsuleCollisionShape playerShape = new CapsuleCollisionShape(.5f,1f); //Ninja-specific
//	characterControl = new CharacterControl(playerShape, stepSize);
//	characterControl.setJumpSpeed(jumpSpeed);
//	characterControl.setFallSpeed(fallSpeed);
//	characterControl.setGravity(gravity);
//	this.addControl(characterControl);
 
//	animControl = model.getControl(AnimControl.class);
//	//animControl.addListener(this);
//	for (String anim : animControl.getAnimationNames()) { System.out.println(anim); }
//        animChannel = animControl.createChannel();
//	animChannel.setAnim(idleAnim);
 
	this.inputManager = inputManager;
        inputManager.setCursorVisible(true);
	setUpKeys();
    }
 
    //Make sure to call this from the main simpleUpdate() loop
    public void update()
    {
	Vector3f camDir = cam.getDirection().clone();
	camDir.y = 0;
	Vector3f camLeft = cam.getLeft().clone();
	camLeft.y = 0;
	walkDirection.set(0, 0, 0);
 
	if (left)  { walkDirection.addLocal(camLeft); }
	if (right) { walkDirection.addLocal(camLeft.negate()); }
	if (up)    { walkDirection.addLocal(camDir); }
	if (down)  { walkDirection.addLocal(camDir.negate()); }
 
	characterControl.setWalkDirection(walkDirection.normalize().multLocal(walkSpeed));
 
	handleAnimations();
    }
 
    private void handleAnimations()
    {
	if(attacking)
	{
	    //waiting for attack animation to finish
            attacking=false;
	}
	else if(attack)
	{
 
	    animChannel.setAnim(attackAnim,.3f);
	    animChannel.setLoopMode(LoopMode.DontLoop);
	    attack = false;
	    attacking = true;
	}
	else if(characterControl.onGround())
	{
	    if(left || right || up || down)
	    {
		if(!animChannel.getAnimationName().equals(walkAnim))
		{
		    animChannel.setAnim(walkAnim,.3f);
		    animChannel.setLoopMode(LoopMode.Loop);
		}
	    }
	    else
	    {
		if(!animChannel.getAnimationName().equals(idleAnim))
		{
		    animChannel.setAnim(idleAnim,.3f);
		    animChannel.setLoopMode(LoopMode.Cycle);
		}
	    }
	}
    }
 
    private void setUpKeys()
    {
//	inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
//	inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
//	inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
//	inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
//	inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
//	inputManager.addMapping("Attack", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Pan", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
	inputManager.addMapping("TurnLeft", new MouseAxisTrigger(MouseInput.AXIS_X,true));
	inputManager.addMapping("TurnRight", new MouseAxisTrigger(MouseInput.AXIS_X,false));
	inputManager.addMapping("MouselookDown", new MouseAxisTrigger(MouseInput.AXIS_Y,true));
	inputManager.addMapping("MouselookUp", new MouseAxisTrigger(MouseInput.AXIS_Y,false));
//	inputManager.addListener(this, "Left");
//	inputManager.addListener(this, "Right");
//	inputManager.addListener(this, "Up");
//	inputManager.addListener(this, "Down");
//	inputManager.addListener(this, "Jump");
//	inputManager.addListener(this, "Attack");
        inputManager.addListener(this, "Pan");
	inputManager.addListener(this, "TurnLeft");
	inputManager.addListener(this, "TurnRight");
	inputManager.addListener(this, "MouselookDown");
	inputManager.addListener(this, "MouselookUp");
    }
 
    public void onAction(String binding, boolean value, float tpf) {
	if (binding.equals("Left"))
	{
	    left = value;
	}
	else if (binding.equals("Right"))
	{
	    right = value;
	}
	else if (binding.equals("Up"))
	{
	    up = value;
	}
	else if (binding.equals("Down"))
	{
	    down = value;
	}
	else if (binding.equals("Jump"))
	{
	    if(characterControl.onGround())
	    {
		characterControl.jump();
		if(!attacking)
		{
		    animChannel.setAnim(jumpAnim,.3f);
		    animChannel.setLoopMode(LoopMode.Loop);
		}
	    }
	}
	else if (binding.equals("Attack"))
	{
	    attack = value;
	}
        else if (binding.equals("Pan"))
        {
            pan=value;
            inputManager.setCursorVisible(!value);
            System.out.println("Pan = : ");
        }
    }
 
    //Analog handler for mouse movement events.
    //It is assumed that we want horizontal movements to turn the character,
    //while vertical movements only make the camera rotate up or down.
    public void onAnalog(String binding, float value, float tpf)
    {
	if (binding.equals("TurnLeft"))
	{
	           if (pan) {
                Quaternion turn = new Quaternion();
	    turn.fromAngleAxis(mouselookSpeed*value, Vector3f.UNIT_Y);
	    characterControl.setViewDirection(turn.mult(characterControl.getViewDirection()));
            }
	}
	else if (binding.equals("TurnRight"))
	{
	    
            if (pan) {
                Quaternion turn = new Quaternion();
	    turn.fromAngleAxis(-mouselookSpeed*value, Vector3f.UNIT_Y);
	    characterControl.setViewDirection(turn.mult(characterControl.getViewDirection()));
            }
	}
	else if (binding.equals("MouselookDown"))
	{
	           if (pan) {
                camera.verticalRotate(mouselookSpeed*value);
            }
            
	}
	else if (binding.equals("MouselookUp"))
	{
	           if (pan) {
                camera.verticalRotate(-mouselookSpeed*value);
            }
            
	}
    }
 
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName)
    {
	if(channel == animChannel && attacking && animName.equals(attackAnim))
	{
	    attacking = false;
	}
    }
 
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName)
    {
    }
 
    public CharacterControl getCharacterControl()
    {
	return characterControl;
    }
 
    public ThirdPersonCamera getCameraNode()
    {
	return camera;
    }
}