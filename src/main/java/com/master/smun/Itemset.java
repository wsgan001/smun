package com.master.smun;

import java.util.ArrayList;
import java.util.List;

public class Itemset {
	private List<Item> items = new ArrayList<Item>();
	private long timestamp = 0;
	
	public void addItem(Item item){
		items.add(item);
	}
	
	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		StringBuilder rs = new StringBuilder();
		rs.append("<");
		for (Item item : items) {
			rs.append(item+" ");
		}
		rs.append(">");
		return rs.toString();
	}	
}
