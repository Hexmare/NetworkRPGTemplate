/*
 * $Id: GamePlayState.java 1205 2013-10-17 08:19:03Z PSpeed42@gmail.com $
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

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.Environment;
import com.jme3.audio.Listener;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.es.ComponentFilter;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Filters;
import com.simsilica.es.Name;
//import com.simsilica.lemur.event.BaseAppState;
import java.util.ArrayList;
import java.util.List;
import NetworkRpg.Components.ArmorStrength;
import NetworkRpg.Components.CombatStrength;
import NetworkRpg.Components.HitPoints;
import NetworkRpg.Components.MaxHitPoints;
//import NetworkRpg.Components.Maze;
import NetworkRpg.Components.ModelType;
import NetworkRpg.Factories.TrapModelFactory;
import NetworkRpg.GameClient;
import NetworkRpg.GameConstants;
import NetworkRpg.TimeProvider;


/**
 *
 *  @author    Paul Speed
 */
public class GamePlayState extends BaseAppState {    
    private List<AppState> gameStates = new ArrayList<AppState>();

    private GameClient client;

    // There will be only one in single player but this way
    // it will get refreshed automatically.
    private EntitySet players;
    private Entity player;

    private Listener audioListener = new Listener(); 

    public GamePlayState( GameClient client ) { 
        this.client = client;
    }

    @Override
    protected void initialize(Application app) {

        // Move this to an audio manager state 
        app.getAudioRenderer().setListener(audioListener);
        
        // Setup the audio environment... here for now              
        app.getAudioRenderer().setEnvironment(Environment.Closet);
        
        //Effects.initialize(client.getRenderTimeProvider(), app.getAssetManager());
 
        // Grab some client properties that we will need for
        // our client-side states.       
        //Maze maze = client.getMaze();
        EntityData ed = client.getEntityData(); 

        TimeProvider time = client.getRenderTimeProvider(); 

        gameStates.add(new EntityDataState(ed));
        gameStates.add(new WorldState());
        gameStates.add(new ModelState(time, new TrapModelFactory((SimpleApplication)app, audioListener, time),ed));
        //gameStates.add(new CharacterAnimState());
        //gameStates.add(new DeathState(time));
        //gameStates.add(new MazeState(maze));
        //gameStates.add(new PlayerState(client, audioListener));
        gameStates.add(new PlayerState(client));
        //gameStates.add(new HudState());
   
        //gameStates.add(new FlyCamAppState());
 
 
        // We only care about the monkeys...
        ComponentFilter filter = Filters.fieldEquals(ModelType.class, 
                                                     "type", 
                                                     GameConstants.TYPE_MONKEY.getType());
        players = ed.getEntities(filter, ModelType.class, Name.class, 
                                         HitPoints.class, MaxHitPoints.class,
                                         CombatStrength.class, ArmorStrength.class);
        
        // Attach all of the child states
        AppStateManager stateMgr = app.getStateManager();
        for( AppState state : gameStates ) {
            stateMgr.attach(state);   
        }                
    }

    @Override
    protected void cleanup(Application app) {
        players.release();
        
        // Detach all the states we added... in reverse order
        AppStateManager stateMgr = app.getStateManager();
        for( int i = gameStates.size() -1; i >= 0; i-- ) {
            AppState state = gameStates.get(i);
            stateMgr.attach(state);   
        }
        gameStates.clear();
        client.close();
    }

    @Override
    protected void enable() {
        //getState(MusicState.class).setSong("Sounds/ambient-theme.ogg", 1);
        //getState(MusicState.class).setVolume(0.5f);

        Node rootNode = ((SimpleApplication)getApplication()).getRootNode(); 
 
        /** A white, directional light source */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.75f, -0.95f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        /** A white ambient light source. */
        AmbientLight ambient = new AmbientLight();
        //ambient.setColor(ColorRGBA.DarkGray);
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);

    }

    @Override
    public void update( float tpf ) {
 
        client.updateRenderTime();
        
        if( players.applyChanges() ) {
            //getState(HudState.class).updatePlayer();
        }
        
        if( player == null ) {
            // In multiplayer it may take a few frames to get the
            // player.
            player = players.getEntity(client.getPlayer());
            if( player != null ) {
                //getState(HudState.class).setPlayer(player);
            }
        }  

    }

    @Override
    protected void disable() {
        //getState(MusicState.class).setSong(null);
    }
}
