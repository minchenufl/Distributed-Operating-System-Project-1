import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * This program achieves Peterson algorithm for 
 * multiple-thread critical section visit
 * @author Min Chen UFID: 1691-6338
 * 02-07/2012
 * */

public class Start 
{
	public static void main(String[] args) throws FileNotFoundException
	{		
		Scanner fr = new Scanner(new File("system.properties")); //read information from file
		int n, m;
		String[] temp;
		temp = fr.nextLine().split("=");
		n = Integer.parseInt(temp[1]);
		
		temp = fr.nextLine().split("=");
		m = Integer.parseInt(temp[1]);
		
		PrintWriter pw = new PrintWriter (new File("event.log"));
		
		
		MySemaphore[] s = new MySemaphore [n];		//create n - 1 semaphore because a tree with n leaf nodes have (n - 1) interal nodes
		for(int i=1; i<n; i++)
			s[i] = new MySemaphore();
		
		
		int[] threadNo = new int[n*m];         		//this array is used to record the number of thread visiting the critical section
		int[] visitCounter = new int[n*m];     		//this array is used to record thread's ith entrance into the critical section
		long [] timeRecorder = new long[n*m];  		//this array is used to record the unix time of thread visiting the critical section
		
		long starter = System.currentTimeMillis();  //starting time
		ForSemaphore fs = new ForSemaphore(s, starter, pw, threadNo, visitCounter, timeRecorder);   //competition for critical section

		
		Thread[] t = new MyThread [n];         		//create n threads
		int opTime, sleepTime; 
		
		for(int j = 0; j < n; j++)
		{
			temp = fr.nextLine().split("=");
			opTime = Integer.parseInt(temp[1]);
			temp = fr.nextLine().split("=");
			sleepTime = Integer.parseInt(temp[1]);
			t[j] = new MyThread(m, fs, j, opTime, sleepTime);
		}
		
		fr.close();
		
		for(int j = 0; j < n; j++)  				//start all threads
			t[j].start();
		
		boolean alive = true; 
		while(alive == true)   						//test whether all threads have ended
		{
			alive = false;
		for(int j=0; j<n; j++)
			alive |= t[j].isAlive();
		}
		
		System.out.println();
		System.out.println("sequence:");
		System.out.println();
		pw.println();
		pw.println("sequence:");
		pw.println();
		for(int j = 0; j < n * m; j++)     			//output the sequence of events
		{
			System.out.println("p[" + threadNo[j] + "-" + visitCounter[j] + "]:" + timeRecorder[j]);
			pw.println("p[" + threadNo[j] + "-" + visitCounter[j] + "]:" + timeRecorder[j]);
		}
		
		pw.close();
		
	}

}
