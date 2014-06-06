/*
 * $Id: TrapModelFactory.java 1184 2013-10-10 07:26:40Z PSpeed42@gmail.com $
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
//import trap.game.ModelType;
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
import NetworkRpg.Controls.ColorControl;
import NetworkRpg.Controls.FloatControl;
import NetworkRpg.Controls.ParticleControl;
import NetworkRpg.MaterialUtils;
import com.jme3.app.SimpleApplication;
import org.lwjgl.opengl.APPLEAuxDepthStencil;
//import trap.task.Tasks;


/**
 *
 *  @author    Paul Speed
 */
public class TrapModelFactory implements ModelFactory {

    private AssetManager assets;
    private Listener audioListener;
    private TimeProvider time;
    private SimpleApplication app;
    private ModelState state;
    
    public TrapModelFactory( SimpleApplication App, Listener audioListener, TimeProvider time ) {
        this.app = App;
        this.assets = app.getAssetManager();
        this.audioListener = audioListener;
        this.time = time;
    }

    public void setState(ModelState state) {
        this.state = state;
    }

    protected Geometry createShadowBox( float xExtent, float yExtent, float zExtent ) {
        Box box = new Box(xExtent, yExtent, zExtent);
        Geometry shadowBox = new Geometry("shadowBounds", box) {
                    @Override
                    public int collideWith( Collidable other, CollisionResults results ) {
                        return 0;
                    }
                };
        shadowBox.move(0,yExtent,0);
            
        Material m = new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setFloat("AlphaDiscardThreshold", 1.1f);  // Don't render it at all
        shadowBox.setMaterial(m);        
        shadowBox.setShadowMode( ShadowMode.Cast );
        return shadowBox;
    }

    protected void fixMaterials( Spatial s ) {
        System.out.println( "Checking:" + s );
        if( s instanceof Geometry ) {
            Geometry geom = (Geometry)s;
            Material m = geom.getMaterial();
System.out.println( "  material name:" + m.getName() + "  asset name:" + m.getAssetName() );            
System.out.println( "  material def name:" + m.getMaterialDef().getName() + "  asset name:" + m.getMaterialDef().getAssetName() );
            
            System.out.println( "  Blend mode:" + m.getAdditionalRenderState().getBlendMode() );
            if( m.getAdditionalRenderState().getBlendMode() == BlendMode.Alpha ) {
                geom.setQueueBucket(Bucket.Transparent);
            } 
        } else {
            Node node = (Node)s;
            for( Spatial child : node.getChildren() ) {
                fixMaterials(child);
            }
        }
    }

    protected void setupMaterials( Spatial s, ColorRGBA diffuse, ColorRGBA ambient ) {
        System.out.println( "Checking:" + s );
        if( s instanceof Geometry ) {
            Geometry geom = (Geometry)s;
            Material m = geom.getMaterial();
System.out.println( "  material name:" + m.getName() + "  asset name:" + m.getAssetName() );            
System.out.println( "  material def name:" + m.getMaterialDef().getName() + "  asset name:" + m.getMaterialDef().getAssetName() );            
System.out.println( "  diffuse: " + m.getParam("Diffuse") );            
System.out.println( "  ambient: " + m.getParam("Ambient") );            
System.out.println( "  useMatColors: " + m.getParam("UseMaterialColors") );            
            
            if( "Common/MatDefs/Light/Lighting.j3md".equals(m.getMaterialDef().getAssetName()) ) {
                m.setBoolean( "UseMaterialColors", true );
                m.setColor( "Diffuse", diffuse );
                m.setColor( "Ambient", ambient );
                
                m.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

System.out.println( "---- after:" );                
System.out.println( "  diffuse: " + m.getParam("Diffuse") );            
System.out.println( "  ambient: " + m.getParam("Ambient") );            
System.out.println( "  useMatColors: " + m.getParam("UseMaterialColors") );            
                 
            }
        } else {
            Node node = (Node)s;
            for( Spatial child : node.getChildren() ) {
                setupMaterials(child, diffuse, ambient);
            }
        }
    }
    
    protected Node createRing( String name, ColorRGBA gemColor, ColorRGBA metalDiffuse, ColorRGBA metalAmbient ) {
    
        Node wrapper = new Node("Ring:" + name);
 
        Node rings = (Node)assets.loadModel( "Models/rings/rings.j3o" );
 
        // Grab the child we want and make sure it's "clean"           
        Spatial ring = rings.getChild( name );
        ring.removeFromParent();
        ring.setLocalScale(1);
        ring.setLocalTranslation(0, 0, 0);            
            
        // Normalize the size and position.                                                            
        BoundingBox bounds = (BoundingBox)ring.getWorldBound();
        ring.setLocalScale( 0.6f / (bounds.getYExtent() * 2) );
        bounds = (BoundingBox)ring.getWorldBound();                        
        ring.setLocalTranslation(0, bounds.getYExtent() - bounds.getCenter().y, 0);
        ring.move(0, 0.75f, 0);

        // Give it a nice shadow            
        wrapper.attachChild(createShadowBox(bounds.getXExtent() * 1.5f, 
                                            bounds.getYExtent(), 
                                            bounds.getZExtent() * 1.5f));
                                            
        // Bob and turn                                            
        wrapper.addControl(new FloatControl());
 
        wrapper.attachChild(ring);

        // First adjust the material colors how we might want before attaching
        // the color control
        MaterialUtils.setColor(wrapper, "Materials/Generated/metal.j3m", "Diffuse", metalDiffuse);
        MaterialUtils.setColor(wrapper, "Materials/Generated/metal.j3m", "Ambient", metalAmbient);
        MaterialUtils.setColor(wrapper, "Materials/Generated/ring-gem.j3m", "Diffuse", gemColor);

        //setupMaterials(wrapper, metalDiffuse, metalAmbient);
        //wrapper.addControl(new ColorControl(metalDiffuse, metalAmbient));
        wrapper.addControl(new ColorControl());        
        
                               
        //wrapper.getControl(ColorControl.class).setColor(new ColorRGBA(0, 1, 0, 0.25f));
                               
        return wrapper;            
    }
    
    protected Node createPotion( String name, ColorRGBA potionColor ) {

        Node wrapper = new Node("Potion:" + name);
 
        Node potions = (Node)assets.loadModel( "Models/potions_0/potions_0.j3o" );
 
        // Grab the child and make sure it's "clean"           
        Spatial bottle = potions.getChild( name );
        bottle.removeFromParent();
        bottle.setLocalScale(1);
        bottle.setLocalTranslation(0, 0, 0);            
 
        // Normalize the size and position.                                                            
        BoundingBox bounds = (BoundingBox)bottle.getWorldBound();
        bottle.setLocalScale( 0.6f / (bounds.getYExtent() * 2) );
        bounds = (BoundingBox)bottle.getWorldBound();                        
        bottle.setLocalTranslation(0, bounds.getYExtent() - bounds.getCenter().y, 0);
        bottle.move(0, 0.75f, 0);
 
        // Give it a bit of a tilt so it looks better spinning           
        bottle.rotate(FastMath.QUARTER_PI, 0, 0);
            
        wrapper.attachChild(createShadowBox(bounds.getXExtent() * 1.5f, 
                                            bounds.getYExtent(), 
                                            bounds.getZExtent() * 1.5f));
        wrapper.addControl(new FloatControl());
 
        wrapper.attachChild(bottle);

        //fixMaterials(wrapper);           
        MaterialUtils.setColor(wrapper, "Materials/Generated/potion-liquid.j3m", "Diffuse", potionColor);
        wrapper.addControl(new ColorControl(new ColorRGBA(1,1,1,1), new ColorRGBA(0.75f, 0.75f, 0.75f, 1)));                       
        //setupMaterials(wrapper, diffuse, ambient);
 
        // testing                      
        //wrapper.getControl(ColorControl.class).setColor(new ColorRGBA(0, 1, 0, 0.25f));                       
                       
        return wrapper;
    }

    public Spatial createModel(Entity e) {

        ModelType type = e.get(ModelType.class);
 
        // Have to do this here since it wants to look at the position
        // of the entity.
        if( GameConstants.TYPE_BLING.equals(type) ) {        
System.out.println( "Creating bling..." );        
            Node wrapper = new Node("Bling");

            ParticleEmitter emitter = new ParticleEmitter("StarBurst", Type.Point, 32);
            emitter.setSelectRandomImage(true);
            emitter.setStartColor(new ColorRGBA(.3f, 0.6f, 0.7f, 1));
            emitter.setEndColor(new ColorRGBA(.1f, .2f, .3f, 0f));
            emitter.setStartSize(0.05f);
            emitter.setEndSize(0.1f);
            emitter.setShape(new EmitterSphereShape(Vector3f.ZERO, 0.1f));
            emitter.setParticlesPerSec(0);
            emitter.setGravity(0, -1, 0);
            emitter.setLowLife(0.4f);
            emitter.setHighLife(0.75f);
            emitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
            emitter.getParticleInfluencer().setVelocityVariation(1f);
            emitter.setImagesX(16);
            emitter.setImagesY(1);
            
            Material mat = new Material(assets, "Common/MatDefs/Misc/Particle.j3md");
            mat.setTexture("Texture", assets.loadTexture("Textures/Smoke.png"));
            mat.setBoolean("PointSprite", true);
            emitter.setMaterial(mat);
            emitter.setLocalTranslation(0, 1f, 0);
            
            wrapper.attachChild(emitter);
            
            AudioNode bling = new AudioNode(assets, "Sounds/bling.ogg", false);
            bling.setVolume(0.25f);
            bling.setPositional(false);
            // It's sort of in tune with the ambient music already so this sounds
            // really off.
            //float random = (float)(Math.random() * 0.05 - 0.025);
            //bling.setPitch(1 + random);
            
            // Having to pass a time index is not ideal.
            Position pos = e.get(Position.class);
            long timeIndex = pos != null ? pos.getTime() : time.getTime();
            timeIndex = Math.max(timeIndex, time.getTime());
            wrapper.addControl(new ParticleControl(emitter, bling, timeIndex, time));
            
            return wrapper;            
        }
                 
        return createModel(type);
    }        
 
    public Spatial createModel( ModelType type ) {
                   
        if( GameConstants.TYPE_MONKEY.equals(type) ) {
            return createMonkey();        
        } else if( GameConstants.TYPE_OGRE.equals(type) ) {
            return createOgre();
        } else if( GameConstants.TYPE_BARRELS.equals(type) ) {
            return createBarrels();
        } else if( GameConstants.TYPE_CHEST.equals(type) ) {
            return createChest();
        } else if( GameConstants.TYPE_BANANA.equals(type) ) {
            Node wrapper = new Node("Banana");
            
            Spatial banana = assets.loadModel( "Models/Banana/Banana.j3o" );
            BoundingBox bounds = (BoundingBox)banana.getWorldBound();
            banana.setLocalScale( 0.5f / (bounds.getYExtent() * 2) );
            bounds = (BoundingBox)banana.getWorldBound();                        
            banana.setLocalTranslation(0, bounds.getYExtent() - bounds.getCenter().y, 0);
            banana.move(0, 0.75f, 0);
            
            wrapper.rotate( 0, (float)(Math.random() * FastMath.TWO_PI), 0 );            
            wrapper.attachChild(createShadowBox(bounds.getXExtent() * 1.5f, 
                                                bounds.getYExtent(), 
                                                bounds.getZExtent() * 1.5f));
            wrapper.addControl(new FloatControl());
 
            wrapper.attachChild(banana);

            ColorRGBA diffuse = new ColorRGBA(1, 1, 1, 1);           
            ColorRGBA ambient = new ColorRGBA(0.75f, 0.75f, 0.75f, 1);
            wrapper.addControl(new ColorControl(diffuse, ambient));                                  
            //setupMaterials(wrapper, diffuse, ambient);
                       
            return wrapper;
                        
        } else if( GameConstants.TYPE_RING1.equals(type) ) {
        
            ColorRGBA diffuse = new ColorRGBA(0.85f, 0.75f, 0.25f, 1);           
            ColorRGBA ambient = new ColorRGBA(0.4f, 0.3f, 0.15f, 1);           
            return createRing("Ouroboros", ColorRGBA.Black, diffuse, ambient); 
        } else if( GameConstants.TYPE_RING2.equals(type) ) {
        
            ColorRGBA diffuse = new ColorRGBA(1f, 1f, 1f, 1);           
            ColorRGBA ambient = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);           
            return createRing("RubyRing", ColorRGBA.Cyan, diffuse, ambient);
            /*
                DiamondRing
                EmeraldRing
                GoldBand
                GoldRing
                Ouroboros
                RubyRing
                SapphireRing
                SignetRing
                SilverBand
                SilverRing
            */           
        } else if( GameConstants.TYPE_RING3.equals(type) ) {
        
            ColorRGBA diffuse = new ColorRGBA(1f, 1f, 1f, 1);           
            ColorRGBA ambient = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);           
            return createRing("RubyRing", ColorRGBA.Red, diffuse, ambient);
        } else if( GameConstants.TYPE_RING4.equals(type) ) {        
        
            ColorRGBA diffuse = new ColorRGBA(1f, 1f, 1f, 1);           
            ColorRGBA ambient = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);           
            return createRing("EmeraldRing", ColorRGBA.Cyan, diffuse, ambient);
        } else if( GameConstants.TYPE_POTION1.equals(type) ) {
                
            return createPotion("HealthBomb", ColorRGBA.Red);
        } else if( GameConstants.TYPE_POTION2.equals(type) ) {
        
            return createPotion("HealthFlask", ColorRGBA.Cyan);            
        } else if( GameConstants.TYPE_POTION3.equals(type) ) {
        
            return createPotion("HealthJar", ColorRGBA.Red);            
        } else if( GameConstants.TYPE_POTION4.equals(type) ) {
        
            return createPotion("HealthJar", ColorRGBA.Cyan);            
        } else {
            throw new RuntimeException("Could not create model for:" + type);
        }
    }

    public Spatial createMonkey() {
        Node monkey = (Node)assets.loadModel( "Models/Jaime/Jaime.j3o" );

        AnimControl anim = monkey.getControl(AnimControl.class);
        AnimChannel channel = anim.createChannel();
        channel.setAnim("Idle");
 
        // The monkey's shadow box is strangley off center so we
        // adjust it... it's also wide because of the splayed arms
        // and we can fix that too
        BoundingBox bounds = (BoundingBox)monkey.getWorldBound();
        monkey.attachChild(createShadowBox(bounds.getXExtent() * 0.7f, 
                                            bounds.getYExtent(), 
                                            bounds.getZExtent()));
                     /*                                                                       
        monkey.addControl(new InterpolationControl(time));
 
        CharAnimControl charAnim = new CharAnimControl(anim);
        charAnim.addMapping("Idle", "Idle", 1);
        charAnim.addMapping("Walk", "Walk", 1.55f * (float)GameConstants.MONKEY_MOVE_SPEED);
        monkey.addControl(charAnim);        
 
        AudioNode walkSound;
        */
        
        /*CharacterAnimAndSoundControl cac = new CharacterAnimAndSoundControl(time, anim);
        cac.addMapping("Idle", "Idle", 1);
        cac.addMapping("Walk", "Walk", 1.55f * (float)GameConstants.MONKEY_MOVE_SPEED);
        walkSound = new AudioNode(assets, "Sounds/monkey-feet.ogg", false);
        walkSound.addControl(new AudioControl(audioListener));
        walkSound.setVolume(0.75f);
        walkSound.setLooping(true);
        walkSound.setRefDistance(4);
        cac.addMapping("Walk", walkSound);
        
        AudioNode punchSound = new AudioNode(assets, "Sounds/monkey-punch.ogg", false);
        punchSound.addControl(new AudioControl(audioListener));
        punchSound.setRefDistance(4);
        cac.addMapping("Attack", "Punches", 2);
        cac.addMapping("Attack", punchSound, 0.1f); 
        monkey.addControl(cac);*/
 
        
        /*
        SoundControl sounds = new SoundControl(audioListener);
        sounds.addSound("Attack", new AudioNode(assets, "Sounds/monkey-punch.ogg", false));
        walkSound = new AudioNode(assets, "Sounds/monkey-feet.ogg", false);
        walkSound.setVolume(0.75f);
        walkSound.setLooping(true);
        sounds.addSound("Walk", walkSound);
        sounds.addSound("Death", new AudioNode(assets, "Sounds/gib.ogg", false));
        
        monkey.addControl(sounds);
 
        TaskControl tasks = new TaskControl(time);
        tasks.setMapping("Idle", Tasks.sequence(Tasks.call(charAnim, "play", "Idle"),
                                               Tasks.call(sounds, "play", "Idle")));
        tasks.setMapping("Walk", Tasks.sequence(Tasks.call(charAnim, "play", "Walk"),
                                               Tasks.call(sounds, "play", "Walk")));
        tasks.setMapping("Attack", AnimationFactories.class, "createMonkeyAttack");
        tasks.setMapping("Death", AnimationFactories.class, "createMonkeyDeath");
        monkey.addControl(tasks);
        */
        
        ColorRGBA diffuse = new ColorRGBA(1, 1, 1, 1);           
        ColorRGBA ambient = new ColorRGBA(0.75f, 0.75f, 0.75f, 1);
        monkey.addControl(new ColorControl(diffuse, ambient));                                  
        
        return monkey;
    }        

    public Spatial createOgre() {    
        Spatial ogre = assets.loadModel( "Models/Sinbad/Sinbad.mesh.j3o" );
            
        // Normalize the ogre to be 1.8 meters tall
        BoundingBox bounds = (BoundingBox)ogre.getWorldBound();                 
        ogre.setLocalScale( 1.8f / (bounds.getYExtent() * 2) );
        bounds = (BoundingBox)ogre.getWorldBound();
        ogre.setLocalTranslation(0, bounds.getYExtent() - bounds.getCenter().y, 0);
 
        AnimControl anim = ogre.getControl(AnimControl.class);
        AnimChannel channel = anim.createChannel();
        channel.setAnim("IdleTop");
        channel = anim.createChannel();
        channel.setAnim("IdleBase");
        
        // Wrap it in a node to keep its local translation adjustment
        Node wrapper = new Node("Ogre");
        wrapper.attachChild(ogre);
 
        // Because Sinbad is made up of lots of objects and the 
        // zExtent is fairly thin, his shadow looks strange so we
        // will tweak it.
        /*
        wrapper.attachChild(createShadowBox(bounds.getXExtent(), 
                                            bounds.getYExtent(), 
                                            bounds.getZExtent() * 1.5f));
        
        wrapper.addControl(new InterpolationControl(time));

        CharAnimControl charAnim = new CharAnimControl(anim);
        charAnim.addMapping("Idle", "IdleTop", 1);
        charAnim.addMapping("Idle", "IdleBase", 1);
        charAnim.addMapping("Walk", "RunTop", 0.2f * (float)GameConstants.OGRE_MOVE_SPEED);
        charAnim.addMapping("Walk", "RunBase", 0.2f * (float)GameConstants.OGRE_MOVE_SPEED);
        wrapper.addControl(charAnim);        
                    */
        
        
        /*CharacterAnimAndSoundControl cac = new CharacterAnimAndSoundControl(time, anim);
        cac.addMapping("Idle", "IdleTop", 1);
        cac.addMapping("Idle", "IdleBase", 1);
        cac.addMapping("Walk", "RunTop", 0.2f * (float)GameConstants.OGRE_MOVE_SPEED);
        cac.addMapping("Walk", "RunBase", 0.2f * (float)GameConstants.OGRE_MOVE_SPEED);
        AudioNode walkSound = new AudioNode(assets, "Sounds/ogre-feet.ogg", false);
        walkSound.addControl(new AudioControl(audioListener));
        walkSound.setLooping(true);
        walkSound.setRefDistance(10);
        cac.addMapping("Walk", walkSound); 
        wrapper.addControl(cac);*/
        
        /*
        SoundControl sounds = new SoundControl(audioListener);
        sounds.addSound("Attack", new AudioNode(assets, "Sounds/ogre-punch.ogg", false));
        AudioNode walkSound = new AudioNode(assets, "Sounds/ogre-feet.ogg", false);
        walkSound.setLooping(true);
        sounds.addSound("Walk", walkSound);
        sounds.addSound("Death", new AudioNode(assets, "Sounds/gib.ogg", false));
        wrapper.addControl(sounds);

        TaskControl tasks = new TaskControl(time);
        tasks.setMapping("Idle", Tasks.sequence(Tasks.call(charAnim, "play", "Idle"),
                                               Tasks.call(sounds, "play", "Idle")));
        tasks.setMapping("Walk", Tasks.compose( Tasks.call(charAnim, "play", "Walk"),
                                                Tasks.call(sounds, "play", "Walk")));
        tasks.setMapping("Attack", AnimationFactories.class, "createOgreAttack");
        tasks.setMapping("Death", AnimationFactories.class, "createOgreDeath");
        wrapper.addControl(tasks);
        */
        ColorRGBA diffuse = new ColorRGBA(1, 1, 1, 1);           
        ColorRGBA ambient = new ColorRGBA(0.75f, 0.75f, 0.75f, 1);
        wrapper.addControl(new ColorControl(diffuse, ambient));                                  

        wrapper.setQueueBucket(Bucket.Transparent);
        wrapper.setUserData("layer", 10);
        //return wrapper;
        return new Avatar("ogre",app);
    }
    
    public Spatial createBarrels() {
        Node wrapper = new Node("Barrels");
        
        Spatial barrel = assets.loadModel( "Models/mini_wood_barrel/mini_wood_barrel.j3o" );
        // Scale the barrel to be 1.2 meters tall
        BoundingBox bounds = (BoundingBox)barrel.getWorldBound();                       
        barrel.setLocalScale( 1.2f / (bounds.getYExtent() * 2) );
        bounds = (BoundingBox)barrel.getWorldBound();                        
        barrel.setLocalTranslation(0, bounds.getYExtent() - bounds.getCenter().y, 0);
 
        float startAngle = (float)(Math.random() * FastMath.TWO_PI);
        for( int i = 0; i < 3; i++ ) {
            Spatial s;
            if( i < 2 ) {
                s = barrel.clone();
            } else {
                s = barrel;
            }
 
            float dir = startAngle + i * FastMath.TWO_PI/3;
            float dist = 0.5f; //(float)(Math.random() * 0.4 + 0.3);
            // ^^ there isn't really enough room to randomize that way.
            // it would be better to lay them out initiallly and then
            // randomly move them out based on what's left in the bounding
            // shape.
            
            s.move( FastMath.cos(dir) * dist, 0, FastMath.sin(dir) * dist );
            s.rotate( 0, (float)(Math.random() * FastMath.TWO_PI), 0 );
                         
            // Wrap it in a node to keep its local translation adjustment
            wrapper.attachChild(s);
        }            
 
        // one shadow for all the barrels
        bounds = (BoundingBox)wrapper.getWorldBound();                                  
        wrapper.attachChild(createShadowBox(bounds.getXExtent(), 
                                            bounds.getYExtent(), 
                                            bounds.getZExtent()));
                                            
        ColorRGBA diffuse = new ColorRGBA(1, 1, 1, 1);           
        ColorRGBA ambient = new ColorRGBA(0.75f, 0.75f, 0.75f, 1);
        wrapper.addControl(new ColorControl(diffuse, ambient));                                  
        
        /*
        SoundControl sounds = new SoundControl(audioListener);
        sounds.addSound("Death", new AudioNode(assets, "Sounds/boom.ogg", false));
        wrapper.addControl(sounds);

        TaskControl tasks = new TaskControl(time);
        tasks.setMapping("Death", AnimationFactories.class, "createBarrelsDeath");
        wrapper.addControl(tasks);
        */
        return wrapper;                
    }
    
    public Spatial createChest() {
        Node wrapper = new Node("Chest");
            
        Spatial chest = assets.loadModel( "Models/Chest/Chest.j3o" );
        BoundingBox bounds = (BoundingBox)chest.getWorldBound();
        chest.setLocalScale( 0.8f / (bounds.getYExtent() * 2) );
        bounds = (BoundingBox)chest.getWorldBound();                        
        chest.setLocalTranslation(0, bounds.getYExtent() - bounds.getCenter().y, 0);
 
        // +/- 45 degrees for interest
        float angle = (float)(Math.random() * FastMath.HALF_PI - FastMath.QUARTER_PI);           
        chest.rotate( 0, angle, 0 );            
        wrapper.attachChild(createShadowBox(bounds.getXExtent() * 1.5f, 
                                            bounds.getYExtent(), 
                                            bounds.getZExtent() * 1.5f));
 
        wrapper.attachChild(chest);
                       
        ColorRGBA diffuse = new ColorRGBA(1, 1, 1, 1);           
        ColorRGBA ambient = new ColorRGBA(0.75f, 0.75f, 0.75f, 1);
        wrapper.addControl(new ColorControl(diffuse, ambient));                                  
          
        /*
        SoundControl sounds = new SoundControl(audioListener);
        sounds.addSound("Death", new AudioNode(assets, "Sounds/boom.ogg", false));
        wrapper.addControl(sounds);
        
        TaskControl tasks = new TaskControl(time);
        tasks.setMapping("Death", AnimationFactories.class, "createChestDeath");
        wrapper.addControl(tasks);
        */
        return wrapper;            
    }
}
