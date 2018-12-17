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
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Etienne
 */
public class Attack extends State {
    
    private boolean shooting = false;
    private double distance  = Double.MAX_VALUE;
    
    private boolean runningToPlayer = false;
    
    private final int MIN_HEALTH = 15;
    
    private final long decentDistance;
    
    public Attack() {
        // Change the title to ATTACK
        super("ATTACK");
        
        this.decentDistance = Math.round(Math.random() * 600) + 600;
    }

    @Override
    public State transition(BotProjetIAS bot) {
        // If the bot is dead we have to return a Dead object
        if (bot.isDead())
            return new Dead();
        
        // If the bot.getEnemy() is killed
        if (this.isEnemyKilled() || bot.getEnemy() == null)
            return new Idle(); // Bot have to walk and search for a new enemy to track
        
        // If someone/something is hurting us -> return a new Hurt object
        if ((bot.isBeingDamaged() || bot.getSenses().isBeingDamaged()) && bot.getInfo().getHealth() < MIN_HEALTH) {
            // TODO : add  a comment
            bot.setBeingDamaged(false);
            
            return new Hurt();
        }
        
        // If our enemy is far or not visible - run to him
        if (!bot.getEnemy().isVisible()
                || !shooting
                || decentDistance < distance)
            return new Search();
        
        // Return this state
        return this;
    }

    @Override
    public void act(BotProjetIAS bot) {
        this.shooting = false;
        
        // Set the info to IDLE
        changeStateName(bot);
        
        // Pick a new enemy if the old one has been lost
        if (bot.getEnemy() == null || !bot.getEnemy().isVisible()) {
            // Pick a new enemy
            bot.setEnemy(bot.getPlayers().getNearestVisiblePlayer(bot.getPlayers().getVisibleEnemies().values()));
            if (bot.getEnemy() == null) {
             //   bot.getLog().info("Can't see any enemies... ???");
                return;
            }
        }

        // Stop shooting if our enemy is not visible
        if (!bot.getEnemy().isVisible()) {
	    if (bot.getInfo().isShooting() || bot.getInfo().isSecondaryShooting()) {
                // Stop shooting
                bot.getAct().act(new StopShooting());
            }
            this.runningToPlayer = false;
        } else {
            // Or shoot on enemy if it is visible
            distance = bot.getInfo().getLocation().getDistance(bot.getEnemy().getLocation());
            if (bot.getShoot().shoot(bot.getWeaponPrefs(), bot.getEnemy()) != null) {
            //   bot.getLog().info("Shooting at bot.getEnemy()!!!");
                this.shooting = true;
            }
        }
        
        if (this.decentDistance < distance && bot.getEnemy().isVisible()) {
            if (!this.runningToPlayer) {
                bot.getNavigation().navigate(bot.getEnemy());
                this.runningToPlayer = true;
            }
        } else {
            this.runningToPlayer = false;
            bot.getNavigation().stopNavigation();
        }
        
        bot.setItem(null);
    }

}