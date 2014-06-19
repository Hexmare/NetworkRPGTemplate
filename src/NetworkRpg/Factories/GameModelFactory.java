/*
 * $Id: GameModelFactory.java 1184 2013-10-10 07:26:40Z PSpeed42@gmail.com $
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
package NetworkRpg.Factories;

import NetworkRpg.AppStates.ModelState;
import NetworkRpg.Components.ModelType;
import NetworkRpg.GameConstants;
import NetworkRpg.Objects.*;
import NetworkRpg.TimeProvider;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Listener;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.simsilica.es.Entity;
import NetworkRpg.Components.Position;

import com.jme3.app.SimpleApplication;

//import trap.task.Tasks;
/**
 *
 * @author Paul Speed
 */
public class GameModelFactory implements ModelFactory {

    private AssetManager assets;
    private Listener audioListener;
    private TimeProvider time;
    private SimpleApplication app;
    private ModelState state;

    public GameModelFactory(SimpleApplication App, Listener audioListener, TimeProvider time) {
        this.app = App;
        this.assets = app.getAssetManager();
        this.audioListener = audioListener;
        this.time = time;
    }

    public void setState(ModelState state) {
        this.state = state;
    }
 

    @Override
    public Spatial createModel(Entity e) {
        ModelType type = e.get(ModelType.class);
        return createModel(type);
    }

    public Spatial createModel(ModelType type) {

        if (GameConstants.TYPE_OGRE.equals(type)) {
            return createOgre();
        } else {
            throw new RuntimeException("Could not create model for:" + type);
        }
    }

    

    public Spatial createOgre() {
        return new Avatar("ogre", app);
    }

    

    
}
