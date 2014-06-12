package NetworkRpg;

import NetworkRpg.AppStates.ModelState;
import NetworkRpg.AppStates.MovementAppState;
import NetworkRpg.AppStates.WorldState;
import NetworkRpg.Factories.TrapModelFactory;
import NetworkRpg.Handlers.GameMessageHandler;
import NetworkRpg.Networking.Util;
import NetworkRpg.Services.GameSystems;
import NetworkRpg.Services.Service;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.JmeContext;
import com.simsilica.es.net.EntitySerializers;
import com.simsilica.es.server.EntityDataHostService;
import com.simsilica.es.server.SessionDataDelegator;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * test
 * @author normenhansen
 */
public class ServerMain extends SimpleApplication {
    
    private GameSystems systems;
    private int port;
    private Server host;    
    private EntityDataHostService edHost;
    
    private ConnectionObserver connectionObserver = new ConnectionObserver();

    
    static {
        EntitySerializers.initialize();
        Util.initializeSerializables();
    }
    
    public static void main(String[] args) {
        ServerMain app = new ServerMain();
        app.setPauseOnLostFocus(false);
        app.start(JmeContext.Type.Headless);
        //app.start();
    }

    @Override
    public void simpleInitApp() {
        this.systems = new GameSystems(this);
        
        try {
            // Create the network hosting connection
            host = Network.createServer(GameConstants.GAME_NAME, 
                                        GameConstants.PROTOCOL_VERSION,
                                        Util.tcpPort, Util.udpPort);
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
                                    
        System.out.println("Adding channel 0 on port:" + (port+1));                                    
        host.addChannel(port+1);
 
        // Add our own stub service to send updates at the end of
        // the game update cycle
        systems.addService(new Service() {
                public void update( long gameTime ) {
                    edHost.sendUpdates();
                }

                public void initialize( GameSystems systems ) {
                    // Setup the network listeners
                    edHost = new EntityDataHostService(host, 0, systems.getEntityData());                 
                }

                public void terminate( GameSystems systems ) {
                    // Remove the listeners for the es hosting
                    edHost.stop();        
                }
            });
        
        // Start the game systems
        systems.start();
        TimeProvider time = systems.getGameTimeProvider();
        
        getStateManager().attach(new WorldState());
        getStateManager().attach(new ModelState(time, new TrapModelFactory(this, null, time),systems.getEntityData(),true));
        //getStateManager().attach(new MovementAppState(systems.getEntityData(),systems));
        // Will delegate certain messages to the GameMessageHandler for
        // a particular connection.
        SessionDataDelegator delegator = new SessionDataDelegator(GameMessageHandler.class, GameMessageHandler.ATTRIBUTE, true);
        host.addMessageListener(delegator, delegator.getMessageTypes());
 
        // Add our own connection listener that will add GameMessageHandlers
        // to connections.
        host.addConnectionListener(connectionObserver);
        
        // Start accepting connections
        host.start();
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
        // Stop accepting network connections and kick any
        // existing clients.
        host.close();
 
        // Shut down the game systems
        systems.stop();
        super.destroy();
    }
    
    private class ConnectionObserver implements ConnectionListener {

        public void connectionAdded(Server server, HostedConnection hc) {
            addConnection(hc);
        }

        public void connectionRemoved(Server server, HostedConnection hc) {
            removeConnection(hc);
        }
    }
    
    protected void addConnection( HostedConnection conn ) {
        System.out.println( "New connection from:" + conn );
        GameMessageHandler handler = new GameMessageHandler(systems, conn);
        conn.setAttribute(GameMessageHandler.ATTRIBUTE, handler);
    }
    
    protected void removeConnection( HostedConnection conn ) {
        System.out.println( "Connection closed:" + conn );        
        GameMessageHandler handler = conn.getAttribute(GameMessageHandler.ATTRIBUTE);
        if( handler != null ) {
            handler.close();
        }
    }
    
}
