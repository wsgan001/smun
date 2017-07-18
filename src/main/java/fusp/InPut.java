package fusp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class InPut {
	private ArrayList<String> databaseInput;
	private float min_support;
	private int type;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public InPut() {
		setMin_support(1.0F);
		setType(0);
		setDatabaseInput(new ArrayList());
	}

	public void readInput(String path) throws IOException {
		BufferedReader myInput = null;
		String line = "";

		FileInputStream fin = new FileInputStream(new File(path));
		myInput = new BufferedReader(new InputStreamReader(fin));
		boolean con = true;

		while (((line = myInput.readLine()) != null) && (con)) {
			String[] curLine = line.split(":");
			if (curLine[0].equals("Input database")) {
				getDatabaseInput().add(curLine[1]);
			} else if (curLine[0].equals("Min_support")) {
				this.min_support = Float.valueOf(curLine[1]).floatValue();
			} else if (curLine[0].equals("Execute type")) {
				setType(Integer.valueOf(curLine[1]).intValue());
				con = false;
			}
		}

		fin.close();
	}

	public float getMin_support() {
		return this.min_support;
	}

	public void setMin_support(float min_support) {
		this.min_support = min_support;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<String> getDatabaseInput() {
		return this.databaseInput;
	}

	public void setDatabaseInput(ArrayList<String> databaseInput) {
		this.databaseInput = databaseInput;
	}
}