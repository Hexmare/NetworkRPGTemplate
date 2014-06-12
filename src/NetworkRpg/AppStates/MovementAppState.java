/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NetworkRpg.AppStates;

import NetworkRpg.Components.Position;
import NetworkRpg.Components.Speed;
import NetworkRpg.Objects.Avatar;
import NetworkRpg.Services.GameSystems;
import NetworkRpg.Services.MovementService;
import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.es.base.DefaultEntityData;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hexmare
 */
public class MovementAppState extends BaseAppState {
    static Logger log = LoggerFactory.getLogger(MovementService.class);
    private GameSystems systems;
    private EntityData ed;
    //private MazeService mazeService;
    //private Maze maze;
    private EntitySet mobs;

    public MovementAppState() {
        
    }

    public MovementAppState( EntityData ed, GameSystems gs ) {
        this.ed = ed;
        this.systems = gs;
    }

    public EntityData getEntityData() {
        return ed;
    }

    @Override
    protected void initialize( Application app ) {
        mobs = ed.getEntities(Position.class, Speed.class);
    }

    @Override
    protected void cleanup( Application app ) {
        mobs.release();
    }

    @Override
    protected void enable() {
    }

    @Override
    protected void disable() {
    }
    
    @Override
    public void update(float tpf)
    {
        
        //System.out.println("Updating");
        // Keep track of the places that are moved to
        // in this frame so that we can cancel additional moves of
        // mobs into those spaces without having to constantly
        // recalculate from the maze service.
        Set<Vector3f> occupied = new HashSet<Vector3f>();

        // The presumption is that the code setting the move to
        // already checked space availability at that time.  We
        // only have to check for availability that changes because
        // of these moves.

        log.debug("Doing actual movements...");
        if (mobs.applyChanges()) {
            ModelState ms = systems.getApplication().getStateManager().getState(ModelState.class);
            // Perform all movements for all active mobs
            for (Entity e : mobs) {
                Avatar modelAvatar = (Avatar)ms.getSpatial(e.getId());
                Vector3f currentPos = modelAvatar.getChild("character node").getLocalTranslation();
                //System.out.println(modelAvatar.getChild("character node").getLocalTranslation());
                //System.out.println(currentPos);
                ed.setComponent(e.getId(), new Position(currentPos,(int)tpf,(int)tpf));
    //            MoveTo to = e.get(MoveTo.class);
    //            if (to == null) {
    //                System.out.println("Incomplete entity:" + e);
    //            }
    //            if (to.getTime() > gameTime) {
    //                continue;
    //            }
    //
    //            Position pos = e.get(Position.class);
    //            Speed speed = e.get(Speed.class);
    //
    //            Direction dir = Direction.fromDelta(pos.getLocation(), to.getLocation());

                // This is a little fragile but we take advantage of
                // the fact that the Position's original direction was
                // set from dir.getFacing()
    //            if (dir.getFacing().equals(pos.getFacing())) {
    //                // Then we can move
    //
    //                if (log.isDebugEnabled()) {
    //                    log.debug("Move:" + e + " to:" + pos);
    //                }
    //
    //                // Remove the component because we no longer need it
    //                ed.removeComponent(e.getId(), MoveTo.class);
    //
    //                if (!occupied.add(to.getLocation())) {
    //                    // Something already moved here... nothing left
    //                    // to do
    //                    continue;
    //                }

                    // Check the maze service, too because if we delayed
                    // moving because of a turn then something might have
                    // moved into our spot.
    //                if (mazeService.isOccupied(to.getLocation())) {
    //                    if (log.isDebugEnabled()) {
    //                        log.debug("Already occupied:" + to.getLocation()
    //                                + " by:" + mazeService.getEntities((int) (to.getLocation().x / 2), (int) (to.getLocation().z / 2)));
    //                    }
    //                    // Something already moved here... nothing left
    //                    // to do
    //                    continue;
    //                }

                    // Right now the distance is always the same so we
                    // won't bother calculating it.               
    //                double stepDistance = 2.0;
    //                long actionTimeMs = (long) (stepDistance / speed.getMoveSpeed() * 1000.0);
    //                long actionTimeNanos = actionTimeMs * 1000000;
    //                //long time = gameTime; // could also be to.getTime()... I'm torn.
    //                long time = to.getTime();
    //                Position next = new Position(to.getLocation(), dir.getFacing(),
    //                        time, time + actionTimeNanos);
    //                Activity act = new Activity(Activity.WALKING, time, time + actionTimeNanos);
    //                ed.setComponents(e.getId(), next, act);

    //            } else {
    //                if (log.isDebugEnabled()) {
    //                    log.debug("Turn:" + e + " to:" + pos);
    //                }
    //                
    //                
    ////                // We need to turn first
    ////                long actionTimeMs = (long) (0.25 / speed.getTurnSpeed() * 1000.0);
    ////                long actionTimeNanos = actionTimeMs * 1000000;
    ////                //aaalong time = gameTime; // could also be to.getTime()... I'm torn.
    ////                long time = to.getTime();
    ////
    ////                Position next = new Position(pos.getLocation(), dir.getFacing(),
    ////                        time, time + actionTimeNanos);
    ////                Activity act = new Activity(Activity.TURNING, time, time + actionTimeNanos);
    ////                ed.setComponents(e.getId(), next, act);
    //
    //                // And reset the move to time to after the turn is done
    //                e.set(to.newTime(act.getEndTime()));
    //            }
            }
        }
        
    }
}
