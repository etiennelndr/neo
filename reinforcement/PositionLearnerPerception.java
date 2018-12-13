/*
 * Created on 18 mai 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.etiennelndr.projetias.bot_pogamut.reinforcement;


import java.awt.Point;
//import maze.*;


/**
 * @author deloor
 *
 * La classe PositionLearnerPerception represente une perception possible
 * correspondant la position du Situated Agent sous la forme de
 * ces coordonnees cartesiennes inserees dans un objet Point
 * 
 */
public class PositionLearnerPerception extends Perception {
	
	private Point _position;
//	private maze.SituatedAgent _myAgent;
	 
//	public PositionLearnerPerception(){
//		_position = new Point();
//		_position = a.getPosition();
//		setAgent(a);
//	}
        
        public PositionLearnerPerception(){
		
	}
	
	public PositionLearnerPerception(PositionLearnerPerception p){
		_position = new Point(p._position);
		setAgent(p.getAgent());
	}
        
        public PositionLearnerPerception copy(){
            PositionLearnerPerception plp = new PositionLearnerPerception();
            plp._position = new Point(_position);
	    plp.setAgent(getAgent());
            return plp;
        }
	
	public void updatePerception(){
                
		_position = getAgent().getPosition();
	}
	public boolean equals(Perception s){
		return(((PositionLearnerPerception)(s))._position.getX()==_position.getX()&&(((PositionLearnerPerception)(s))._position.getY()==_position.getY()));
	}
	public Point getPosition(){
		return  _position;
	}
	
	
	public void display(){
		System.out.println("Etat : "+String.valueOf(_position.getX())+" "+String.valueOf(_position.getY()));
	}
}


