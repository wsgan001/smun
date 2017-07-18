package fusp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MiningSequentialPatterns {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	Map<Integer, Integer> headerTable = new HashMap();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	ArrayList<Sequence> sequences = new ArrayList();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	ArrayList<Node> rootS = new ArrayList();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	ArrayList<Node> rootI = new ArrayList();
	Node track = new Node();
	int minSup;
	int mark = 0;

	public ArrayList<Sequence> getSequence() {
		return this.sequences;
	}

	void setSequence(ArrayList<Sequence> sequences) {
		this.sequences = sequences;
	}

	void setHeaderTable(Map<Integer, Integer> headerTable) {
		this.headerTable = headerTable;
	}

	void setMinSub(int minSup) {
		this.minSup = minSup;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void SPTreeMine(ArrayList<Node> roots, Sequence sequence) {
		for (Map.Entry entry : this.headerTable.entrySet()) {
			if (((Integer) entry.getValue()).intValue() < this.minSup)
				continue;
			this.rootS = new ArrayList();
			this.rootI = new ArrayList();

			for (Node r : roots) {
				this.track = r;
				for (Node n : r.get_children()) {
					this.mark = 0;
					FirstOccNode(((Integer) entry.getKey()).intValue(), n, 0, 0);
				}

			}

			ArrayList _rootS = new ArrayList(this.rootS);
			ArrayList _rootI = new ArrayList(this.rootI);

			Node nodeKey = new Node();

			int sumS = SumOfTheCountOfRoot(_rootS);
			if (sumS >= this.minSup) {
				nodeKey.set_label(((Integer) entry.getKey()).intValue());
				nodeKey.set_count(sumS);
				if (sequence == null) {
					nodeKey.set_transID(1);
					sequence = new Sequence();
				} else {
					nodeKey.set_transID(sequence.getLastestNodeTransId() + 1);
				}

				Sequence curSeq = new Sequence(sequence.getSequence());
				curSeq.getSequence().add(nodeKey);
				this.sequences.add(curSeq);

				SPTreeMine(_rootS, curSeq);
			}

			int sumI = SumOfTheCountOfRoot(_rootI);
			if (sumI < this.minSup)
				continue;
			nodeKey.set_label(((Integer) entry.getKey()).intValue());
			nodeKey.set_count(sumI);
			if (sequence == null) {
				nodeKey.set_transID(1);
				sequence = new Sequence();
			} else {
				nodeKey.set_transID(sequence.getLastestNodeTransId());
			}

			Sequence curSeq = new Sequence(sequence.getSequence());
			curSeq.getSequence().add(nodeKey);
			this.sequences.add(curSeq);

			SPTreeMine(_rootI, curSeq);
		}
	}

	private void FirstOccNode(int key, Node n, int markS, int markI) {
		if ((markS != 0) && (markI != 0)) {
			return;
		}

		if ((((n.get_label() == this.track.get_label()) ? 1 : 0) & ((n
				.get_label() != key) ? 1 : 0)) != 0) {
			this.mark = n.get_transID();
		}
		if (n.get_label() == key) {
			if ((((n.get_transID() == this.track.get_transID()) ? 1 : 0) & ((markI == 0)
					? 1
					: 0)) != 0) {
				this.rootI.add(n);
				markI = 1;
			}
			if ((((n.get_transID() != this.track.get_transID()) ? 1 : 0)
					& ((this.mark == n.get_transID()) ? 1 : 0) & ((markI == 0)
						? 1
						: 0)) != 0) {
				this.rootI.add(n);
				markI = 1;
			}
			if ((((n.get_transID() != this.track.get_transID()) ? 1 : 0) & ((markS == 0)
					? 1
					: 0)) != 0) {
				this.rootS.add(n);
				markS = 1;
			}
		}

		for (Node _n : n.get_children()) {
			FirstOccNode(key, _n, markS, markI);
		}
	}

	private int SumOfTheCountOfRoot(ArrayList<Node> root) {
		int count = 0;

		if (root != null) {
			for (Node n : root) {
				count += n.get_count();
			}
		}

		return count;
	}
}