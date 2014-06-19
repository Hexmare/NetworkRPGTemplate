/*
 * $Id: PlayerState.java 1169 2013-10-05 06:43:37Z PSpeed42@gmail.com $
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

import NetworkRpg.GameClient;
import NetworkRpg.Main;
import NetworkRpg.Networking.Msg.CommandSet;
//import trap.game.Direction;
//import trap.game.Position;
//import trap.game.SensorArea;
import com.jme3.app.Application;
import com.jme3.audio.Listener;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.lwjgl.opengl.DisplayMode;

//import com.simsilica.lemur.GuiGlobals;
//import com.simsilica.lemur.event.BaseAppState;
//import com.simsilica.lemur.input.AnalogFunctionListener;
//import com.simsilica.lemur.input.FunctionId;
//import com.simsilica.lemur.input.InputMapper;

/**
 *
 * @author Paul Speed
 */
public class PlayerState extends BaseAppState implements ActionListener, AnalogListener { //implements AnalogFunctionListener {

    private GameClient client;
    private EntityData ed;
    private EntityId player;
    private Client networkClient;
    private Node interpNode;
    //private Position lastPos;
    private Quaternion cameraAngle;
    private Vector3f cameraDelta;
    private Vector3f audioDelta;
    private float cameraDistance = 15; //20; //12;
    private InputManager inputManager;
    // Here for the moment
    //private SensorArea sensor;
    private int xLast = -1;
    private int yLast = -1;
    private boolean fwd = false;
    private boolean rev = false;
    private boolean left = false;
    private boolean right = false;
    private boolean jump = false;
    private boolean updateCommand = false;
    private boolean pan = false;
    private boolean altPressed = false;
    private Listener audioListener = new Listener();

    public PlayerState(GameClient client, Listener audioListener) {
        this.client = client;
        this.audioListener = audioListener;
    }

    public PlayerState(GameClient client) {
        this.client = client;
        this.audioListener = null;
    }

    public GameClient getClient() {
        return client;
    }

    public Listener getAudioListener() {
        return audioListener;
    }

    @Override
    protected void initialize(Application app) {
        this.networkClient = getApplication().getStateManager().getState(ConnectionState.class).getClient();
        this.inputManager = app.getInputManager();
        inputManager.setCursorVisible(true);
        this.ed = client.getEntityData();
        this.player = client.getPlayer();
        //app.getCamera().setLocation(new Vector3f(10f,10f,10f));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("ToggleDebug", new KeyTrigger(KeyInput.KEY_F12));
        inputManager.addMapping("Pan", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("TurnLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("TurnRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MouselookDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("MouselookUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("lAlt", new KeyTrigger(KeyInput.KEY_LMENU));
        inputManager.addMapping("enter", new KeyTrigger(KeyInput.KEY_RETURN));

        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
        inputManager.addListener(this, "ToggleDebug");
        inputManager.addListener(this, "Pan");
        inputManager.addListener(this, "TurnLeft");
        inputManager.addListener(this, "TurnRight");
        inputManager.addListener(this, "MouselookDown");
        inputManager.addListener(this, "MouselookUp");
        inputManager.addListener(this, "lAlt");
        inputManager.addListener(this, "enter");


        /*
         InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
         inputMapper.addAnalogListener(this,
         PlayerFunctions.F_NORTH,
         PlayerFunctions.F_SOUTH,
         PlayerFunctions.F_EAST,
         PlayerFunctions.F_WEST);
 
         cameraAngle = new Quaternion().fromAngles(FastMath.QUARTER_PI * 1.3f, FastMath.PI, 0);
         cameraDelta = cameraAngle.mult(Vector3f.UNIT_Z);
         cameraDelta.multLocal(-cameraDistance);

         audioListener.setRotation(cameraAngle);
         audioDelta = cameraAngle.mult(Vector3f.UNIT_Z);
         audioDelta.multLocal(4);        
        
         // Back it up a little so the framing is more even
         cameraDelta.addLocal(0, -1, 0);
        
         sensor = new SensorArea(getState(MazeState.class).getMaze(), 4);       
         * */
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            left = value;
            updateCommand = true;

        } else if (binding.equals("Right")) {
            right = value;
            updateCommand = true;
        } else if (binding.equals("Up")) {
            fwd = value;
            updateCommand = true;
        } else if (binding.equals("Down")) {
            rev = value;
            updateCommand = true;
        }
        if (binding.equalsIgnoreCase("toggledebug")) {
            if (!value) {
                BulletAppState bs = getApplication().getStateManager().getState(BulletAppState.class);
                bs.setDebugEnabled(!bs.isDebugEnabled());
                //System.out.println("setting debug");
            }

        }
        if (binding.equals("Jump")) {
            jump = value;
            updateCommand = true;

        }
        
        if (binding.equals("lAlt")) {
            altPressed = value;


        }
        
        if (binding.equals("enter") && altPressed) {
            if (value) {
                boolean isFullScreen = ((Main)getApplication()).getIsFullScreen();
                AppSettings newAppSettings = new AppSettings(true);
                if (isFullScreen) {
                    newAppSettings.setResolution(1024, 768);
                }
                else
                {
                    newAppSettings.setResolution(1920, 1080);
                }
                
                newAppSettings.setFullscreen(!isFullScreen);
                ((Main)getApplication()).setIsFullScreen(!isFullScreen);
                getApplication().setSettings(newAppSettings);
                getApplication().restart();
                //toggleToFullscreen();
            }


        }
        
        if (binding.equals("Pan")) {
            pan = value;
            inputManager.setCursorVisible(!value);
            //System.out.println("Pan = : ");
            updateCommand = true;
        }


    }

    public void onAnalog(String binding, float value, float tpf) {
        if (pan) {
            getApplication().getStateManager().getState(ModelState.class).setAvatarDirection(binding, value, tpf);
            //updateCommand = true;
        }
    }

    public void toggleToFullscreen() {
        AppSettings settings = new AppSettings(false);
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        java.awt.DisplayMode[] modes = device.getDisplayModes();
        int i=0; // note: there are usually several, let's pick the first
        settings.setResolution(modes[i].getWidth(),modes[i].getHeight());
        settings.setFrequency(modes[i].getRefreshRate());
        settings.setBitsPerPixel(modes[i].getBitDepth());
        settings.setFullscreen(device.isFullScreenSupported());
        getApplication().setSettings(settings);
        getApplication().restart(); // restart the context to apply changes
      }
    
    @Override
    protected void cleanup(Application app) {
        
    }

    @Override
    public void update(float tpf) {
        Camera cam = getApplication().getCamera();
        if (updateCommand) {
            updateCommand = false;
            CommandSet cs = new CommandSet(player, fwd, rev, left, right, jump);
            networkClient.send(cs);
            getApplication().getStateManager().getState(ModelState.class).setAvatarCommand(cs);
        }
        /*
         Position pos = ed.getComponent(player, Position.class);

         if( interpNode != null ) {
 
         if( pos != lastPos ) {
         lastPos = pos;
         if( pos != null ) {
         interpNode.getControl(InterpolationControl.class).setTarget(pos.getLocation(), pos.getFacing(), pos.getChangeTime(), pos.getTime());
         }
         }
            
         // Make sure it is up to date
         interpNode.updateLogicalState(tpf);
        
         Vector3f loc = cam.getLocation();
         loc.set(interpNode.getLocalTranslation());
         loc.addLocal(cameraDelta);
         cam.setLocation(loc);
 
         loc = audioListener.getLocation(); 
         loc.set(interpNode.getLocalTranslation());
         loc.addLocal(audioDelta);
         audioListener.setLocation(loc);
         }                
        
         if( pos != null ) {        
         Vector3f loc = pos.getLocation();
            
         int x = (int)loc.x / 2; 
         int y = (int)loc.z / 2;
            
         if( x != xLast || y != yLast ) {
         xLast = x;
         yLast = y;
         sensor.setCenter(x, y);
 
         getState(MazeState.class).clearVisibility(MazeState.PLAYER_VISIBLE);
         getState(MazeState.class).setVisibility(sensor, MazeState.PLAYER_VISIBLE | MazeState.PLAYER_VISITED); 
         }            
         }       
        
         */
    }

    @Override
    protected void enable() {
        /*
         InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();    
         inputMapper.activateGroup(PlayerFunctions.GROUP);
        
         // Create a node that we will use for interpolation... this
         // way we get to reuse the interpolation control.
         interpNode = new Node("interp");
         Position pos = ed.getComponent(player, Position.class);
         if( pos != null ) {
         interpNode.setLocalTranslation(pos.getLocation().mult(2));
         }
         interpNode.addControl(new InterpolationControl(client.getRenderTimeProvider()));
 
         Camera cam = getApplication().getCamera();       
         cam.setRotation(cameraAngle);
         */
    }

    @Override
    protected void disable() {
        /*
         InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();    
         inputMapper.deactivateGroup(PlayerFunctions.GROUP);
         */
    }

    /*
     public void valueActive( FunctionId func, double value, double tpf ) {
     if( Math.abs(value) < 0.5 ) {
     return;
     }
        
     if( func == PlayerFunctions.F_NORTH ) {
     client.move(Direction.North);
     } else if( func == PlayerFunctions.F_SOUTH ) {
     client.move(Direction.South);
     } else if( func == PlayerFunctions.F_EAST ) {
     client.move(Direction.East);
     } else if( func == PlayerFunctions.F_WEST ) {
     client.move(Direction.West);
     }
     }*/
}
