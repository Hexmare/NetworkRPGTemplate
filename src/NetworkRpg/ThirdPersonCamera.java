package NetworkRpg;
 
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
 
/**
 *
 * @author Berzee
 */
public class ThirdPersonCamera
{
    //The "pivot" Node allows for easy third-person mouselook! It's actually
    //just an empty Node that gets attached to the center of the Player.
    //
    //The CameraNode is set up to always position itself behind the *pivot*
    //instead of behind the Player. So when we want to mouselook around the
    //Player, we simply need to spin the pivot! The camera will orbit behind it
    //while the Player object remains still.
    //
    //NOTE: Currently only vertical mouselook (around the X axis) is working.
    //The other two axes could be added fairly easily, once you have an idea
    //for how they should actually behave (min and max angles, et cetera).
    private Node pivot;
    private CameraNode cameraNode;
 
    //Change these as you desire. Lower verticalAngle values will put the camera
    //closer to the ground.
    public float followDistance = 7;
    public float verticalAngle = 30 * FastMath.DEG_TO_RAD;
 
    //These bounds keep the camera from spinning too far and clipping through
    //the floor or turning upside-down. You can change them as needed but it is
    //recommended to keep the values in the (-90,90) range.
    public float maxVerticalAngle = 85 * FastMath.DEG_TO_RAD;
    public float minVerticalAngle = 5 * FastMath.DEG_TO_RAD;
 
    public ThirdPersonCamera(String name, Camera cam, Node player)
    {
	pivot = new Node("CamTrack");	
	player.attachChild(pivot);
 
	cameraNode = new CameraNode(name, cam);
        cameraNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
	pivot.attachChild(cameraNode);
//	player.attachChild(cameraNode);
//        cameraNode.setLocalTranslation(new Vector3f(0, 0, followDistance));
        cameraNode.setLocalTranslation(new Vector3f(0, 5, -5));
        Quaternion rotQuat1 = new Quaternion();
        rotQuat1.fromAngleAxis((float)Math.PI * 0.5f, new Vector3f(0, 1, 0));
        pivot.setLocalRotation(rotQuat1);
        
        
	cameraNode.lookAt(pivot.getLocalTranslation(), Vector3f.UNIT_Y);
//        cameraNode.lookAt(player.getLocalTranslation(), Vector3f.UNIT_Y);
        
	pivot.getLocalRotation().fromAngleAxis(-verticalAngle, Vector3f.UNIT_X);
    }
 
    public void verticalRotate(float angle)
    {
	verticalAngle += angle;
 
	if(verticalAngle > maxVerticalAngle)
	{
	    verticalAngle = maxVerticalAngle;
	}
	else if(verticalAngle < minVerticalAngle)
	{
	    verticalAngle = minVerticalAngle;
	}
 
	pivot.getLocalRotation().fromAngleAxis(-verticalAngle, Vector3f.UNIT_X);
    }
 
    public CameraNode getCameraNode()
    {
	return cameraNode;
    }
 
    public Node getCameraTrack()
    {
	return pivot;
    }
}