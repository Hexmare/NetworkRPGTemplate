/*
 * $Id: ParticleControl.java 1174 2013-10-07 07:17:47Z PSpeed42@gmail.com $
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

package NetworkRpg.Controls;

import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import NetworkRpg.TimeProvider;


/**
 *
 *  @author    Paul Speed
 */
public class ParticleControl extends AbstractControl {

    private ParticleEmitter emitter;
    private TimeProvider time;
    private long startTime;
    private long endTime;
    private boolean started;
    private boolean stopped;
    private AudioNode sound;
 
    public ParticleControl( ParticleEmitter emitter, long startTime, TimeProvider time ) {
        this(emitter, null, startTime, time);
    }
    
    public ParticleControl( ParticleEmitter emitter, AudioNode sound, long startTime, TimeProvider time ) {
        this.emitter = emitter;
        this.sound = sound;
        this.startTime = startTime >= 0 ? startTime : time.getTime();
        this.endTime = this.startTime + 1000 * 1000000L;
        this.time = time;
    } 
    
    @Override
    protected void controlUpdate( float tpf ) {
        if( stopped ) {
            return;
        }
 
        if( !started && time.getTime() >= startTime ) {        
            emitter.emitAllParticles();
            started = true;
            if( sound != null ) {
                sound.play();
            }
        }
        
        if( started && time.getTime() > endTime ) {
            emitter.killAllParticles();
            stopped = true;
            started = false;
            
            // And remove it... it's done
            spatial.removeFromParent();
        }                       
    }

    @Override
    protected void controlRender( RenderManager rm, ViewPort vp ) {
    }
}

