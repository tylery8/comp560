package qubic;

// Qubic is an interface for a 4x4x4 TicTacToe game. It can initiate a board, determine the
// turn, determine the winner, move, manage a utility function, and print according to the
// descriptions below

public interface Qubic {
	
	// Returns a Qubic in starting position
	public static Qubic newGame() { return new QubicImpl(); }
	
	// Returns true if it is X's turn, false otherwise
	public boolean xTurn();
	
	// Returns 1 if X won, -1 if O won, 0 if drawn, and null otherwise
	public Integer winner();
	
	// Returns the Qubic that results from making a move at the given location. The
	// "plane" is a number between 0 and 3 where 0 represents the bottom plane and
	// 3 represents the top plane. The "line" is a number between 0 and 3 where 0
	// represents the lowest line within the plane and 3 represents the top line
	// within the plane. The "index" is a number between 0 and 3 where 0 represents
	// the leftmost index within the line and 3 represents the rightmost index within
	// the line
	public Qubic move(int plane, int line, int index);
	
	// Returns the Qubic that results from exploiting the utility_function or exploring
	// if the depth is 0. In the case of a tie, a random best move is returned
	public Qubic move(UtilityFunction utility_function, int depth);
	
	// Returns the Qubic that results from exploiting the utility_function or exploring
	// if the max_depth is 0. It will search to at least the min_depth and continue searching
	// until the max_depth if the position is not inactive (a win case is set up). In the case
	// of a tie, a random best move is returned
	public Qubic move(UtilityFunction utility_function, int min_depth, int max_depth);
	
	// Updates the given utility_function using the given learning rate. It will only
	// update if the game is over and not drawn. The value calculated for each SquareType
	// based on the finished Qubic is equal to (percent of wining pattern in the
	// SquareType - percent of losing pattern in the SquareType). The new value is a linear
	// combination of this value and the old value, using the learning rate as the factor
	public void updateUtilityFunction(UtilityFunction utility_function, double learning_rate);

	// Prints the Qubic with the planes "unstacked" where plane n prints before plane n-1. 
	// It will also print the end result if the game is over
	public void print();

}
