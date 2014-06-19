/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.Handlers;

import NetworkRpg.AppStates.ModelState;
import NetworkRpg.GameConstants;
import NetworkRpg.Services.GameSystems;
import NetworkRpg.Networking.Msg.CommandSet;
import NetworkRpg.Networking.Msg.GameTimeMessage;
import NetworkRpg.Networking.Msg.PlayerInfoMessage;
import NetworkRpg.Factories.EntityFactories;
import NetworkRpg.Networking.Msg.ChatMessage;
import NetworkRpg.Networking.Msg.ViewDirection;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.Name;
import java.util.Random;

/**
 *
 * @author hexmare
 */
public class GameMessageHandler {
    public static final String ATTRIBUTE = "GameHandler";
    
    private HostedConnection conn;
    private GameSystems systems;
    private EntityData ed;
    private String name;
    private EntityId player;
    
    public GameMessageHandler( GameSystems systems, HostedConnection conn ) {
        this.systems = systems;
        this.ed = systems.getEntityData();
        this.conn = conn;
    }
    
 
    
    public void close() {
        // Here we can remove the player's entity, etc.
        
        if( player != null ) {
            ed.removeEntity(player);
        }
    }
 
    protected void ping( GameTimeMessage msg ) {
        // Send the latest game time back
        long time = systems.getGameTime();
        conn.send(msg.updateGameTime(time).setReliable(true));
    }
    
 
    
    protected void playerInfo( PlayerInfoMessage msg ) {
        System.out.println( "Got player info:" + msg );    
        if( player == null ) {
            this.name = msg.getName();
            // Create a player
            long time = systems.getGameTime();
            Random rand = new Random();
            float temp = rand.nextFloat() * 5;
            player = EntityFactories.createObject( GameConstants.TYPE_OGRE,
                                                   time, 
                                                   //loc,
                                                   new Vector3f(temp,5f,temp),
                                                   new Name(name)//,
                                                   //new Activity(Activity.SPAWNING, time, time + 2 * 1000 * 1000000) 
                                                    );            
 
            // Send a message back to the player with their entity ID
            conn.send(new PlayerInfoMessage(player).setReliable(true));
            
            // Send the current game time
            conn.send(new GameTimeMessage(time).setReliable(true));
            
          
        }
    }
    
    

    

    
    protected void commmandMessage(CommandSet msg){
        systems.getApplication().getStateManager().getState(ModelState.class).setAvatarCommand(msg);
        conn.getServer().broadcast(msg);
    }
    
    protected void viewDirection(ViewDirection msg){
        systems.getApplication().getStateManager().getState(ModelState.class).setAvatarViewDirection(msg);
        conn.getServer().broadcast(msg);
    }
    
    protected void chatMessage(ChatMessage msg){
        conn.getServer().broadcast(msg);
    }
    
    
    
}
