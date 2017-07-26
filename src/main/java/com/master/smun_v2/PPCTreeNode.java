package com.master.smun_v2;

public class PPCTreeNode {
	public int label;
	public PPCTreeNode firstChild;
	public PPCTreeNode rightSibling;
	public PPCTreeNode labelSibling; // save for headtable link
	public PPCTreeNode father;
	public int count;
	public int foreIndex;
	public int backIndex;
}
