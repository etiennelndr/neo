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
public class Search extends State {
    
    private int pursueCount;
    
    private final int MAX_PURSUE_COUNT = 30;
    
    private final String TITLE = "SEARCH";
    
    /**
     * Constructor for Search class
     */
    public Search() {
        super();
        
        this.pursueCount = 0;
    }

    @Override
    public State transition(HunterBot bot) {
        // If the bot is dead we have to return a Dead object
        if (bot.isDead())
            return new Dead();
        
        if (this.pursueCount > MAX_PURSUE_COUNT || bot.getEnemy() ==  null) {
            // Reset some of the bot attributes
            bot.reset();
            // Return a new Idle object
            return new Idle();
        }
        
        // Return this state
        return this;
    }

    @Override
    public void act(HunterBot bot) {
        //log.info("Decision is: PURSUE");
        ++this.pursueCount;
        
        // Set the info to PURSUE
        bot.getBot().getBotName().setInfo(TITLE);
        // Navigate to enemy
        bot.getNavigation().navigate(bot.getEnemy());
        // Set item to null
        bot.setItem(null);
    }
}