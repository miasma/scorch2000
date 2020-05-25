package scorch;

/*
  Class:  ScorchApplet
  Author: Mikhail Kruk

  Description: the main applet class that has all the players, handles 
  network interactions and GUI callbacks
*/

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;

import scorch.utility.*;
import scorch.weapons.*;
import scorch.items.*;
import scorch.gui.*;
import swindows.*;

public class ScorchApplet extends Applet implements FocusListener {
    public static final String Version = "v1.061, 4/11/2001";

    public static boolean sounds = false;

    private Random rand;
    private static final String paramWidth = "gameWidth";
    private static final String paramHeight = "gameHeight";
    private static final String paramPort = "port";                             // server port
    private static final String paramLeaveURL = "leaveURL";
    private static final String paramHelpURL = "helpURL";
    // development version
    // desync test


    private static ScorchApplet saInstance = null;

    private URL leaveURL, helpURL;

    private int gameWidth = 640, gameHeight = 480, port = 4242, width, height;

    public static String JVM;

    private ScorchField scorch;
    private ScorchFrame scorchFrame;
    private Network network;
    private Vector<ScorchPlayer> players;
    private PlayersLister playersList;
    private GameSettings gameSettings;
    private MessageBox msg;
    private ChatBox chatBox;
    private MainToolbar mainToolbar;
    private GradientPanel gp; // turned off

    private boolean master, massKilled = false, newPlayer = false;
    private int myID, activePlayer = -1, roundCount = 0, moveCount = 0;
    private boolean paid = false;
    private ScorchPlayer myPlayer;

    private Vector<String> banners, bannersURL;
    private Label timerLabel;

    public boolean GalslaMode = false;

    public void init() {
        String runMode = "";

        saInstance = this;

        addFocusListener(this);

        getParameters();

        System.out.println("Scorched Earth 2000 " + Version);
        System.out.println
                ("Copyright (C) 1999-2001 KAOS Software\n" +
                        "Scorched Earth 2000 comes with ABSOLUTELY NO WARRANTY;\n" +
                        "This is free software, and you are welcome to redistribute it\n" +
                        "under certain conditions. Please go to the \"System\" menu,\n" +
                        "\"About Scorch\", \"License\" for details.\n");

        JVM = System.getProperty("java.version") + " " +
                System.getProperty("java.vendor") + " " +
                System.getProperty("os.name") + " " +
                System.getProperty("os.arch");

        if (Debug.desyncTest) {
            System.out.println("\tDesync-test mode");
            runMode = runMode + "desync-test ";
        }
        if (Debug.dev) {
            System.out.println("\tDeveloper's mode");
            runMode = runMode + "dev mode ";
        }
        if (Debug.debugLevel > 0) {
            System.out.println("\tDebug level set to " + Debug.debugLevel);
            runMode = runMode + "debug level = " + Debug.debugLevel + " ";
        }

        runMode = runMode + Version;

        Dimension d = getSize();
        width = d.width;
        height = d.height;
        setBackground(Color.black);

        setLayout(null);

        Label ver = new Label(runMode, Label.RIGHT);
        ver.setForeground(Color.white);
        add(ver);
        ver.setLocation(0, height - 20);
        ver.setSize(width, 20);
        timerLabel = new Label("", Label.RIGHT);
        timerLabel.setForeground(Color.white);
        add(timerLabel);
        timerLabel.setLocation(0, 0);
        timerLabel.setSize(width, 20);

        loginWindow("");

        validate();
    }

    public void start() {
        if (scorch != null)
            scorch.start();
    }

    public void stop() {
        if (gp != null) {
            gp.stop();  // it's a good stop, not thread's stop()
            gp = null;
        }

        if (msg != null) {
            msg.close();
            msg = null;
        }

        if (chatBox != null) {
            chatBox.close();
            chatBox = null;
        }

        if (scorchFrame != null)
            scorchFrame.close();
        if (mainToolbar != null) {
            mainToolbar.hideMenu();
            mainToolbar.close();
        }

        if (scorch != null)
            scorch.stop(); // it's a good stop, not thread's stop
        if (network != null)
            network.quit();

        Debug.closeConsole();

        System.out.println("Scorched Earth 2000 Terminated.");
    }

    public String getAppletInfo() {
        return "Scorched Earth 2000 " + Version + "\n" +
                "by the KAOS Software team";
    }

    public String[][] getParameterInfo() {

        return new String[][]{
                {paramWidth, "int", "game field width"},
                {paramHeight, "int", "game field height"},
                {paramPort, "int", "port on which to connect to the server"},
                {paramLeaveURL, "String", "url of the Scorch start page"},
                {paramHelpURL, "String", "url of the Scorch on-line help"}
        };
    }

    private void getParameters() {
        String param;

        param = getParameter(paramWidth);
        if (param != null)
            gameWidth = Integer.parseInt(param);

        param = getParameter(paramHeight);
        if (param != null)
            gameHeight = Integer.parseInt(param);

        param = getParameter(paramPort);
        if (param != null)
            port = Integer.parseInt(param);

        String paramDev = "dev";
        param = getParameter(paramDev);
        if (param != null)
            Debug.dev = param.equals("true");

        String paramDT = "dt";
        param = getParameter(paramDT);
        if (param != null)
            Debug.desyncTest = param.equals("true");

        try {
            param = getParameter(paramLeaveURL);
            if (param != null)
                leaveURL = new URL(param);
            else
                leaveURL = new URL("http://www.scorch2000.com/");

            param = getParameter(paramHelpURL);
            if (param != null)
                helpURL = new URL(param);
            else
                helpURL = new URL("http://www.scorch2000.com/");

            String paramBanners = "banners";
            param = getParameter(paramBanners);
            if (param != null) {
                banners = new Vector<>();
                bannersURL = new Vector<>();
                StringTokenizer st = new StringTokenizer(param, "|");
                while (st.hasMoreTokens()) {
                    banners.addElement(st.nextToken());
                    bannersURL.addElement(st.nextToken());
                }
            }
        } catch (MalformedURLException e) {
            System.err.println("bad URL in parameter: " + e);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getGameWidth() {
        return gameWidth;
    }

    public int getMyID() {
        return myID;
    }

    public ScorchPlayer getMyPlayer() {
        return myPlayer;
    }

    public ScorchPlayer getPlayerByID(int id) {
        ScorchPlayer sp;

        for (int i = 0; i < getPlayersNum(); i++) {
            sp = getPlayer(i);
            if (sp.getID() == id)
                return sp;
        }

        return null;
    }

    public synchronized ScorchPlayer getPlayer(int num) {
        return players.elementAt(num);
    }

    public synchronized ScorchPlayer getActivePlayer() {
        return getPlayerByID(activePlayer);
    }

    public synchronized int getPlayersNum() {
        return players.size();
    }

    public synchronized Vector<ScorchPlayer> getPlayers() {
        return players;
    }

    public String getName() {
        return myPlayer.getName();
    }

    public boolean isMaster() {
        return master;
    }

    public boolean isGuest() {
        return myPlayer.getProfile().isGuest();
    }

    public boolean isNewPlayer() {
        return newPlayer;
    }

    public boolean isMassKilled() {
        return massKilled;
    }

    public int getMaxRounds() {
        return gameSettings.maxRounds;
    }

    public int getRoundCount() {
        return roundCount;
    }

    // 

    public synchronized void sendTankDead(int id) // made synchr 6/8/0
    {
        if (master)
            network.sendTankDead(id);
    }

    public synchronized void sendEOT() {
        sendEOT("");
    }

    public synchronized void sendEOT(String msg)           // made synchr 6/8/0
    {
        // dts = desync test string
        StringBuilder dts = new StringBuilder("" + rand.nextInt());

        for (int i = 0; i < getPlayersNum(); i++)
            dts.append(Protocol.separator).append(getPlayer(i).getPowerLimit());

        Debug.log("sendEOT(): " + msg + " " + Thread.currentThread());
        network.sendEOT(dts.toString());
    }

    public void updateUser(PlayerProfile profile, boolean encrypt) {
        if (!profile.isGuest())
            network.sendUpdateUser(profile, encrypt);
    }

    // UI callbacks

    private synchronized boolean connect() {
        if (network == null)
            try {
                network =
                        new Network(getDocumentBase().getHost(), port, this);

                if (Debug.dev)
                    Debug.initConsole(network);

                return true;
            } catch (Exception e) {
                String[] b = {"OK"};
                String[] c = {"Quit"};
                MessageBox error = new MessageBox
                        ("Error",
                                "Connection to the server failed", b, c, this);
                error.display();
            }
        return false;
    }

    public synchronized void selectWeapon(int type) {
        myPlayer.setWeapon(type);
        mainToolbar.updateWeapon();
    }

    public /*synchronized*/ void useItem(int type, int arg) {
        myPlayer.useItem(type, arg);
        network.sendUseItem(type, arg);
    }

    // called by the Fuel item when user activates it
    public synchronized void showFuelWindow(Fuel fuel) {
        FuelBox fb = new FuelBox(this);
        mainToolbar.enableKeys(false);
        fb.display();
    }

    // called from Fuel Window whet it is closed to let the game continue
    public synchronized void closeFuelWindow() {
        mainToolbar.enableKeys(true);
    }

    public synchronized void loginWindow(String name) {
        LoginWindow login = new LoginWindow(name, this);
        login.display();
    }

    public synchronized void LogIn(PlayerProfile profile, String gpassword) {
        newPlayer = false;
        if (connect())
            network.sendLogin(profile, gameWidth + "x" + gameHeight + " " +
                    Version + gpassword);
    }

    public synchronized void newUser(String name) {
        NewUser newUser = new NewUser(name, this);
        newUser.display();
    }

    public synchronized void sendNewUser(PlayerProfile profile) {
        newPlayer = true;
        if (connect())
            network.sendNewUser(profile, gameWidth + "x" + gameHeight + " " + Version);
    }

    public synchronized void createGame(int maxPlayers, String[] aiplayers) {

        GameOptions options =
                new GameOptions(myPlayer.getName() + " (master)", maxPlayers,
                        myPlayer.getProfile(), this);
        options.display();
        playersList = options;

        network.sendMaxPlayers(maxPlayers);
        for (String aiplayer : aiplayers) network.sendAIPlayer(aiplayer);
    }

    public synchronized void setGameOptions(PlayerProfile profile) {
        msg = new MessageBox
                ("Message", "Please wait while players set their options",
                        new String[0], new String[0], this);
        msg.display();

        playersList = null;
        network.sendPlayerSettings(myID, new PlayerSettings(profile));
        network.sendGameSettings(new GameSettings(profile));

        if (profile.getSounds())
            loadSounds();
    }

    public synchronized void joinGame(PlayerSettings pSettings) {
        playersList = null;
        network.sendPlayerSettings(myID, pSettings);

        msg = new MessageBox
                ("Message", "Please wait while master sets game options",
                        new String[0], new String[0], this);
        msg.display();

        if (pSettings.sounds)
            loadSounds();
    }

    public synchronized void showChat(char c) {
        if (chatBox != null) return;
        chatBox = new ChatBox(c, this);
        chatBox.display();
    }

    // called by chat window when its closed. say determines whether user
    // decided to send message or not, idx determines recipient of the message
    public synchronized void closeChat(boolean say, int idx) {
        if (say) {
            if (idx > 0) idx--;
            else idx = -1;
            network.sendShout
                    (chatBox.getMessage(), myPlayer.getName(), idx);
        }

        chatBox = null;
        mainToolbar.requestFocus();
    }

    public synchronized int changeAngle(int inc) {
        int na;

        na = myPlayer.incAngle(inc);
        return na;
    }

    public synchronized int changePower(int inc) {
        return myPlayer.incPower(inc);
    }

    public void updatePowerLabel(ScorchPlayer sp) {
        if (sp == myPlayer)
            mainToolbar.updatePowerLabel(sp.getPower());
    }

    public synchronized void setWeapon(int weapon) {
        myPlayer.setWeapon(weapon);
    }

    public synchronized void sendUpdate(ScorchPlayer sp) {
        network.sendUpdate(sp.getID(), sp.getPower(), sp.getAngle());
    }

    public /*synchronized*/ void recieveUseItem(int item, int p) {
        ScorchPlayer sp;

        if (activePlayer == myID)
            return;
        sp = getActivePlayer();
        sp.useItem(item, p);
    }

    public synchronized void fire() {
        int w = myPlayer.getWeapon();

        sendUpdate(myPlayer);
        network.sendUseWeapon(w);
        scorch.fire(myPlayer, w);
        myPlayer.decWeapon();
    }

    public synchronized void fire(int weapon) {
        ScorchPlayer sp;

        mainToolbar.enableKeys(false);
        if (activePlayer == myID)
            return;

        sp = getActivePlayer();
        scorch.fire(sp, weapon);
    }

    // user selects mass kill, we notify server and do nothing
    // server waits until the current turn is finished (doesn't wait if 
    // it is master turn) and forwards the masskill message. 
    // see ScorchApplet.recieveMassKill()
    public synchronized void massKill() {
        if (!massKilled) {
            mainToolbar.enableKeys(false);
            massKilled = true;
            network.sendMassKill();
        }
    }

    public synchronized void displayReference() {
        String help =
                "Hot keys:\nArrow keys : change power and angle setting with step 1\nCtrl : (with arrow keys) change power and angle with step 5\nPgUp/PgDn : change power with step 10\nHome/End : change angle with step 10\nSpace : Fire\nF1 : this reference window\nF2 : Statistics\nF3 : Edit profile\nF4 : Mass kill\nF5 : Inventory\nF10 : System menu\nTo chat during the game just start typing and chat window will popup.\n";
        String[] b = {"OK, thanks", "Open manual"};
        String[] c = {null, "showHelp"};
        MessageBox ref =
                new MessageBox("Quick Reference", help, b, c, Label.LEFT, this);

        ref.display();
    }

    public synchronized void requestTopTen() {
        network.sendTopTen();
    }

    // Network callbacks

    public synchronized void loggedIn(int id, PlayerProfile profile,
                                      boolean ai) {
        ScorchPlayer newPlayer;

        if (players == null) // I am logged in. Initialize everything
        {
            // first player is always me
            myID = id;
            players = new Vector<>(ScorchPlayer.MAX_PLAYERS);
            master = (myID == 0);

            if (Debug.dev) {
                ChatScreen chatScreen =
                        new ChatScreen(width, height, this);
                add(chatScreen);
                chatScreen.setLocation(0, 0);
                chatScreen.validate();
                chatScreen.setVisible(true);
            }
		
		/*gp = new GradientPanel(width, height, 
				       new Color(Tanks.getTankColor(myID)),
				       Color.black);
		add(gp);
		validate();*/

            sWindow options;
            if (master)
                options = new StartGame(this);
            else {
                options = new JoinGame(profile, this);
			/*options = new GameOptions
			  (profile.getName(), -1, false, this);*/
                playersList = (PlayersLister) options;
            }
            options.display();

        }

        if (!ai)
            newPlayer = new ScorchPlayer(id, profile, this);
        else
            newPlayer = new AIPlayer(id, profile, this);

        if (id == myID) {
            myPlayer = newPlayer;
        }

        for (int i = 0; i <= getPlayersNum(); i++)
            if ((i == getPlayersNum()) || (getPlayer(i).getID() > id)) {
                players.insertElementAt(newPlayer, i);
                break;
            }

        if (playersList != null) {
            String name = profile.getName();
            if (id == 0) name = name + " (master)";
            if (ai) name = name + " (AI)";
            playersList.addPlayer(name);
        }
    }

    public synchronized void showDisconnect(String msg) {
        String[] b = {"OK"};
        String[] c = {"Quit"};
        MessageBox error = new MessageBox("Message", msg, b, c, this);
        network.quit();
        this.removeAll();
        error.display();
    }

    // this doesn't need to be synronized because it is called by 
    // server only when all users controls are disabled (when all users
    // expect MAKETURN command). Also it is called only by the Network
    // thread, so it's safe. The problem with this is that network
    // is unusable while player explodes. This means that chat will not
    // work and on a *slow* system client may time out.
    // the solution would be to start separate threads in ScorchField
    // but it doesn't work for playerLeft since server doesn't expect 
    // EOT and sends MAKETURN immedeately. 
    // TODO: talk with Alex, change protocol so that LEFT would work
    // the same way as MASSKILL does
    // the same applies to the recieveMassKill()
    public void playerLeft(int ID) {
        ScorchPlayer sp = getPlayerByID(ID);

        if (sp == null) {
            System.err.println("ScorchApplet.playerLeft(): Player " + ID +
                    " not found");
            return;
        }

        if (chatBox != null) // dirty trick to save private messages
        {
            chatBox.close();
            chatBox = null;
        }

        if (playersList != null) {
            if (ID != 0)
                playersList.removePlayer(sp.getName());
            else
                playersList.removePlayer(sp.getName() + " (master)");
        }

        players.removeElement(sp);

        if (scorch != null)
            scorch.playerLeft(sp);
    }

    public synchronized void errorLogin(String name, String reason) {
        String[] b = {"Retry", "Cancel"};
        String[] cb = {"loginWindow", "Quit"};
        String[] args = {name, null};
        MessageBox mb;
        String message = "Login failed for unknow reason";

        network.quit();
        network = null;

        if (reason.equals(Protocol.wrongusername)) {
            message = "Unrecognized username";
            //args[0] = "";
        }
        if (reason.equals(Protocol.usernametaken)) {
            message = "This username is already taken";
            args[0] = "";
        }
        if (reason.equals(Protocol.wrongpassword))
            message = "Incorrect password";
        if (reason.equals(Protocol.alreadyloggedin))
            message = "User " + name + " is already logged in";

        mb = new MessageBox
                ("Login failed", message, b, cb, args, this);
        mb.display();
    }

    public synchronized void Quit() {
        stop();
        getAppletContext().showDocument(leaveURL);
    }

    public synchronized void banner(URL burl) {
        if (burl != null) {
            getAppletContext().showDocument(burl, "_blank");
            paid = true;
        }
    }

    public synchronized void showHelp() {
        getAppletContext().showDocument(helpURL, "Scorched Earth 2000 Help");
    }

    public synchronized void showTopTen(Vector<?> profiles) {
        StatsWindow sw = new StatsWindow(StatsWindow.TT, profiles, this);
        sw.display();
    }

    public synchronized void shop() {
        if (msg != null) {
            msg.close();
            msg = null;
        }

        ShopWindow sw = new ShopWindow((int) (gameWidth * 0.9),
                (int) (gameHeight * .7), this);
        sw.display();
    }

    public synchronized void startGame() {
        if (gp != null) {
            gp.stop();
            gp = null;
        }
        this.removeAll();

        msg = new MessageBox
                ("Message", "Please wait while game loads",
                        new String[0], new String[0], this);
        msg.display();

        chatBox = null;

        for (int i = 0; i < getPlayersNum(); i++) {
            ScorchPlayer sp = getPlayer(i);
            sp.resetAll();
        }

        massKilled = false;

        scorchFrame = new ScorchFrame(gameWidth, gameHeight, rand, this);
        scorchFrame.display();
        scorch = scorchFrame.getScorch();

        // TODO: do we have to do this EACH round??
        mainToolbar = new MainToolbar(players, this);
        mainToolbar.display();
        mainToolbar.enableKeys(false);

        playersList = mainToolbar.getPlayersList();
        if (playersList == null)
            System.err.println("ScorchApplet.startGame() playersList is null." +
                    "Please report to meshko@scorch2000.com");

        if (gameSettings.wind == GameSettings.CONST_WIND)
            Physics.setWind(rand.nextInt() % Physics.MAX_WIND);

        if (master) aiBuyItems();

        moveCount = 0;
        sendEOT();

        if (msg != null) msg.close();
        msg = new MessageBox
                ("Message", "Please wait for other players to sync",
                        new String[0], new String[0], this, mainToolbar);
        msg.display();

        // if it is first turn of a guest or a new player show help
        if (roundCount == 0 && (isGuest() || isNewPlayer()))
            displayReference();

        if (!paid && banners != null) {
            Random r = new Random();
            int bNum = Math.abs(r.nextInt() % banners.size());
            (new BannerWindow(this, banners.elementAt(bNum),
                    bannersURL.elementAt(bNum))).display();
        }

        scorch.newSysMsg("Round #" + (++roundCount) + " out of " + getMaxRounds());
    }

    private void aiBuyItems() {
        for (int i = 0; i < getPlayersNum(); i++) {
            ScorchPlayer sp = players.elementAt(i);
            if (sp instanceof AIPlayer)
                ((AIPlayer) sp).buyAmmunition();
        }
    }

    public synchronized void makeTurn(int id) {
        activePlayer = id;

        if (playersList != null)
            ((PlayersListControl) playersList).highlight(getPlayerByID(id));

        if (msg != null) {
            msg.close();
            msg = null;
        }

        if (Debug.desyncTest) {
            getPlayerByID(id).activateParachute(3);
            network.sendUseItem(Item.Parachute, 3);
        }

        if (gameSettings.wind == GameSettings.CHANGING_WIND) {
            Physics.setWind(rand.nextInt() % Physics.MAX_WIND);
            scorch.repaint();
        }

        // for desync testing stop round after some number of shots
        moveCount++;
        if (master && Debug.desyncTest && moveCount > 90) {
            massKill();
            if (id == myID) return;
        }

        if (id == myID) // our player's turn
        {
            if (Debug.desyncTest) // skip turn if desync test
            {
                mainToolbar.enableKeys(false);
                fire();
            } else {
                if (!myPlayer.isFirstTurn()) {
                    mainToolbar.enableKeys(true);
                    // request focus only if not in chat window
                    if (chatBox == null)
                        mainToolbar.requestFocus();
                } else {
                    if (myPlayer.useAutoDefense()) {
                        AutoDefenseWnd adw =
                                new AutoDefenseWnd(this);
                        adw.display();
                    } else
                        sendEOT();
                }
            }
        } else {
            mainToolbar.enableKeys(false);

            ScorchPlayer sp = getPlayerByID(id);

            if (master && sp instanceof AIPlayer)
                ((AIPlayer) sp).makeTurn(); // takes care of first turn too
            else if (sp.isFirstTurn())
                sendEOT();

		/*if( sp.isFirstTurn() )
		    sendEOT();
		else
		    if( master && sp instanceof AIPlayer )
			((AIPlayer)sp).aimedFire();*/
        }
    }

    synchronized void aiFire(ScorchPlayer aip) {
        sendUpdate(aip);
        network.sendUseWeapon(aip.getWeapon());
    }

    synchronized void recieveGameSettings(GameSettings gSettings, long seed) {
        gameSettings = gSettings;
        rand = new Random(seed); //seed

        for (int i = 0; i < getPlayersNum(); i++)
            players.elementAt(i).setRand(rand);

        Physics.setGravity(gameSettings.gravity);

        if (gameSettings.wind == GameSettings.NO_WIND)
            Physics.setWind(0);

        // give all the items in the lamer mode. This must be done before
        // startGame() is called because startGame() loads some UI components
        if (gSettings.lamerMode)
            myPlayer.giveWeapons();

        // no clue why this check is here. investigate
        if (playersList == null) {
            myPlayer.setCash(gSettings.initialCash);
            if (gSettings.initialCash <= 0)
                startGame();
            else
                shop();
        } else
            System.err.println("ScorchApplet.recieveGameSettings(): playersList == null");
    }

    synchronized void recievePlayerOptions(int id, int type) {
        ScorchPlayer sp = getPlayerByID(id);

        if (sp != null)
            sp.setTankType(type);
        else
            System.err.println("recievePlayerOptions: invalid id");
    }

    synchronized void recieveUpdate(int id, int new_power, int new_angle) {
        // ignore the update for itself if server forwards it for some reason
        if (id == myID)
            return;

        ScorchPlayer sp = getPlayerByID(id);

        // if ai player is updated master should ignore this
        if (master && sp instanceof AIPlayer)
            return;

        sp.setAngle(new_angle);
        sp.setPower(new_power);
    }

    synchronized void recieveEOR() {
        massKilled = true; // to disable masskill until next round starts

        if (playersList != null)
            ((PlayersListControl) playersList).highlight(null);

        StatsWindow sw = new StatsWindow(StatsWindow.EOR, this);

        if (Debug.desyncTest)
            startGame();
        else
            sw.display();
    }

    synchronized void recieveEOG() {
        if (playersList != null)
            ((PlayersListControl) playersList).highlight(null);
        updateUser(myPlayer.getProfile(), false);

        StatsWindow sw = new StatsWindow(StatsWindow.EOG, this);
        sw.display();
    }

    synchronized void chatMsg(String msg) {
        if (scorch != null)
            scorch.newChatMsg(msg);
    }

    // server decided that it's ok now to masskill so just do it
    // see comments to the playerLeft()
    public void recieveMassKill() {
        scorch.massKill();
    }

    public void focusLost(FocusEvent evt) {
    }

    public void focusGained(FocusEvent evt) {
        if (mainToolbar != null) mainToolbar.requestFocus();
    }

    public void setTimerLabel(int time) {
        if (time < 0)
            timerLabel.setText("");
        else
            timerLabel.setText("" + time);
    }

    private void loadSounds() {
        sounds = true;

        GenericMissile.loadSounds(this);
        SimpleExplosion.loadSounds(this);
        FireExplosion.loadSounds(this);
    }

    public static Image getImage(String s) {
        if (saInstance == null) {
            System.err.println("getImage() call failed!");
            return null;
        } else
            return saInstance.getImage(saInstance.getCodeBase(), s);
    }
}
