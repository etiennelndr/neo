package com.etiennelndr.projetias.bot_pogamut;

import com.etiennelndr.projetias.bot_pogamut.database.Database;
import com.etiennelndr.projetias.bot_pogamut.reinforcement.SarsaSituatedAgent;
import com.etiennelndr.projetias.bot_pogamut.states.State;

import java.util.List;
import java.util.logging.Level;

import cz.cuni.amis.introspection.java.JProp;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.Weaponry;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Players;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.NavigationState;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import javax.vecmath.Vector3d;

/**
 *
 * @author Etienne (elandure@gmail.com)
 * @author Matthieu
 */
@AgentScoped
public class BotProjetIAS extends UT2004BotModuleController<UT2004Bot> {
    
    //renforcement
    SarsaSituatedAgent agent =  new SarsaSituatedAgent("perception.PositionLearnerPerception");   

    static String addressTCP = "localhost";
    static int portTCP       = 12400;
    ClientTCP clientTCP;

    /**
     *
     * @return
     */
    public ClientTCP getClientTCP() {
        return this.clientTCP;
    }

    public static Lock locker;

    /**
     * Current state of our bot
     */
    State currentState;

    /**
     * Return an instance of the current state
     *
     * @return State
     */
    public State getCurrentState() {
        return this.currentState;
    }

    /**
     * Database to store each states
     */
    public static Database db;

    /**
     *
     * @return BotProjetIAS
     */
    public static Database getDb() {
        return BotProjetIAS.db;
    }

    /**
     * True if the bot is dead, otherwise false
     */
    private boolean dead;

    /**
     *
     * @return boolean
     */
    public boolean isDead() {
        return dead;
    }

    /**
     *
     * @param dead
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * boolean switch to activate engage behavior
     */
    @JProp
    public boolean shouldEngage = true;
    /**
     * boolean switch to activate pursue behavior
     */
    @JProp
    public boolean shouldPursue = true;
    /**
     * boolean switch to activate rearm behavior
     */
    @JProp
    public boolean shouldRearm = true;
    /**
     * boolean switch to activate collect health behavior
     */
    @JProp
    public boolean shouldCollectHealth = true;
    /**
     * how low the health level should be to start collecting health items
     */
    @JProp
    public int healthLevel = 75;
    /**
     * how many bot the hunter killed other bots (i.e., bot has fragged them /
     * got point for killing somebody)
     */
    @JProp
    private int frags = 0;

    /**
     *
     * @return int
     */
    public int getFrags() {
        return this.frags;
    }

    /**
     * how many times the hunter died
     */
    @JProp
    public int deaths = 0;

    /**
     * {@link PlayerKilled} listener that provides "frag" counting + is switches
     * the state of the hunter.
     *
     * @param event
     */
    @EventListener(eventClass = PlayerKilled.class)
    public void playerKilled(PlayerKilled event) {
        if (info.getId().equals(event.getKiller())) {
            ++this.frags;
            // Set isEnemyKilled from currentState to true
            currentState.setEnemyKilled(true);
        }
        if (enemy == null) {
            return;
        }
        if (enemy.getId().equals(event.getId())) {
            enemy = null;
        }
    }

    /**
     * Used internally to maintain the information about the bot we're currently
     * hunting, i.e., should be firing at.
     */
    protected Player enemy = null;

    /**
     *
     * @return Player : enemy
     */
    public Player getEnemy() {
        return enemy;
    }

    /**
     *
     * @param enemy : set a new enemy
     */
    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    /**
     *
     * @return Weaponry
     */
    public Weaponry getWeaponry() {
        return this.weaponry;
    }

    /**
     *
     * @return Players
     */
    public Players getPlayers() {
        return this.players;
    }

    /**
     * Item we're running for.
     */
    protected Item item = null;

    /**
     * Set a new value to the item
     *
     * @param i
     */
    public void setItem(Item i) {
        this.item = i;
    }

    /**
     * Taboo list of items that are forbidden for some time.
     */
    protected TabooSet<Item> tabooItems = null;

    /**
     *
     * @return TabooSet<Item>
     */
    public TabooSet<Item> getTabooItems() {
        return tabooItems;
    }

    /**
     *
     * @param tabooItems
     */
    public void setTabooItems(TabooSet<Item> tabooItems) {
        this.tabooItems = tabooItems;
    }

    private UT2004PathAutoFixer autoFixer;

    private static int instanceCount = 0;

    private int idBot;

    /**
     * Return the value of idBot attribute
     *
     * @return int
     */
    public int getIdBot() {
        return this.idBot;
    }

    private boolean beingDamaged;

    /**
     * Return the value of the attribute begingDamaged
     *
     * @return boolean
     */
    public boolean isBeingDamaged() {
        return beingDamaged;
    }

    /**
     * Set a new value to the attribute begingDamaged
     *
     * @param beingDamaged
     */
    public void setBeingDamaged(boolean beingDamaged) {
        this.beingDamaged = beingDamaged;
    }

    /**
     * Bot's preparation - called before the bot is connected to GB2004 and
     * launched into UT2004.
     *
     * @param bot
     */
    @Override
    public void prepareBot(UT2004Bot bot) {

        tabooItems = new TabooSet<Item>(bot);
        autoFixer = new UT2004PathAutoFixer(bot, navigation.getPathExecutor(), fwMap, aStar, navBuilder); // auto-removes wrong navigation links between navpoints

        // listeners
        navigation.getState().addListener(new FlagListener<NavigationState>() {

            @Override
            public void flagChanged(NavigationState changedValue) {
                switch (changedValue) {
                    case PATH_COMPUTATION_FAILED:
                    case STUCK:
                        if (item != null) {
                            tabooItems.add(item, 10);
                        }
                        reset();
                        break;

                    case TARGET_REACHED:
                        reset();
                        break;
                }
            }
        });

        // DEFINE WEAPON PREFERENCES
        weaponPrefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, true);
        weaponPrefs.addGeneralPref(UT2004ItemType.BIO_RIFLE, true);

        // Set the current state to Idle
        currentState = State.resetState();

        // Set the parameter dead to false
        this.dead = false;

        // Set beingDamaged to false
        this.beingDamaged = false;

        // Set the number of frags to 0
        this.frags = 0;

        // Connect to the server
        this.clientTCP = new ClientTCP(addressTCP, portTCP);
    }

    /**
     * Here we can modify initializing command for our bot.
     *
     * @return
     */
    @Override
    public Initialize getInitializeCommand() {
        // Just set the name of the bot and his skill level, 1 is the lowest, 7 is the highest
    	// skill level affects how well will the bot aim
        // First of all, init the value of the idBot
        this.idBot = ++instanceCount;

        // Insert a new key/value in the bots map
        BotDatas.bots.put("Hunter-" + String.valueOf(this.idBot), this);
        
        Location spawn = new Location(1326.04, -567.77);
        Rotation rotation = new Rotation(0, 64300, 0);
        
        return new Initialize().setName("Hunter-" + (this.idBot)).setDesiredSkill(5).setLocation(spawn).setRotation(rotation);
    }

    /**
     * Resets the state of the Hunter.
     */
    public void reset() {
    	item  = null;
        enemy = null;
        navigation.stopNavigation();
        itemsToRunAround = null;
        currentState     = State.resetState();
    }

    @EventListener(eventClass=PlayerDamaged.class)
    public void playerDamaged(PlayerDamaged event) {
    //	log.info("I have just hurt other bot for: " + event.getDamageType() + "[" + event.getDamage() + "]");
    }

    @EventListener(eventClass=BotDamaged.class)
    public void botDamaged(BotDamaged event) {
        // Face the enemy
        if (event.getInstigator() != null)
            body.getLocomotion().turnTo(players.getPlayer(event.getInstigator()));
        this.beingDamaged = true;
    	//log.info("I have just been hurt by other bot for: " + event.getDamageType() + "[" + event.getDamage() + "]");
    }

    /**
     * Main method that controls the bot - makes decisions what to do next. It
     * is called iteratively by Pogamut engine every time a synchronous batch
     * from the environment is received. This is usually 4 times per second - it
     * is affected by visionTime variable, that can be adjusted in GameBots ini
     * file in UT2004/System folder.
     */
    @Override
    @SuppressWarnings("LockAcquiredButNotSafelyReleased")
    public void logic() {
        // Transition
        currentState = currentState.transition(this);

        // Renforcement
        //agent.sarsaAlgorithmeStep(this,currentState.STATE);
      
        // Act
        currentState.act(this);
        
        try {
            this.raycasting.createRay(new Vector3d(), 150, false, false, false).get().getHitLocation();
        } catch (InterruptedException ex) {
            Logger.getLogger(BotProjetIAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(BotProjetIAS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Renforcement
        //agent.learn(this.getWeaponry().getCurrentWeapon().getType());

        // Lock the code
        locker.lock();

        // Update map
        BotDatas.bots.replace(this.getName().toString().split(" ")[0], this);

        // Unlock the code
        locker.unlock();
    }

    ////////////////////////////
    // STATE RUN AROUND ITEMS //
    ////////////////////////////
    protected List<Item> itemsToRunAround = null;

    ////////////////
    // BOT KILLED //
    ////////////////
    @Override
    public void botKilled(BotKilled event) {
        // réduire récompense renforcement bot 
        this.dead = true;
    }

    ///////////////////////////////////
    public static void main(String args[]) throws PogamutException {
        // Instantiate the map
        BotDatas.bots = new HashMap<String, BotProjetIAS>();

        // Create the locker
        locker = new ReentrantLock();

        // Starts 4 Hunters at once
        // Note that this is the most easy way to get a bunch
        // of (the same) bots running at the same time
    	new UT2004BotRunner(BotProjetIAS.class, "Hunter").setMain(true).setLogLevel(Level.INFO).startAgents(1);
    }

    // Static class to access private datas of each bot
    public static class BotDatas {
        // Static map
        public static Map<String, BotProjetIAS> bots;
    }

    public class ClientTCP {
        /**
         * Socket which defined the connection to the server
         */
        Socket socket;

        BufferedOutputStream  outToServer;
        BufferedReader inFromServer;

        public ClientTCP(String address, int port) {
            try {
                this.socket       = new Socket(address, port);
                this.outToServer  = new BufferedOutputStream(this.socket.getOutputStream());
                this.inFromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        public String sendMessage(String msg) {
            try {
                // Write the datas in the buffer
                this.outToServer.write(msg.getBytes());
                // Flush the buffer to send the datas
                this.outToServer.flush();
                // Wait for a response
                String response = this.inFromServer.readLine();
                // Then print it
                System.out.println("FROM SERVER: " + response);

                return response;
            } catch(IOException e) {
                e.printStackTrace();
            }
            return "ERROR";
        }
    }
}
