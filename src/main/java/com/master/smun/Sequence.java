package com.master.smun;

import java.util.ArrayList;
import java.util.List;

public class Sequence {
	List<Itemset> sequence = new ArrayList<Itemset>();
	public void addItemset(Itemset itemset) {
		sequence.add(itemset);
	}
	
	public List<Itemset> getSequence() {
		return sequence;
	}
	public void setSequence(List<Itemset> sequence) {
		this.sequence = sequence;
	}

	@Override
	public String toString() {
		StringBuilder rs = new StringBuilder();
		for (Itemset itemset : sequence) {
			rs.append(itemset+" ");
		}
		return rs.toString();
	}	
}
