package com.master.smun_v5;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class MainTestSMUN {

	public static void main(String [] arg) throws IOException{
		//String filename = "/input.txt";
		String filename = "/BIBLE.txt";
		//String filename = "/BMS1_spmf.txt";
		//String filename = "/Kosarak.txt";
		//String filename = "/FIFA.txt";
		String input = fileToPath(filename);
		String output = "/Users/bvbang/workspace/smun/src/main/resources/output_smun.txt";  // the path for saving the frequent sequences found		
		double minsup = 0.01;
		// Applying the algorithm
		SMUN smun = new SMUN();
		smun.runAlgorithm(input, minsup, output);
		smun.printStats();
	}
	
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestSMUN.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
