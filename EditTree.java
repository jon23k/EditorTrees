
package editortrees;
import java.util.Stack;


// A height-balanced binary tree with rank that could be the basis for a text editor.

public class EditTree {

	private Node root;
	private Stack<Node> adjustmentStack;
	private int rotations = 0;
	private int half = 0;

	/**
	 * Construct an empty tree
	 */
	public EditTree() {
		this.root = new Node();
	}

	public EditTree(Node parent, char element, Node left, Node right){
		this.root = new Node(parent, element, left, right);
	}
	
	/**
	 * Construct a single-node tree whose element is c
	 * 
	 * @param c
	 */
	public EditTree(char c) {
		this.root = new Node(null, c);
	}

	/**
	 * Create an EditTree whose toString is s. This can be done in O(N) time,
	 * where N is the length of the tree (repeatedly calling insert() will be N
	 * log N).
	 * 
	 * @param s
	 */
	public EditTree(String s) {
		this.adjustmentStack = new Stack<Node>();
		if(s.length() % 2 == 0){
			this.half = (s.length() / 2) - 1;
		}
		else{
			this.half = (int) Math.floor(s.length() / 2);
		}
		char[] array = s.toCharArray();
		this.root = new Node(null, array[this.half]);
		Node current = this.root;
		this.adjustmentStack.push(current);
		
		int index = this.half + 2;
		while(index < array.length){
			current.right = new Node(current, array[index]);
			this.adjustmentStack.push(current);
			current = current.right;
			current.left = new Node(current, array[index - 1]);
			this.adjustmentStack.push(current.left);
			index += 2;
		}
		if(index == array.length){
			current.right = new Node(current, array[index - 1]);
		}
		
		current = this.root;
		index = this.half - 2;
		while(index >= 0){
			current.left = new Node(current, array[index]);
			this.adjustmentStack.push(current);
			current = current.left;
			current.right = new Node(current, array[index + 1]);
			this.adjustmentStack.push(current.right);
			index -= 2;
		}
		if(index == -1){
			current.left = new Node(current, array[index + 1]);
		}
		
		this.rotateHandler();
		
		
	}

	/**
	 * Make this tree be a copy of e, with all new nodes, but the same shape and
	 * contents.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {
		this.root = e.root;
		nodeReplicator(this.root, e.root);
	}

	/**
	 * Creates all new nodes for the tree
	 * 
	 */
	private void nodeReplicator(Node newNode, Node oldNode) {

		if (oldNode.left != null) {
			Node n = new Node(newNode, oldNode.left.element, oldNode.left.left,
					oldNode.left.right);
			nodeReplicator(n, oldNode.left);
		}
		if (oldNode.right != null) {
			Node n = new Node(newNode, oldNode.right.element,
					oldNode.right.left, oldNode.right.right);
			nodeReplicator(n, oldNode.right);
		}
	}

	/**
	 * @return the height of this tree
	 */
	public int height() {
		if (this.root.element == '\0') {
			return -1;
		} else
			return this.root.height();
	}

	/**
	 * 
	 * @return the number of nodes in this tree
	 */
	public int size() {
		return this.root.size;
	}

	/**
	 * return the string produced by an inorder traversal of this tree
	 */
	public String toString() {
		return this.root.inOrderString();
	}

	/**
	 * 
	 * @param c
	 *            character to add to the end of this tree.
	 */
	public void add(char c) {
		add(c, this.size());
	}

	/**
	 * 
	 * @param c
	 *            character to add
	 * @param pos
	 *            character added in this inorder position
	 * @throws IndexOutOfBoundsException
	 *             id pos is negative or too large for this tree
	 */
	public void add(char c, int pos) throws IndexOutOfBoundsException {

		if (pos > this.size() || pos < 0) {
			throw new IndexOutOfBoundsException();
		}

		if (this.size() == 0) {
			this.root = new Node(null, c);
			this.root.size = 1;
			return;
		}
		this.adjustmentStack = new Stack<Node>();
		add(c, pos, this.root);
	}

	/**
	 * 
	 * @param c
	 *            character to add
	 * @param pos
	 *            character added in this inorder position
	 * @param n
	 *            the parent node, allows us to traverse down the tree easily
	 *            and push onto our stack
	 */
	private void add(char c, int pos, Node n) {
		this.adjustmentStack.push(n);
		if (pos <= n.getInOrderPos()) {
			if (n.left == null) {
				n.left = new Node(n, c);
				this.adjustmentStack.push(n.left);
				if (n.right == null)
					addAdjustHandler();
				else
					n.balance = Node.Code.SAME;
			}
			else
				add(c, pos, n.left);
		} else if (pos > n.getInOrderPos()) {
			if (n.right == null) {
				n.right = new Node(n, c);
				this.adjustmentStack.push(n.right);
				if (n.left == null)
					addAdjustHandler();
				else
					n.balance = Node.Code.SAME;
			}
			else
				add(c, pos, n.right);
		}
		while(!this.adjustmentStack.isEmpty()){
			Node currentNode = this.adjustmentStack.pop();
			currentNode.setSize();
			currentNode.setBalance();
		}
	}

	private void addAdjustHandler() {
		while(!this.adjustmentStack.isEmpty()){
			Node currentNode = this.adjustmentStack.pop();
			currentNode.setSize();
			if(currentNode.parent!=null){
				if(currentNode.parent.right != null && currentNode.parent.right.equals(currentNode)){
					int indicator = currentNode.parent.shiftRight();
					if(indicator == 1){
						if(currentNode.balance == Node.Code.LEFT){
							if(currentNode.parent.equals(this.root))
								this.root = doubleLeftRotate(currentNode.parent);
							else
								doubleLeftRotate(currentNode.parent);
							break;
						}
						else if(currentNode.balance == Node.Code.RIGHT){
							if(currentNode.parent.equals(this.root))
								this.root = LeftRotate(currentNode.parent);
							else
								LeftRotate(currentNode.parent);
							break;
						}
					}
					else if(indicator == 2)
						break;
				}
				else if (currentNode.parent.left != null && currentNode.parent.left.equals(currentNode)){
					int indicator = currentNode.parent.shiftLeft();
					if(indicator == 1){
						if(currentNode.balance == Node.Code.LEFT){
							if(currentNode.parent.equals(this.root))
								this.root = RightRotate(currentNode.parent);
							else
								RightRotate(currentNode.parent);
							break;
						}
						else if(currentNode.balance == Node.Code.RIGHT){
							if(currentNode.parent.equals(this.root))
								this.root = doubleRightRotate(currentNode.parent);
							else
								doubleRightRotate(currentNode.parent);
							break;
						}
					}
					else if(indicator == 2)
						break;
				}
			}
		}
	}

	/**
	 * Uses the stack created in add and delete to check if the tree needs
	 * balancing.
	 */

	private void rotateHandler() {
		while (!this.adjustmentStack.isEmpty()) {
			Node currentNode = this.adjustmentStack.pop();
			currentNode.setSize();
			if (currentNode.balance == Node.Code.LEFT) {
				if (currentNode.left.balance == Node.Code.LEFT) {
					if (currentNode.equals(this.root))
						this.root = RightRotate(currentNode);
					else
						RightRotate(currentNode);
				} else if (currentNode.left.balance == Node.Code.RIGHT) {
					if (currentNode.equals(this.root))
						this.root = doubleRightRotate(currentNode);
					else
						doubleRightRotate(currentNode);
				}
			} else if (currentNode.balance == Node.Code.RIGHT) {
				if (currentNode.right.balance == Node.Code.RIGHT) {
					if (currentNode.equals(this.root))
						this.root = LeftRotate(currentNode);
					else
						LeftRotate(currentNode);
				} else if (currentNode.right.balance == Node.Code.LEFT) {
					if (currentNode.equals(this.root))
						this.root = doubleLeftRotate(currentNode);
					else
						doubleLeftRotate(currentNode);
				}
			}
		}
	}

	/**
	 * Rotates the tree to the right
	 * 
	 * @param n
	 *            - the node to rotate around
	 * @return the new rotated tree
	 */
	private Node RightRotate(Node n) {
		n.balance = Node.Code.SAME;
		if(n.left!=null)
			n.left.balance = Node.Code.SAME;
		this.rotations += 1;
		return n.rotateRight();
	}

	/**
	 * Rotates the tree to the left
	 * 
	 * @param n
	 *            - the node to rotate around
	 * @return the new rotated tree
	 */

	private Node LeftRotate(Node n) {
		this.rotations += 1;
		n.balance = Node.Code.SAME;
		if(n.right!=null)
			n.right.balance = Node.Code.SAME;
		return n.rotateLeft();
	}

	/**
	 * Performs a Left-Right rotation
	 * 
	 * @param n
	 *            - the node to rotate around
	 * @return the new rotated tree
	 */

	private Node doubleRightRotate(Node n) {
		this.rotations += 2;
		n.left.balance = Node.Code.SAME;
		n.balance = Node.Code.SAME;
		n.left.rotateLeft();
		return n.rotateRight();
	}

	/**
	 * Performs a Right-Left rotation
	 * 
	 * @param n
	 *            - the node to rotate around
	 * @return the new rotated tree
	 */

	private Node doubleLeftRotate(Node n) {
		this.rotations += 2;
		n.balance = Node.Code.SAME;
		n.right.balance = Node.Code.SAME;
		n.right.rotateRight();
		return n.rotateLeft();
	}

	/**
	 * 
	 * @param pos
	 *            position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {
		if (pos >= this.size() || pos < 0)
			throw new IndexOutOfBoundsException("Number is too large.");

		// Find the node
		Node currentNode = this.root;
		while (currentNode.getInOrderPos() != pos) {
			this.adjustmentStack.push(currentNode);
			if (pos > currentNode.getInOrderPos())
				currentNode = currentNode.right;
			else if (pos < currentNode.getInOrderPos())
				currentNode = currentNode.left;
		}
		// set the node to a temporary variable to store it's data as it is
		// deleted
		Node deleteNode = currentNode;

		if (currentNode.left != null) { // if it has a left subtree
			currentNode = currentNode.left;
			char ind = 'l';
			if (currentNode.right != null) { // if it has a right subtree of
												// it's left subtree
				while (currentNode.right != null) {
					currentNode = currentNode.right; // moves it to the bottom
														// right of the left
														// subtree
					ind = 'r';
				}
				// removes the currentNode from the tree to allow it to be moved
				// up the tree
				currentNode.parent.right = currentNode.left;
				if(currentNode.left != null)
				currentNode.left.parent = currentNode.parent;
			} else { // if it has no right subtree of it's left subtree
				// removes the currentNode from the tree to allow it to be moved
				// up the tree
				currentNode.parent.left = currentNode.left;
				if(currentNode.left != null)
					currentNode.left.parent = currentNode.parent;
				ind = 'l';
			}
			currentNode.balance = deleteNode.balance;

			// pushes the nodes in the correct order onto the stack
			Node pushNode = currentNode;
			while (pushNode != deleteNode) {
				this.adjustmentStack.push(pushNode);
				pushNode = pushNode.parent;
			}

			// effectively removes the node to be deleted from the tree
			if (deleteNode.parent != null) {
				// removes the node by removing the pointer to it
				if (deleteNode.parent.left.equals(deleteNode))
					deleteNode.parent.left = currentNode;
				else if (deleteNode.parent.right.equals(deleteNode))
					deleteNode.parent.right = currentNode;
			} else {
				// sets the removed node to the new root of the tree, because
				// the tree's root is to be deleted
				this.root = currentNode;
				this.root.parent = null;
			}
			// replaces the deleteNode in the tree with the currentNode
			if (!currentNode.equals(deleteNode.left))
				currentNode.left = deleteNode.left;
			currentNode.right = deleteNode.right;
			if (currentNode.left != null)
				currentNode.left.parent = currentNode;
			if (currentNode.right != null)
				currentNode.right.parent = currentNode;
			currentNode.parent = deleteNode.parent;
			deleteAdjustHandler(ind);
		} else { // if it has no left subtree
			if (deleteNode.parent != null) {
				if (deleteNode.parent.left != null)
					if (deleteNode.parent.left.equals(deleteNode)){
						deleteNode.parent.left = deleteNode.right;
						if(deleteNode.right != null)
							deleteNode.right.parent = deleteNode.parent;
						deleteAdjustHandler('l');
					}
				if (deleteNode.parent.right != null)
					if (deleteNode.parent.right.equals(deleteNode)){
						deleteNode.parent.right = deleteNode.right;
						if(deleteNode.right != null)
							deleteNode.right.parent = deleteNode.parent;
						deleteAdjustHandler('r');
					}
			} else {// if the root node was deleted
				if (deleteNode.right == null)
					this.root = new Node();
				else
					this.root = deleteNode.right;
			}
		}

		currentNode.setSize();
		return deleteNode.element;
	}

	private void deleteAdjustHandler(char childInd) {
		Node currentNode;
		if(!this.adjustmentStack.isEmpty()){
			currentNode = this.adjustmentStack.pop();
			currentNode.setSize();
			if(childInd == 'r'){
				int indicator = 0;
				if(currentNode.left == null){
					currentNode.balance = Node.Code.SAME;
					indicator = 2;
				}
				else
					indicator = currentNode.shiftLeft();
				if(indicator == 1){
					if(currentNode.left != null && currentNode.left.balance == Node.Code.RIGHT){
						if(this.root.equals(currentNode))
							this.root = doubleRightRotate(currentNode);
						else{
							if(currentNode.parent.right.equals(currentNode))
								childInd = 'r';
							else if(currentNode.parent.left.equals(currentNode))
								childInd = 'l';
							doubleRightRotate(currentNode);
							deleteAdjustHandler(childInd);
						}
					}
					else if(currentNode.left != null && (currentNode.left.balance == Node.Code.LEFT || currentNode.left.balance == Node.Code.SAME)){
						if(this.root.equals(currentNode))
							this.root = RightRotate(currentNode);
						else{
							if(currentNode.parent.right.equals(currentNode))
								childInd = 'r';
							else if(currentNode.parent.left.equals(currentNode))
								childInd = 'l';
							RightRotate(currentNode);
							deleteAdjustHandler(childInd);
						}
					}
				}
				else if(indicator == 2){
					if(currentNode.parent != null){
						if(currentNode.parent.left != null && currentNode.parent.left.equals(currentNode))
							deleteAdjustHandler('l');
						else if(currentNode.parent.right != null && currentNode.parent.right.equals(currentNode))
							deleteAdjustHandler('r');
					}
				}
				else
					while(!this.adjustmentStack.isEmpty()){
						currentNode = this.adjustmentStack.pop();
						currentNode.setSize();
					}
			}
			else{
				int indicator = 0;
				if(currentNode.right == null){
					currentNode.balance = Node.Code.SAME;
					indicator = 2;
				}
				else
					indicator = currentNode.shiftRight();
				if(indicator == 1){
					if(currentNode.right != null && currentNode.right.balance == Node.Code.LEFT){
						if(this.root.equals(currentNode))
							this.root = doubleLeftRotate(currentNode);
						else{
							if(currentNode.parent.right.equals(currentNode))
								childInd = 'r';
							else if(currentNode.parent.left.equals(currentNode))
								childInd = 'l';
							doubleLeftRotate(currentNode);
							deleteAdjustHandler(childInd);
						}
					}
					else if(currentNode.right != null && (currentNode.right.balance == Node.Code.RIGHT || currentNode.right.balance == Node.Code.SAME)){
						if(this.root.equals(currentNode))
							this.root = LeftRotate(currentNode);
						else{
							if(currentNode.parent.right.equals(currentNode))
								childInd = 'r';
							else if(currentNode.parent.left.equals(currentNode))
								childInd = 'l';
							LeftRotate(currentNode);
							deleteAdjustHandler(childInd);
						}
					}
				}
				if(indicator == 2){
					if(currentNode.parent!=null){
						if(currentNode.parent.left != null && currentNode.parent.left.equals(currentNode))
							deleteAdjustHandler('l');
						else if(currentNode.parent.right != null && currentNode.parent.right.equals(currentNode))
							deleteAdjustHandler('r');
					}
				}
				else
					while(!this.adjustmentStack.isEmpty()){
						currentNode = this.adjustmentStack.pop();
						currentNode.setSize();
					}
			}
		}
	}

	/**
	 * 
	 * @param pos
	 *            position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		if (pos >= this.size() || pos < 0 || this.size() < 0)
			throw new IndexOutOfBoundsException();
		Node currentNode = this.root;
		while (currentNode != null && currentNode.getInOrderPos() != pos) {
			if (currentNode.getInOrderPos() > pos)
				currentNode = currentNode.left;
			else
				currentNode = currentNode.right;
		}
		if (currentNode == null)
			return '\0';

		return currentNode.element;
	}

	/**
	 * This method operates in O(length*log N), where N is the size of this
	 * tree.
	 * 
	 * @param pos
	 *            location of the beginning of the string to retrieve
	 * @param length
	 *            length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException
	 *             unless both pos and pos+length-1 are legitimate indexes
	 *             within this tree.
	 */

	public String get(int pos, int length) throws IndexOutOfBoundsException {
		String string = "";

		for (int i = pos; i < pos + length; i++) {
			string += get(i);
		}
		return string;
	}

	/**
	 * This method is provided for you, and should not need to be changed. If
	 * split() and concatenate() are O(log N) operations as required, delete
	 * should also be O(log N)
	 * 
	 * @param start
	 *            position of beginning of string to delete
	 * 
	 * @param length
	 *            length of string to delete
	 * @return an EditTree containing the deleted string
	 * @throws IndexOutOfBoundsException
	 *             unless both start and start+length-1 are in range for this
	 *             tree.
	 */
	public EditTree delete(int start, int length)
			throws IndexOutOfBoundsException {
		if (start < 0 || start + length >= this.size())
			throw new IndexOutOfBoundsException(
					(start < 0) ? "negative first argument to delete"
							: "delete range extends past end of string");
		EditTree t2 = this.split(start);
		EditTree t3 = t2.split(length);
		this.concatenate(t3);
		return t2;
	}

	/**
	 * Append (in time proportional to the log of the size of the larger tree)
	 * the contents of the other tree to this one. Other should be made empty
	 * after this operation.
	 * 
	 * @param other
	 * @throws IllegalArgumentException
	 *             if this == other
	 */
	public void concatenate(EditTree other) throws IllegalArgumentException {
		this.adjustmentStack = new Stack<Node>();
		if(this.equals(other)){
			throw new IllegalArgumentException("trees are the same");
		}
		
		if(this.size() == 0 || other.size() == 0){
			if(this.size() == 0){
				this.root = other.root;
				return;
			}
			else{
				return;
			}
		}
		
		int thisHeight = this.root.height();
		int otherHeight = other.root.height();
		Node current = other.root;
		while(current.left != null){
			current = current.left;
		}
		other.delete(0);
		paste(this, current, other);
		other.root = new Node();
		this.rotateHandler();
		
	}
	
	public void paste(EditTree T, Node q, EditTree V){
		int tHeight = T.root.height();
		int vHeight = V.root.height();
		if(vHeight == 0){
			V.root = null;
		}
		if(tHeight >= vHeight){
			this.adjustmentStack.clear();
			Node current = T.root;
			int currentHeight = tHeight;
			Node parent = null;
			while(currentHeight - vHeight >= 1){
				this.adjustmentStack.push(current);
				if(current.balance == Node.Code.LEFT){
					currentHeight -= 2;
				}
				else{
					currentHeight -= 1;
				}
				parent = current;
				current = current.right;
			}
			if(V.root == null){
				vHeight = -1;
			}
			q.left = current;
			q.right = V.root;
			current.parent = q;
			if(V.root != null){
				V.root.parent = q;
			}
			q.parent = parent;
			if(currentHeight == vHeight){
				q.balance = Node.Code.SAME;
			}
			else{
				q.balance = Node.Code.LEFT;
			}
			if(parent != null){
				parent.right = q;
			}
			else{
				this.root = q;
			}
			
			this.adjustmentStack.push(q);
		}
		else{
			this.adjustmentStack.clear();
			Node current = T.root;
			int currentHeight = tHeight;
			Node parent = null;
			while(currentHeight - vHeight >= -1){
				this.adjustmentStack.push(current);
				if(current.balance == Node.Code.LEFT){
					currentHeight -= 2;
				}
				else{
					currentHeight -= 1;
				}
				parent = current;
				current = current.right;
			}
			
			q.left = current;
			q.right = V.root;
			current.parent = q;
			V.root.parent = q;
			q.parent = parent;
			if(currentHeight == vHeight){
				q.balance = Node.Code.SAME;
			}
			else{
				q.balance = Node.Code.RIGHT;
			}
			if(parent != null){
				parent.right = q;
			}
			else{
				this.root = q;
			}
			
			this.adjustmentStack.push(q);
		}
	}
	/**
	 * This operation must be done in time proportional to the height of this
	 * tree.
	 * 
	 * @param pos
	 *            where to split this tree
	 * @return a new tree containing all of the elements of this tree whose
	 *         positions are >= position. Their nodes are removed from this
	 *         tree.
	 * @throws IndexOutOfBoundsException
	 */
	public EditTree split(int pos) throws IndexOutOfBoundsException {
		//gets current to correct position
		this.adjustmentStack = new Stack<Node>();
		Node current = this.root;
		while(current.getInOrderPos() <= pos){
			if(current.getInOrderPos() > pos){
				this.adjustmentStack.push(current);
				current = current.right;
			}
			else{
				this.adjustmentStack.push(current);
				current = current.left;
			}
		}
		this.adjustmentStack.push(current);
		
		//passes in new variables for calculation
		EditTree toReturn = splitTree();
		return toReturn;
		
		
	}
	
	public EditTree splitTree(){
		//makes the two separate trees
		Node current = this.adjustmentStack.pop();
		EditTree S = new EditTree();
		S.root = current.left;
		EditTree T = new EditTree();
		T.root = current.right;
		
		paste(S, current, null);
		Node child = current;
		while(!this.adjustmentStack.isEmpty()){
			child = current;
			current = this.adjustmentStack.pop();
			if(child == current.right){
				EditTree newTree = new EditTree();
				newTree.root = current.left;
				paste(newTree, current, S);
			}
			else{
				EditTree newTree = new EditTree();
				newTree.root = current.right;
				paste(T, current, newTree);
			}
		}
		return this;
	}

	/**
	 * Don't worry if you can't do this one efficiently.
	 * 
	 * @param s
	 *            the string to look for
	 * @return the position in this tree of the first occurrence of s; -1 if s
	 *         does not occur
	 */
	public int find(String s) {
		char[] compareArray = this.toString().toCharArray();
		System.out.println(compareArray);

		for (int i = 0; i < compareArray.length - (s.length() - 1); i++) {
			String stringToCompare = "";
			for (int j = i; j < i + s.length(); j++) {
				stringToCompare += compareArray[j];
			}
			if (s.equals(stringToCompare)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param s
	 *            the string to search for
	 * @param pos
	 *            the position in the tree to begin the search
	 * @return the position in this tree of the first occurrence of s that does
	 *         not occur before position pos; -1 if s does not occur
	 */
	public int find(String s, int pos) {
		char[] compareArray = this.toString().toCharArray();
		System.out.println(compareArray);

		for (int i = pos; i < compareArray.length - (s.length() - 1); i++) {
			String stringToCompare = "";
			for (int j = i; j < i + s.length(); j++) {
				stringToCompare += compareArray[j];
			}
			if (s.equals(stringToCompare)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @return The root of this tree.
	 */
	public Node getRoot() {
		return this.root;
	}

	public int totalRotationCount() {
		return this.rotations;
	}
}

