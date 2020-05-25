package scorch;

import scorch.utility.*;

public class dRandom extends java.util.Random
{
    private int curID = -1, count = 0;
    private int ri[];
    private float rf[];
    private int ip = -1, fp = -1;
    
    public dRandom(long s)
    {
	super(s);

	/*ri = new int[100];
	rf = new float[100];

	for(int i = 0; i < 100; i++)
	    {
		ri[i] = super.nextInt();
		rf[i] = super.nextFloat();
		//System.out.println(ri[i]+" "+rf[i]);
		}*/
    }

    public synchronized int nextInt()
    {
	/*if( count++ % 117 == -1 )
	    System.out.println("random: "+ip+" "+fp+" thread: "+Thread.currentThread().getName() );

	    ip++; if( ip == 100 ) ip = 0;*/
	return super.nextInt(); //ri[ip];
    }

    public synchronized float nextFloat()
    {
	/*if( count++ % 100 == -1 )
	    System.out.println("random: "+ip+" "+fp+" thread: "+Thread.currentThread().getName() );
	
	    fp++; if( fp == 100 ) fp = 0;*/
	return super.nextFloat(); //rf[fp];
    }
}
