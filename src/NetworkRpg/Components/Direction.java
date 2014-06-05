/*
 * $Id: Direction.java 1133 2013-09-24 07:48:51Z PSpeed42@gmail.com $
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

package NetworkRpg.Components;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


/**
 *
 *  @author    Paul Speed
 */
public enum Direction
{
    North(0,-1, new Quaternion().fromAngles(0,FastMath.PI,0)),
    South(0,1, new Quaternion()), 
    East(1,0, new Quaternion().fromAngles(0,FastMath.HALF_PI,0)), 
    West(-1,0, new Quaternion().fromAngles(0,-FastMath.HALF_PI,0));
    
    private int xDelta;
    private int yDelta;
    private Quaternion facing;
    
    private Direction( int x, int y, Quaternion facing ) {
        this.xDelta = x;
        this.yDelta = y;
        this.facing = facing;
    }
 
    public static Direction random() {
        int i = (int)(Math.random() * 4);
        return values()[i];
    }
 
    public static Direction fromDelta( Vector3f from, Vector3f to ) {
        float x = to.x - from.x;
        float z = to.z - from.z;
        if( x == 0 ) {
            return z < 0 ? North : South;
        } else {
            return x < 0 ? West : East;
        }        
    }
    
    public Direction left() {
        switch( this ) {
            case North:
                return West;
            case South:
                return East;
            case East:
                return North;
            case West:
                return South;
            default:
                return this;                
        }
    }

    public Direction right() {
        switch( this ) {
            case North:
                return East;
            case South:
                return West;
            case East:
                return South;
            case West:
                return North;
            default:
                return this;                
        }
    }
 
    public int getXDelta() {
        return xDelta;
    }
    
    public int getYDelta() {
        return yDelta;
    }
 
    public Quaternion getFacing() {
        return facing;  
    }
    
    public Vector3f forward(Vector3f from, float scale) {
        return from.add(xDelta*scale, 0, yDelta*scale); 
    }
}
