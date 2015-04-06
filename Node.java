
package editortrees;


// A node in a height-balanced binary tree with rank.

public class Node {
	
	enum Code {SAME, LEFT, RIGHT};
	
	String sLeft = "";
	String sRight = "";
	int centerLeft = 0;
	int centerRight = 0;
	char element;            
	Node left, right; // subtrees
	int size;
	Code balance; 
	Node parent;  // You may want this field.
	
	public Node(){ //basic null node constructor
		this.parent = null;
		this.element = '\0';
		this.left = null;
		this.right = null;
		this.balance = Code.SAME;
		this.size = 0;
	}
	
	public Node(Node p, char c, Node left, Node right){ //node constructor that is needed so it can be inserted
		this.parent = p;
		this.element = c;
		this.left = left;
		this.right = right;
		this.setBalance();
		this.setSize();
	}
	
	public Node(Node p, char c){ //basic node constructor with data
		this.parent = p;
		this.element = c;
		this.left = null;
		this.right = null;
		this.balance = Code.SAME;
		this.setSize();
	}

	/**
	 * Sets the balance of the node based on height O(log n)
	 */
	public void setBalance() { //sets the balance
		int leftHeight = -1;
		int rightHeight = -1;
		if(this.left != null)
			leftHeight = this.left.height(); //if there is a left, then recurse through the left
		if(this.right != null)
			rightHeight = this.right.height(); //if there is a right, then recurse through the right
		if(leftHeight - rightHeight<0)
			this.balance = Code.RIGHT; //if there is a left has a lesser height then the right,then code = \
		else if(leftHeight - rightHeight>0)
			this.balance = Code.LEFT; //if there is a left has a bigger height then the right,then code = /
		else
			this.balance = Code.SAME; //if there is a left height = the right,then code is =
	}

	/**
	 * Finds the height in O(log n) time
	 * 
	 * @return height
	 */
	public int height() {
		if (this.left == null && this.right == null)
			return 0; //base case. this is for when the leaf is hit.
		else if (this.left == null)
			return 1 + this.right.height(); // if there is a right. then add 1 and recurse down the right
		else if (this.right == null)
			return 1 + this.left.height(); // if there is a right. then add 1 and recurse down the left
		if(this.balance == Code.SAME || this.balance == Code.LEFT)
			return 1 + this.left.height(); // if there is 2 childern or the parent is left slighted with children, add 1 and recurse down to the left
		else
			return 1 + this.right.height(); // otherwise go down right	
		}
	
	/**
	 * Things that must happen in these methods:
	 * DONE might need to check children's balance
	 * DONE The current right node must be made the root node.
	 * DONE The root node's right node must be set to the right node's left node.
	 * DONE The right node's left node must be set to the current root node.
	 */
	public Node rotateLeft(){
		if(this.parent!=null){ 
			if(this.equals(this.parent.left)) // if there is a parent & the node is the left child then make the node's original position the right child 
				this.parent.left = this.right;
			else if(this.equals(this.parent.right)) // if there is a parent & the node is the right child then make the node's right child the parent's right child
				this.parent.right = this.right;
		}
		Node temporaryNode = null;
		if(this.right.left!=null){
			temporaryNode = this.right.left;
			temporaryNode.parent = this; //takes care of the double case
		}
		this.right.left = this;
		this.right.parent = this.parent;
		this.parent = this.right;
		this.right = temporaryNode; 
		
		this.setSize();
		this.parent.setSize();
		this.parent.balance = Node.Code.SAME; //since the parent always has two children the code is made to be same
		
		return this.parent;
	}
	
	public Node rotateRight(){
		if(this.parent!=null){
			if(this.equals(this.parent.left))
				this.parent.left = this.left; // if there is a parent & the node is the left child then make the node's original position the left child 
			else if(this.equals(this.parent.right))
				this.parent.right = this.left;// if there is a parent & the node is the right child then make the node's right child the parent's left child
		}
		Node temporaryNode = null;
		if(this.left.right!=null){ 
			temporaryNode = this.left.right;
			temporaryNode.parent = this; //takes care of the double case
		}
		this.left.right = this;
		this.left.parent = this.parent;
		this.parent = this.left;
		this.left = temporaryNode;
		
		
		this.setSize();
		this.parent.setSize();
		this.parent.balance = Node.Code.SAME;
		
		return this.parent;

	}

	/**
	 * Uses our size field to set the size of the succeeding nodes.
	 * Because we store size as a field, we need only access its two children in order to reset size
	 */
	
	public void setSize() {
		if(this.left == null && this.right == null)
			this.size = 1;
		else if(this.left == null)
			this.size = 1 + this.right.size;
		else if(this.right == null)
			this.size = 1 + this.left.size;
		else
			this.size = 1 + this.left.size + this.right.size;
	}
	
	/**
	 * @return the inOrder position in this tree.
	 */
	
	public int getInOrderPos(){
		Node currentNode = this;
		int pos = 0;
		
		if(currentNode.left != null)
			pos += currentNode.left.size;
		
		while(currentNode.parent != null) {
			if(currentNode.equals(currentNode.parent.right)) {
				pos += 1;
				if(currentNode.parent.left != null) {
					pos += currentNode.parent.left.size;
				}
			}
			currentNode = currentNode.parent;
		}	
		return pos;
		
	}
	
	/**
	 * @return string representation of an inOrder traversal of this tree.
	 */
	
	public String inOrderString(){
		String inOrderString = "";
		if (this.left != null)
			inOrderString += this.left.inOrderString(); // Left
		if(this.element!='\0')
			inOrderString += this.element; // Node
		if (this.right != null)
			inOrderString += this.right.inOrderString();
		return inOrderString;
	}

	public int shiftLeft() {
		if(this.balance == Code.LEFT)
			return 1;
		else if(this.balance == Code.SAME)
			this.balance = Code.LEFT;
		else{
			this.balance = Code.SAME;
			return 2;
		}
		return 0;
	}

	public int shiftRight() {
		if(this.balance == Code.RIGHT)
			return 1;
		else if(this.balance == Code.SAME)
			this.balance = Code.RIGHT;
		else{
			this.balance = Code.SAME;
			return 2;
		}
		return 0;
	}

}
