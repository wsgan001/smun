package com.master.smun_v4;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class MainTestSMUN {

	public static void main(String [] arg) throws IOException{
		String input = fileToPath("BMS1_spmf.txt");
		String output = ".//output.txt";  // the path for saving the frequent sequences found		
		double minsup = 0.01; // means a minsup of 2 transaction (we used a relative support)		
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
