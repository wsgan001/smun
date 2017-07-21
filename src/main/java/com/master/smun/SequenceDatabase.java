package com.master.smun;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequenceDatabase {

	private List<Sequence> sequences = new ArrayList<Sequence>();
	private ItemFactory itemFactory = new ItemFactory();

	@SuppressWarnings("resource")
	public void scanDB1(String fileName) throws Exception {
		Map<Integer, Integer> itemCount = new HashMap<Integer, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;

		Itemset itemset = new Itemset();
		Sequence sequence = new Sequence();

		while ((line = br.readLine()) != null) {
			long timestamp;
			// if the line is a comment, is empty or is a kind of metadata
			if (line.isEmpty() == true || line.charAt(0) == '#'
					|| line.charAt(0) == '%' || line.charAt(0) == '@')
				continue;

			String[] lineSplited = line.split(" ");
			for (String element : lineSplited) {
				if (element.codePointAt(0) == '<') { // Timestamp
					String value = element.substring(1, element.length() - 1);
					timestamp = Long.parseLong(value);
					itemset.setTimestamp(timestamp);
				} else if (element.equals("-1")) { // new itemset
					long time = itemset.getTimestamp() + 1;
					sequence.addItemset(itemset);
					itemset = new Itemset();
					itemset.setTimestamp(time);
				} else if (element.equals("-2")) { // end sequence
					sequences.add(sequence);
					sequence = new Sequence();
				} else {
					// extract the value for an item
					Item item = itemFactory.getItem(Integer.parseInt(element));
					itemset.addItem(item);
				}
			}
		}
	}

	public List<Sequence> getSequences() {
		return sequences;
	}

	public void setSequences(List<Sequence> sequences) {
		this.sequences = sequences;
	}

	@Override
	public String toString() {
		StringBuilder rs = new StringBuilder();
		for (Sequence sequence : sequences) {
			rs.append(sequence + "\n");
		}
		return rs.toString();
	}
}
