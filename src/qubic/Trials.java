package qubic;

import java.util.Scanner;

// Trials contains the driver method and static methods that allow the Qubic to train a
// UtilityFunction and then play a game using those values

public class Trials {

	public static void main(String[] args) { 
		Scanner input = new Scanner(System.in);
			
		System.out.println("Number of trials (input: n1 n2 n3):");
		UtilityFunction learned_values = runTrials(input.nextInt(), input.nextInt(), input.nextInt());
		
		try {
			while(true) {
				System.out.println("Play game (input: x_uses_ai o_uses_ai) or exit (any other input):");
				playGame(learned_values, input.nextBoolean(), input.nextBoolean());
			}
		} catch (Exception e) {
			
		}
		
	}
	
	// Runs n3 trials and prints out the values in the UtilityFunction after n1, 
	// n2, and n3 trials have been run
	public static UtilityFunction runTrials(int n1, int n2, int n3) {
		UtilityFunction learned_values = new UtilityFunction();
		for (int i = 0; i < n3; i++) {
			if (i == n1 || i == n2) {
				System.out.println("After " + i + " trials:");
				learned_values.print();
			}
			runTrial(learned_values, i+1, n3);
		}
		
		System.out.println("After " + n3 + " trials:");
		learned_values.print();
		
		return learned_values;
	}
	
	// Runs a single trial by playing a game against itself. It plays against a random opponent
	// and chooses its own squares randomly or according to a one ply search with the
	// utility_function based on the exploitation_rate. The utility_function is updated of the
	// result and changes its values according to the learning_rate
	private static void runTrial(UtilityFunction uf, double current_trial, int total_trials) {
		double learning_rate = 1/current_trial;
		double exploitation_rate = current_trial/total_trials-.5;
		
		Qubic trial = Qubic.newGame();
		boolean is_x = Math.random() < 0.5;
		while (trial.winner() == null)
			trial = trial.xTurn() ^ is_x ? trial.move(uf, 0) : trial.move(uf, Math.random() < exploitation_rate ? 1 : 0);
		trial.updateUtilityFunction(uf, learning_rate);
	}

	// Plays a game of Qubic using ai for 0, 1, or 2 of the players according to the inputs. 
	// If an ai is used, it evaluates positions according to the given utility_function
	public static void playGame(UtilityFunction uf, boolean x_uses_ai, boolean o_uses_ai) {
		Scanner user = new Scanner(System.in);
		Qubic game = Qubic.newGame();
		Qubic previous = game;
		game.print();
		while (game.winner() == null) {
			if (game.xTurn() ? x_uses_ai : o_uses_ai) {
				game = game.move(uf,2,4);
			} else {
				System.out.println("Make move (input: plane line index) or undo (any other input):");
				try {
					int plane = Integer.parseInt(user.next());
					int line = Integer.parseInt(user.next());
					int index = Integer.parseInt(user.next());
					previous = game;
					game = game.move(plane, line, index);
				} catch (NumberFormatException n) {
					System.err.println("undoing...");
					game = previous;
				} catch (IllegalArgumentException e) {
					System.err.println("Illegal Move. Try again");
				}
			}
			game.print();
		}
		
	}

}
