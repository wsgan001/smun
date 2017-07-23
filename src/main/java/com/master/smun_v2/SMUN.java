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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SMUN {
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
						child.count++;
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
				ppcNode.count = 1;
				curRoot = ppcNode;
			}
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
				System.out.println(root.label * (root.label - 1) / 2 + temp.label);
				itemsetCount[root.label * (root.label - 1) / 2 + temp.label] += root.count;
				temp = temp.father;
			}
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

	void initializeTree() {
		NodeListTreeNode lastChild = null;
		for (int t = numOfFItem - 1; t >= 0; t--) {
			if (bf_cursor > bf_currentSize - headTableLen[t] * 3) {
				bf_col++;
				bf_cursor = 0;
				bf_currentSize = 10 * bf_size;
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
			PPCTreeNode ni = headTable[t];
			while (ni != null) {
				nlNode.support += ni.count;
				bf[bf_col][bf_cursor++] = ni.foreIndex;
				bf[bf_col][bf_cursor++] = ni.backIndex;
				bf[bf_col][bf_cursor++] = ni.count;
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
		nlNode.support = 0;
		nlNode.NLStartinBf = bf_cursor;
		nlNode.NLCol = bf_col;
		nlNode.NLLength = 0;

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
			if (ni.support == nlNode.support && (nlNode.NLLength == 1)) {
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

		// System.out.println("==== traverse(): " + curNode.label + " "+ level +
		// " " + sameCount);
		NodeListTreeNode sibling = curNode.next;
		NodeListTreeNode lastChild = null;
		while (sibling != null) {
			if (level > 1 || (level == 1
					&& itemsetCount[(curNode.label - 1) * curNode.label / 2 + sibling.label] >= minSupport)) {
				// tangible.RefObject<Integer> tempRef_sameCount = new
				// tangible.RefObject<Integer>(
				// sameCount);
				// int sameCountTemp = sameCount;
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

		// create a stringuffer
		StringBuilder buffer = new StringBuilder();

		if (curNode.support >= minSupport) {
			outputCount++;

			// append items from the itemset to the StringBuilder
			for (int i = 0; i < resultLen; i++) {
				buffer.append(item[result[i]].id);
				buffer.append(' ');
			}
			// append the support of the itemset
			buffer.append("#SUP: ");
			buffer.append(curNode.support);
			buffer.append("\n");
		}
		// === Write all combination that can be made using the node list of
		// this itemset
		if (sameCount > 0) {
			// generate all subsets of the node list except the empty set
			for (long i = 1, max = 1 << sameCount; i < max; i++) {
				for (int k = 0; k < resultLen; k++) {
					buffer.append(item[result[k]].id);
					buffer.append(' ');
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
					}
				}
				buffer.append("#SUP: ");
				buffer.append(curNode.support);
				buffer.append("\n");
				outputCount++;
			}
		}
		// write the strinbuffer to file and create a new line
		// so that we are ready for writing the next itemset.
		writer.write(buffer.toString());
	}

	/**
	 * Print statistics about the latest execution of the algorithm to
	 * System.out.
	 */
	public void printStats() {
		String prePost = "PrePost";
		System.out.println("========== " + prePost + " - STATS ============");
		System.out.println(" Minsup = " + minSupport + "\n Number of transactions: " + numOfSequences);
		System.out.println(" Number of frequent  itemsets: " + outputCount);
		System.out.println(" Total time ~: " + (endTimestamp - startTimestamp) + " ms");
		System.out.println(" Max memory:" + MemoryLogger.getInstance().getMaxMemory() + " MB");
		System.out.println("=====================================");
	}

	/**
	 * Class to pass an integer by reference as in C++
	 */
	class IntegerByRef {
		int count;
	}

}
