# ConcurrentBST
N-threads lock-base Concurrent Binary Search Tree
information of compile source code, run the program.
strategy I use for testing


FG/CG BST(external representation):
structure: binary search tree order: right child greater then parnet node, left child smaller or equal to parant node. 
invariants:an internal node must has two children
	   data node has no child
	   leaves' key value increases from left to right	 

Instruction:
The program may take up to 9 system parameters:
	args[0]: treeType: fine grain BST or coarse grain BST. FG: fine grain and CG: coarse grain
	args[1]: threadNumber: number of threads in the task.
	args[2]: key: key space. Max size of the list.
	args[3]: sOperation: percentage of search operations in decimal digit.
	args[4]: iOperation: percentage of insert operations in decimal digit.
	args[5]: dOperation: percentage of delete operations in decimal digit.	
	args[6]: rounds: run how many tasks for this set up. Default is 1.
	args[7]: sentinelCheck: boolean, enter T for true, F for false. True if user wants to turn on the half way sentinel check, false otherwise. Default is false.
	args[8]: printOnOff: boolean,  enter T for true, F for false. True if user want to see the whole BST in preorder traversal, false otherwise. Default is false.

Note: 
	(1)
	args[2]: The max key space is 1000000, min is 1.
      
	(2)
	I stronly suggest that don't turn on printOnOff when having large key space and threadNumber. I/O operations might crash the program since tree will be tramendusly large.
	It is mainly used for debuging and check the invariants, so low key space and resonable threadNumber would work the best.
	Please note that each thread would execute no less than one million operations. There might be some subtle deviation since we distribute i million operations 
	into search, insert and delete, but the program guarantee each thread execute no less than 1 million operations. High thread number 	

	(3)
	sOperation + dOperation + iOperation = 1
      
	(4)
	Turn on sentinelCheck might effect to the performance. Set sentinelCheck to true for debug purpose, otherwise please turn off. 

	(5)
	If the "args[6]: rounds" is specified and the value is not 1, the average throughput would show at the end when all tests complete.

Compile and run:
Extract the zip. open terminal under current folder.
1)
javac src/*.java				//compile src code

2)
java -cp src TestAlgorithm FG 2 1000 0 0.5 0.5    	//run 1 test for this set up fine grain, 2 threads, key space 1000, 0% search, 50% inset, 50% delete

3)
java -cp src TestAlgorithm FG 2 1000 0 0.5 0.5 3  	//run 3 tests for this set up

4)
java -cp src TestAlgorithm FG 2 1000 0 0.5 0.5 1 T	//run 1 tests for this set up and show halfway sentinel check

5)
java -cp src TestAlgorithm FG 4 100 0 0.5 0.5 1 T T	//run 1 tests for this set up, show halfway sentinel check and print the tree in preorder
