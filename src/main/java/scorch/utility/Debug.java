package scorch.utility;

/*
  Class:  Debug
  Authors: Mikhail Kruk, Alexander Rasin

  Description: debug printouts, stack dumps, mermory and time measurements
*/

import java.awt.*;
import java.awt.event.*;

import scorch.Network;
import scorch.Protocol;

public class Debug
{
    private static String log = "";

    public static final int debugLevel = 0;
    public static boolean desyncTest = false;
    public static boolean dev = false;

    private static long time;
    private static Console console;

    private static int frameCount;

    public static void startTimer()
    {
	time = System.currentTimeMillis();
    }

    public static void stopTimer(String msg)
    {
	println("timer ("+msg+"): "+
		(System.currentTimeMillis() - time)+" ms");
    }
    
    public static void printMem()
    {
	println(getMemInfo());
    }

    public static String getMemInfo()
    {
	long mem = Runtime.getRuntime().totalMemory()-
	    Runtime.getRuntime().freeMemory();
	return mem/1000+"Kb";
    }

    public static void println(String msg)
    {
	println(msg, 0);
    }

    public static void println(String msg, int dlevel)
    {
	if(debugLevel > dlevel)
	    System.out.println(msg);
    }

    public static void printStack()
    {
	printStack(Thread.currentThread());
    }
    
    public static void printStack(Thread thread)
    {
	Thread.dumpStack();
    }

    public static void printThreads()
    {
	Thread[] threads = new Thread[Thread.activeCount()];
	Thread.enumerate(threads);
		for (Thread thread : threads) {
			System.out.println("/t/t" + thread);
			printStack(thread);
		}
    }

    public static void pause(int ms)
    {
	try
	    {
		Thread.sleep(ms);
	    }
	catch(Exception e)
	    {
		System.out.println(e);
	    }
	    
    }

    // expects the timer to be already started.  takes 3sec samples
    public static void calcFPSRate( )
    {
	long diff = System.currentTimeMillis() - time;
	if ( diff > 3000 )
	    {
		System.out.println("FPS = " + (frameCount /3) + ", approx. " +
				   (3000 / frameCount) + "ms/frame " +
				   " (" + frameCount + " frames in 3 sec)");
		frameCount = 0;
		time = System.currentTimeMillis();
	    }
	frameCount++;
    }

    public static void initConsole(Network network)
    {
	if( console != null ) console.dispose();
	console = new Console(network);
	console.show();
    }

    public static void consolePrint(String msg)
    {
	if( console != null ) console.addMessage(msg);
	
	// temp logging feature. disable later
	msg = msg.replace(Protocol.separator,' ');
	msg += Protocol.separator;
	log += msg;
    }

    public static void closeConsole()
    {
	if( console != null )
	    console.dispose();
	console = null;
    }

    public static void log(String msg)
    {
	log += msg;
    }

    public static String getLog()
    {
	return log;
    }

    public static void clearLog()
    {
	log = "";
    }
}

class Console extends Frame implements FocusListener, ActionListener
{
    private final TextArea textArea;
    private final TextField textField;
    private final Network network;

    public Console(Network network)
    {
	super("Scorch Debug Console");
	
	addWindowListener(new wndListener());
	
	this.network = network;

	setLayout(new BorderLayout());
	textArea = new TextArea(20,80);
	textArea.addFocusListener(this);
	textField = new TextField(80);
	textField.addActionListener(this);

	Panel t = new Panel(new FlowLayout());
	Button bt = new Button("Select All");
	bt.addActionListener(this);
	t.add(textField);
	t.add(bt);
	bt = new Button("Clear");
	bt.addActionListener(this);
	t.add(bt);

	textArea.setEditable(false);
	add(textArea, BorderLayout.CENTER);
	add(t, BorderLayout.SOUTH);
	pack();
    }

    public void focusLost(FocusEvent evt) {}

    public void focusGained(FocusEvent evt)
    {
	if( evt.getComponent() == textArea )
	    textField.requestFocus();
    }

    public void actionPerformed(ActionEvent evt)
    {
	if( evt.getSource() == textField )
	    {
		String m = textField.getText();
		m = m.replace(' ', Protocol.separator);
		network.sendMessage(m);
		textField.setText("");
	    }
	if( evt.getActionCommand().equals("Select All"))
	    textArea.selectAll();
	if( evt.getActionCommand().equals("Clear"))
	    textArea.setText("");
    }

    public void addMessage(String s)
    {
	s = s.replace(Protocol.separator,' ');
	textArea.append(s+"\n");
    }

    static class wndListener extends WindowAdapter
    {
	public void windowClosing(WindowEvent e)
	{
	    Debug.closeConsole();
	}
    }
}
