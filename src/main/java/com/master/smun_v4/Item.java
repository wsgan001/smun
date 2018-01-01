package com.master.smun_v4;

public class Item {
	public int id;
	public int count;
	
	@Override
	public String toString() {
		return String.valueOf(id).toString()+":"+String.valueOf(count).toString();
	}	
}
