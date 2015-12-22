import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class Node
{
	private Integer key = null;
	private volatile boolean isData = false;
	private volatile boolean marked = false;
	private volatile Node rightChild=null;
	private volatile Node leftChild=null;
	private volatile Node parent=null;
	public Lock lock = new ReentrantLock();
	
	public Node()
	{
		Double inf = Double.POSITIVE_INFINITY;;
		key = inf.hashCode();		
	}
	
	public Node( Integer item )
	{
		key = item.hashCode();		
	}
	
	public Node( Integer item, boolean isData )
	{
		key = item.hashCode();
		this.isData = isData;
	}
	
	public void setNodeToDataNode()
	{
		this.isData = true;		
	}	
	
	public void markForDeletion()
	{
		marked = true;		
	}
	
	public Integer getkey()
	{
		return this.key;		
	}	
	
	public Node getLeftChild()
	{
		return leftChild;		
	}	
	
	public Node getRightChild()
	{
		return rightChild;		
	}
	
	public void setLeftChild(Node node)
	{
		this.leftChild = node;
		node.setParent(this);
	}	
	
	public void setRightChild(Node node)
	{
		this.rightChild = node;
		node.setParent(this);
	}
	
	public boolean isData()
	{
		return isData;		
	}
	
	public Boolean isMarked()
	{
		return marked;		
	}
	
	public Node getParent()
	{
		return parent;
	}
	
	public void setParent(Node node)
	{
		this.parent = node;
	}	
}
