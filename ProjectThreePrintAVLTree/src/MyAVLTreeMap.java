
import net.datastructures.*;
import java.util.Comparator;


public class MyAVLTreeMap<K,V> extends TreeMap<K,V> {

	/** Constructs an empty map using the natural ordering of keys. */
	public MyAVLTreeMap() { super(); }

	/**
	 * Constructs an empty map using the given comparator to order keys.
	 * @param comp comparator defining the order of keys in the map
	 */
	public MyAVLTreeMap(Comparator<K> comp) { super(comp); }

	/** Returns the height of the given tree position. */
	protected int height(Position<Entry<K,V>> p) {
		return tree.getAux(p);
	}

	/** Recomputes the height of the given position based on its children's heights. */
	protected void recomputeHeight(Position<Entry<K,V>> p) {
		tree.setAux(p, 1 + Math.max(height(left(p)), height(right(p))));
	}

	/** Returns whether a position has balance factor between -1 and 1 inclusive. */
	protected boolean isBalanced(Position<Entry<K,V>> p) {
		return Math.abs(height(left(p)) - height(right(p))) <= 1;
	}

	/** Returns a child of p with height no smaller than that of the other child. */
	protected Position<Entry<K,V>> tallerChild(Position<Entry<K,V>> p) {
		if (height(left(p)) > height(right(p))) return left(p);     // clear winner
		if (height(left(p)) < height(right(p))) return right(p);    // clear winner
		// equal height children; break tie while matching parent's orientation
		if (isRoot(p)) return left(p);                 // choice is irrelevant
		if (p == left(parent(p))) return left(p);      // return aligned child
		else return right(p);
	}

	/**
	 * Utility used to rebalance after an insert or removal operation. This traverses the
	 * path upward from p, performing a trinode restructuring when imbalance is found,
	 * continuing until balance is restored.
	 */
	protected void rebalance(Position<Entry<K,V>> p) {
		int oldHeight, newHeight;
		do {
			oldHeight = height(p);                       // not yet recalculated if internal
			if (!isBalanced(p)) {                        // imbalance detected
				// perform trinode restructuring, setting p to resulting root,
				// and recompute new local heights after the restructuring
				p = restructure(tallerChild(tallerChild(p)));
				recomputeHeight(left(p));
				recomputeHeight(right(p));
			}
			recomputeHeight(p);
			newHeight = height(p);
			p = parent(p);
		} while (oldHeight != newHeight && p != null);
	}

	/** Overrides the TreeMap rebalancing hook that is called after an insertion. */
	@Override
	protected void rebalanceInsert(Position<Entry<K,V>> p) {
		rebalance(p);
	}

	/** Overrides the TreeMap rebalancing hook that is called after a deletion. */
	@Override
	protected void rebalanceDelete(Position<Entry<K,V>> p) {
		if (!isRoot(p))
			rebalance(parent(p));
	}

	/** Ensure that current tree structure is valid AVL (for debug use only). */
	private boolean sanityCheck() {
		for (Position<Entry<K,V>> p : tree.positions()) {
			if (isInternal(p)) {
				if (p.getElement() == null)
					System.out.println("VIOLATION: Internal node has null entry");
				else if (height(p) != 1 + Math.max(height(left(p)), height(right(p)))) {
					System.out.println("VIOLATION: AVL unbalanced node with key " + p.getElement().getKey());
					dump();
					return false;
				}
			}
		}
		return true;
	}





	// 2D array to store elements in the tree
	// will be stored with the root at arr[0][0] and each subsequent level (n) will be placed at arr[n]
	public String[][] arr = new String[100][100];

	public void printTree() { // Put your code to print AVL tree here

		// if there's nothing to print, nothing happens
		if(root() == null) return;

		// call to printTreeTwo which is a recursive method that places the nodes in arr
		printTreeTwo(root(), 0);

		// find the last row and get the maximum number of elements in it
		int lastRow = 0;
		int numInLast = 0;

		for (int i = 0; i < arr.length; i++) {
			// the row after the last row will only have -1 and null in it
			boolean allNegOnes = true;
			for (int j = 0; j < arr[i].length; j++) {
				if (arr[i][j] != null && !arr[i][j].equals("-1")) {
					allNegOnes = false;
				}
			}
			if (allNegOnes) {
				lastRow = i - 1;
				numInLast = (int) Math.pow(2, lastRow); // the number of possible elements in row N is 2^N
				break;
			}
		}

		// new array to store each line to print
		// there is double the number of rows minus one to make room for the rows with the slashes
		String[] toPrint = new String[(lastRow + 1) * 2 - 1];

		// totalLength is the maximum length of the printout (the width of the last row)
		// each element in the last row should have 9 spaces between them
		int totalLength = numInLast + (numInLast * 9);

		// currentPrintRow keeps track of which row of the toPrint array we are on (since the for loop below loops through the rows of arr)
		int currentPrintRow = toPrint.length - 1;

		// loops backwards until it reaches the last row
		for (int i = lastRow; i >= 0; i--) {

			int numInCurrRow = (int) Math.pow(2, i);

			// this is for formatting: we want the elements to be displayed evenly within the total length
			int interiorSpaces = totalLength/numInCurrRow; // the space between elements (the max that fits in the total length
			int exteriorSpaces = (totalLength - (interiorSpaces * (numInCurrRow-1) + numInCurrRow))/2; // evenly divides the leftover spaces for the front and back of the line

			toPrint[currentPrintRow] = String.format("%" + exteriorSpaces + "s", "");
			for (int j = 0; j < numInCurrRow; j++) {
				if (arr[i][j] == null || arr[i][j].equals("-1")) {
					arr[i][j] = " ";
				}
				toPrint[currentPrintRow] += String.format("%-" + (interiorSpaces + 1) + "s", arr[i][j]);
			}
			toPrint[currentPrintRow] += String.format("%" + (exteriorSpaces + 1) + "s", "");

			if (i != 0) { // we make a row with slashes above every row that is not the top row

				// the following variables are used to determine the midpoint between the parent and child (this is where the slash will go)
				int numInNextRow = (int) Math.pow(2, i-1);
				int nextIntSpaces = totalLength/numInNextRow;
				int nextExtSpaces = (totalLength - (nextIntSpaces * (numInNextRow-1) + numInNextRow))/2;

				int oneToCompare = exteriorSpaces;
				int twoToCompare = nextExtSpaces;

				toPrint[currentPrintRow - 1] = ""; // initialize the row so we can add stuff to it

				for (int j = 0; j < numInCurrRow; j++) {

					// to get the position of the slash, we get the midpoint between oneToCompare and twoToCompare (they represent the child and parent respectively)
					int position = Math.min(oneToCompare, twoToCompare) + ((Math.abs(oneToCompare - twoToCompare))/2);

					if (!arr[i][j].equals(" ")) {
						// if there is no element, then no slash

						while (toPrint[currentPrintRow - 1].length() <= position) {
							toPrint[currentPrintRow - 1] += " ";
						}

						if (j % 2 == 0) {
							// if the position of the element is even, forward slash
							toPrint[currentPrintRow - 1] += "/";
						} else {
							// if odd, back slash
							toPrint[currentPrintRow - 1] += "\\";
						}
					}

					// since there are two children per parent, we only change twoToCompare every other time
					if (j % 2 == 0) {
						oneToCompare += interiorSpaces + 1;
					} else {
						oneToCompare += interiorSpaces + 1;
						twoToCompare += nextIntSpaces + 1;
					}

				}
			}
			// we subtract two (one for the row of elements and one for the row of slashes)
			currentPrintRow -= 2;
		}

		// this loop prints out the toPrint array
		for (int i = 0; i < toPrint.length; i++) {
			
			// it adds a println between every line
			if (toPrint[i].indexOf("/") >= 0 || toPrint[i].indexOf("\\") >= 0) {
				System.out.println();
				System.out.println(toPrint[i]);
				System.out.println();
			} else {
				System.out.println(toPrint[i]);
			}
		}

		System.out.println();
	}






	public void printTreeTwo(Position<Entry<K,V>> root, int num) {
		// this uses preorder tree traversal
		// num keeps track of which level we are on (also determines which row of arr we insert the nodes into)

		// if the root exists, we can add it to arr and continue with recursion
		if (root.getElement() != null) {

			// loops through the row in arr to find the first null space to insert the node
			for (int i = 0; i < arr[num].length; i++) {
				if (arr[num][i] == null) {
					arr[num][i] = root.getElement().getKey().toString();
					break;
				}
			}

			// recursive calls for the left and right nodes of root
			printTreeTwo(left(root), num+1);
			printTreeTwo(right(root), num+1);

		} else {

			// since the root does not exist, we insert -1 into the array at the appropriate place (-1 to be used later)
			for (int i = 0; i < arr[num].length; i++) {
				if (arr[num][i] == null) {
					arr[num][i] = "-1";
					break;
				}
			}

		}

	}





}
