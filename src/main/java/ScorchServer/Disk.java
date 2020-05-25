/*
  Class:  Disk

  Author: Alexander Rasin

  This is a class that keeps the player table (in hash table) that is simply
  a mapping between a user name and a provile.
  This class can add new profiles to the table, and look them up.
*/

package ScorchServer;

import java.io.*;
import java.util.*;

import scorch.*;

public class Disk implements Runnable
{
    private BufferedReader in;
    private BufferedWriter out;
    private String st, fileName;
    private String table = "players.db";
    private Hashtable hash = null;
    private Thread t = null;
    //the time to wait between checking whether profiles need to be
    //written to disk.
    private int WRITE_DELAY = 10000;
    private boolean changed = false, keep_running = true;

    private static String desyncLogFile = "desync.log";
    private static int desyncCount = 0;

    //constructor
    public Disk()
    {
	File f = new File(table);

	try {
	    //make a new file for profile storage if it does not exist
	    if (!f.exists())
		{
		    System.out.println("db file lacking. Please create file "+table + " manually");
		    System.exit(0);
		}
	    //	f.createNewFile();

	    // load the table from file to memory (hashtable)
	    in = new BufferedReader(new FileReader(table));
	    loadTable();
	    in.close();
	    in = null;
	    changed = false;

	    t = new Thread( this );
	    t.start();
	}
	catch(Exception e) {
	    System.err.println("Warning: Unable to read/write profiles " + e);
	}

    }
    
    //add a new profile.
    //currently used to replace a profile
    public synchronized void add(PlayerProfile profile)
    {
	if (profile != null && hash.get(profile.getName()) != null)
	   System.out.println("WARNING: profile for " + profile.getName() + " already exists (overwriting)");

	changed = true;
	ScorchServer.insertTopTen(profile);
	hash.put(profile.getName(), profile);
    }

    public static synchronized void recordGame(Game g, String os, String ns)
    {
	BufferedOutputStream outBuf = null;

	String temp = os + "\n" + ns + "\nSeed: " + g.getSeed() + "\n" + g
	    + g.getAllHumanPlayersString();
	
	try
	    {
		outBuf = new BufferedOutputStream
		    (new FileOutputStream(Disk.desyncLogFile
					  +Disk.desyncCount++));

		for (int i = 0; i < temp.length(); i++)
		    outBuf.write(temp.charAt(i));

		outBuf.close();
	    }
	catch (IOException e)
	    {
		System.out.println("Disk: Desync Log write failed " + e);
	    }
    }

    public static synchronized void recordClientLog(String log, int cl_id)
    {
	BufferedOutputStream outBuf = null;

	try
	    {
		outBuf = new BufferedOutputStream
		    (new FileOutputStream("desync_" + (Disk.desyncCount-1) +
					  "_forClient_" + cl_id));

		log = log.replace(Protocol.separator, '\n');
		
		for (int i = 0; i < log.length(); i++)
		    outBuf.write(log.charAt(i));

		outBuf.close();
	    }
	catch (IOException e)
	    {
		System.out.println("Disk: Client Log write failed " + e);
	    }
    }

    //calls add so don't need to change the boolean flag changed
    public synchronized void change(PlayerProfile newProfile)
    {
	String name = newProfile.getName();

	remove(name);
	add(newProfile);
    }

    public synchronized PlayerProfile findProfileByName( String name )
    {
	Enumeration e = hash.elements();
	PlayerProfile prof = null;

	while( e.hasMoreElements() )
	    {
		prof = (PlayerProfile)e.nextElement();
		if (prof.getName().equals(name))
		    return prof;
	    }

	return null;
    }

    public synchronized void encryptPasswords()
    {
	Enumeration e = hash.elements();

	while( e.hasMoreElements() )
	    add(((PlayerProfile)e.nextElement()).encrypt());
    }

    public static void main(String args[])
    {
	Disk disk = new Disk();

	disk.encryptPasswords();

	//	System.out.println("a is " + (byte)'a' + " A is " + (byte)'A' + " z is " 
	//		   + (byte)'z' + " Z is " + (byte)'Z');
    //System.out.println(" 0 "+(byte)'0' + " 9 is " + (byte)'9');
	//disk.add(new PlayerProfile("ME2342", "Parol' "));
	//disk.add(new PlayerProfile("ME1344", "Parol' "));
	//disk.add(new PlayerProfile("shuri165234k", "Parol' "));
	
	//String blah = new String("command bl12 23 152");
	
	//System.out.println(blah.substring("command".length()+1, blah.length()));
	//disk.writeTable();
    }

    public PlayerProfile getProfile( String user )
    {
	Object pr;

	if (user == null)
	    return null;

	pr = hash.get(user);

	if (pr != null)
	    return (PlayerProfile)pr;

	return null;
    }

    public void remove(String name)
    {
	hash.remove(name);
    }

    public static int getDesyncCount()
    {
	return Disk.desyncCount;
    }

    public void loadTable()
    {
	String profileString = null;
	PlayerProfile profile = null;
	long time = new Date().getTime();
	int count = 0;

	hash = new Hashtable();
	
	try {
	    //read the profiles from the profile file.
	    while ((profileString = in.readLine()) != null)
		{
		    profile = new PlayerProfile(profileString);
		    add(profile);
		    //sorted insert, will only work if score is in top ten
		    ScorchServer.insertTopTen(profile);
		    count++;
		}
	}
	catch (Exception e) 
	    {
		System.err.println("table load failed: " + e);
	    } 
	System.out.println(" Profile table of (" + count + ") loaded in " +
			   ((new Date().getTime() - time)/1000.0) +
			   " seconds ");
    }
    
    //public void writeTable()
    public void run()
    {
	Enumeration hashKeys = null;
	String elt = null;

	while( keep_running ) {
	    try 
		{
		    
		    while (!changed)
			Thread.currentThread().sleep(WRITE_DELAY);
		    synchronized (this) {
			//create a new stream (and thus empty the file?)
			out = new BufferedWriter(new OutputStreamWriter
			    (new FileOutputStream(table)));
			
			if (hash != null) {
			    hashKeys = hash.keys();
			    
			    long time = new Date().getTime();
			    
			    while (hashKeys.hasMoreElements()) {
				elt = ((PlayerProfile)hash.get
				       ((String)hashKeys.nextElement())) +"";
				out.write(elt, 0, elt.length());
				out.write('\n');
				Thread.currentThread().yield();
			    }
			    out.flush();
			    out.close();
			    changed = false;
			    
			    System.out.println(" Table write completed in " +
					       ((new Date().getTime() - 
						 time)/1000.0) + " seconds ");
			}
		    }
		}
	    catch (Exception e) 
		{
		    System.err.println("table write failed: " + e);
		}
	}
	System.out.println("Disk exiting... " );
    }
    
    public void shutdown()
    {
	changed = true;
	keep_running = false;
    }
}





