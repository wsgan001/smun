package com.master.smun;

class Item {
	public int id;
	
	public Item(int key){
		this.id = key;
	}

	@Override
	public String toString() {
		return String.valueOf(id).toString();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}	
}
