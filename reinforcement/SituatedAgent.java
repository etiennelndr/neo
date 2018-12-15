/*
 * Created on 2 juin 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.etiennelndr.projetias.bot_pogamut.reinforcement;

import com.etiennelndr.projetias.bot_pogamut.BotProjetIAS;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;



/**
 *
 * La classe Situated Agent permet de faire se promener un Agent dans un Labyrinte
 * (Classe Maze). Ici, le nombre d'actions possibles est fixe a 4 (haut,bas,droite et gauche)
 * Mais, a part le constructeur, le code est generique (pour un nombre quelconque
 * d'actions)
 * De meme la perception est ici une PositionLearnerPerception
 */

public class SituatedAgent {
	protected  Map<ItemType, Weapon> _possibleActions;
	private PositionLearnerPerception _myPerception;
	/**
	 * _A est l'arme choisi
	 */
	protected Weapon _weapons ;
	protected int _R = 0; // nombre de kill avec l'arme

	public SituatedAgent(/*BotProjetIAS bot*/){
                //_possibleActions = bot.getWeaponry().getLoadedWeapons(); //possible action arme chargé
		_myPerception = new PositionLearnerPerception();
                       // PositionLearnerPerception(this);
	/**
	 * A modifier en fonction du module choisi
	 * Peut-etre faire une classe intermediaire
	 */
        }

	public Weapon getWeapon(){
		return _weapons;
	}

//	public Map<ItemType, Weapon> getPossibleActions(){
//    	return(_possibleActions);}

	public void init(){
    	//_myPerception.updatePerception();
        //init avec l'arme en cours
    	//_weapons=bot.getWeaponry().getCurrentWeapon();
    }

	public void runAction(BotProjetIAS bot){
                bot.getShoot().changeWeapon(_weapons);
		_myPerception.updatePerception(bot);
	}

}
