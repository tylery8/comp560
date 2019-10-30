package qubic;

public interface Qubic {
	
	// Returns a Qubic in starting position where the cpu use the uniform utility function
	public static Qubic newGame() { return new QubicImpl(); }
	
	// Returns a Qubic in starting position where the cpu will follow the
	// specified utility function
	public static Qubic newGame(UtilityFunction utility_function) { return new QubicImpl(utility_function); }
	
	// Returns true if it is X's turn, false otherwise
	public boolean xTurn();
	
	// Returns 1.0 if X won, -1.0 if O won, 0.0 if drawn, and null otherwise
	public Double winner();
	
	// Returns the Qubic that results from making a move at the given location
	public Qubic move(int plane, int line, int index);
	
	// Returns the Qubic that results from exploiting its utility_function or exploring
	// if the depth is 0. In the case of a tie, a random best move is returned
	public Qubic move(int depth);
	
	// Returns the utility_function
	public UtilityFunction getUtilityFunction();
	
	// Updates utility_function based on the current reward
	public void updateUtilityFunction(double learning_rate);

	// Prints the Qubic in a readable format
	public void print();

}
