/*
 * $Id: MovementService.java 1204 2013-10-17 07:36:02Z PSpeed42@gmail.com $
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
package NetworkRpg.Services;

import NetworkRpg.AppStates.ModelState;
import NetworkRpg.Components.Position;

import NetworkRpg.Objects.Avatar;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Watches for entities with MoveTo, Position, and Speed components and
 * processes their actual movement.
 *
 * @author Paul Speed
 */
public class MovementService implements Service {

    static Logger log = LoggerFactory.getLogger(MovementService.class);
    private GameSystems systems;
    private EntityData ed;
    private EntitySet mobs;

    public MovementService() {
    }

    public void initialize(GameSystems systems) {
        this.systems = systems;
        this.ed = systems.getEntityData();
        mobs = ed.getEntities(Position.class);
    }

    public void update(long gameTime) {
        mobs.applyChanges();
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


        ModelState ms = systems.getApplication().getStateManager().getState(ModelState.class);
        // Perform all movements for all active mobs
        for (Entity e : mobs) {
            Avatar modelAvatar = (Avatar)ms.getSpatial(e.getId());
            Vector3f currentPos = modelAvatar.getChild("character node").getLocalTranslation();
            System.out.println(currentPos);
            ed.setComponent(e.getId(), new Position(currentPos,gameTime,gameTime));
        }
    }

    public void terminate(GameSystems systems) {
        mobs.release();
    }
}
