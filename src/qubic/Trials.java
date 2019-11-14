package qubic;

import java.util.Scanner;

public class Trials {

	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);		
		UtilityFunction learned_values = runTrials(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
		
		playGame(learned_values, true);
		
	}
	
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
	
	private static void runTrial(UtilityFunction uf, double current_trial, int total_trials) {
		double learning_rate = 1/current_trial;
		double exploitation_rate = current_trial/total_trials-.5;
		
		Qubic trial = Qubic.newGame(uf);
		boolean is_x = Math.random() < 0.5;
		while (trial.winner() == null)
			trial = trial.xTurn() ^ is_x ? trial.move(0) : trial.move(Math.random() < exploitation_rate ? 1 : 0);
		trial.updateUtilityFunction(learning_rate);
	}

	public static void playGame(UtilityFunction utility_function, boolean player_is_x) {
		Scanner scanner = new Scanner(System.in);
		Qubic game = Qubic.newGame(utility_function);
		game.print();
		while (game.winner() == null)
			try {
				game = game.xTurn() ^ player_is_x ? game.move(4) : game.move(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
				game.print();
			} catch (IllegalArgumentException e) {
				System.err.println("Illegal Move. Try again");
			}
	}

}
