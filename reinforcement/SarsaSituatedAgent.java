/*
 * Created on 4 mai 2005
 * Revised on September 2011
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.etiennelndr.projetias.bot_pogamut.reinforcement;

import com.etiennelndr.projetias.bot_pogamut.BotProjetIAS;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import sun.util.logging.resources.logging;

/**
 * @author deloor
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SarsaSituatedAgent extends SituatedAgent {
        private String _perceptionClass;
	private ArrayList _memory;
	private float _bestQuality = 10;
	private Perception _S;
	private Perception _SP;
	private Weapon _weaponP;
	private float _alpha = (float)0.5;
	private float _lambda = (float)0.5;
	private float _epsilon = (float)0.5;
        private float _slow = 50;
        
        private int _nbStep = 1000;
	private int _nbEpisodeMax = 200;
        private int _nbStepTotal = 0;
        private int _nbEpisode=0;

        float _moyennePonderee;
        int _action;
        int _compteurByEpisode =0;
        int _nStepTemp=0;
        
        private ArrayList _trace;
        
        private int passageBoucle = 0;
        
        // thread 
       // private Thread _myThread;
       // private boolean running;
        
      //  private boolean selected;

        //private String _name;     // son nom, id
	//Lprivate boolean _onAppli = false; //visible sur l'interface
	
	public SarsaSituatedAgent(/*BotProjetIAS bot*/ String perceptionClass){
		super();
		_memory = new ArrayList();
                _trace = new ArrayList();
                _perceptionClass = perceptionClass;
                  
	}
	
	public void init(){
		super.init();
                 Class<?> classPerception = null;
                try {
                    classPerception = Class.forName(_perceptionClass);
                } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                }
                try {
                    _S = (Perception)(classPerception.newInstance());
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (InstantiationException ex) {
            
                }
                ((Perception)_S).setAgent((SituatedAgent)this); 
                ((Perception)_S).updatePerception();
               
	}
	
        
         //public void startLife(){
             //MyThread = new Thread(this);
             //MyThread.start();
             //running= true;
        //}
    
    
    
         public void startLife() {
            // Point p = new Point();
            //p = _maze.findAPositionFreeRandomly();
            // Random generator = new Random();
             _compteurByEpisode =0;
             _nStepTemp=0;
             _nbEpisode=0;
             //init(bot);
         }



        public void oneStep(BotProjetIAS bot) {
            sarsaAlgorithmeStep(bot);
        }
         
        
       
       
         
        /**
          * Pour ne pas saturer la mémoire, cette méthode réduit lengthnombre de points de la trace
          * d'apprentissage. Pour cela elle effectue une moyenne des points qu'elle supprime
          * et les remplace par un nouveau point caractérisant cette moyenne
          */
        
        public void linearizeTrace2(int sizeMax){
            if(this._trace.size()>2*sizeMax){
                ArrayList newTrace = new ArrayList();
                double nextTrace = ((Point)_trace.get(_trace.size()-1)).getX();
                double step = nextTrace/sizeMax;
                int cpt=0;
                double nextStep = step;
                double valTemp = 0;
                for(int i = 0 ; i<_trace.size(); i++){
                    cpt++;
                    if(((Point)_trace.get(i)).getX()<=nextStep){
                        valTemp += ((Point)_trace.get(i)).getY();
                    }
                    else{
                        valTemp=valTemp/cpt;
                        cpt=0;
                        newTrace.add(new Point((int)nextStep,(int)valTemp));
                        valTemp=((Point)_trace.get(i)).getY();
                        nextStep+=step;
                    }
                }
                _trace = newTrace;
            }
        }


         public void linearizeTrace(int sizeMax){
            if(this._trace.size()>2*sizeMax){
                ArrayList newTrace = new ArrayList();
                for(int i=(_trace.size()/2); i<_trace.size(); i++){
                    newTrace.add(_trace.get(i));
                }
                _trace = newTrace;
            }
        }



        /**
         * la "bestQuality" est la valeur la plus élevée de qualité trouvée en mémoire _memory
         */
	public float getBestQuality(){
		return _bestQuality;
	}
	
        /**
         * Cette méthode retourne la qualité la plus élevée pour un état donné
         * Elle recherche toutes les qualités associées à cet état (généralement autant qu'il y
         * a d'actions ...
         */
        
	public float getBestValueForState(Perception aState){
		float value=-1000;
		for(int i=0;i<_memory.size();i++){
			if(((MemoryPattern)(_memory.get(i))).getPerception().equals(aState))
					if((((MemoryPattern)(_memory.get(i))).getQualitie())>value)
							value = ((MemoryPattern)(_memory.get(i))).getQualitie();
		}
		return value;
	}
	
	
    public ArrayList getMemory(){return _memory;}
    public void clearMemory() { _memory.clear(); }
  
  
    
    public void setEpsilon(float value){_epsilon = value; 
                                        }
    public float getEpsilon(){return _epsilon;}
    
    public void setAlpha(float value){_alpha = value;}
    public float getAlpha(){return _alpha;}
    
    public void setLambda(float value){_lambda = value;}
    public float getLambda(){return _lambda;}
    
    public void setCompteur(int value){_nbStepTotal = value;}
    public int getCompteur(){return _nbStepTotal;}
    
    public void setNbEpisode(int value){_nbEpisode = value;}
    public int getNbEpisode(){return _nbEpisode;}

    public void setSlow(long value){_slow = value;}
    public float getSlow(){return _slow;}
    
    public ArrayList getTrace(){return _trace;}
    public void clearTrace() { _trace.clear(); }
    
    public void createNewMemoryWith(Perception perception){
        Weapon weapon;
        Object[] listWeapon = _possibleActions.values().toArray();
    	for(int i=0; i<_possibleActions.size();i++){
            weapon = (Weapon)listWeapon[i];
            float quality = new Random().nextFloat()*10;
    	    MemoryPattern mp = new MemoryPattern(perception, quality, weapon);
   	    _memory.add(mp);
   	}	
    }
    
    public void displayMemory(){
    	
    	for(int i=0;i<this._memory.size();i++){
    		//System.out.println("Memory Pattern : ");
    		MemoryPattern mp = (MemoryPattern)_memory.get(i);
    		String s;
                s = mp.getPerception().getClass().getName();
    		//s = String.valueOf(((mp.getPerception().getPosition().x)));
    		//s+=" - ";
    		//s+= String.valueOf((((mp.getPerception().getPosition().y))));
    		s+="  : ";
    		s+= String.valueOf(mp.getAction());
    		s+=" -->  ";
    		s+= String.valueOf(mp.getQualitie());
    		//System.out.println(s);
    	}
    	
    }
    
    //init pour d�marrage
    
    public boolean existeAMemorieWith(Perception state){
    	for(int i=0;i<this._memory.size();i++){
    		Perception lns = ((MemoryPattern)(_memory.get(i))).getPerception();
    		if (lns.equals(state))
    			return true;
    		
    	}
    	return false;
    }
    
    public void learn(){ 	
    	float QSA = getQSA(_S,_weapons);
    	float QSAPrime = getQSA(_SP, _weaponP);
    	float newQSA=QSA+_alpha*(_R+_lambda*QSAPrime-QSA);
    	if (newQSA >_bestQuality){_bestQuality=newQSA;}
    	setQSA(_S,_weapons,newQSA);
//        Class<?> classPerception = null;
//        try {
//            classPerception = Class.forName(_perceptionClass);
//        } catch (ClassNotFoundException ex) {
//            ex.printStackTrace();
//        }
//        try {
//            _S = (Perception)(classPerception.newInstance());
//        } catch (IllegalAccessException ex) {
//            ex.printStackTrace();
//        } catch (InstantiationException ex) {
//            ex.printStackTrace();
//        }

//	_S= _SP.copy();
    	_weapons= (_weaponP);
//       System.out.println(" done_learn");
    }
    
    public void sarsaAlgorithmeStep(BotProjetIAS bot){

    	runAction(bot);
    	chooseAPAction(bot);
	learn(); 
        System.out.println("Not bug 4");
    }
    
    private void printSarsaState(){
//    	System.out.println("Sarsa state");
//    	_S.display();
//    	System.out.println(_A);
//    	_SP.display();
//    	System.out.println(_AP);
    	
    }
    
    
    private void setQSA(Perception state, Weapon action, float value)
    {
    	for(int i=0; i<_memory.size(); i++){
    		MemoryPattern mp = (MemoryPattern)_memory.get(i);
    		if (mp.getPerception().equals(state)&&mp.getAction().equals(action))
    		{
    			mp.setQualitie(value);
    			return;
    		}
    	}
    }
    
    private float getQSA(Perception perception, Weapon action  ){
    	float value=0;
    	for(int i=0;i<getMemory().size();i++){
    		MemoryPattern mp = (MemoryPattern)getMemory().get(i);
    		if (mp.getPerception().equals(perception)&&mp.getAction().equals(action))
    			{
    			value = mp.getQualitie();
    			}
    	}
    	return value;
    }
    
    public void chooseAPAction(BotProjetIAS bot){
    	//Exploration ou exploitation with ratio ; bug for now
 
    	double choose = 0.3; 
        passageBoucle++;
//         System.out.println("passageBoucle " + passageBoucle);
//        if(passageBoucle>20) {
// //       choose = bot.getStats().getDeaths(); //bug
//        }
        
        System.out.println("RATIO IS " + choose);
        chooseAPActionRandomly(bot);
    	if(passageBoucle<250){
    		chooseAPActionRandomly(bot);
    		} //exploration
    	
    	else {
    	//	chooseAPGreedyAction();
    		} //exploitation ;	;	
    }
    
    public void chooseAPActionRandomly(BotProjetIAS bot){
        // recupere une arme charge aléatoirement ; works
        Map<ItemType, Weapon> loadedWeapons = bot.getWeaponry().getLoadedWeapons();
        //Object[] weaponsArray = loadedWeapons.keySet().toArray();
        int n = (new Random().nextInt(loadedWeapons.size()));
        // choix d'une arme au hasard
       
        Collection<Weapon> collectionWeapons = loadedWeapons.values();
        Iterator<Weapon> itWeapon = collectionWeapons.iterator();
        Weapon armeSelected = bot.getWeaponry().getCurrentWeapon();
        for(int i= 0; i<n; i++)
        {
        armeSelected = itWeapon.next();
        }
        bot.getShoot().changeWeapon(armeSelected);    
        System.out.println(armeSelected.toString()); /// says the name of weapon 
    }
    
    public void chooseAPGreedyAction(){             // to change 
        
    	float q = -100;  
    	for(int i =0; i<_memory.size();i++){
    		MemoryPattern mp = (MemoryPattern)_memory.get(i);
    		if(mp.getPerception().equals(_SP)){
    			if(q<mp.getQualitie()){
    				q=mp.getQualitie();
    				_weapons=mp.getAction();
    			}
    		}
    		
    	} 	
    }
    
    public void runAction(BotProjetIAS bot){
    	super.runAction(bot);
//         Class<?> classPerception = null;
//                try {
//                    classPerception = Class.forName(_perceptionClass);
//                } catch (ClassNotFoundException ex) {
////                ex.printStackTrace();
//                }
//                try {
//                    _SP = (Perception)(classPerception.newInstance());
//                } catch (IllegalAccessException ex) {
//                    ex.printStackTrace();
//                } catch (InstantiationException ex) {
//            
//                }
//                ((Perception)_SP).setAgent((SituatedAgent)this); 
//                ((Perception)_SP).updatePerception();
//   
//    	if(!(existeAMemorieWith(_SP)))
//    	{   
//    		createNewMemoryWith(_SP);
//    	}	
    }
    
    
    //public String getName(){return _name;}
   // public void setSelected(){ selected = true;}
    //public void setUnSelected(){ selected = false;}
    //public boolean isSelected(){return selected;}
}
