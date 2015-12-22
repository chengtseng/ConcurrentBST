import java.math.BigDecimal;

public class TestAlgorithm 
{	
	public static void main( String [] args ) throws InterruptedException
	{	
		String treeType;
		int threadNumber; 
		int key; 
		int rounds = 1;
		BigDecimal sOperation;
		BigDecimal dOperation;
		BigDecimal iOperation;		
		Task task = null;		
		boolean sentinelCheck =false;
		boolean printOnOff = false;		
		
		if (args.length < 6) 
		{
			System.out.println( "Please enter treeType, number of thread, key range, percentage(in decimal digit, i,e: 0.1 for 10% ) of Search, Insert, Delete operations\n");
			return;
		}
		else if(args.length >= 6 && args.length <= 9)
		{
			treeType = args[0];
			threadNumber = Integer.parseInt(args[1]);
			key = Integer.parseInt(args[2]);
			sOperation = new BigDecimal(args[3]);
			iOperation =new BigDecimal(args[4]);
			dOperation = new BigDecimal(args[5]);
			if( sOperation.add(iOperation).add( dOperation).equals(1))
			{
				System.out.println("Percentage sum of three operations should be 1.");
				return;
			}
			if(args.length == 6)
				rounds = 1;
			else if(args.length == 7)
				rounds = Integer.parseInt(args[6]);
			else if(args.length == 8)
			{
				rounds = Integer.parseInt(args[6]);
				
				switch(args[7])
				{
					case "T" : sentinelCheck = true;
						break;
						
					case "F" : sentinelCheck = false;
						break;
					
					default: System.out.println("not valid"); return;	
				}				 
			}
			else if(args.length == 9)
			{
				rounds = Integer.parseInt(args[6]);
				
				switch(args[7])
				{
					case "T" : sentinelCheck = true;
						break;
						
					case "F" : sentinelCheck = false;
						break;
					
					default: System.out.println("not valid"); return;	
				}
				
				switch(args[8])
				{
					case "T" : printOnOff = true;
						break;
						
					case "F" : printOnOff = false;
						break;
					
					default: System.out.println("not valid"); return;	
				}		
			}
		}
		else
		{
			System.out.printf("Not valid input, please refer to README\n");
			return;
		}
		
		try
		{	Double sumOfThroughPutForAllTestAtThisDataPoint = 0.0;		
			for(int i = 0; i < rounds; i++)
			{				
				task = new Task(treeType, threadNumber, key, sOperation, iOperation, dOperation, rounds, sentinelCheck, printOnOff);
				task.runTheTask();
				System.out.println("##############################--------------------------------FINAL CHECK POINT------------------------------########################################");
				task.sentinelCheck();
				Double thoughtPut = (task.getTotalOperation())/(task.getTotalTime());
				System.out.println("Throughput:"+thoughtPut);
				sumOfThroughPutForAllTestAtThisDataPoint+= thoughtPut;
			}
			if(rounds>1)
			System.out.println("Average throughput for "+rounds+" tests is "+(sumOfThroughPutForAllTestAtThisDataPoint/rounds));
			
			System.out.println();
		}
		catch (Exception e) 
		{				
			e.printStackTrace();
		}
	}	
}


