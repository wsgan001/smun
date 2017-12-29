package com.master.smun_v3;

import java.util.Set;

public class PPCTreeNode {
	public int label;
	public PPCTreeNode firstChild;
	public PPCTreeNode rightSibling;
	public PPCTreeNode labelSibling; // save for header table link
	public PPCTreeNode father;
	public int nodeLink;
	public int count;
	public int foreIndex;
	public int backIndex;
	public Set<Integer> sequenceId;
}
