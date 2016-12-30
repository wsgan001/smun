package com.master.smun;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author bangbv
 *
 */
public class Smun 
{
 
	private int numOfTrans;
	
    void getData(String fileName, double support) throws IOException{
    	numOfTrans = 0;
    	Map<String, Integer> mapItemCount = new HashMap<>();
   
    	BufferedReader bf = new BufferedReader(new FileReader(fileName));
    	String line;
    	// each line (transaction) until the end of the file
    	while((line = bf.readLine()) != null){
    		// if the line is a comment, is empty or is a kind of metadata
    		if(line.isEmpty() || line.charAt(0) == '#' || line.charAt(0) == '%' || line.charAt(0) == '@'){
    			continue;
    		}
    		
    		numOfTrans++;
    		
    		// split the line into items
    		String[] lineSplited = line.split(" ");
    		// for each item in the transaction
    		for (String itemString : lineSplited) {
				// count
    			Integer count = mapItemCount.get(itemString);
    			if(count == null){
    				
    			}else{
    				
    			}
			}
    	}
    }
    
    void runAlgorithm(String fileName, double support, String output) throws IOException{
    	getData(fileName,support);
    }
}
