package com.master.smun;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class MainTestSmun {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String input = readInput("contextPasquier99.txt");
		String output = "output.txt";
		
		double minsup = 0;
		
		
		
		System.out.println(input);
	}

	private static String readInput(String fileName) throws UnsupportedEncodingException {
		URL url = MainTestSmun.class.getResource(fileName);
		return URLDecoder.decode(url.getPath(), "UTF-8");
	}
}
