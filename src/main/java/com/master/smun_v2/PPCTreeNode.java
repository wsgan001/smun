package com.master.smun_v2;

import java.util.Set;

public class PPCTreeNode {
	public int label;
	public PPCTreeNode firstChild;
	public PPCTreeNode rightSibling;
	public PPCTreeNode labelSibling; // save for headtable link
	public PPCTreeNode father;
	public Set<Integer> sequenceId;
	//public int count;
	public int foreIndex;
	public int backIndex;
}
