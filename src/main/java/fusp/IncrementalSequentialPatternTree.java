package fusp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class IncrementalSequentialPatternTree {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	Map<Integer, Integer> headerTable = new HashMap();
	String inputPath = "";
	Node root = new Node();
	int countRow = 0;

	public void setInputPath(String path) {
		this.inputPath = path;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public Node getRoot() {
		return this.root;
	}

	public void setHeaderTable(Map<Integer, Integer> headerTable) {
		this.headerTable = headerTable;
	}

	public Map<Integer, Integer> getHeaderTable() {
		return this.headerTable;
	}

	public int getcountRow() {
		return this.countRow;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void IncreateNewTree() throws IOException {
		BufferedReader myInput = null;

		Node current = this.root;
		try {
			FileInputStream fin = new FileInputStream(new File(this.inputPath));
			myInput = new BufferedReader(new InputStreamReader(fin));
			String si;
			while ((si = myInput.readLine()) != null) {
				//String si;
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
									if (this.headerTable.get(Integer
											.valueOf(itemI)) == null)
										this.headerTable.put(
												Integer.valueOf(itemI),
												Integer.valueOf(1));
									else {
										this.headerTable
												.put(Integer.valueOf(itemI),
														Integer.valueOf(((Integer) this.headerTable.get(Integer
																.valueOf(itemI)))
																.intValue() + 1));
									}
								}
							}
						}
					}
				}
				current = this.root;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (myInput != null)
				myInput.close();
		}
	}
}