package com.master.smun_v2;

import java.util.List;

public class PPCTreeNode {
	public int label;
	public PPCTreeNode firstChild;
	public PPCTreeNode rightSibling;
	public PPCTreeNode labelSibling; // save for headtable link
	public PPCTreeNode father;
	public List<Integer> sequenceId;
	//public int count;
	public int foreIndex;
	public int backIndex;
}
