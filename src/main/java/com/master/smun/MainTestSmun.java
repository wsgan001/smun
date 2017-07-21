package com.master.smun;

import java.io.UnsupportedEncodingException;
import java.net.URL;

public class MainTestSmun {

	public static void main(String[] args) throws Exception {
		String input = fileToPath("input.txt");
		String output = "output.txt";		
		double minsup = 0.4;		
		Smun smun = new Smun();
		smun.runAlgorithm(input, minsup, output);		
	}

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = Smun.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
    }	
}
