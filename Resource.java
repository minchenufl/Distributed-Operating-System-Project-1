import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * this class provide the partially synchronized method op() for
 * all threads, they complete for the entrance to the critical section,
 * also, we print the states of all semaphores here
 */
public class Resource 
{
	private static int visitOrder = 0;     	//it is just a index counter for arrays threadNo[], visitCounter[] and timeRecorder[]
	private long startingTime;         		//starting time of the program
	private int n;
	private PrintWriter pw;
	
	private int[] threadNo;
	private int[] visitCounter;
	private long [] timeRecorder;
	private MySemaphore[] semaphores;
	
	/*
	 * constructor of ForSemaphore
	 */
	Resource(MySemaphore[] s, long starter, PrintWriter pw, int[] threadNo, int[] visitCounter, long [] timeRecorder) 
	{
		this.n = s.length;
		this.semaphores = s;
		this.startingTime = starter;
		this.pw = pw;
		this.threadNo = threadNo;
		this.visitCounter = visitCounter;
		this.timeRecorder = timeRecorder;
	}
	
	/**
	 * Threads complete for the semaphores, print all semaphores' states if any 
	 * thread enter into the critical section
	 * @param myt this thread 
	 */
	public void visitResource(MyThread mythread) throws FileNotFoundException
	{	
		int height = (int) (Math.log(n) / Math.log(2));
		
		try {
			Thread.sleep(mythread.sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int temp = mythread.getIndex() + n; 			//temp is used to find the way to root
		int[] pathToRoot = new int[height]; 			//left or right child of its father node, 0->left, 1->right

		
		for(int i=1; i<=height; i++)
		{
			pathToRoot[height-i] = temp % 2;
			temp = temp / 2;
			semaphores[temp].P(mythread);
		}
		
		
		/*record the information of the thread entering into the critical section*/
		
		threadNo[visitOrder] = semaphores[1].getholdingThread().getIndex() + 1;
		visitCounter[visitOrder] = semaphores[1].getholdingThread().getCounter();
		timeRecorder[visitOrder] = System.currentTimeMillis() - startingTime;
		visitOrder++;
		
		try {
			Thread.sleep(mythread.opTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				}
		
		for(int j = 1; j <= height; j++)
		{
			for(int i = (int) (Math.pow(2, (j-1))); i < (int) (Math.pow(2, j)); i++)
			{
				if(semaphores[i].getholdingThread() != null)
				{
					System.out.print("(s" + semaphores[i].getIndex()+", " + semaphores[i].getMutex() + "," +
							" p[" + (semaphores[i].getholdingThread().getIndex()+1) + "," + semaphores[i].getholdingThread().getCounter() + "], ");
					pw.print("(s" + semaphores[i].getIndex()+", " + semaphores[i].getMutex() + "," +
							" p[" + (semaphores[i].getholdingThread().getIndex()+1) + "," + semaphores[i].getholdingThread().getCounter() + "], ");
				}
				else
				{
					System.out.print("(s" + semaphores[i].getIndex() + ", " + semaphores[i].getMutex() +  ", p[0,0], ");
					pw.print("(s" + semaphores[i].getIndex() + ", "  + semaphores[i].getMutex()+", p[0,0], ");
			
				}
				if(semaphores[i].getwaitingThread()!=null)
				{
					System.out.print("p[" + (semaphores[i].getwaitingThread().getIndex()+1)+"," + semaphores[i].getwaitingThread().getCounter() + "])");
					pw.print("p[" + (semaphores[i].getwaitingThread().getIndex()+1) + "," + semaphores[i].getwaitingThread().getCounter()+"])");
				}
				else
				{
					System.out.print("p[0,0])");
					pw.print("p[0,0])");
				}
			
				System.out.print("\t");
				pw.print("\t");
			}
			
			System.out.print("\n");
			pw.println("");	
		}
	
		System.out.println("Unix Time = " + (System.currentTimeMillis() - startingTime));
		pw.println("Unix Time = " + (System.currentTimeMillis() - startingTime));
		pw.println();
	
		//release semaphores from root to leaf
		semaphores[1].V();
		int indicator = 1;
		for(int i = 1; i < height; i++)
		{
			indicator = indicator * 2 + pathToRoot[i-1];
			semaphores[indicator].V();
		
		}
	}
}
