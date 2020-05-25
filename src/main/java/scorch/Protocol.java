/*
  Class : Protocol

  Author: Alexander Rasin

  The protocol that client and server use.  Those are all the instructions
  that the client and the server can use, each one of them has expected 
  parameters in comments
*/

package scorch;

public final class Protocol
{
    public static final char separator = (char)0;

    //general commands
    public static final String commandfailed = "COMMANDFAILED";//'reason'
    public static String debug = "DEBUG"; // 'String'
    public static final String guest = "guest";   // guest user
    public static final String setplayeroptions = "PLOPTIONS";
    //'player id' 'tank type'
    public static final String masskill = "MK";
    public static String boot = "BOOT"; // player id
    //kill all the tanks in the game. only executed by master
    public static final String topten = "TOP10";

    //Commands understood by server
    public static final String login = "LOGIN";   // 'User Name' 'Password'
    public static final String newplayer = "NEW"; //'user' 'password' 'profile'
    public static final String jvminfo = "JVM";   //'jvm version'
    public static String removeplayer = "RMPLAYER"; //'username' 'password'
    public static final String pong = "PONG";     // answer to ping
    public static final String setgameoptions = "OPTIONS";
    //'intial cash' 'wind' 'nature hazards'
    public static final String addaiplayer = "ADDAI";          //'ai player type'
    public static final String setmaxplayers = "SETMAXPL";     //'number'
    public static final String playerdead = "PLDEAD";          //'player id'
    public static final String endofturn = "EOT";
    public static final String shout = "SHOUT";          //'message'
    public static final String say = "SAY";              //'pl id' 'message'
    public static final String update = "UPDATE";        //'power' 'angle'
    public static final String useweapon = "USEWEAPON";  //'weapon_id'
    public static final String useitem = "USEITEM";      //'item_id' 'quantity'
    public static final String quit = "QUIT";
    public static final String changeprofile = "CHPROF"; //'new profile'
    public static String donebuying = "DONEBUYING"; 
    public static final String endofround = "EOR";             //'stats of round'
    public static final String endofgame = "EOG";              //'stats of game'

    //Commands understood by client
    public static final String ping = "PING"; //ping
    public static final String loggedin = "LOGGEDIN"; //'name' 'Id'(int) 'profile'
    public static final String ailoggedin = "AILOGGEDIN"; //'name' 'Id' 'profile'
    public static final String loginfailed = "LOGINFAILED"; //'reason'
    //reasons for failed login
    public static final String wrongpassword = "PASSWD";    //wrong password
    public static final String alreadyloggedin = "INGAME";  //user already playing
    public static final String wrongusername = "UNAME";     // wrong username
    public static final String usernametaken = "UNAMETAKE"; // username already taken

    public static final String disconnect = "DISCONNECT"; //'message'
    public static final String playerleft = "LEFT";       //'id'
    public static final String gameoptions = "OPTIONS";
    //'initial cash' 'wind' 'nature hazards' 'seed'
    public static String playerusedweapon = "USEWEAPON"; //'weapon_id'
    public static String playeruseditem = "USEDITEM";    //'item_id' 'quantity'
    public static final String maketurn = "MAKETURN";          //'player_id'

    public static final String requestlog = "RLOG";
    public static final String clientlog = "CLOG"; // 'log'
}
