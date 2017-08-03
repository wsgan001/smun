package com.master.smun_v2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.master.prepost.MemoryLogger;

public class SMUN {
	public boolean usePrePostPlus = false;	
	long startTimestamp,endTimestamp;
	int outputCount;
	BufferedWriter writer = null;
	public int[][] bf;
	public int bf_cursor;
	public int bf_size;
	public int bf_col;
	public int bf_currentSize;

	public int numOfFItem; // Number of items
	public int minSupport; // minimum support
	public Item[] item; // list of items sorted by support

	// public FILE out;
	public int[] result; // the current itemset
	public int resultLen; // the size of the current itemset
	public int resultCount;
	public int nlLenSum; // node list length of the current itemset

	// Tree stuff
	public PPCTreeNode ppcRoot;
	public NodeListTreeNode nlRoot;
	public PPCTreeNode[] headTable;
	public int[] headTableLen;
	public int[] itemsetCount;
	public int[] sameItems;
	public int nlNodeCount;

	/**
	 * Comparator to sort items by decreasing order of frequency
	 */
	static Comparator<Item> comp = new Comparator<Item>() {
		public int compare(Item a, Item b) {
			return ((Item) b).count - ((Item) a).count;
		}
	};
	private int numOfSequences;
	
	public void runAlgorithm(String filename, double minsup, String output) throws IOException {
		outputCount = 0;
		nlNodeCount = 0;
		ppcRoot = new PPCTreeNode();
		nlRoot = new NodeListTreeNode();
		resultLen = 0;
		resultCount = 0;
		nlLenSum = 0;
		MemoryLogger.getInstance().reset();
		writer = new BufferedWriter(new FileWriter(output));
		startTimestamp = System.currentTimeMillis();
		bf_size = 1000000;
		bf = new int[100000][];
		bf_currentSize = bf_size * 10;
		bf[0] = new int[bf_currentSize];
		bf_cursor = 0;
		bf_col = 0;
		getData(filename, minsup);
		resultLen = 0;
		result = new int[numOfFItem];
		buildTree(filename);
		nlRoot.label = numOfFItem;
		nlRoot.firstChild = null;
		nlRoot.next = null;
		// create N-list of 1-itemset
		initializeTree();
		sameItems = new int[numOfFItem];
		int from_cursor = bf_cursor;
		int from_col = bf_col;
		int from_size = bf_currentSize;
		// Recursively traverse the tree
		NodeListTreeNode curNode = nlRoot.firstChild;
		NodeListTreeNode next = null;
		while (curNode != null) {
			next = curNode.next;
			// call the recursive "traverse" method
			traverse(curNode, nlRoot, 1, 0);
			for (int c = bf_col; c > from_col; c--) {
				bf[c] = null;
			}
			bf_col = from_col;
			bf_cursor = from_cursor;
			bf_currentSize = from_size;
			curNode = next;
		}
		writer.close();
		MemoryLogger.getInstance().checkMemory();
		endTimestamp = System.currentTimeMillis();		
	}

	void getData(String filename, double support) throws IOException {
		numOfSequences = 0;
		Map<Integer, Integer> mapItemCount = new HashMap<Integer, Integer>();		
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;
		while (((line = reader.readLine()) != null)) {
			if (line.isEmpty() == true || line.charAt(0) == '#' || line.charAt(0) == '%' || line.charAt(0) == '@') {
				continue;
			}
			numOfSequences++;
			List<Integer> itemOfSequences = new ArrayList<Integer>();
			String[] lineSplited = line.split(" ");
			for (String itemString : lineSplited) {
				Integer item = Integer.parseInt(itemString);
				if (item.equals(-1)) {

				} else if (item.equals(-2)) {
					for (Integer itemOfSequence : itemOfSequences) {
						Integer count = mapItemCount.get(itemOfSequence);
						if (count == null) {
							mapItemCount.put(itemOfSequence, 1);
						} else {
							mapItemCount.put(itemOfSequence, ++count);
						}
					}
				} else {
					if(!itemOfSequences.contains(item)){
						itemOfSequences.add(item);
					}
				}
			}

		}
		reader.close();
		minSupport = (int) Math.ceil(support * numOfSequences);
		numOfFItem = mapItemCount.size();
		Item[] tempItems = new Item[numOfFItem];
		int i = 0;
		for (Entry<Integer, Integer> entry : mapItemCount.entrySet()) {
			if (entry.getValue() >= minSupport) {
				tempItems[i] = new Item();
				tempItems[i].id = entry.getKey();
				tempItems[i].count = entry.getValue();
				i++;
			}
		}
		item = new Item[i];
		System.arraycopy(tempItems, 0, item, 0, i);
		numOfFItem = item.length;
		Arrays.sort(item, comp);
		Util.printArray(item);
	}

	void buildTree(String filename) throws IOException {
		ppcRoot.label = -1;
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;
		Item[] sequence = new Item[1000];
		int sequenceId = 0;
		while (((line = reader.readLine()) != null)) {
			if (line.isEmpty() == true || line.charAt(0) == '#' || line.charAt(0) == '%' || line.charAt(0) == '@') {
				continue;
			}
			System.out.println(line);
			String[] lineSplited = line.split(" ");
			int tLen = 0;
			for (String itemString : lineSplited) {
				int itemX = Integer.parseInt(itemString);
				for (int j = 0; j < numOfFItem; j++) {
					if (itemX == item[j].id) {
						sequence[tLen] = new Item();
						sequence[tLen].id = itemX; // the item
						sequence[tLen].count = 0 - j;
						tLen++;
						break;
					}
				}
			}
			Arrays.sort(sequence, 0, tLen, comp);
			Util.printArray(sequence);			
			int curPos = 0;
			PPCTreeNode curRoot = (ppcRoot);
			PPCTreeNode rightSibling = null;
			while (curPos != tLen) {
				PPCTreeNode child = curRoot.firstChild;
				while (child != null) {
					if (child.label == 0 - sequence[curPos].count) {
						curPos++;
						//child.count++;
						child.sequenceId.add(sequenceId);
						curRoot = child;
						break;
					}
					if (child.rightSibling == null) {
						rightSibling = child;
						child = null;
						break;
					}
					child = child.rightSibling;
				}
				if (child == null)
					break;
			}
			for (int j = curPos; j < tLen; j++) {
				PPCTreeNode ppcNode = new PPCTreeNode();
				ppcNode.label = 0 - sequence[j].count;
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
				ppcNode.sequenceId = new HashSet<Integer>();
				ppcNode.sequenceId.add(sequenceId);
				curRoot = ppcNode;
			}
			sequenceId++;
		}
		reader.close();
		// Create a header table
		headTable = new PPCTreeNode[numOfFItem];
		headTableLen = new int[numOfFItem];
		PPCTreeNode[] tempHead = new PPCTreeNode[numOfFItem];
		itemsetCount = new int[(numOfFItem - 1) * numOfFItem / 2];
		PPCTreeNode root = ppcRoot.firstChild;
		int pre = 0;
		int last = 0;
		while (root != null) {
			root.foreIndex = pre;
			pre++;
			if(pre == 7){
				System.out.println("pre:"+pre);
			}
			if (headTable[root.label] == null) {
				headTable[root.label] = root;
				tempHead[root.label] = root;
			} else {
				tempHead[root.label].labelSibling = root;
				tempHead[root.label] = root;
			}
			headTableLen[root.label]++;

			PPCTreeNode temp = root.father;
			while (temp.label != -1) {
				int x = root.label * (root.label - 1) / 2 + temp.label;
				System.out.print(x+":");
				if(x == 3){
					System.out.println(x);
				}
				//itemsetCount[root.label * (root.label - 1) / 2 + temp.label] += root.count;
				itemsetCount[temp.label] += root.sequenceId.size();
				temp = temp.father;
			}
			System.out.println();
			if (root.firstChild != null) {
				root = root.firstChild;
			} else {
				// back visit
				root.backIndex = last;
				last++;
				if (root.rightSibling != null) {
					root = root.rightSibling;
				} else {
					root = root.father;
					while (root != null) {
						// back visit
						root.backIndex = last;
						last++;
						if (root.rightSibling != null) {
							root = root.rightSibling;
							break;
						}
						root = root.father;
					}
				}
			}
		}
	}
	// construct the N-list of each frequent 1-itemset
	void initializeTree() {
		NodeListTreeNode lastChild = null;
		for (int t = numOfFItem - 1; t >= 0; t--) {
			// check buffer size
			if (bf_cursor > bf_currentSize - headTableLen[t] * 3) {
				bf_col++;
				bf_cursor = 0;
				bf_currentSize = 10 * bf_size;
				bf[bf_col] = new int[bf_currentSize];
			}

			NodeListTreeNode nlNode = new NodeListTreeNode();
			nlNode.label = t;
			//nlNode.support = 0;
			nlNode.NLStartinBf = bf_cursor;
			nlNode.NLLength = 0;
			nlNode.NLCol = bf_col;
			nlNode.firstChild = null;
			nlNode.next = null;
			nlNode.sequenceId = new HashSet<Integer>();
			PPCTreeNode ni = headTable[t];
			while (ni != null) {
				//nlNode.support += ni.count;
				nlNode.sequenceId.addAll(ni.sequenceId);
				bf[bf_col][bf_cursor++] = ni.foreIndex;
				bf[bf_col][bf_cursor++] = ni.backIndex;
				//bf[bf_col][bf_cursor++] = ni.count;
				bf[bf_col][bf_cursor++] = -1;
				nlNode.NLLength++;
				ni = ni.labelSibling;
			}
			if (nlRoot.firstChild == null) {
				nlRoot.firstChild = nlNode;
				lastChild = nlNode;
			} else {
				lastChild.next = nlNode;
				lastChild = nlNode;
			}
		}
	}

	NodeListTreeNode iskItemSetFreq(NodeListTreeNode ni, NodeListTreeNode nj, int level, NodeListTreeNode lastChild,
			IntegerByRef sameCountRef) {

		if (bf_cursor + ni.NLLength * 3 > bf_currentSize) {
			bf_col++;
			bf_cursor = 0;
			bf_currentSize = bf_size > ni.NLLength * 1000 ? bf_size : ni.NLLength * 1000;
			bf[bf_col] = new int[bf_currentSize];
		}

		NodeListTreeNode nlNode = new NodeListTreeNode();
		//nlNode.support = 0;
		nlNode.NLStartinBf = bf_cursor;
		nlNode.NLCol = bf_col;
		nlNode.NLLength = 0;
		nlNode.sequenceId = new HashSet<Integer>();
		
		int cursor_i = ni.NLStartinBf;
		int cursor_j = nj.NLStartinBf;
		int col_i = ni.NLCol;
		int col_j = nj.NLCol;
		int last_cur = -1;
		while (cursor_i < ni.NLStartinBf + ni.NLLength * 3 && cursor_j < nj.NLStartinBf + nj.NLLength * 3) {
			if (bf[col_i][cursor_i] > bf[col_j][cursor_j] && bf[col_i][cursor_i + 1] < bf[col_j][cursor_j + 1]) {
				if (last_cur == cursor_j) {
					bf[bf_col][bf_cursor - 1] += bf[col_i][cursor_i + 2];
				} else {
					bf[bf_col][bf_cursor++] = bf[col_j][cursor_j];
					bf[bf_col][bf_cursor++] = bf[col_j][cursor_j + 1];
					bf[bf_col][bf_cursor++] = bf[col_i][cursor_i + 2];
					nlNode.NLLength++;
				}
				//nlNode.support += bf[col_i][cursor_i + 2];
				
				for (int si : ni.sequenceId) {
					if(nj.sequenceId.contains(si)){
						nlNode.sequenceId.add(si);
					}
				}
				last_cur = cursor_j;
				cursor_i += 3;
			} else if (bf[col_i][cursor_i] < bf[col_j][cursor_j]) {
				cursor_i += 3;
			} else if (bf[col_i][cursor_i + 1] > bf[col_j][cursor_j + 1]) {
				cursor_j += 3;
			}
		}
		if (nlNode.sequenceId.size() >= minSupport) {
			if (ni.sequenceId.size() == nlNode.sequenceId.size() && (usePrePostPlus || nlNode.NLLength == 1)) {
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

	public void traverse(NodeListTreeNode curNode, NodeListTreeNode curRoot, int level, int sameCount)
			throws IOException {
		MemoryLogger.getInstance().checkMemory();
		NodeListTreeNode sibling = curNode.next;
		NodeListTreeNode lastChild = null;
		while (sibling != null) {
			if (level > 1 || (level == 1
					&& itemsetCount[(curNode.label - 1) * curNode.label / 2 + sibling.label] >= minSupport)) {
				IntegerByRef sameCountTemp = new IntegerByRef();
				sameCountTemp.count = sameCount;
				lastChild = iskItemSetFreq(curNode, sibling, level, lastChild, sameCountTemp);
				sameCount = sameCountTemp.count;

			}
			sibling = sibling.next;
		}
		resultCount += Math.pow(2.0, sameCount);
		nlLenSum += Math.pow(2.0, sameCount) * curNode.NLLength;
		result[resultLen++] = curNode.label;
		// ============= Write itemset(s) to file ===========
		writeItemsetsToFile(curNode, sameCount);
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

	private void writeItemsetsToFile(NodeListTreeNode curNode, int sameCount) throws IOException {
		System.out.println("print...");
		StringBuilder buffer = new StringBuilder();
		if (curNode.sequenceId.size() >= minSupport) {
			outputCount++;
			// append items from the itemset to the StringBuilder
			for (int i = 0; i < resultLen; i++) {
				buffer.append(item[result[i]].id);
				buffer.append(' ');
				
				System.out.print(item[result[i]].id);
				System.out.print(' ');				
			}
			// append the support of the itemset
			buffer.append("#SUP: ");
			buffer.append(curNode.sequenceId.size());
			buffer.append("\n");
			
			System.out.print("#SUP: ");
			System.out.print(curNode.sequenceId.size());
			System.out.print("\n");			
		}
		// === Write all combination that can be made using the node list of
		// this itemset
		if (sameCount > 0) {
			// generate all subsets of the node list except the empty set
			for (long i = 1, max = 1 << sameCount; i < max; i++) {
				for (int k = 0; k < resultLen; k++) {
					buffer.append(item[result[k]].id);
					buffer.append(' ');
					
					System.out.print(item[result[k]].id);
					System.out.print(' ');					
				}
				// we create a new subset
				for (int j = 0; j < sameCount; j++) {
					// check if the j bit is set to 1
					int isSet = (int) i & (1 << j);
					if (isSet > 0) {
						// if yes, add it to the set
						buffer.append(item[sameItems[j]].id);
						buffer.append(' ');
						// newSet.add(item[sameItems[j]].index);
						System.out.print(item[sameItems[j]].id);
						System.out.print(' ');						
					}
				}
				buffer.append("#SUP: ");
				buffer.append(curNode.sequenceId.size());
				buffer.append("\n");
				outputCount++;
				
				System.out.print("#SUP: ");
				System.out.print(curNode.sequenceId.size());
				System.out.print("\n");				
			}
		}
		// write the strinbuffer to file and create a new line
		// so that we are ready for writing the next itemset.
		writer.write(buffer.toString());
	}
	/**
	 * Print statistics about the latest execution of the algorithm to
	 */
	public void printStats() {
		String smun = "SMUN";
		System.out.println("========== " + smun + " - STATS ============");
		System.out.println(" Minsup = " + minSupport + "\n Number of sequences: " + numOfSequences);
		System.out.println(" Number of frequent sequences: " + outputCount);
		System.out.println(" Total time ~: " + (endTimestamp - startTimestamp) + " ms");
		System.out.println(" Max memory:" + MemoryLogger.getInstance().getMaxMemory() + " MB");
		System.out.println("============================================");
	}
}
