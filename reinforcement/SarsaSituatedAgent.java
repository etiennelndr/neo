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
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

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
                _perceptionClass = perceptionClass;


                ///////ajout de assaut dans le weaponQualities

                  
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
	

    
         public void startLife() {
            // Point p = new Point();
            //p = _maze.findAPositionFreeRandomly();
            // Random generator = new Random();
             _compteurByEpisode =0;
             _nStepTemp=0;
             _nbEpisode=0;
             //init(bot);
         }



//        public void oneStep(BotProjetIAS bot) { //useless
//            sarsaAlgorithmeStep(bot);
//        }
         
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
    
//    public ArrayList getTrace(){return _trace;}
//   public void clearTrace() { _trace.clear(); }
    
    public void createNewMemoryWith(Perception perception, BotProjetIAS bot){   // USELESS
        Weapon weapon = bot.getWeaponry().getCurrentWeapon();
        int quality = 1;
   	MemoryPattern mp = new MemoryPattern(perception, quality, weapon);
   	_memory.add(mp);
    }	
  
    
//    public void displayMemory(){
//    	
//    	for(int i=0;i<this._memory.size();i++){
//    		//System.out.println("Memory Pattern : ");
//    		MemoryPattern mp = (MemoryPattern)_memory.get(i);
//    		String s;
//                s = mp.getPerception().getClass().getName();
//    		//s = String.valueOf(((mp.getPerception().getPosition().x)));
//    		//s+=" - ";
//    		//s+= String.valueOf((((mp.getPerception().getPosition().y))));
//    		s+="  : ";
//    		s+= String.valueOf(mp.getAction());
//    		s+=" -->  ";
//    		s+= String.valueOf(mp.getQualitie());
//    		//System.out.println(s);
//    	}
//    	
//    }
    
    //init pour d�marrage
    
    public boolean existeAMemorieWith(Perception state, BotProjetIAS bot){
//    	for(int i=0;i<this._memory.size();i++){
//    		Perception lns = ((MemoryPattern)(_memory.get(i))).getPerception();
//    		if (lns.equals(state))
//    			return true;
//    		
//    	}
    	return _memory.contains(bot.getWeaponry().getCurrentWeapon());
    }
    
    public void learn(ItemType weapon){ 	
        //Weapon weapon =bot.getWeaponry().getCurrentWeapon();
        //weaponQualitie.putIfAbsent(UT2004ItemType.ASSAULT_RIFLE,(float)1);
        float QSA = 1;// old weapon
        //float QSAPrime = weaponQualitie.get(weapon);
        float QSAPrime = 2;
        float newQSA=QSA+_alpha*(_R+_lambda*QSAPrime-QSA);
        if (newQSA >_bestQuality){
            _bestQuality=newQSA;
           // memoriesWeapon(bot,newQSA);
        }    
    }
    

   
    
    private void setQSA(Perception state, Weapon weapon, float value)
    {
    	for(int i=0; i<_memory.size(); i++){
    		MemoryPattern mp = (MemoryPattern)_memory.get(i);
    		if (mp.getPerception().equals(state)&&mp.getAction().equals(weapon))
    		{
    			mp.setQualitie(value);
    			return;
    		}
    	}

    }
    private float getQSA(Perception perception, Weapon weapon  ){
    	float value=0;
    	for(int i=0;i<getMemory().size();i++){
    		MemoryPattern mp = (MemoryPattern)getMemory().get(i);
    		if (mp.getPerception().equals(perception)&&mp.getAction().equals(weapon))
    			{
    			value = mp.getQualitie();
    			}
    	}
    	return value;
    }
    
    public void chooseAPAction(BotProjetIAS bot){
    //Exploration ou exploitation with ratio ; bug for now

    bot.getBot().getBotName().setInfo(_perceptionClass);
    double ratio = 0.3; 
    if(bot.getStats().getDeaths()>=1) {
       ratio = (double)(bot.getStats().getKilledOthers())/(double)(bot.getStats().getDeaths()); 
        }
    //System.out.println("RATIO IS " + ratio);
     bot.getBot().getBotName().setInfo("MY RATIO IS " + ratio);
    // bot.getBot().("ratio is " + choose);
    chooseAPActionRandomly(bot);
   	if(ratio<2){                     // for upgrade; for now 
    		chooseAPActionRandomly(bot);
    		} //exploration
//    	
    	else {
    		chooseAPGreedyAction();
//    		} //exploitation ;		
            }
    }
       
    
    public void chooseAPActionRandomly(BotProjetIAS bot){
         Weapon armeSelected ;
        // eviter les bug de changement d'amre 
        if(!bot.getInfo().isShooting())
        {
            // recupere une arme charge aléatoirement ; works
            Map<ItemType, Weapon> loadedWeapons = bot.getWeaponry().getLoadedWeapons();
            int n = (new Random().nextInt(loadedWeapons.size()))+1; 
            // choix d'une arme au hasard
            if(n==1){
                n=2; 
            }
            Collection<Weapon> collectionWeapons = loadedWeapons.values();
            Iterator<Weapon> itWeapon = collectionWeapons.iterator();
             armeSelected = bot.getWeaponry().getCurrentWeapon();
            for(int i= 0; i<n; i++)
            {
            armeSelected = itWeapon.next();
            }
            bot.getShoot().changeWeapon(armeSelected);    
        //    System.out.println(armeSelected.toString()); /// says the name of weapon
        }
    }
    
    public void chooseAPGreedyAction(){             // to change 
        
        // choose best weapons selon la quality 
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
//              ((Perception)_SP).setAgent((SituatedAgent)this); 
//              ((Perception)_SP).updatePerception();
//   
   	if((!existeAMemorieWith(_SP,bot)))
   	{   
         //   bot.getBot().getBotName().setInfo("CREATE MEMORIE");
        //   createNewMemoryWith(_SP,bot);
    	}	
//  }
    
    
    //public String getName(){return _name;}
   // public void setSelected(){ selected = true;}
    //public void setUnSelected(){ selected = false;}
    //public boolean isSelected(){return selected;}
}
//    public void memoriesWeapon(BotProjetIAS bot, float quality){
//        int newValue = 0;
//        Weapon currentWeapon = bot.getWeaponry().getCurrentWeapon();
//        if(!weaponQualitie.containsKey(currentWeapon)){
//            weaponQualitie.put(currentWeapon.getType(),(float)(1));
//                    }
//        else
//            weaponQualitie.replace(currentWeapon.getType(),quality);
// 
//    }

    public void sarsaAlgorithmeStep(BotProjetIAS bot, String state){
        if (state.equals("ATTACK"))         // avoid random change during idle
        {
            //runAction(bot);                     // useless 
            chooseAPAction(bot);
           // learn(bot);       // learn after act 
        }
    }
}   
