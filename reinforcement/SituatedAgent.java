/*
 * Created on 2 juin 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.etiennelndr.projetias.bot_pogamut.reinforcement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import com.etiennelndr.projetias.bot_pogamut.BotProjetIAS;

/**
 * 
 * La classe Situated Agent permet de faire se promener un Agent dans un Labyrinte
 * (Classe Maze). Ici, le nombre d'actions possibles est fixe a 4 (haut,bas,droite et gauche)
 * Mais, a part le constructeur, le code est generique (pour un nombre quelconque
 * d'actions)
 * De meme la perception est ici une PositionLearnerPerception
 */

public class SituatedAgent {
	protected Point _position;
	protected Random _randomGenerator;
	protected ArrayList _possibleActions;
	private PositionLearnerPerception _myPerception;
	/**
	 * _A est l'action courante a executer
	 */
	protected String _A;
	protected int _R = 0; // trouver la correspondance
	/**
	 * Ce constructeur defini les actions possibles de l'agent
	 * (ici 4) c'est certainement a revoir
	 * @param m Est le labyrinthe (Maze) dans lequel
	 * se promene le SituatedAgent
	 * Il est egalement dote d'une perception PositionLearnerPerception
	 * Si tout va bien et que l'on change la perception en respectant 
	 * l'interface Perception, ca devrait toujours tourner.
	 */
	public SituatedAgent(BotProjetIAS bot){
		_position = new Point(1,1);	
		_randomGenerator = new Random();
		_possibleActions = new ArrayList();
		_myPerception = new PositionLearnerPerception();
                       // PositionLearnerPerception(this);
	/**
	 * A modifier en fonction du module choisi
	 * Peut-etre faire une classe intermediaire
	 */	
        }

	
	public Point getPosition(){
		return _position;
	}	
	public void setPosition(Point p){_position=p;}
	public ArrayList getPossibleActions(){
    	return(_possibleActions);
    }
	public void init(){
		//Point p = _maze.findAPositionFreeRandomly();
    	//setPosition(p);
    	_myPerception.updatePerception();
    	_A=(String)(_possibleActions.get(_randomGenerator.nextInt(_possibleActions.size())));	
    }
    
	public void runAction(){
	
    	
//    	Brick b=_maze.returnTheBrickHere(newPosition);
//    	_R=0;
//		if(b!=(Brick)null)
//			_R = b.getValue();
//		
//		// les murs rewards -1
//		if(_R!=-1){
//			_position = newPosition;	
//		}
//		_myPerception.updatePerception();
        // maybe 
	}
	

}
