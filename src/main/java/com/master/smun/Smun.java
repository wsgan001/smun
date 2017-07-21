package com.master.smun;

/** 
 * @author bangbv
 */
public class Smun {
	
	public Item[] items;

	void runAlgorithm(String fileName, double support, String output) throws Exception {
	// Scan DB
	SequenceDatabase sdb = new SequenceDatabase();
	sdb.scanDB1(fileName);
	System.out.println(sdb.toString());
	// Build tree
	// project FUSP tree
	// 
		
	}	
}
