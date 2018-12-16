/*
 * Created on 2 juin 2005
 * Last release june 2008
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.etiennelndr.projetias.bot_pogamut.reinforcement;

/**
 * @author deloor
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Perception {
    private SituatedAgent _myAgent;

     public void updatePerception() {
         System.out.println("Attention Appel de updatePercetion dans la classe Perception !!");
     }

     public Perception copy() {
         System.out.println("Attention Appel de copy dans la classe Perception !!"); 
         return  new Perception();
     }

     public void display(){
         System.out.println("Attention Appel de display dans la classe Perception !!");
     }

     public boolean equals(Perception s) {
         System.out.println("Attention Appel de equals dans la classe Perception !!");
         return this.equals(s);
     }

     /**
      * Cette methode permet a l'interface d'afficher la memoire
      * d'un agent. Ceci pose un probleme : Pour une PositionLearnerPerception
      * la position est celle associee a la perception, mais pour d'autres
      * Perception, il faudra la definir differemment
      * @return Un point ou il faut afficher la memoire sur une representation
      * graphique
      */
     //abstract public Point getPosition();
     public SituatedAgent getAgent(){//System.out.println("Attention Appel de getAgent dans la classe Perception !!");
         return _myAgent;
     }

     public boolean setAgent(SituatedAgent a){
        // System.out.println("Attention Appel de setAgent dans la classe Perception !!"); 
         _myAgent=a;
         return(true);
     }  
}
