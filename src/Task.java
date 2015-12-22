import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Task 
{	
	private int numberOfThread; 
	private int keyRange;
	private BigDecimal approximateTotalOperationPerThread = new BigDecimal(1000000);
	private int totalOperation;
	private int sOperationEach;
	private int iOperationEach;
	private int dOperationEach;
	private String treeType;
	private int originalTreeSize=0;
	private double totalTime = 0;
	private boolean sentinelCheckRequested;
	private boolean printON;
	private AtomicInteger stillActiveThread = new AtomicInteger(0);
	private AtomicInteger completeExecution = new AtomicInteger(0);
	private Lock lock = new ReentrantLock();	
	private Condition isHalfWay = lock.newCondition();
	private Condition notHalfWay = lock.newCondition();
	private Thread [] threadPool;	
	private ConcurrentBinarySearchTree tree;
	private long startTime = 0;
	
	public Task()
	{
		tree = new FGBST();		
	}

	public Task( String treeType, int threadNumber, int key, BigDecimal sOperation, BigDecimal iOperation, BigDecimal dOperation, int rounds, boolean sentinelCheck, boolean printOn)
	{
		numberOfThread = threadNumber;		
		keyRange = key;
		this.printON = printOn;
		this.treeType = treeType;
		
		sOperationEach = (sOperation.multiply(approximateTotalOperationPerThread)).intValue();
		iOperationEach = (iOperation.multiply(approximateTotalOperationPerThread)).intValue();
		dOperationEach = (dOperation.multiply(approximateTotalOperationPerThread)).intValue();
		totalOperation = (sOperationEach+ iOperationEach +dOperationEach) * numberOfThread;		
		
		sentinelCheckRequested = sentinelCheck;
		
		switch(treeType)										//decide tree	
		{
			case "FG": tree = new FGBST();
			break;
			
			case "CG": tree = new CGBST();
			break;
			
			default : return;
		}		
		
		/*original insertion*/
		for( int i = 0; i < keyRange/2; i++ )					//insertion for half full tree
		{
			Integer preItem = randInt(1,keyRange);
			tree.insert(preItem);
		}
		
		originalTreeSize =tree.successInsert.get();
		tree.successInsert.set(0);								//start calculating success insert after half full tree built.
		System.out.println("##############################----original state: approximately half full tree complete, start from here-----########################################");
		
		/*sentinel check on--add check thread into system*/		
		if(sentinelCheckRequested == true)
		{
			threadPool = new Thread[numberOfThread+1];
			threadPool[0] = new checkThread();
			
			for( int i = 1; i < threadPool.length; i++ )
			{
				threadPool[i] =new workingThread();	
			}
		}
		
		/*sentinel check off--all thread are working threads in the system*/	
		else
		{
			threadPool = new Thread[numberOfThread];			
			
			for( int i = 0; i < threadPool.length; i++ )
			{
				threadPool[i] =new workingThread();	
			}
		}		
	}
	
	/*wake up all threads and start the task*/
	public void runTheTask()
	{	
		startTime = System.currentTimeMillis();		
		try
		{
			for( int i = 0; i < threadPool.length; i++)
			{
				threadPool[i].start();
				stillActiveThread.getAndIncrement();
			}
			for (int i = 0 ; i < threadPool.length; i++) 
		    {
		    	threadPool[i].join();
		    }	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis();
		this.totalTime = (double)(endTime - startTime);		
	}
	
	/*sentinel check thread*/
	private class checkThread extends Thread
	{
		public void run()
		{			
			lock.lock();
			try 
			{
				while( completeExecution.get() < totalOperation/2 )
				{
					stillActiveThread.getAndDecrement();
					notHalfWay.await();
				}
				stillActiveThread.getAndIncrement();		//check thread is awake in the system
				sentinelCheck();
				isHalfWay.signalAll();
				stillActiveThread.getAndDecrement();		//check thread is done and leaves the system		
			} 
			catch (InterruptedException e) 
			{				
				e.printStackTrace();
			}
			finally
			{
				lock.unlock();				
			}		
		}				
	}
	
	/*working thread*/
	private  class workingThread extends Thread 						
	{		
		public void run()
		{
			int i = 0;
			int d = 0;
			int s = 0;				
			
			while(i < iOperationEach || d < dOperationEach || s < sOperationEach  )
			{		
				while( sentinelCheckRequested && completeExecution.get() >= totalOperation/2 )
				{				
					lock.lock();
					try
					{	
						if(stillActiveThread.get() == 1 )
						{							
							notHalfWay.signalAll();
						}
						stillActiveThread.getAndDecrement();
						isHalfWay.await();
						stillActiveThread.getAndIncrement();
						sentinelCheckRequested = false;
						break;
					}
					catch( Exception ex)
					{
						ex.printStackTrace();
					}
					finally
					{						
						lock.unlock();						
					}										
				}
				if( i < iOperationEach )
				{
					Integer item = randInt(1,keyRange);
					tree.insert(item);
					i++;
					completeExecution.getAndIncrement();
				}
				if( d < dOperationEach )
				{
					Integer item = randInt(1,keyRange);
					tree.delete(item);					
					d++;
					completeExecution.getAndIncrement();					
				}
				if( s < sOperationEach )
				{
					Integer item = randInt(1,keyRange);
					tree.search(item);
					s++;
					completeExecution.getAndIncrement();
				}	
			}			
			stillActiveThread.getAndDecrement();				//working thread is complete and leaves
		}			
	}	
	
	/*random number creator*/
	public static int randInt(int min, int max) 
	{
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}	
	
	/* sentinelCheck*/
	public void sentinelCheck()
	{		
		System.out.println("--------------------------------------------------------------sentinel check point---"+treeType+"--------------------------------------------------------------");		
		System.out.println("Number of threads: "+numberOfThread+", Key space: "+keyRange+".");
		System.out.println("Original valid data in the tree: "+getOriginalTreeSize());
		System.out.println("Each thread Operations: search: "+sOperationEach+" insert: "+iOperationEach+" delete: "+dOperationEach);
		System.out.println("Success Insertion: "+tree.successInsert);
		System.out.println("Success Deletion: "+tree.successDelete);
		if(printON)
		System.out.print("print tree preorder: ");
		System.out.println("\nInternal nodes has two children, leaf(Data) nodes has no children: "+tree.preorder(tree.getRoot(), printON));				
		System.out.println("Valid data after operation should be( original valid data in tree + successful insertion - successful deletion ): "+tree.validData);
		if(printON)
		System.out.println("print leaves preorder: ");
		System.out.println("\ndata leaves: "+ ((tree.countLeaves(tree.getRoot(), 0, printON))-tree.invalidDataNode) );
		System.out.println("Total time: "+(System.currentTimeMillis()-startTime));		
	}
	
	/*get methods*/
	public int getTotalOperation()
	{
		return totalOperation;		
	}	
	
	public int getOriginalTreeSize() 
	{	   
	    return originalTreeSize;
	}
	
	public double getTotalTime()
	{
		return totalTime;
	}
}
