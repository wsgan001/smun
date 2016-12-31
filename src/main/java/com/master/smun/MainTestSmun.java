package com.master.smun;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class MainTestSmun {

	public static void main(String[] args) throws IOException {
		String input = readInput("contextPasquier99.txt");
		String output = "output.txt";
		
		double minsup = 0.4;
		
		Smun smun = new Smun();
		smun.runAlgorithm(input, minsup, output);
		
	}

	private static String readInput(String fileName) throws UnsupportedEncodingException {
		URL url = MainTestSmun.class.getResource(fileName);
		return URLDecoder.decode(url.getPath(), "UTF-8");
	}
}
