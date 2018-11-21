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
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.utils.collections.MyCollections;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Etienne
 */
public class Idle extends State {
    /**
     * Constructor for Idle class
     */
    public Idle() {
        // Change the title to IDLE
        super("IDLE");
    }

    @Override
    public State transition(BotProjetIAS bot) {
        // If the bot is dead we have to return a Dead object
        if (bot.isDead())
            return new Dead();
        
        // If we see an enemy and we have a loaded weapon -> return an Attack object
        if (bot.getPlayers().canSeeEnemies() && bot.getWeaponry().hasLoadedWeapon())
            return new Attack();
        
        // Return this state
        return this;
    }

    @Override
    public void act(BotProjetIAS bot) {
        // Set the info to IDLE
        changeStateName(bot);
        
        if (bot.getNavigation().isNavigatingToItem())
            return;
        
        List<Item> interesting = new ArrayList<Item>();
        
        // ADD WEAPONS
        for (ItemType itemType : ItemType.Category.WEAPON.getTypes()) {
            if (!bot.getWeaponry().hasLoadedWeapon(itemType)) interesting.addAll(bot.getItems().getSpawnedItems(itemType).values());
        }
        // ADD ARMORS
        for (ItemType itemType : ItemType.Category.ARMOR.getTypes()) {
            interesting.addAll(bot.getItems().getSpawnedItems(itemType).values());
        }
        // ADD QUADS
        interesting.addAll(bot.getItems().getSpawnedItems(UT2004ItemType.U_DAMAGE_PACK).values());
        // ADD HEALTHS
        if (bot.getInfo().getHealth() < 100) {
            interesting.addAll(bot.getItems().getSpawnedItems(UT2004ItemType.HEALTH_PACK).values());
        }
        
        Item item = MyCollections.getRandom(bot.getTabooItems().filter(interesting));
        if (item == null) {
            bot.getLog().warning("NO ITEM TO RUN FOR!");
            if (bot.getNavigation().isNavigating())
                return;
            bot.getBot().getBotName().setInfo("RANDOM NAV");
            bot.getNavigation().navigate(bot.getNavPoints().getRandomNavPoint());
        } else {
            bot.setItem(item);
            bot.getLog().info("RUNNING FOR: " + item.getType().getName());
            bot.getBot().getBotName().setInfo("ITEM: " + item.getType().getName() + "");
            bot.getNavigation().navigate(item);  	
        }    
    }
}