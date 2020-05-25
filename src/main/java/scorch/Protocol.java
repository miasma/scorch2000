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
    public static char separator = (char)0;

    //general commands
    public static String commandfailed = "COMMANDFAILED";//'reason'
    public static String debug = "DEBUG"; // 'String'
    public static String guest = "guest";   // guest user
    public static String setplayeroptions = "PLOPTIONS";     
    //'player id' 'tank type'
    public static String masskill = "MK";
    public static String boot = "BOOT"; // player id
    //kill all the tanks in the game. only executed by master
    public static String topten = "TOP10"; 

    //Commands understood by server
    public static String login = "LOGIN";   // 'User Name' 'Password' 
    public static String newplayer = "NEW"; //'user' 'password' 'profile'
    public static String jvminfo = "JVM";   //'jvm version'
    public static String removeplayer = "RMPLAYER"; //'username' 'password'
    public static String pong = "PONG";     // answer to ping
    public static String setgameoptions = "OPTIONS";
    //'intial cash' 'wind' 'nature hazards'
    public static String addaiplayer = "ADDAI";          //'ai player type'
    public static String setmaxplayers = "SETMAXPL";     //'number'
    public static String playerdead = "PLDEAD";          //'player id'
    public static String endofturn = "EOT";        
    public static String shout = "SHOUT";          //'message'
    public static String say = "SAY";              //'pl id' 'message'
    public static String update = "UPDATE";        //'power' 'angle'
    public static String useweapon = "USEWEAPON";  //'weapon_id' 
    public static String useitem = "USEITEM";      //'item_id' 'quantity'
    public static String quit = "QUIT"; 
    public static String changeprofile = "CHPROF"; //'new profile' 
    public static String donebuying = "DONEBUYING"; 
    public static String endofround = "EOR";             //'stats of round'
    public static String endofgame = "EOG";              //'stats of game'

    //Commands understood by client
    public static String ping = "PING"; //ping 
    public static String loggedin = "LOGGEDIN"; //'name' 'Id'(int) 'profile'
    public static String ailoggedin = "AILOGGEDIN"; //'name' 'Id' 'profile'
    public static String loginfailed = "LOGINFAILED"; //'reason'
    //reasons for failed login
    public static String wrongpassword = "PASSWD";    //wrong password
    public static String alreadyloggedin = "INGAME";  //user already playing
    public static String wrongusername = "UNAME";     // wrong username
    public static String usernametaken = "UNAMETAKE"; // username already taken

    public static String disconnect = "DISCONNECT"; //'message'
    public static String playerleft = "LEFT";       //'id'
    public static String gameoptions = "OPTIONS"; 
    //'initial cash' 'wind' 'nature hazards' 'seed'
    public static String playerusedweapon = "USEWEAPON"; //'weapon_id'
    public static String playeruseditem = "USEDITEM";    //'item_id' 'quantity'
    public static String maketurn = "MAKETURN";          //'player_id'

    public static String requestlog = "RLOG"; 
    public static String clientlog = "CLOG"; // 'log'
}
