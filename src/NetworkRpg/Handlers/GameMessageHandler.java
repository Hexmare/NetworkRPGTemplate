/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.Handlers;

import NetworkRpg.AppStates.ModelState;
import NetworkRpg.Components.Activity;
import NetworkRpg.Components.Dead;
import NetworkRpg.GameConstants;
import NetworkRpg.Services.GameSystems;
import NetworkRpg.Networking.Msg.ClientKill;
import NetworkRpg.Networking.Msg.CommandSet;
import NetworkRpg.Networking.Msg.GameTimeMessage;
import NetworkRpg.Networking.Msg.HelloMessage;
import NetworkRpg.Networking.Msg.LocAndDir;
import NetworkRpg.Networking.Msg.PlayerInfoMessage;
import NetworkRpg.Networking.Msg.ServerKill;
import NetworkRpg.Factories.EntityFactories;
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
    
    protected void move( LocAndDir msg ) {
        //System.out.println( "Got Walk LocAndDir info:");    
    }
    
    public void close() {
        // Here we can remove the player's entity, etc.
        
        if( player != null ) {
            // Kill the player so their loot drops.
            ed.setComponent(player, new Dead(systems.getGameTime()));
            ed.removeEntity(player);
        }
    }
 
    protected void ping( GameTimeMessage msg ) {
        // Send the latest game time back
        long time = systems.getGameTime();
        conn.send(msg.updateGameTime(time).setReliable(true));
    }
    
    protected void locdir( LocAndDir msg ) {
        // Send the latest game time back
        long time = systems.getGameTime();
        //conn.send(msg.updateGameTime(time).setReliable(true));
        //System.out.println("caught locanddir msg");
        conn.send(new HelloMessage("Yup got it"));
    }
    
    protected void playerInfo( PlayerInfoMessage msg ) {
        System.out.println( "Got player info:" + msg );    
        if( player == null ) {
            this.name = msg.getName();
            System.out.println(this.name);
            // Find a position for the player
            //Vector3f loc = systems.getService(MazeService.class).getPlayerSpawnLocation();
            //Maze maze = systems.getService(MazeService.class).getMaze(); 
        
            // Create a player
            long time = systems.getGameTime();
            Random rand = new Random();
            float temp = rand.nextFloat() * 5;
            player = EntityFactories.createObject( GameConstants.TYPE_OGRE,
                                                   time, 
                                                   //loc,
                                                   new Vector3f(temp,5f,temp),
                                                   new Name(name),
                                                   new Activity(Activity.SPAWNING, time, time + 2 * 1000 * 1000000) 
                                                    );            
 
            // Send a message back to the player with their entity ID
            conn.send(new PlayerInfoMessage(player).setReliable(true));
            
            // Send the current game time
            conn.send(new GameTimeMessage(time).setReliable(true));
            
            // Go ahead and send them the maze, also
            //conn.send(new MazeDataMessage(maze).setReliable(true));           
        }
    }
    
    
    protected void hm( HelloMessage msg ) {
        // Send the latest game time back
        long time = systems.getGameTime();
        //conn.send(msg.updateGameTime(time).setReliable(true));
        
        System.out.println(((HelloMessage)msg).getMessage());
        
    }
    
    protected void sk(ServerKill msg){
        System.out.println("Got server kill message");
        //Need to  do these 
        //server.broadcast(new ClientKill());
        //Need to issue stop();
        
    }
    
    protected void commmandMessage(CommandSet msg){
        System.out.println("got command Message : " + systems.getGameTime());
        systems.getApplication().getStateManager().getState(ModelState.class).setAvatarCommand(msg);
        conn.getServer().broadcast(msg);
    }
    
    
    
    
}
