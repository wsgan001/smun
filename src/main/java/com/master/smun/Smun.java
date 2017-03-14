package com.master.smun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.master.prepost.MemoryLogger;

/**
 * 
 * @author bangbv
 *
 */
public class Smun {
	// the start time and end time of the last algorithm execution
	long startTimestamp;
	long endTimestamp;
	
	int outputCount; 
	// object to write the output file
	BufferedWriter writer = null;
	
	public int[][] bf;
	public int bf_cursor;
	public int bf_col;
	public int bf_size;
	public int bf_currentSize;
	
	public int minSupport;
	public int numOfFItem;
	public int[] itemsetCount;
	public Item[] item;

	// public FILE out;
	public int[] result; // the current itemset
	public int resultLen; // the size of the current itemset
	public int resultCount;
	public int nlLenSum; // node list length of the current itemset
	
	private int numOfTrans;

	static Comparator<Item> comp = new Comparator<Item>() {

		@Override
		public int compare(Item o1, Item o2) {
			return o2.num - o1.num;
		}
	};

	// Tree stuff
	public PPCTreeNode ppcRoot;
	public NodeListTreeNode nlRoot;
	public PPCTreeNode[] headerTable;
	public int[] headerTableLen;
	public int[] sameItems;
	public int nlNodeCount;
	
	// if this parameter is set to true, the PrePost+ algorithm is run instead of PrePost
	// (both are implemented in this file, because they have similarities)
	public boolean usePrePostPlus = false;
	/**
	 * Use this method to indicate that you want to use the PrePost+ algorithm
	 * instead of PrePost.
	 * @param usePrePostPlus if true, PrePost+ will be run instead of PrePost when executing the method runAlgorithm()
	 */
	public void setUsePrePostPlus(boolean usePrePostPlus) {
		this.usePrePostPlus = usePrePostPlus;
	}
	
	void getData(String fileName, double support) throws IOException {
		numOfTrans = 0;
		Map<Integer, Integer> mapItemCount = new HashMap<>();

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		System.out.println("Transaction DB:");
		// each line (transaction) until the end of the file
		while ((line = br.readLine()) != null) {
			// if the line is a comment, is empty or is a kind of metadata
			if (line.isEmpty() || line.charAt(0) == '#' || line.charAt(0) == '%' || line.charAt(0) == '@') {
				continue;
			}
			System.out.println(line);
			numOfTrans++;

			// split the line into items
			String[] lineSplited = line.split(" ");
			// for each item in the transaction
			for (String itemString : lineSplited) {
				// increase the support count of the item by 1
				Integer item = Integer.parseInt(itemString);
				Integer count = mapItemCount.get(item);
				if (count == null) {
					mapItemCount.put(item, 1);
				} else {
					mapItemCount.put(item, ++count);
				}
			}
		}
		// close the input file
		br.close();
		System.out.println("mapItemCount:" + mapItemCount);

		minSupport = (int) Math.ceil(support * numOfTrans);
		System.out.println("minSupport:" + minSupport);

		numOfFItem = mapItemCount.size();
		System.out.println("num of Item:" + numOfFItem);

		Item[] tempItem = new Item[numOfFItem];
		int i = 0;
		for (Entry<Integer, Integer> entry : mapItemCount.entrySet()) {
			if (entry.getValue() >= minSupport) {
				tempItem[i] = new Item();
				tempItem[i].index = entry.getKey();
				tempItem[i].num = entry.getValue();
				i++;
			}
		}

		item = new Item[i];
		System.arraycopy(tempItem, 0, item, 0, i);

		numOfFItem = item.length;

		Arrays.sort(item, comp);
		printArray(item);
	}

	void printArray(Object[] o) {
		System.out.print("F-LIST:");
		for (Object object : o) {
			System.out.print(object.toString() + "|");
		}
		System.out.println();
	}

	void buildTree(String fileName) throws IOException {
		ppcRoot.label = -1;
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;

		Item[] transaction = new Item[1000];
		System.out.println("Transaction after soft:");
		// for each transaction 
		while ((line = br.readLine()) != null) {
			if (line.isEmpty() || line.charAt(0) == '#' || line.charAt(0) == '%' || line.charAt(0) == '@') {
				continue;
			}
			String[] lineSplited = line.split(" ");
			// for each item in the transaction
			int tLen = 0; // transaction length
			for (String itemString : lineSplited) {
				int itemX = Integer.parseInt(itemString);
				// add each item to the transaction except infrequent item
				for (int j = 0; j < numOfFItem; j++) {
					// if item is frequent item then add to the transaction
					if (itemX == item[j].index) {
						transaction[tLen] = new Item();
						transaction[tLen].index = itemX;
						// 
						transaction[tLen].num = 0 - j;
						tLen++;
						break;
					}
				}
			}

			Arrays.sort(transaction, 0, tLen, comp);
			// Print the transaction
			for (int j = 0; j < tLen; j++) {
				System.out.print(" " + transaction[j].index + " ");
			}
			System.out.println();
			int curPos = 0;
			PPCTreeNode curRoot = (ppcRoot);
			PPCTreeNode rightSibling = null;
			while (curPos != tLen) {
				PPCTreeNode child = curRoot.firstChild;
				while (child != null) {
					// travel left node
					if (child.label == 0 - transaction[curPos].num) {
						curPos++;
						child.count++;
						curRoot = child;
						break;
					}
					// travel right node
					// if not exist then create right node
					if(child.rightSibling == null){
						rightSibling = child;
						child = null;
						break;
					}
					child =  child.rightSibling;
				}
				// condition to break
				if(child == null){
					break;
				}				
			}
			
			// for the first time
			for (int j = curPos; j < tLen; j++) {
				PPCTreeNode ppcNode = new PPCTreeNode();
				ppcNode.label = 0 - transaction[j].num;
				if (rightSibling != null) {
					rightSibling.rightSibling = ppcNode;
					rightSibling = null;
				} else {
					curRoot.firstChild = ppcNode;
				}
				ppcNode.rightSibling = null;
				ppcNode.firstChild = null;
				ppcNode.father = curRoot;
				ppcNode.labelSibling = null;
				ppcNode.count = 1;
				curRoot = ppcNode;
			}
		}
		
		// reader close
		br.close();
		// create a header table
		headerTable = new PPCTreeNode[numOfFItem];
		headerTableLen = new int[numOfFItem];
		
		// save node of 
		PPCTreeNode[] tempHead = new PPCTreeNode[numOfFItem];
		// ???? 		
		itemsetCount = new int[(numOfFItem-1)*numOfFItem/2];
		
		PPCTreeNode root = ppcRoot.firstChild;
		int pre = 0;
		int last = 0;
		// travel all node of tree
		while(root != null){
			root.foreIndex = pre;
			pre++;
			
			// ???
			System.out.println("root_lable:"+root.label);
			if(headerTable[root.label] == null){
				headerTable[root.label] = root;
				tempHead[root.label] = root;
			}else{
				tempHead[root.label].labelSibling = root;
				tempHead[root.label] = root;
			}
			headerTableLen[root.label]++;
			
			// count of node
			PPCTreeNode temp = root.father;
			while(temp.label != -1){
				itemsetCount[root.label*(root.label-1)/2+temp.label] += root.count; 
				temp = temp.father;
			}
			if(root.firstChild != null){ // travel left side
				root = root.firstChild;
			}else{
				// back visit
				root.backIndex = last;
				last++;				
				if(root.rightSibling != null){ // travel right side if exist 
					root = root.rightSibling;
				}else{ // travel revert
					root = root.father;
					while(root != null){
						// 
						root.backIndex = last;
						last++;
						if(root.rightSibling != null){
							root = root.rightSibling;
							break;
						}
						root = root.father;
					}
				}
			}
		}
	}

	void initilizeTree(){
		NodeListTreeNode lastChild = null;
		for(int t = numOfFItem-1;t>=0;t--){
			if(bf_cursor > bf_currentSize - headerTableLen[t]*3){
				bf_col++;
				bf_cursor = 0;
				bf_currentSize = 10*bf_size;
				bf[bf_col] = new int[bf_currentSize];
			}
			
			NodeListTreeNode nlNode = new NodeListTreeNode();
			nlNode.label = t;
			nlNode.support = 0;
			nlNode.NLStartinBf = bf_cursor;
			nlNode.NLLength = 0;
			nlNode.NLCol = bf_col;
			nlNode.firstChild = null;
			nlNode.next = null;
			PPCTreeNode ni = headerTable[t];
			while(ni != null){
				nlNode.support += ni.count;
				bf[bf_col][bf_cursor++] = ni.foreIndex;
				bf[bf_col][bf_cursor++] = ni.backIndex;
				bf[bf_col][bf_cursor++] = ni.count;
				nlNode.NLLength++;
				ni = ni.rightSibling;
			}
			if(nlRoot.firstChild == null){
				nlRoot.firstChild = nlNode;
				lastChild = nlNode;
			}else{
				lastChild.next = nlNode;
				lastChild = nlNode;
			}			
		}
	}
	
	void runAlgorithm(String fileName, double support, String output) throws IOException {
		outputCount = 0;
		nlNodeCount = 0;
		ppcRoot = new PPCTreeNode();
		nlRoot = new NodeListTreeNode();
		resultLen = 0;
		resultCount = 0;
		nlLenSum = 0;

		MemoryLogger.getInstance().reset();

		// create object for writing the output file
		writer = new BufferedWriter(new FileWriter(output));

		// record the start time
		startTimestamp = System.currentTimeMillis();

		bf_size = 1000000;
		bf = new int[100000][];
		bf_currentSize = bf_size * 10;
		bf[0] = new int[bf_currentSize];

		bf_cursor = 0;
		bf_col = 0;
		
		getData(fileName, support);

		resultLen = 0;
		result = new int[numOfFItem];		
		// Build tree
		buildTree(fileName);
		printTree(ppcRoot);
		nlRoot.label = numOfFItem;
		nlRoot.firstChild = null;
		nlRoot.next = null;
		
		initilizeTree();
		sameItems = new int[numOfFItem];

		int from_cursor = bf_cursor;
		int from_col = bf_col;
		int from_size = bf_currentSize;
		
		// Recursively traverse the tree
		NodeListTreeNode curNode = nlRoot.firstChild;
		NodeListTreeNode next = null;
		while(curNode != null){
			next = curNode.next;
			traverse(curNode,nlRoot,1,0);
			for (int c = bf_col; c > from_col; c--) {
				bf[c] = null;
			}
			bf_col = from_col;
			bf_cursor = from_cursor;
			bf_currentSize = from_size;			
			curNode = next;
		}
	}
	
	public void traverse(NodeListTreeNode curNode,NodeListTreeNode curRoot,int level, int sameCount){
		MemoryLogger.getInstance().checkMemory();

		// System.out.println("==== traverse(): " + curNode.label + " "+ level +
		// " " + sameCount);
		NodeListTreeNode sibling = curNode.next;
		NodeListTreeNode lastChild = null;
		while (sibling != null) {
			if (level > 1
					|| (level == 1 && itemsetCount[(curNode.label - 1)
							* curNode.label / 2 + sibling.label] >= minSupport)) {
				// tangible.RefObject<Integer> tempRef_sameCount = new
				// tangible.RefObject<Integer>(
				// sameCount);
				// int sameCountTemp = sameCount;
				IntegerByRef sameCountTemp = new IntegerByRef();
				sameCountTemp.count = sameCount;
				lastChild = iskItemSetFreq(curNode, sibling, level, lastChild,
						sameCountTemp);
				sameCount = sameCountTemp.count;

			}
			sibling = sibling.next;
		}
		resultCount += Math.pow(2.0, sameCount);
		nlLenSum += Math.pow(2.0, sameCount) * curNode.NLLength;

		result[resultLen++] = curNode.label;

		// ============= Write itemset(s) to file ===========
		//writeItemsetsToFile(curNode, sameCount);
		// ======== end of write to file

		nlNodeCount++;

		int from_cursor = bf_cursor;
		int from_col = bf_col;
		int from_size = bf_currentSize;
		NodeListTreeNode child = curNode.firstChild;
		NodeListTreeNode next = null;
		while (child != null) {
			next = child.next;
			traverse(child, curNode, level + 1, sameCount);
			for (int c = bf_col; c > from_col; c--) {
				bf[c] = null;
			}
			bf_col = from_col;
			bf_cursor = from_cursor;
			bf_currentSize = from_size;
			child = next;
		}
		resultLen--;
	}
	
	// Algorithm 5 mining frequent k-itemsets
	NodeListTreeNode iskItemSetFreq(NodeListTreeNode ni, NodeListTreeNode nj,int level,NodeListTreeNode lastChild, IntegerByRef sameCountRef){
		if (bf_cursor + ni.NLLength * 3 > bf_currentSize) {
			bf_col++;
			bf_cursor = 0;
			bf_currentSize = bf_size > ni.NLLength * 1000 ? bf_size
					: ni.NLLength * 1000;
			bf[bf_col] = new int[bf_currentSize];
		}

		NodeListTreeNode nlNode = new NodeListTreeNode();
		nlNode.support = 0;
		nlNode.NLStartinBf = bf_cursor;
		nlNode.NLCol = bf_col;
		nlNode.NLLength = 0;

		int cursor_i = ni.NLStartinBf;
		int cursor_j = nj.NLStartinBf;
		int col_i = ni.NLCol;
		int col_j = nj.NLCol;
		int last_cur = -1;
		while (cursor_i < ni.NLStartinBf + ni.NLLength * 3
				&& cursor_j < nj.NLStartinBf + nj.NLLength * 3) {
			if (bf[col_i][cursor_i] > bf[col_j][cursor_j]
					&& bf[col_i][cursor_i + 1] < bf[col_j][cursor_j + 1]) {
				if (last_cur == cursor_j) {
					bf[bf_col][bf_cursor - 1] += bf[col_i][cursor_i + 2];
				} else {
					bf[bf_col][bf_cursor++] = bf[col_j][cursor_j];
					bf[bf_col][bf_cursor++] = bf[col_j][cursor_j + 1];
					bf[bf_col][bf_cursor++] = bf[col_i][cursor_i + 2];
					nlNode.NLLength++;
				}
				nlNode.support += bf[col_i][cursor_i + 2];
				last_cur = cursor_j;
				cursor_i += 3;
			} else if (bf[col_i][cursor_i] < bf[col_j][cursor_j]) {
				cursor_i += 3;
			} else if (bf[col_i][cursor_i + 1] > bf[col_j][cursor_j + 1]) {
				cursor_j += 3;
			}
		}
		if (nlNode.support >= minSupport) {
			if (ni.support == nlNode.support && (usePrePostPlus || nlNode.NLLength == 1)) {
				sameItems[sameCountRef.count++] = nj.label;
				bf_cursor = nlNode.NLStartinBf;
				if (nlNode != null) {
						nlNode = null;
				}
			} else {
				nlNode.label = nj.label;
				nlNode.firstChild = null;
				nlNode.next = null;
				if (ni.firstChild == null) {
					ni.firstChild = nlNode;
					lastChild = nlNode;
				} else {
					lastChild.next = nlNode;
					lastChild = nlNode;
				}
			}
			return lastChild;
		} else {
			bf_cursor = nlNode.NLStartinBf;
			if (nlNode != null)
				nlNode = null;
		}
		return lastChild;
	}
	
	public void printTree(PPCTreeNode ppcRoot){
		PPCTreeNode root = ppcRoot.firstChild;
		// travel all node of tree
		while(root != null){			
			// count of node
			System.out.print(item[root.label].index+":"+root.count+"|");
			if(root.firstChild != null){ // travel left side
				root = root.firstChild;
			}else{
				// back visit			
				if(root.rightSibling != null){ // travel right side if exist 
					root = root.rightSibling;
				}else{ // travel revert
					root = root.father;
					while(root != null){
						if(root.rightSibling != null){
							root = root.rightSibling;
							break;
						}
						root = root.father;
					}
				}
			}
		}
	}
}
