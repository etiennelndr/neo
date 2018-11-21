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
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;

/**
 *
 * @author Etienne
 */
public class Hurt extends State {
    
    Item item;
    
    /**
     * Constructor for Hurt class
     */
    public Hurt() {
        // Change the title to HURT
        super("HURT");
    }

    @Override
    public State transition(BotProjetIAS bot) {
        // If the bot is dead we have to return a Dead object
        if (bot.isDead())
            return new Dead();
        
        this.item = bot.getItems().getPathNearestSpawnedItem(ItemType.Category.HEALTH);
        if (item == null) {
            bot.getLog().warning("NO HEALTH ITEM TO RUN TO => ITEMS");
            // Return a new Attack object
            return new Attack();
        }
        
        // Return this state
        return this;
    }

    @Override
    public void act(BotProjetIAS bot) {
        // Set the info to HURT
        changeStateName(bot);
        
        bot.getNavigation().navigate(item);
        bot.setItem(item);
    }
}