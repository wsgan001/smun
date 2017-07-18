package fusp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BuildTree {
	Node _root = new Node();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	Map<Integer, Integer> _headerTable = new HashMap();

	public int countRow = 0;

	public Node getTree() {
		return this._root;
	}

	public Map<Integer, Integer> getHeaderTable() {
		return this._headerTable;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void createTree(String path) throws IOException {
		this._root.set_label(0);
		this._root.set_count(0);
		this._root.set_transID(0);

		Node current = this._root;

		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(path));
			myInput = new BufferedReader(new InputStreamReader(fin));
			String si;
			while ((si = myInput.readLine()) != null) {
				HashMap curHeader = new HashMap();

				if ((!(si.isEmpty())) && (si.charAt(0) != '#')
						&& (si.charAt(0) != '%') && (si.charAt(0) != '@')) {
					this.countRow += 1;

					si = si.replace("-2", "");

					String[] arr_si = si.split("-1");
					for (int j = 0; j < arr_si.length; ++j) {
						String[] arr_ej = arr_si[j].split(" ");
						for (String itemI_str : arr_ej) {
							if (!(itemI_str.equals(""))) {
								int itemI = Integer.valueOf(itemI_str)
										.intValue();
								boolean nodeExist = false;
								for (Node c : current.get_children()) {
									if ((c.get_label() == itemI)
											&& (c.get_transID() == j + 1)) {
										nodeExist = true;
										c.set_count(c.get_count() + 1);
										current = c;
										break;
									}

								}

								if (!(nodeExist)) {
									Node I = new Node();
									I.set_count(1);
									I.set_label(itemI);
									I.set_transID(j + 1);
									current.get_children().add(I);
									current = I;
								}

								if (curHeader.get(Integer.valueOf(itemI)) == null) {
									curHeader.put(Integer.valueOf(itemI),
											Integer.valueOf(1));
									if (this._headerTable.get(Integer
											.valueOf(itemI)) == null)
										this._headerTable.put(
												Integer.valueOf(itemI),
												Integer.valueOf(1));
									else {
										this._headerTable
												.put(Integer.valueOf(itemI),
														Integer.valueOf(((Integer) this._headerTable.get(Integer
																.valueOf(itemI)))
																.intValue() + 1));
									}
								}
							}
						}
					}
				}
				current = this._root;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (myInput != null)
				myInput.close();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<Integer, Integer> inCrease() {
		Map treeMap = new TreeMap(this._headerTable);
		return treeMap;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<Integer, Integer> disCrease() {
		Map treeMap = new TreeMap(new Comparator() {
			@SuppressWarnings("unused")
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}

			@Override
			public int compare(Object o1, Object o2) {
				return ((File) o2).compareTo((File) o1);
			}

		});
		treeMap.putAll(this._headerTable);
		return treeMap;
	}
}
