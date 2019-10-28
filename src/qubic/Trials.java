package qubic;

import java.util.Scanner;

public class Trials {

	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);		
		UtilityFunction learned_values = runTrials(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
		
//		playGame(learned_values, true);
		
	}
	
	public static UtilityFunction runTrials(int n1, int n2, int n3) {
		UtilityFunction learned_values = new UtilityFunction();
		for (double trials_done = 0; trials_done < n3; trials_done++) {
			if (trials_done == n1) {
				System.out.println("After " + n1 + " trials:");
				learned_values.print();
			}
			if (trials_done == n2) {
				System.out.println("After " + n2 + " trials:");
				learned_values.print();
			}
			
			runTrial(learned_values, 1/(trials_done+1), Math.pow(trials_done/n3, 2));
		}
		System.out.println("After " + n3 + " trials:");
		learned_values.print();
		return learned_values;
	}
	
	private static void runTrial(UtilityFunction uf, double learning_rate, double exploitation_rate) {
		Qubic trial = Qubic.newGame(uf);
		boolean play_as_x = Math.random() < .5;
		while (trial.winner() == null)
			trial = trial.xTurn() ^ play_as_x ? trial.move(false) : trial.move(Math.random() < exploitation_rate);
		trial.updateUtilityFunction(learning_rate);
	}

	public static void playGame(UtilityFunction utility_function, boolean player_is_x) {
		Scanner scanner = new Scanner(System.in);
		Qubic game = Qubic.newGame(utility_function);
		game.print();
		while (game.winner() == null)
			try {
				game = game.xTurn() ^ player_is_x ? game.move(true) : game.move(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
				game.print();
			} catch (IllegalArgumentException e) {
				System.err.println("Illegal Move. Try again");
			}
	}
	
	public static double learningRate(double proportion_done) {
		return 0.2*(1-1/(1+Math.pow(Math.E,-2*(proportion_done-0.5))));
	}

}
