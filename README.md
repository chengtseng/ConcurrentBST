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

 ____________________________
|Strategy I used for testing:|_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _

below is the out put of (5), the sentinel check and print tree are turned on. Please read the side node after //.



 
##############################----original state: approximately half full tree complete, start from here-----########################################	//we start from half full tree, before this line we do insertion, after this line the task start.
--------------------------------------------------------------sentinel check point---FG--------------------------------------------------------------	//the halfway sentinel check point
Number of threads: 4, Key space: 10.
Original valid data in the tree: 5
Each thread Operations: search: 0 insert: 500000 delete: 500000
Success Insertion: 527280																//record successful insertion and deletion.
Success Deletion: 527280
print tree preorder: (in)100000000 (in)9999999 (in)10 (in)6 (in)1 (ex)1 (ex)2 (in)8 (ex)7 (in)9 (ex)9 (ex)10 (ex)9999998 (ex)10000000 (ex)2146435072 	//print the while tree in preorder.(in) implies internal nodes. (ex) implies external nodes (leaves). 
Internal nodes has two children, leaf(Data) nodes has no children: true											//check the invariant that each internal node has two children and no child for any external node
Valid data after operation should be( original valid data in tree + successful insertion - successful deletion ): 5					//expectation base on successful insertion and deletion.
print leaves preorder: 																	//show how many valid data (leaves) in the tree. 
1 2 7 9 10 9999998 10000000 2146435072															//all the leaves including sentinel nodes, which are leaves node but not valid data.For CG two sentinel nodes: keys: 9999998,2146435072; FG two sentinel nodes: keys: three sentinel 9999998 10000000 2146435072
data leaves: 5																																											
Total time: 2680																																					
##############################--------------------------------FINAL CHECK POINT------------------------------########################################	//final check point
--------------------------------------------------------------sentinel check point---FG--------------------------------------------------------------					
Number of threads: 4, Key space: 10.
Original valid data in the tree: 5
Each thread Operations: search: 0 insert: 500000 delete: 500000
Success Insertion: 1054142
Success Deletion: 1054144
print tree preorder: (in)100000000 (in)9999999 (in)10 (in)3 (ex)1 (in)6 (ex)6 (ex)9 (ex)9999998 (ex)10000000 (ex)2146435072 
Internal nodes has two children, leaf(Data) nodes has no children: true
Valid data after operation should be( original valid data in tree + successful insertion - successful deletion ): 3
print leaves preorder: 
1 6 9 9999998 10000000 2146435072															//leaves 1, 6, 9 are final valid data in the tree 
data leaves: 3
Total time: 4427																	//the task end. If user run more then one round, the extra average throughput would be shown 
Throughput:2436.05359317905






 ________
|Insight:|_ _ _ _ _ _ _　_ _ _ _ _ _　_ _ _ _ _ _　_ _ _ _ _ _　_ _ _ _ _ _　_ _ _ _ _ _　_ _ _ _ _ _　_ _ _ _ _ _　_ _ _ _ _ _ _ _ _　_ _ _ _ _ _ _ _ _　

Define:
data leaves is yield by running preorder traversal on the tree, whould be the actual valid data leaves in the tree. 
Success Insertion and Deletion : accumulate successful insertions and deletion times.

Checking strategy
1)After each deletion and insertion, printing success or failure messages showing that whether the histroy are linearizable.  
2)I check "data leaves" counts with the print out tree.
3)Valid data after operation should be equal to "data leaves" The assumption of valid data is base on arithmatic on successful insertion for building 
  half full tree and the following successful insertions and successful deletions.
	
	data leaves should be equal to original valid data(building half full tree) + successful insertions - successful deletions.

4)Internal nodes has two children, leaf(Data) nodes has no children must be true. No operation could break these invariant.
5)The program printing tree and printing leaves are both done by preorder traversal, so if the program prints leaves then the leaves' keys should be in increasing order. 


another example:
##############################----original state: approximately half full tree complete, start from here-----########################################
##############################--------------------------------FINAL CHECK POINT------------------------------########################################
--------------------------------------------------------------sentinel check point---FG--------------------------------------------------------------
Number of threads: 32, Key space: 100.
Original valid data in the tree: 37
Each thread Operations: search: 200000 insert: 400000 delete: 400000
Success Insertion: 6441115
Success Deletion: 6441098
print tree preorder: (in)100000000 (in)9999999 (in)93 (in)32 (in)17 (in)6 (in)3 (ex)3 (ex)5 (in)9 (in)8 (ex)7 (ex)9 (in)11 (ex)11 (in)14 (ex)14 (ex)15 (in)21 (ex)19 (in)28 (in)25 (in)23 (ex)23 (ex)24 (ex)26 (ex)30 (in)52 (in)39 (in)38 (in)34 (in)33 (ex)33 (ex)34 (in)36 (ex)36 (ex)37 (ex)39 (in)46 (in)43 (in)40 (ex)40 (in)41 (ex)41 (ex)43 (in)44 (ex)44 (ex)46 (in)48 (in)47 (ex)47 (ex)48 (in)50 (ex)50 (ex)51 (in)72 (in)59 (in)55 (in)54 (ex)54 (ex)55 (in)56 (ex)56 (in)58 (ex)58 (ex)59 (in)64 (ex)64 (in)67 (in)65 (ex)65 (in)66 (ex)66 (ex)67 (in)68 (ex)68 (in)69 (ex)69 (in)70 (ex)70 (in)71 (ex)71 (ex)72 (in)80 (in)79 (in)73 (ex)73 (in)75 (ex)75 (ex)77 (ex)80 (in)85 (in)81 (ex)81 (ex)85 (in)88 (in)87 (in)86 (ex)86 (ex)87 (ex)88 (in)89 (ex)89 (in)91 (ex)91 (ex)92 (in)100 (in)96 (ex)96 (ex)98 (ex)9999998 (ex)10000000 (ex)2146435072 
Internal nodes has two children, leaf(Data) nodes has no children: true
Valid data after operation should be( original valid data in tree + successful insertion - successful deletion ): 54
print leaves preorder: 
3 5 7 9 11 14 15 19 23 24 26 30 33 34 36 37 39 40 41 43 44 46 47 48 50 51 54 55 56 58 59 64 65 66 67 68 69 70 71 72 73 75 77 80 81 85 86 87 88 89 91 92 96 98 9999998 10000000 2146435072 
data leaves: 54
Total time: 5308
Throughput:6035.458317615994


Base on these facts, I validate my tree implementation is thread safe.
