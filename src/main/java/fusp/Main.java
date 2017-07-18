package fusp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws IOException {
		InPut in = new InPut();
		in.readInput(fileToPath("input.txt"));

		String input1 = fileToPath((String) in.getDatabaseInput().get(0));
		String output1 = "Output_" + ((String) in.getDatabaseInput().get(0));

		Node root = new Node();
		Map headerTable = new HashMap();
		ArrayList sequences = new ArrayList();
		int minSub = 0;
		long startTime1 = 0L;
		long buildTree1 = 0L;
		long mining1 = 0L;

		long startTime2 = 0L;
		long buildTree2 = 0L;
		long mining2 = 0L;

		startTime1 = System.currentTimeMillis();

		BuildTree tree = new BuildTree();
		tree.createTree(input1);

		buildTree1 = System.currentTimeMillis();

		System.out.println("Time build tree 1: " + (buildTree1 - startTime1));

		root = tree.getTree();
		headerTable = tree.inCrease();
		minSub = (int) (in.getMin_support() * tree.countRow / 100.0F);

		MiningSequentialPatterns mining = new MiningSequentialPatterns();
		ArrayList roots = new ArrayList();

		mining.setHeaderTable(headerTable);
		mining.setSequence(sequences);
		mining.setMinSub(minSub);

		roots.add(root);

		mining.SPTreeMine(roots, null);
		sequences = mining.getSequence();

		mining1 = System.currentTimeMillis();

		System.out.println("Time mining 1: " + (mining1 - buildTree1));
		System.out.println("Total sequential pattern: " + sequences.size());
		System.out.println("-----------------------------------------------");

		OutPut out = new OutPut();
		out.PrintOutSequencesToFile(output1, sequences, headerTable,
				tree.countRow, startTime1, buildTree1, mining1, minSub);

		if (in.getType() == 2)
			for (int i = 1; i < in.getDatabaseInput().size(); ++i) {
				sequences = mining.getSequence();

				input1 = fileToPath((String) in.getDatabaseInput().get(i));
				output1 = "Output_" + ((String) in.getDatabaseInput().get(i));

				startTime2 = System.currentTimeMillis();

				IncrementalSequentialPatternTree increate = new IncrementalSequentialPatternTree();
				increate.setInputPath(input1);
				increate.setHeaderTable(headerTable);
				increate.setRoot(root);
				increate.IncreateNewTree();

				buildTree2 = System.currentTimeMillis();

				System.out.println("Time build tree 2: "
						+ (buildTree2 - startTime2));

				roots = new ArrayList();
				sequences = new ArrayList();

				roots.add(increate.getRoot());
				mining.setHeaderTable(increate.getHeaderTable());
				mining.setSequence(sequences);
				tree.countRow += increate.getcountRow();
				minSub = (int) (in.getMin_support() * tree.countRow / 100.0F);

				mining.setMinSub(minSub);

				mining.SPTreeMine(roots, null);
				sequences = mining.getSequence();

				mining2 = System.currentTimeMillis();

				sequences = mining.getSequence();

				System.out.println("Time mining " + (i + 1) + " :"
						+ (mining2 - buildTree2));
				System.out.println("Total sequential pattern: "
						+ sequences.size());
				System.out
						.println("-----------------------------------------------");

				OutPut out1 = new OutPut();
				out1.PrintOutSequencesToFile(output1, sequences, headerTable,
						increate.getcountRow() + tree.countRow, startTime2,
						buildTree2, mining2, minSub);
			}
	}

	public static String fileToPath(String filename)
			throws UnsupportedEncodingException {
		String workingDir = System.getProperty("user.dir");

		String url = workingDir + "\\" + filename;
		return URLDecoder.decode(url, "UTF-8");
	}

}
