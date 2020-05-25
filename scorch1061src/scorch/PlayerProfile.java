package scorch;

/*
  Class: PlayerProfile
  Author: Mikhail Kruk
  Description: the profile of the player send from the server
*/

import java.io.Serializable;
import java.util.*;

import scorch.utility.*;

public class PlayerProfile implements Serializable
{  
    // if username is guest the user is considered guest, his profile is not
    // stored and his username is extracted from the password field. 

    // fileds stored in profile
    private String name, password;  // main user fields
    private String email;           // contact info
    
    private int overallKills;       // players scores: number of tanks killed
    private long overallGain;       // and total amount of money gained

    private int tankType = -1, nRounds = -1, wind = -1;
    private float gravity = -1;
    private long initCash = -1;
    private boolean hazards = false;

    private long timeCreation, timeAccess;

    private boolean sounds = false;

    // run-time data
    private boolean bGuest = false; // guest flag
    private boolean modified = false; // dirty flag for server needs
    
    private boolean lamerMode = false; // do not store this
    
    // constructors
    public PlayerProfile(String profile)
    {
	this(new StringTokenizer(profile, Protocol.separator+""));
    }

    public PlayerProfile(StringTokenizer st)
    {
	try
	    {
		this.name = st.nextToken();
		this.password = st.nextToken();
		this.email = st.nextToken();
		this.overallKills = Integer.parseInt(st.nextToken());
		this.overallGain = Integer.parseInt(st.nextToken());
	    }
	catch(NoSuchElementException e)
	    {
		Debug.println
		    ("PlayerProfile constructor: broken profile");
	    }
	
	try
	    {
		this.tankType = Integer.parseInt(st.nextToken());
		this.nRounds = Integer.parseInt(st.nextToken());
		this.wind = Integer.parseInt(st.nextToken());
		this.gravity = new Float(st.nextToken()).floatValue();
		this.initCash = Long.parseLong(st.nextToken());
		this.hazards = (new Boolean(st.nextToken())).booleanValue();
		this.timeCreation = Long.parseLong(st.nextToken());
		this.timeAccess = Long.parseLong(st.nextToken());
		this.sounds = (new Boolean(st.nextToken())).booleanValue();
	    }
	catch(NoSuchElementException  e)
	    {
		// old style profile, we don't care
		// or do we?
	    }

	if( name != null && name.equals(Protocol.guest))
	    {
		Debug.println("setting user to guest: "+password);
		setGuest(true);
		this.name = this.password;
		this.password = Protocol.guest;
	    }
    }

    public PlayerProfile(String name, String password)
    {
	this(name, password, "");
    }
    
    public PlayerProfile(String name, String password, String email)
    {
	this.name = name;
	this.password = password;
    
	// it seems that Alex and I didn't get this straight...
	if( name != null && name.equals(Protocol.guest))
	    {
		Debug.println("setting user to guest: "+password);
		setGuest(true);
		this.name = this.password;
		this.password = Protocol.guest;
	    }

	setEmail(email);
    }

    public void setEmail(String email)
    {
	if( email == null || email.equals("") )
	    this.email = "not specified";
	else
	    this.email = email;
    }

    public void setPassword(String password)
    {
	if( password == null || password.equals("") )
	    this.password = "*";
	else
	    this.password = password;
    }

    public String getName()
    {
	return name;
    }
    
    public String getPassword()
    {
	return password;
    }

    public String getEmail()
    {
	return email;
    }

    public int getOverallKills()
    {
	return overallKills;
    }

    public long getOverallGain()
    {
	return overallGain;
    }

    public void setOverallKills(int overallKills)
    {
	this.overallKills = overallKills;
    }

    public void setOverallGain(long overallGain)
    {
	this.overallGain = overallGain;
    }
    
    public void incOverallKills(int inc)
    {
	this.overallKills += inc;
    }

    public void incOverallGain(long inc)
    {
	this.overallGain += inc;
    }
    
    public boolean isGuest()
    {
	return bGuest;
    }

    public void setGuest(boolean g)
    {
	bGuest = g;
    }

    public boolean isModofied()
    {
	return modified;
    }

    public void setModified(boolean m)
    {
	modified = m;
    }

    public int getTankType()
    {
	return tankType;
    }
    
    public int getNumRounds()
    {
	return nRounds;
    }
    
    public int getWind()
    {
	return wind;
    }
    
    public float getGravity()
    {
	return gravity;
    }
    
    public long getCash()
    {
	return initCash;
    }

    public boolean getHazards()
    {
	return hazards;
    }

    public boolean getSounds()
    {
	return sounds;
    }

    public void setTankType(int v)
    {
	tankType = v;
    }
    
    public void setNumRounds(int v)
    {
	nRounds = v;
    }
    
    public void setWind(int v)
    {
	wind = v;
    }
    
    public void setGravity(float v)
    {
	gravity = v;
    }
    
    public void setCash(long v)
    {
	initCash = v;
    }

    public void setHazards(boolean v)
    {
	hazards = v;
    }
    
    public void setSounds(boolean v)
    {
        sounds = v;
    }

    public void setLamerMode(boolean v)
    {
	lamerMode = v;
    }

    public boolean getLamerMode()
    {
	return lamerMode;
    }

    public long getAccessTime()
    {
	return timeAccess;
    }

    public long getCrationTime()
    {
	return timeCreation;
    }

    public void setAccessTime(long nv)
    {
	timeAccess = nv;
    }

    public void setCrationTime(long nv)
    {
	timeCreation = nv;
    }

    public String makeLoginString()
    {
	String result = 
	    (isGuest() ? 
	     password+Protocol.separator+name :
	     name+Protocol.separator+password);
	return result;
    }

    public String toString()
    {
	String result = makeLoginString() +
	    Protocol.separator+email+Protocol.separator+overallKills+
	    Protocol.separator+overallGain+
	    Protocol.separator+tankType+
	    Protocol.separator+nRounds+
	    Protocol.separator+wind+
	    Protocol.separator+gravity+
	    Protocol.separator+initCash+
	    Protocol.separator+hazards+
	    Protocol.separator+timeCreation+
	    Protocol.separator+timeAccess+
	    Protocol.separator+sounds;

	Debug.println("PlayerProfile.toString(): "+result);
	return result;
    }

    /*
      encrypts the profile's password.  Takes first and last letter of the
      user name as the salt (user name cannot be changed)
    */
    public PlayerProfile encrypt()
    {
	if( !bGuest )
	    {
		
		String salt = ""+name.charAt(0) + name.charAt(name.length()-1);
	
		password = Crypt.crypt(salt, password);
	    }

	return this;
    }
}
