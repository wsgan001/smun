package com.master.smun_v5;

public class Util {

	public static void printArray(Item[] a){
		for (Item item : a) {
			if(item == null){ 
				System.out.println();
				return;
			}
			System.out.print("<"+item.id+":"+item.count+">");
		}
		System.out.println();
	}
}
