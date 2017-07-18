package fusp;

import java.util.ArrayList;

public class Node {
	int _label;
	int _count;
	int _transID;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	ArrayList<Node> _children = new ArrayList();

	public Node() {
		this._label = 0;
		this._count = 0;
		this._transID = 0;
	}

	public Node(Node n) {
		this._label = n._label;
		this._count = n._count;
		this._transID = n._transID;
	}

	public int get_label() {
		return this._label;
	}

	public void set_label(int _label) {
		this._label = _label;
	}

	public int get_count() {
		return this._count;
	}

	public void set_count(int _count) {
		this._count = _count;
	}

	public int get_transID() {
		return this._transID;
	}

	public void set_transID(int _transID) {
		this._transID = _transID;
	}

	public ArrayList<Node> get_children() {
		return this._children;
	}

	public void set_children(ArrayList<Node> _children) {
		this._children = _children;
	}
}