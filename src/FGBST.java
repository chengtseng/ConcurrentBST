public class FGBST extends ConcurrentBinarySearchTree
{
	private volatile Node sNodeRoot;
	private volatile Node sNodeRoot_R;
	private volatile Node sNodeRoot_L;
	private volatile Node sNode1;	
	private volatile Node sNode3;
	static final int empty = 0;	
		
	FGBST()
	{
		sNodeRoot = new Node(100000000);
		sNodeRoot_R = new Node();
		sNode1 = new Node(9999998);
		sNodeRoot_L = new Node(9999999);
		sNode3 = new Node(10000000);
		sNodeRoot.setLeftChild(sNodeRoot_L);
		sNodeRoot.setRightChild(sNodeRoot_R);
		sNode1.setNodeToDataNode();
		sNode3.setNodeToDataNode();
		sNodeRoot_R.setNodeToDataNode();
		sNodeRoot_L.setLeftChild(sNode1);
		sNodeRoot_L.setRightChild(sNode3);
		invalidDataNode = 3;
	}
	
	@Override
	public boolean insert( Integer item )
	{
		Integer itemKey = item.hashCode();
		while(true)
		{
			Node pred = new ThreadLocalNode().initialValue();	
			Node curr = pred.getLeftChild();
			
			while(curr!=null && !curr.isData())
			{							
				if(curr.getkey() < itemKey)				//searching: if the itemKey is smaller, search left, until curr approach real data or null		
				{
					pred = curr;						
					curr = curr.getRightChild();					
				}
				else if(curr.getkey() >= itemKey)		//searching: if the itemKey is smaller, search right, until curr approach real data	or null		
				{							
					pred = curr;
					curr = curr.getLeftChild();						
				}								
			}
			if(curr == null )
			{				
				continue;					
			}			
			
			pred.lock.lock();
			try
			{
				curr.lock.lock();
				
				try
				{
					if(validate(curr, pred))
					{
						if(curr.getkey().equals(itemKey))	//duplicate founded, failure.
						{
//							System.out.println("insert "+itemKey+" F"+Thread.currentThread().getId());													
							return false;
						}
						else
						{
							Node newLeaf = new Node(itemKey,true);
													
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
//							System.out.println("insert "+itemKey+" S"+Thread.currentThread().getId());
							return true;							
						}
					}					
				}
				finally
				{
					curr.lock.unlock();
				}
			}
			finally
			{
				pred.lock.unlock();				
			}
		}		
	}
	
	@Override
	public boolean delete( Integer item )
	{
		Integer itemKey = item.hashCode();
		
		while(true)
		{
			Node grandPD = new ThreadLocalNode().initialValue();			
			Node predD =  grandPD.getLeftChild();	
			Node currD = predD.getLeftChild();
			
			while(currD != null && !currD.isData())
			{
				if(currD.getkey() < itemKey )				//searching: if the itemKey is smaller, search left, until curr approach real data or null		
				{
					grandPD = predD;
					predD = currD;						
					currD = currD.getRightChild();					
				}
				else if(currD.getkey() >= itemKey)			//searching: if the itemKey is smaller, search right, until curr approach real data	or null		
				{
					grandPD = predD;
					predD = currD;
					currD = currD.getLeftChild();						
				}					
			}
			if(currD == null )
			{
				continue;					
			}					

			grandPD.lock.lock(); 
			try
			{
				predD.lock.lock();
				
				try
				{					
					currD.lock.lock();
					
					try
					{	
						if(validateD(currD, predD, grandPD))
						{							
							if(!currD.getkey().equals(itemKey))	//duplicate founded, failure.
							{
//								System.out.println("Delete "+itemKey+" F "+Thread.currentThread().getId());
								return false;								
							}							
							else
							{
								predD.markForDeletion();
								currD.markForDeletion();
								
								if(grandPD.getkey()>= predD.getkey())
								{
									if( currD.getkey() <= predD.getkey())
									{									
										grandPD.setLeftChild(predD.getRightChild());
									}
									else
									{
										grandPD.setLeftChild(predD.getLeftChild());
									}														
								}								
								else if(grandPD.getkey()< predD.getkey())
								{
									if( currD.getkey() <= predD.getkey())
									{									
										grandPD.setRightChild(predD.getRightChild());										
									}
									else
									{
										grandPD.setRightChild(predD.getLeftChild());
									}	
								}
								validData.getAndDecrement();
								successDelete.getAndIncrement();
//								System.out.println("Delete "+itemKey+" S "+Thread.currentThread().getId());
								return true;						
							}
						}					
					}					
					finally
					{
						currD.lock.unlock();
					}
				}				
				finally
				{
					predD.lock.unlock();					
				}				
			}			
			finally
			{			
				grandPD.lock.unlock();			
				
			}		
		}		
	}	
	
	@Override
	public boolean search( Integer item )
	{
		Integer itemKey = item.hashCode();		
		while(true)
		{
			Node predS = new ThreadLocalNode().initialValue();	
			Node currS = predS.getLeftChild();
			while(currS!=null && !currS.isData())
			{
				if(currS.getkey() < itemKey)						
				{
					predS = currS;						
					currS = currS.getRightChild();					
				}
				else if(currS.getkey() >= itemKey)			
				{							
					predS = currS;
					currS = currS.getLeftChild();						
				}			
			}
			if(currS == null )
			{				
				continue;					
			}
			else
			{
//				System.out.println("search "+ itemKey+" "+(currS.getkey().equals(itemKey) && !currS.isMarked()));
				return currS.getkey().equals(itemKey) && !currS.isMarked();	
			}
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
	
	private Boolean validate( Node curr, Node pred)
	{
		return !(pred.isMarked() || curr.isMarked()) && curr.getParent()==pred;		
	}
	
	private Boolean validateD( Node curr, Node pred, Node grand)
	{
		return !(pred.isMarked() || curr.isMarked() || grand.isMarked()) && (curr.getParent() == pred) && (pred.getParent() == grand);		
	}	
}
