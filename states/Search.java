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
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;

/**
 *
 * @author Etienne
 */
public class Search extends State {
    
    private int pursueCount;
    
    private final int MAX_PURSUE_COUNT = 30;
    
    /**
     * Constructor for Search class
     */
    public Search() {
        // Change the title to SEARCH
        super("SEARCH");
        
        // Set pursueCount to 0
        this.pursueCount = 0;
    }

    @Override
    public State transition(BotProjetIAS bot) {
        // If the bot is dead we have to return a Dead object
        if (bot.isDead())
            return new Dead();
        
        if (this.pursueCount > MAX_PURSUE_COUNT 
                || bot.getEnemy() ==  null
                || this.isEnemyKilled()) {
            // Reset some of the bot attributes
            bot.reset();
            
            // Return a new Idle object
            return new Idle();
        }
        
        // If our enemy is not too far
        int decentDistance = Math.round(bot.getRandom().nextFloat() * 600) + 400;
        double distance = bot.getInfo().getLocation().getDistance(bot.getEnemy().getLocation());
        if (bot.getEnemy().isVisible() && distance < decentDistance)
            return new Attack();
        
        // Return this state
        return this;
    }

    @Override
    public void act(BotProjetIAS bot) {
        // If we're currently shooting
        if (bot.getInfo().isShooting() || bot.getInfo().isSecondaryShooting()) {
            // Stop shooting
            bot.getAct().act(new StopShooting());
        }
        
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