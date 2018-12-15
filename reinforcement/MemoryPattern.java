/*
 * Created on 4 mai 2005
 * Revised on September 2011
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.etiennelndr.projetias.bot_pogamut.reinforcement;

import com.etiennelndr.projetias.bot_pogamut.reinforcement.Perception;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;

/**
 * @author deloor
 *
 * Un MemoryPattern synthétise tout ce qui est associé à une qualité dans un algorithme
 * à différence temporel. Il s'agit d'un état (ici un PositionLearnerPerception, d'une action
 * et de la qualité associée.
 * Amélioration : la perception ne devrait pas être une PositionLearnerPerception, mais une classe
 * abstraite "LearnerPerception", qui serait dans certains cas une PositionLearnerPerception et dans d'autres
 * une "LocalPerception" par exemple .
 */
public class MemoryPattern {
	private Perception _perception;
	private Weapon _action;
	private float _qualitie;

	public MemoryPattern(Perception state, float qualitie, Weapon action){
            //System.out.println("Construction d'un memory Pattern avec une Perception de type " + state.getClass().getName());
	   /* Class<?> classPerception = null;
                try {
                    classPerception = Class.forName(perceptionClass);
                } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                }
                try {
                    _perception = (Perception)(classPerception.newInstance());
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (InstantiationException ex) {

                }*/


                _perception=state.copy();
		_qualitie=qualitie;
		_action = action;
	}

	public Perception getPerception(){return _perception;}
	public float getQualitie(){return _qualitie;}
	public Weapon getAction(){return _action;}
	public void setQualitie(float q){_qualitie=q;}

}
