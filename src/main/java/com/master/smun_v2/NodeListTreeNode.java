package com.master.smun_v2;

import java.util.Set;

/**
 * N-list
 * @author banbui
 *
 */
public class NodeListTreeNode {
	public int label;
	public NodeListTreeNode firstChild;
	public NodeListTreeNode next;
	public Set<Integer> sequenceId;
	//public int support;
	public int NLStartinBf;
	public int NLLength;
	public int NLCol;
}
