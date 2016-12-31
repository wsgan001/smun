package com.master.smun;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author bangbv
 *
 */
public class Smun 
{
	public int minSupport;
	public int numOfFItem;
	public Item[] item;
	
	private int numOfTrans;
	
	static Comparator<Item> comp = new Comparator<Item>() {
		
		@Override
		public int compare(Item o1, Item o2) {			
			return o2.num - o1.num;
		}
	};
	
    void getData(String fileName, double support) throws IOException{
    	numOfTrans = 0;
    	Map<String, Integer> mapItemCount = new HashMap<>();
   
    	BufferedReader br = new BufferedReader(new FileReader(fileName));
    	String line;
    	// each line (transaction) until the end of the file
    	while((line = br.readLine()) != null){
    		// if the line is a comment, is empty or is a kind of metadata
    		if(line.isEmpty() || line.charAt(0) == '#' || line.charAt(0) == '%' || line.charAt(0) == '@'){
    			continue;
    		}
    		
    		numOfTrans++;
    		
    		// split the line into items
    		String[] lineSplited = line.split(" ");
    		// for each item in the transaction
    		for (String itemString : lineSplited) {
				// increase the support count of the item by 1 
    			Integer count = mapItemCount.get(itemString);
    			if(count == null){
    				mapItemCount.put(itemString, 1);
    			}else{
    				mapItemCount.put(itemString, ++count);
    			}
			}
    	}
    	// close the input file
    	br.close();
    	System.out.println("mapItemCount:"+mapItemCount);
    	
    	minSupport = (int) Math.ceil(support*numOfTrans);
    	System.out.println("minSupport:"+minSupport);
    	
    	numOfFItem = mapItemCount.size();    	
    	System.out.println("num of Item:"+numOfFItem);
    	
    	Item[] tempItem = new Item[numOfFItem];
    	int i = 0;
    	for (Entry<String, Integer> entry : mapItemCount.entrySet()) {
			if(entry.getValue() >= minSupport){
				tempItem[i] = new Item();
				tempItem[i].index = entry.getKey();
				tempItem[i].num = entry.getValue();
				i++;
			}			
		}
    	
    	item = new Item[i];
    	System.arraycopy(tempItem, 0, item, 0, i);
    	
    	numOfFItem = item.length;
    	
    	Arrays.sort(item,comp);
    	printArray(item);
    }
    
    void printArray(Object[] o){
    	System.out.println();
    	for (Object object : o) {
			System.out.print(object.toString()+"|");
		}
    	System.out.println();
    }
    
    void buildTree(String fileName) throws IOException{
    	
    	BufferedReader br = new BufferedReader(new FileReader(fileName));
    	String line;
    	
    	Item[] transaction = new Item[1000];
    	while((line = br.readLine()) != null){
    		if(line.isEmpty() || line.charAt(0) == '#' || line.charAt(0) == '%' || line.charAt(0) == '@'){
    			continue;
    		}
    		
    		String[] itemString = line.split(" ");
    		
    		for (String item : itemString) {
				
			}
    	}
    }
    
    void runAlgorithm(String fileName, double support, String output) throws IOException{
    	getData(fileName,support);
    	
    	//Build tree
    	
    }
}
