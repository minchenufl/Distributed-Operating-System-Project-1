/**
 * MySemaphore is the class for semaphore in this program,
 * it is used to create semaphore objects  
 */

public class MySemaphore
{	
	private MyThread holdingThread;   	//the thread which is holding this semaphore
	private MyThread waitingThread;   	//the thread which is waiting for this semaphore
	private int mutex;		       	 	//number of permissions for each semaphore, which is 1 in this project
	                              		//meaning only one thread can hold this semaphore
	private static int counter = 1;
	private int index;	          		//semaphore's name
	                                                                               
	public MySemaphore()		
	{
		mutex = 1;						//default number of permissions
		index = counter;
		counter ++;
	}
	
	public MySemaphore(int permits)		
	{
		mutex = permits;
		index = counter;
		counter ++;
	}
	
	/**
	 * P operation, if mutex>0, the thread acquires the semaphore, otherwise, waits until some thread releases it
	 * @param mythread is a MyThread object, which wants to get this semaphore 
	*/
	public synchronized void P (MyThread mythread)
	{
		if(mutex > 0)
		{
			holdingThread = mythread;
			mutex--;
		}
		
		else 
		{		
			this.waitingThread = mythread;
			
			while(this.mutex ==0)
			//why while not if? e.x p1 release s2, p2 is waiting for s2, 
			//but it is possible p1 acquire s2 again, and p2 still waits  
			{
				try {
					this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						}
			}
			
			waitingThread = null;
			holdingThread = mythread;
			mutex--;
			
		}
	}
	
	
	/**
	 * V operation, thread releases the held semaphore, 
	 * and notify the thread which is waiting for this semaphore
	 */
	public synchronized void V()
	{
		mutex++;
		holdingThread = null;
		this.notify();
		/*try {
			this.wait(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
	}

	/**
	 * @return the thread holding this semaphore
	 */
	public MyThread getholdingThread()
	{
		return holdingThread;
	}
	
	/**
	 * @return the thread waiting for this semaphore
	 */
	public MyThread getwaitingThread()
	{
		return waitingThread;
	}
	
	/**
	 * @return the name of this semaphore
	 */
	public int getIndex()
	{
		return index;
	}
	
	/**
	 * @return the value of this semaphore
	 */
	public int getMutex()
	{
		return mutex;
	}
	
}
