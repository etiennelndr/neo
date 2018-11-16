/*
 * Copyright (C) 2018 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.etiennelndr.projetias.bot_pogamut.states;

import com.etiennelndr.projetias.bot_pogamut.BotProjetIAS;

/**
 *
 * @author Etienne
 */
public abstract class State {
    
    public final String TITLE;
    
    private boolean isEnemyKilled;
    
    // Abstract methods
    /**
     * Transit between different states
     * 
     * @param bot : a reference to the Hunter Bot
     * @return State it can a be a new State or the same one
     */
    public abstract State transition(BotProjetIAS bot);
    
    /**
     * Use this method so the bot can act
     * 
     * @param bot : a reference to the hunter bot
     */
    public abstract void act(BotProjetIAS bot);
    
    /**
     * Constructor for State class
     * 
     * @param title 
     */
    protected State(String title) {
        this.TITLE = title;
        
        this.isEnemyKilled = false;
    }
    
    /**
     * Use this method when an enemy has been killed by our bot.
     * 
     * @param val 
     */
    public void setEnemyKilled(boolean val) {
        this.isEnemyKilled = val;
    }
    
    /**
     * Return a boolean that indicates if an enemy has been killed (true) 
     * or not (false).
     * 
     * @return boolean
     */
    public boolean isEnemyKilled() {
        return this.isEnemyKilled;
    }
    
    /**
     * Static method which return an Idle object.
     * 
     * @return @see com.etiennelndr.projetias.bot_pogamut.Idle
     */
    public static State resetState() {
        return new Idle();
    }
}