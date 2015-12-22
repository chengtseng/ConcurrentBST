import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CGBST extends ConcurrentBinarySearchTree
{
	private volatile Node sNodeRoot;
	private volatile Node sNodeRoot_R;
	private volatile Node sNodeRoot_L;
	private Lock lock = new ReentrantLock();
	static final int empty = 0;
		
	CGBST()
	{
		sNodeRoot = new Node(9999999);
		sNodeRoot_R = new Node();
		sNodeRoot_L = new Node(9999998);
		sNodeRoot_R.setNodeToDataNode();
		sNodeRoot_L.setNodeToDataNode();
		sNodeRoot.setLeftChild(sNodeRoot_L);
		sNodeRoot.setRightChild(sNodeRoot_R);
		invalidDataNode = 2;
	}

	@Override
	public boolean insert(Integer item) 
	{
		lock.lock();
		Integer itemKey = item.hashCode();
		Node pred = new ThreadLocalNode().initialValue();	
		Node curr = pred.getLeftChild() ;		
		
		try
		{
			while(curr!=null && !curr.isData())
			{
				if(curr.getkey() < itemKey)						
				{
					pred = curr;						
					curr = curr.getRightChild();					
				}
				else if(curr.getkey() >= itemKey)			
				{							
					pred = curr;
					curr = curr.getLeftChild();						
				}				
			}			

			if(curr.getkey().equals(itemKey))	//duplicate founded, failure.
			{
//				System.out.println("insert "+itemKey+" F");										
				return false;
			}
			else
			{
				Node newLeaf = new Node(item,true);
										
				if(pred.getkey()>= curr.getkey())
				{
					if( newLeaf.getkey() <= pred.getkey() && newLeaf.getkey() > curr.getkey())
					{									
						Node newRouteNode = new Node(curr.getkey());
						pred.setLeftChild(newRouteNode);
						newRouteNode.setRightChild(newLeaf);
						newRouteNode.setLeftChild(curr);
					}
					else if(newLeaf.getkey() <= pred.getkey() && newLeaf.getkey() < curr.getkey())
					{									
						Node newRouteNode = new Node(newLeaf.getkey());
						pred.setLeftChild(newRouteNode);
						newRouteNode.setRightChild(curr);
						newRouteNode.setLeftChild(newLeaf);									
					}								
				}								
				else if(pred.getkey()<curr.getkey())
				{
					if( newLeaf.getkey() > pred.getkey() && newLeaf.getkey() > curr.getkey())
					{
						Node newRouteNode = new Node(curr.getkey());
						pred.setRightChild(newRouteNode);
						newRouteNode.setRightChild(newLeaf);
						newRouteNode.setLeftChild(curr);
					}
					else if( newLeaf.getkey() > pred.getkey() && newLeaf.getkey() < curr.getkey())
					{
						Node newRouteNode = new Node(newLeaf.getkey());
						pred.setRightChild(newRouteNode);
						newRouteNode.setRightChild(curr);
						newRouteNode.setLeftChild(newLeaf);
					}
				}
				validData.getAndIncrement();
				successInsert.getAndIncrement();
//				System.out.println("insert "+itemKey+" S"+this.validData);
				return true;							
			}			
		}
		finally
		{
			lock.unlock();
		}		
	}

	@Override
	public boolean delete(Integer item) 
	{
		lock.lock();
		Integer itemKey = item.hashCode();
		Node predD = new ThreadLocalNode().initialValue();	
		Node currD = predD.getLeftChild();
				
		try
		{
			while(!currD.isData())
			{
				if(currD != null && currD.getkey() < itemKey)				//searching: if the itemKey is smaller, search left, until curr approach real data or null		
				{
					predD = currD;						
					currD = currD.getRightChild();					
				}
				else if(currD.getkey() >= itemKey)		//searching: if the itemKey is smaller, search right, until curr approach real data	or null		
				{							
					predD = currD;
					currD = currD.getLeftChild();						
				}				
			}
			
			if(!currD.getkey().equals(itemKey))	//duplicate founded, failure.
			{
//				System.out.println("Delete "+itemKey+" F");				
				return false;
			}
			else
			{				
				if(predD.getParent().getkey()>= predD.getkey())
				{
					if( currD.getkey() <= predD.getkey())
					{									
						predD.getParent().setLeftChild(predD.getRightChild());
					}
					else
					{
						predD.getParent().setLeftChild(predD.getLeftChild());
					}														
				}								
				else if(predD.getParent().getkey()< predD.getkey())
				{
					if( currD.getkey() <= predD.getkey())
					{									
						predD.getParent().setRightChild(predD.getRightChild());
					}
					else
					{
						predD.getParent().setRightChild(predD.getLeftChild());
					}	
				}
				validData.getAndDecrement();
				successDelete.getAndIncrement();
//				System.out.println("Delete "+itemKey+" S "+this.validData);
				return true;
			}
		}
		finally
		{
			lock.unlock();
		}		
	}

	@Override
	public boolean search(Integer item) 
	{
		lock.lock();
		Integer itemKey = item.hashCode();		
		Node pred = new ThreadLocalNode().get();	
		Node curr = pred.getLeftChild();	
		
		try
		{
			while(!curr.isData())
			{
				if(curr.getkey() < itemKey)						
				{
					pred = curr;						
					curr = curr.getRightChild();					
				}
				else if(curr.getkey() >= itemKey)			
				{							
					pred = curr;
					curr = curr.getLeftChild();						
				}				
			}			
//			System.out.println("search "+ itemKey+" "+(curr.getkey().equals(itemKey)));
			return curr.getkey().equals(itemKey);
			
		}
		finally
		{
			lock.unlock();
		}
	}
	
	@Override
	public Node getRoot()
	{
		return sNodeRoot;		
	}	
	
	private class ThreadLocalNode extends ThreadLocal<Node>
	{
		protected synchronized Node initialValue()	
		{
      		return sNodeRoot;
    	}		
	}
}