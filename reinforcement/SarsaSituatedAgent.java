/*
 * Created on 4 mai 2005
 * Revised on September 2011
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.etiennelndr.projetias.bot_pogamut.reinforcement;

import com.etiennelndr.projetias.bot_pogamut.BotProjetIAS;
import java.awt.Point;
import java.util.ArrayList;
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
	private String _AP;
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
        
        // thread 
        private Thread _myThread;
        private boolean running;
        
        private boolean selected;

        //private String _name;     // son nom, id
	//Lprivate boolean _onAppli = false; //visible sur l'interface
	
	public SarsaSituatedAgent(BotProjetIAS bot, String perceptionClass){
		super(bot);
		_memory = new ArrayList();
                _trace = new ArrayList();
		//_application = application;
                //_name = name;
                //_view = viewer;
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
             init();
         }



        public void oneStep() {
            if(_nStepTemp<_nbStep)
            {
                //if(isSelected())
                //   _view.eraseMe(this);
               // if(_application.getSimulationStatut()=="run")
                {
                    sarsaAlgorithmeStep();
                    _nStepTemp++;
                    _nbStepTotal++;
                    // Reward de 10 == recompense == gagné == changement de position
                    if(_R==10)
                    {
                        Point p = new Point(_nbEpisode,_nStepTemp);
                        _trace.add(p);
                        linearizeTrace(2000);
                        _nStepTemp =0;
                        init();
                        _nbEpisode++;
                        _R = 0;
                        //Thread.yield();
                    }
                }
            }
            else
            {
                Point p = new Point(_nbEpisode++,_nStepTemp);
                _nStepTemp=0;
                _trace.add(p);  
                linearizeTrace(2000);
                //_position=_maze.findAPositionFreeRandomly();
                _nbEpisode ++;

            }
        }
         
        
        private void computeAutoEpsilon()
        {
           
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
    	String action;
        //System.out.println("Appel de createNewMemory avec un argument de type " + perception.getClass().getName());
             
    	Perception copyState = perception.copy();
          
                
    	for(int i=0; i<_possibleActions.size();i++){
    		action = (String) _possibleActions.get(i);	
    		float quality = _randomGenerator.nextFloat()*10;
    	    MemoryPattern mp = new MemoryPattern(perception, quality, action);
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
    	float QSA = getQSA(_S,_A);
    	float QSAPrime = getQSA(_SP, _AP);
    	float newQSA=QSA+_alpha*(_R+_lambda*QSAPrime-QSA);
    	if (newQSA >_bestQuality){_bestQuality=newQSA;}
    	setQSA(_S,_A,newQSA);
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
            ex.printStackTrace();
        }
    	_S= _SP.copy();
    	_A= new String(_AP);  	
    }
    
    public void sarsaAlgorithmeStep(){
    	runAction();
    	chooseAPAction();
	learn();          
    }
    
    private void printSarsaState(){
    	System.out.println("Sarsa state");
    	_S.display();
    	System.out.println(_A);
    	_SP.display();
    	System.out.println(_AP);
    	
    }
    
    
    private void setQSA(Perception state, String action, float value)
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
    
    private float getQSA(Perception perception, String action  ){
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
    
    public void chooseAPAction(){
    	//Exploration ou exploitation
    	
    	float choose = _randomGenerator.nextFloat();
    	if(choose<_epsilon){
    		chooseAPActionRandomly();
    		} //exploration
    	
    	else {
    		chooseAPGreedyAction();
    		} //exploitation ;	;	
    }
    
    public void chooseAPActionRandomly(){
    	
    	int action = _randomGenerator.nextInt(_possibleActions.size());
    	_AP = (String)_possibleActions.get(action);
    	//System.out.println("Random = "+String.valueOf(action));
    }
    
    public void chooseAPGreedyAction(){
    	float q = -100;  
    	for(int i =0; i<_memory.size();i++){
    		MemoryPattern mp = (MemoryPattern)_memory.get(i);
    		if(mp.getPerception().equals(_SP)){
    			if(q<mp.getQualitie()){
    				q=mp.getQualitie();
    				_AP=mp.getAction();
    			}
    		}
    		
    	} 	
    }
    
    public void runAction(){
    	super.runAction();
         Class<?> classPerception = null;
                try {
                    classPerception = Class.forName(_perceptionClass);
                } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                }
                try {
                    _SP = (Perception)(classPerception.newInstance());
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (InstantiationException ex) {
            
                }
                ((Perception)_SP).setAgent((SituatedAgent)this); 
                ((Perception)_SP).updatePerception();
   
    	if(!(existeAMemorieWith(_SP)))
    	{   
    		createNewMemoryWith(_SP);
    	}	
    }
    
    
    //public String getName(){return _name;}
    public void setSelected(){ selected = true;}
    public void setUnSelected(){ selected = false;}
    //public boolean isSelected(){return selected;}
}
