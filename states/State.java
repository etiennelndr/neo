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
public abstract class State {
    
    protected boolean isEnemyKilled;
    
    // Abstract methods
    public abstract State transition(HunterBot bot);
    public abstract void action(HunterBot bot);
    
    public void enemyKilled() {
        this.isEnemyKilled = true;
    }
}
