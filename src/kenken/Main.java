package kenken;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		
		// Uses standard input as a default but attempts to read input from a file
		Scanner reader = new Scanner(System.in);
		try {
			reader = new Scanner(new File(System.getProperty("user.dir") + "/input.txt"));
		} catch (FileNotFoundException e) {
			
		}
		
		// Read the first line to determine n
		String first_line = reader.next();
		int n = first_line.length();
		
		// Initialize n by n layout for the puzzle and HashMap that will map each
		// character to a mathematical expression
		char[][] layout = new char[n][n];
		Map<Character, String> cages = new HashMap<Character, String>();
		
		// Reread the first line and put it in the layout and initialize that
		// letter in the map
		for (int i = 0; i < n; i++) {
			layout[i][0] = first_line.charAt(i);
			cages.put(first_line.charAt(i), null);
		}
		
		// Repeat for all other lines
		for (int j = 1; j < n; j++) {
			first_line = reader.next();
			for (int i = 0; i < n; i++) {
				layout[i][j] = first_line.charAt(i);
				cages.put(first_line.charAt(i), null);
			}
		}

		// For each letter in the map, map it to its corresponding expression
		for (int i = 0; i < cages.keySet().size(); i++) {
			String line = reader.next();
			cages.put(line.charAt(0), line.substring(2));
		}
		reader.close();
		
		// Solve the puzzle using backtracking, optimized backtracking, and local search.
		// All are set to the default limit of 100000 nodes/iterations which can be changed
		// with an optional argument
		KenKen obt_puzzle = new KenKen(layout, cages);
		String obt_nodes = obt_puzzle.backtrack(true);
		
		KenKen bt_puzzle = new KenKen(layout, cages);
		String bt_nodes = bt_puzzle.backtrack(false, obt_puzzle.solved() ? 100000 : 0);
		
		KenKen ls_puzzle = new KenKen(layout, cages);
		String ls_iterations = ls_puzzle.localSearch();
		
		// Print the puzzle and the respective statistics for backtracking, optimized
		// backtracking, and local search
		if (obt_puzzle.solved()) obt_puzzle.print();
		else if (ls_puzzle.solved()) ls_puzzle.print();
		else System.out.println("*No method successfully solved the puzzle*" + '\n');
		
		System.out.println(bt_nodes);
		System.out.println(obt_nodes);
		System.out.println(ls_iterations);
	}

}
