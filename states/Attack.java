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

import com.etiennelndr.projetias.bot_pogamut.HunterBot;

/**
 *
 * @author Etienne
 */
public class Attack extends State {
    
    public Attack() {
        super();
    }

    @Override
    public State transition(HunterBot bot) {
        // If the enemy is killed
        if (this.isEnemyKilled)
            // Bot have to walk and search for a new enemy to track
            return new Idle();
        
        // Reset isEnemyKilled attribute
        this.isEnemyKilled = false;
        // Return this state
        return this;
    }

    @Override
    public void action(HunterBot bot) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
