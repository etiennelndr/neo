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
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;

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
        // If the bot.getEnemy() is killed
        if (this.isEnemyKilled())
            // Bot have to walk and search for a new bot.getEnemy() to track
            return new Idle();
        
        // Reset isEnemyKilled attribute
        this.setEnemyKilled(false);
        // Return this state
        return this;
    }

    @Override
    public void act(HunterBot bot) {
        boolean shooting = false;
        double distance = Double.MAX_VALUE;
        bot.setPursueCount(0);

        // 1) pick new bot.getEnemy() if the old one has been lost
        if (bot.getEnemy() == null || !bot.getEnemy().isVisible()) {
            // pick new bot.getEnemy()
            bot.setEnemy(bot.getPlayers().getNearestVisiblePlayer(bot.getPlayers().getVisibleEnemies().values()));
            if (bot.getEnemy() == null) {
                bot.getLog().info("Can't see any enemies... ???");
                return;
            }
        }

        // 2) stop shooting if bot.getEnemy() is not visible
        if (!bot.getEnemy().isVisible()) {
	        if (bot.getInfo().isShooting() || bot.getInfo().isSecondaryShooting()) {
                // stop shooting
                bot.getAct().act(new StopShooting());
            }
            bot.setRunningToPlayer(false);
        } else {
        	// 2) or shoot on bot.getEnemy() if it is visible
	        distance = bot.getInfo().getLocation().getDistance(bot.getEnemy().getLocation());
	        if (bot.getShoot().shoot(bot.getWeaponPrefs(), bot.getEnemy()) != null) {
	            bot.getLog().info("Shooting at bot.getEnemy()!!!");
	            shooting = true;
	        }
        }

        // 3) if bot.getEnemy() is far or not visible - run to him
        int decentDistance = Math.round(bot.getRandom().nextFloat() * 800) + 200;
        if (!bot.getEnemy().isVisible() || !shooting || decentDistance < distance) {
            if (!bot.isRunningToPlayer()) {
                bot.getNavigation().navigate(bot.getEnemy());
                bot.setRunningToPlayer(true);
            }
        } else {
            bot.setRunningToPlayer(false);
            bot.getNavigation().stopNavigation();
        }
        
        bot.setItem(null);
    }
}
