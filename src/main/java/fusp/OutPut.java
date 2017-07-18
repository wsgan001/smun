package fusp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OutPut {
	public void printOutDatabaseInfo(Map<String, Integer> headerTable,
			long totalSeq) {
		System.out.println(CreateStringOutDatabaseInfo(headerTable, totalSeq));
	}

	public void PrintOutSequences(ArrayList<Sequence> sequences, int minSub) {
		System.out.println(CreateStringOutSequences(sequences, minSub));
	}

	public void PrintOutSequence(List<Node> lst) {
		System.out.println(CreateStringOutSequence(lst));
	}

	public void PrintOutSequencesToFile(String outPut,
			ArrayList<Sequence> sequences, Map<Integer, Integer> headerTable,
			long totalSeq, long start1, long start2, long start3, int minSub) {
		try {
			File file = new File(outPut);
			String content = "";

			if (!(file.exists())) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			content = content
					+ CreateStringOutExecuteTime(start1, start2, start3);

			content = content + "\n";
			content = content + CreateStringOutSequences(sequences, minSub);

			bw.write(content);
			bw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private String CreateStringOutSequences(ArrayList<Sequence> sequences,
			int minSub) {
		String content = "";
		content = "======================================================\nNumber of sequential patternts: "
				+ sequences.size()
				+ "\n"
				+ "Minimum support: "
				+ minSub
				+ "\n"
				+ "======================================================";

		for (Sequence sq : sequences) {
			content = content + CreateStringOutSequence(sq.getSequence());
		}

		return content;
	}

	private String CreateStringOutSequence(List<Node> lst) {
		String content = "";

		content = content + "\n(";

		for (int i = 0; i < lst.size(); ++i) {
			content = content + ((Node) lst.get(i)).get_label();

			if (i + 1 < lst.size()) {
				if (((Node) lst.get(i + 1)).get_transID() != ((Node) lst.get(i))
						.get_transID()) {
					content = content + ")(";
				} else {
					content = content + " ";
				}
			} else {
				content = content + ") - Count:"
						+ ((Node) lst.get(i)).get_count() + " TransID: "
						+ ((Node) lst.get(i)).get_transID();
			}
		}
		content = content + "\n";

		return content;
	}

	private String CreateStringOutDatabaseInfo(
			Map<String, Integer> headerTable, long totalSeq) {
		String content = "";

		content = content + "=========Header Table: " + totalSeq
				+ "=========\n";
		int countOfLine = 0;
		for (String key : headerTable.keySet()) {
			content = content + key + ":" + headerTable.get(key) + "; ";
			++countOfLine;
			if (countOfLine != 5)
				continue;
			content = content + "\n";
			countOfLine = 0;
		}

		return content;
	}

	private String CreateStringOutExecuteTime(long start1, long start2,
			long start3) {
		String content = "";

		content = content + "=========Time create tree 1: \t\t\t\t"
				+ (start2 - start1) + "=========\n";
		content = content + "=========Time mining sequential pattern 1: \t"
				+ (start3 - start2) + "=========\n";
		content = content + "=========Total time: \t\t\t\t\t\t"
				+ (start3 - start1) + "=========\n";

		return content;
	}
}