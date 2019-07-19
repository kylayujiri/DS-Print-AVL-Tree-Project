import net.datastructures.Entry;
import net.datastructures.Position;

//import net.datastructures.*;
public class ProgProject3 {
	
	public static void main(String[] args) {
		
		String[] inputs = {
				
				"CBDAE",
				"DACBEFMLGHJK",
				"JABCDEFISN",
				// my own examples below
				"KYLAUJIR",
				"HELOWRD",
				"SORYFMULTIP",
		};
		
		for (int k=0; k<inputs.length;k++) {
			
			MyAVLTreeMap<String,Integer> mytree = new MyAVLTreeMap<String,Integer>();
			
			// this code populates your tree
			for (int i =0 ; i< inputs[k].length(); i++) {
				mytree.put(inputs[k].substring(i,i+1), 1);
			}
			
			System.out.println("Input of " + inputs[k]);
			
			// this line of code call the printTree method you are to write
			mytree.printTree();
			
			System.out.println();
			
		}

	}
	
}
