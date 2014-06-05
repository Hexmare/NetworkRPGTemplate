/*
 * $Id: CombatStrength.java 1160 2013-10-01 07:18:57Z PSpeed42@gmail.com $
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

import com.simsilica.es.EntityComponent;


/**
 *  An entity's attack, defense, damage, and armor strength values.
 *
 *  @author    Paul Speed
 */
public class CombatStrength implements EntityComponent {
    private int attack;
    private int defense;
    private int damage;
    
    public CombatStrength() {
    }
    
    public CombatStrength( int damage ) {
        this(0, 0, 1);
    }
    
    public CombatStrength( int attack, int defense, int damage ) {
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
    }
 
    public CombatStrength newAdded( CombatStrength delta ) {
        return new CombatStrength(attack + delta.attack, 
                                  defense + delta.defense,
                                  damage + delta.damage);
    }

    public CombatStrength newRemoved( CombatStrength delta ) {
        return new CombatStrength(attack - delta.attack, 
                                  defense - delta.defense,
                                  damage - delta.damage);
    }
    
    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }
    
    public int getDamage() {
        return damage;
    }
    
    @Override
    public String toString() {
        return "CombatStrength[" + attack + ", " + defense + ", " + damage + "]";
    }
}