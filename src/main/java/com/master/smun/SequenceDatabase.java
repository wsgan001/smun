package com.master.smun;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class SequenceDatabase {
	
	public void scanDB1(String fileName) throws Exception{
		Map<Integer, Integer> itemCount = new HashMap<Integer, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;
		while ((line = br.readLine()) != null) {
			// if the line is a comment, is empty or is a kind of metadata
			if (line.isEmpty() == true || line.charAt(0) == '#'	|| line.charAt(0) == '%' || line.charAt(0) == '@') continue;
			
			String[] lineSplited = line.split(" ");
		}
	}
}
