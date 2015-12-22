import java.util.concurrent.atomic.AtomicInteger;


public abstract class ConcurrentBinarySearchTree 
{
	public AtomicInteger validData = new AtomicInteger(0);
	public AtomicInteger successDelete =  new AtomicInteger(0);
	public AtomicInteger successInsert = new AtomicInteger(0);	
	public abstract boolean insert( Integer item );
	public abstract boolean delete( Integer item );
	public abstract boolean search( Integer item );
	public abstract Node getRoot();
	Integer leaf;
	int invalidDataNode;
		
	public int countLeaves( Node nodeP , int startCount , boolean printON )
	{		
		if(nodeP!=null && nodeP.isData() == false)
		{			
			startCount += countLeaves(nodeP.getLeftChild(), startCount, printON);			
			startCount += countLeaves(nodeP.getRightChild(),0 , printON);					
		}
		else if(nodeP!= null && nodeP.isData() == true)
		{
			if(printON)
			System.out.print(nodeP.getkey()+" ");
			
			return 1;			
		}
		return startCount;
	}	
	
	public boolean preorder( Node nodeP , boolean printON )
	{
		boolean hasTwoChildren = false;		
		
		if(nodeP!=null && nodeP.isData() == false)
		{
			if(nodeP.getLeftChild() != null && nodeP.getRightChild() != null )
			{				
				if(printON)
					System.out.print("(in)"+nodeP.getkey()+" ");
				hasTwoChildren = true;
			}
			else
			{	
				if(printON)
					System.out.print("(erro)"+nodeP.getkey()+" ");			
				hasTwoChildren = false;
			}
			
			hasTwoChildren = preorder(nodeP.getLeftChild() , printON) && hasTwoChildren;
			hasTwoChildren = preorder(nodeP.getRightChild() , printON)&& hasTwoChildren;
			
		}
		else if(nodeP!=null && nodeP.isData() == true)
		{
			if(nodeP.getLeftChild() == null && nodeP.getRightChild() == null )
			{
				if(printON)
					System.out.print("(ex)"+nodeP.getkey()+" ");
				hasTwoChildren = true;
				return hasTwoChildren;
			}
			else
			{
				if(printON)				
					System.out.print("(erro)"+nodeP.getkey()+" ");
				hasTwoChildren = false;
				return hasTwoChildren;
			}
		}		
		return hasTwoChildren;
	}
}