/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.Networking;

//import SurviveES.Components.*;
//import SurviveES.Msg.*;
import NetworkRpg.Components.*;
import NetworkRpg.Networking.Msg.*;
import com.jme3.network.serializing.Serializer;
import com.jme3.network.serializing.serializers.FieldSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author hexmare
 */
public class Util {
    public static final int tcpPort = 31337;
    public static final int udpPort = 31337;
    public static final String GAME_NAME ="survive";
    public static final int PROTOCOL_VERSION = 1;
    
    static Logger log = LoggerFactory.getLogger(Util.class);
    
    private static final Class[] classes = {
        GameTimeMessage.class,
        HelloMessage.class,
        ServerKill.class,
        ClientKill.class,
        LocAndDir.class,
        PlayerInfoMessage.class,
        CommandSet.class,
        ViewDirection.class
    };
    
    private static final Class[] forced = {
        walkDirection.class,
        Activity.class,
        ArmorStrength.class,
        CombatStrength.class,
        //Direction.class,
        HitPoints.class,
        MaxHitPoints.class,
        ModelType.class,
        Position.class,
        Speed.class,
        Dead.class
        
    };
    
    public static void initializeSerializables(){
        Serializer.registerClasses(classes);
        Serializer fieldSerializer = new FieldSerializer();
        boolean error = false;        
        for( Class c : forced) {
            try {
                Serializer.registerClass(c, fieldSerializer);
            } catch( Exception e ) {
                System.out.println("Error registering class:" + e.getMessage());
                log.error("Error registering class:" + c, e);
                error = true;
            }
        }
        if( error ) {
            throw new RuntimeException("Some classes failed to register");
        }
    }
    
}
