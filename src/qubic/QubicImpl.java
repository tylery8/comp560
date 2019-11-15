package qubic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// QubicImpl implements the Qubic interface (see Qubic) using a long to represent the
// position of all the X's (1 for an X, 0 for not an X) and another long to represent the
// position of all the O's (1 for an O, 0 for not an O). Additionally, each QubicImpl
// stores and updates a utility_function that maps all SquareTypes to a double value.

public class QubicImpl implements Qubic {

	private long _x;
	private long _o;

	private static Map<QubicImpl, Double> _transposition_table;
	
	// Comparator used to order moves without evaluating the two moves. It simply
	// accesses their values from the transposition table. If the QubicImpl is not
	// in the table, it is put later in the move ordering (this does not happen often
	// because the minimax function uses iterative deepening)
	private static final Comparator<QubicImpl> MOVE_ORDER = new Comparator<QubicImpl>() {
		public int compare(QubicImpl q1, QubicImpl q2) {
			Double q1_value = _transposition_table.get(q1);
			Double q2_value = _transposition_table.get(q2);
			if (q2_value == null)
				return 1;
			if (q1_value == null)
				return -1;
			return q1_value.compareTo(q2_value);
		}
	};

	// The long representations of the 74 different win cases
	private static final long[] WIN_PATTERNS = new long[] {
			0b0000000000000000000000000000000000000000000000000000000000001111L,
			0b0000000000000000000000000000000000000000000000000000000011110000L,
			0b0000000000000000000000000000000000000000000000000000111100000000L,
			0b0000000000000000000000000000000000000000000000001111000000000000L,
			0b0000000000000000000000000000000000000000000011110000000000000000L,
			0b0000000000000000000000000000000000000000111100000000000000000000L,
			0b0000000000000000000000000000000000001111000000000000000000000000L,
			0b0000000000000000000000000000000011110000000000000000000000000000L,
			0b0000000000000000000000000000111100000000000000000000000000000000L,
			0b0000000000000000000000001111000000000000000000000000000000000000L,
			0b0000000000000000000011110000000000000000000000000000000000000000L,
			0b0000000000000000111100000000000000000000000000000000000000000000L,
			0b0000000000001111000000000000000000000000000000000000000000000000L,
			0b0000000011110000000000000000000000000000000000000000000000000000L,
			0b0000111100000000000000000000000000000000000000000000000000000000L,
			0b1111000000000000000000000000000000000000000000000000000000000000L,

			0b0000000000000000000000000000000000000000000000000001000100010001L,
			0b0000000000000000000000000000000000000000000000000010001000100010L,
			0b0000000000000000000000000000000000000000000000000100010001000100L,
			0b0000000000000000000000000000000000000000000000001000100010001000L,
			0b0000000000000000000000000000000000010001000100010000000000000000L,
			0b0000000000000000000000000000000000100010001000100000000000000000L,
			0b0000000000000000000000000000000001000100010001000000000000000000L,
			0b0000000000000000000000000000000010001000100010000000000000000000L,
			0b0000000000000000000100010001000100000000000000000000000000000000L,
			0b0000000000000000001000100010001000000000000000000000000000000000L,
			0b0000000000000000010001000100010000000000000000000000000000000000L,
			0b0000000000000000100010001000100000000000000000000000000000000000L,
			0b0001000100010001000000000000000000000000000000000000000000000000L,
			0b0010001000100010000000000000000000000000000000000000000000000000L,
			0b0100010001000100000000000000000000000000000000000000000000000000L,
			0b1000100010001000000000000000000000000000000000000000000000000000L,

			0b0000000000000001000000000000000100000000000000010000000000000001L,
			0b0000000000000010000000000000001000000000000000100000000000000010L,
			0b0000000000000100000000000000010000000000000001000000000000000100L,
			0b0000000000001000000000000000100000000000000010000000000000001000L,
			0b0000000000010000000000000001000000000000000100000000000000010000L,
			0b0000000000100000000000000010000000000000001000000000000000100000L,
			0b0000000001000000000000000100000000000000010000000000000001000000L,
			0b0000000010000000000000001000000000000000100000000000000010000000L,
			0b0000000100000000000000010000000000000001000000000000000100000000L,
			0b0000001000000000000000100000000000000010000000000000001000000000L,
			0b0000010000000000000001000000000000000100000000000000010000000000L,
			0b0000100000000000000010000000000000001000000000000000100000000000L,
			0b0001000000000000000100000000000000010000000000000001000000000000L,
			0b0010000000000000001000000000000000100000000000000010000000000000L,
			0b0100000000000000010000000000000001000000000000000100000000000000L,
			0b1000000000000000100000000000000010000000000000001000000000000000L,

			0b0000000000000000000000000000000000000000000000001000010000100001L,
			0b0000000000000000000000000000000010000100001000010000000000000000L,
			0b0000000000000000100001000010000100000000000000000000000000000000L,
			0b1000010000100001000000000000000000000000000000000000000000000000L,

			0b0000000000000000000000000000000000000000000000000001001001001000L,
			0b0000000000000000000000000000000000010010010010000000000000000000L,
			0b0000000000000000000100100100100000000000000000000000000000000000L,
			0b0001001001001000000000000000000000000000000000000000000000000000L,

			0b0000000000001000000000000000010000000000000000100000000000000001L,
			0b0000000010000000000000000100000000000000001000000000000000010000L,
			0b0000100000000000000001000000000000000010000000000000000100000000L,
			0b1000000000000000010000000000000000100000000000000001000000000000L,

			0b0000000000000001000000000000001000000000000001000000000000001000L,
			0b0000000000010000000000000010000000000000010000000000000010000000L,
			0b0000000100000000000000100000000000000100000000000000100000000000L,
			0b0001000000000000001000000000000001000000000000001000000000000000L,

			0b0001000000000000000000010000000000000000000100000000000000000001L,
			0b0010000000000000000000100000000000000000001000000000000000000010L,
			0b0100000000000000000001000000000000000000010000000000000000000100L,
			0b1000000000000000000010000000000000000000100000000000000000001000L,

			0b0000000000000001000000000001000000000001000000000001000000000000L,
			0b0000000000000010000000000010000000000010000000000010000000000000L,
			0b0000000000000100000000000100000000000100000000000100000000000000L,
			0b0000000000001000000000001000000000001000000000001000000000000000L,

			0b1000000000000000000001000000000000000000001000000000000000000001L,

			0b0001000000000000000000100000000000000000010000000000000000001000L,

			0b0000000000001000000000000100000000000010000000000001000000000000L,

			0b0000000000000001000000000010000000000100000000001000000000000000L };

	// Creates a QubicImpl in starting position
	public QubicImpl() {
		_x = 0;
		_o = 0;
	}

	// Creates a QubicImpl based that is the result of the previous QubicImpl after the given
	// move has been taken
	private QubicImpl(QubicImpl previous, long move) {
		_x = previous._x;
		_o = previous._o;

		if (xTurn())
			_x |= move;
		else
			_o |= move;
	}

	// See Qubic
	@Override
	public boolean xTurn() {
		return numOnes(_x|_o) % 2 == 0;
	}

	// See Qubic
	@Override
	public Integer winner() {
		for (long win_pattern : WIN_PATTERNS) {
			if ((_x & win_pattern) == win_pattern)
				return 1;
			if ((_o & win_pattern) == win_pattern)
				return -1;
		}
		return (~(_x | _o) == 0) ? 0 : null;
	}

	// See Qubic
	@Override
	public Qubic move(int plane, int line, int index) {
		if (plane < 0 || line < 0 || index < 0 || plane > 3 || line > 3 || index > 3)
			throw new IllegalArgumentException();
		long move = 1L << (16 * plane + 4 * line + index);
		if (((_x | _o) & move) == 0 && winner() == null)
			return new QubicImpl(this, move);
		throw new IllegalArgumentException();
	}
	
	// See Qubic
	@Override
	public Qubic move(UtilityFunction utility_function, int depth) {
		return move(utility_function, depth, depth);
	}

	// See Qubic
	@Override
	public Qubic move(UtilityFunction utility_function, int min_depth, int max_depth) {
		QubicImpl overall_best = null;
		List<QubicImpl> next_moves = nextMoves();

		// Random move
		if (max_depth <= 0)
			return next_moves.get(0);

		_transposition_table = new HashMap<QubicImpl, Double>();
		
		// Uses iterative deepening to calculate the values of each move starting from a
		// depth of 1 up to a depth of max_depth. Moves and values are saved in the transposition
		// table to help with move ordering for the next iteration. The alphabeta method also
		// contributes to the transposition table
		for (int depth = 1; depth <= max_depth; depth++) {
			QubicImpl best = null;
			double best_eval = 0;
			Collections.sort(next_moves, MOVE_ORDER);
			for (QubicImpl move : next_moves) {
				double move_eval = move.alphabeta(utility_function, depth - 1, max_depth-min_depth, -1000 - max_depth, 1000 + max_depth);
				_transposition_table.put(move, move_eval);
				if (best == null || xTurn() && move_eval > best_eval || !xTurn() && move_eval < best_eval) {
					best = move;
					best_eval = move_eval;
				}
			}
			overall_best = best;
		}

		return overall_best;
	}

	// See Qubic
	@Override
	public void updateUtilityFunction(UtilityFunction utility_function, double learning_rate) {
		Integer winner = winner();
		if (winner == null || winner == 0)
			return;

		// Counting how many "winners" and "losers" each SquareType has
		long winner_pattern = winner > 0 ? _x : _o;
		long loser_pattern = winner < 0 ? _x : _o;
		double winner_total = 0;
		double loser_total = 0;
		Map<SquareType, Integer> winner_spread = new HashMap<SquareType, Integer>();
		Map<SquareType, Integer> loser_spread = new HashMap<SquareType, Integer>();

		for (SquareType st : SquareType.values()) {
			winner_spread.put(st, 0);
			loser_spread.put(st, 0);
		}
		for (long i = 1L; i != 0; i <<= 1) {
			if ((winner_pattern & i) == i) {
				winner_total++;
				winner_spread.put(SquareType.valueOf(i), winner_spread.get(SquareType.valueOf(i)) + 1);
			}
			if ((loser_pattern & i) == i) {
				loser_total++;
				loser_spread.put(SquareType.valueOf(i), loser_spread.get(SquareType.valueOf(i)) + 1);
			}
		}

		// Use the counts to calculate a net value for each SquareType, then update
		// the value of that SquareType using the reinforced learning formula
		for (SquareType st : SquareType.values()) {
			double net_value = winner_spread.get(st) / winner_total - loser_spread.get(st) / loser_total;
			utility_function.setValue(st, (1 - learning_rate) * utility_function.getValue(st) + learning_rate * net_value);
		}
	}

	// See Qubic
	@Override
	public void print() {
		String out = "";
		for (long i = 1L; i != 0; i <<= 1) {
			if ((_x & i) != 0)
				out += "X";
			else if ((_o & i) != 0)
				out += "O";
			else
				out += " ";
		}

		for (int i = 3; i >= 0; i--) {
			for (int j = 3; j >= 0; j--) {
				for (int k = 0; k < 4; k++) {
					System.out.print(out.charAt(16 * i + 4 * j + k));
					if (k == 3)
						System.out.println();
					else
						System.out.print("|");
				}
			}
			System.out.println();
		}
		if (winner() == null)
			System.out.println("_______");
		else if (winner() > 0)
			System.out.println("X won!");
		else if (winner() < 0)
			System.out.println("O won!");
		else
			System.out.println("Draw!");
		System.out.println();
		System.out.println();
	}
	
	// Recursive minimax algorithm with alpha-beta pruning. Move ordering and quiescence
	// search are also included
	private double alphabeta(UtilityFunction utility_function, int depth, int static_buffer, double alpha, double beta) {
		List<QubicImpl> next_moves = nextMoves();
		
		// If the game is over return the appropriate value
		if (next_moves.size() == 0)
			return (1000 + depth) * winner();
		
		// If the depth has been reached (or it is static and in the static buffer region)
		// return the evaluation at its current position
		if (depth <= 0 || depth <= static_buffer && isStatic())
			return utility(utility_function);

		// Order moves by what was found in previous iterations and stored in the transposition
		// table
		Collections.sort(next_moves, MOVE_ORDER);

		// Standard alphabeta algorithm (with storing values in transposition table)
		if (xTurn()) {
			double max = -1000 - depth;
			for (QubicImpl move : next_moves) {
				double evaluation = move.alphabeta(utility_function, depth - 1, static_buffer, alpha, beta);
				_transposition_table.put(move, evaluation);
				if (evaluation > max)
					max = evaluation;
				if (evaluation > alpha)
					alpha = evaluation;
				if (alpha >= beta)
					return alpha;
			}
			return max;
		} else {
			double min = 1000 + depth;
			for (QubicImpl move : next_moves) {
				double evaluation = move.alphabeta(utility_function, depth - 1, static_buffer, alpha, beta);
				_transposition_table.put(move, evaluation);
				if (evaluation < min)
					min = evaluation;
				if (evaluation < beta)
					beta = evaluation;
				if (alpha >= beta)
					return beta;
			}
			return min;
		}
	}

	// Returns a list of all the possible QubicImpls that are one ply ahead of the
	// current position
	private List<QubicImpl> nextMoves() {
		List<QubicImpl> next_moves = new ArrayList<QubicImpl>();
		if (winner() == null)
			for (long move = 1L; move != 0; move <<= 1)
				if (((_x | _o) & move) == 0)
					next_moves.add(new QubicImpl(this, move));
		Collections.shuffle(next_moves);
		return next_moves;
	}

	// Returns the utility of the current position according to the utility_function
	private double utility(UtilityFunction utility_function) {
		double total = 0;
		for (long i = 1L; i != 0; i <<= 1) {
			if ((_x & i) != 0)
				total += utility_function.getValue(SquareType.valueOf(i));
			else if ((_o & i) != 0)
				total -= utility_function.getValue(SquareType.valueOf(i));
		}
		return total;
	}
	
	// Determines if the current position is static. A static position is defined as
	// any position in which the moving player is not being forced to go to a particular
	// square (in other words, there is no active three-in-a-row for the opponent)
	private boolean isStatic() {
		boolean x_turn = xTurn();
		long defender = x_turn ? _x : _o;
		long attacker = x_turn ? _o : _x;
		for (long win_pattern : WIN_PATTERNS)
			if (numOnes(attacker & win_pattern) == 3 && (defender & win_pattern) == 0)
				return false;
		return true;
	}
	
	// Counts the number of ones in a long
	private static int numOnes(long l) {
		return l == 0 ? 0 : numOnes(l & (l-1)) + 1;
	}

	@Override
	public boolean equals(Object other) {
		QubicImpl other_qubic = (QubicImpl) other;
		return _x == other_qubic._x && _o == other_qubic._o;
	}

}