package fusp;

import java.util.ArrayList;

public class Sequence {
	ArrayList<Node> sequence;

	public ArrayList<Node> getSequence() {
		return this.sequence;
	}

	public void setSequence(ArrayList<Node> sequence) {
		this.sequence = sequence;
	}

	public Node getLastestNode() {
		if (this.sequence.size() == 0) {
			return null;
		}
		return ((Node) this.sequence.get(this.sequence.size() - 1));
	}

	public int getLastestNodeTransId() {
		if (this.sequence.size() == 0) {
			return 0;
		}
		return ((Node) this.sequence.get(this.sequence.size() - 1))._transID;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Sequence(ArrayList<Node> sequence) {
		this.sequence = new ArrayList(sequence);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Sequence() {
		this.sequence = new ArrayList();
	}
}
