package qubic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QubicImpl implements Qubic {

	private long _x;
	private long _o;
	private UtilityFunction _utility_function;

	private static Map<QubicImpl, Double> _transposition_table;

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

	public QubicImpl() {
		this(new UtilityFunction());
	}

	public QubicImpl(UtilityFunction utility_function) {
		_x = 0;
		_o = 0;
		_utility_function = utility_function;
	}

	private QubicImpl(QubicImpl previous, long move) {
		_x = previous._x;
		_o = previous._o;
		_utility_function = previous._utility_function;

		if (xTurn())
			_x |= move;
		else
			_o |= move;
	}

	public boolean xTurn() {
		int count = 0;
		for (long taken = _x | _o; taken != 0; taken &= taken - 1)
			count++;
		return count % 2 == 0;
	}

	public Integer winner() {
		if (hasWon(_x))
			return 1;
		if (hasWon(_o))
			return -1;
		if (~(_x | _o) == 0)
			return 0;
		return null;
	}

	public Qubic move(int plane, int line, int index) {
		if (plane < 0 || line < 0 || index < 0 || plane > 3 || line > 3 || index > 3)
			throw new IllegalArgumentException();
		return move(1L << (16 * plane + 4 * line + index));
	}

	private Qubic move(long move) {
		if (((_x | _o) & move) == 0 && winner() == null)
			return new QubicImpl(this, move);
		throw new IllegalArgumentException();
	}

	public Qubic move(int max_depth) {
		_transposition_table = new HashMap<QubicImpl, Double>();

		QubicImpl overall_best = null;

		List<QubicImpl> next_moves = nextMoves();

		if (max_depth <= 0)
			return next_moves.get((int) (Math.random() * next_moves.size()));

		for (int depth = 1; depth <= max_depth; depth++) {

			sortMoves(next_moves);

			QubicImpl best = null;
			double best_eval = 0;
			for (QubicImpl move : next_moves) {
				double move_eval = move.alphabeta(depth - 1, -1000 - max_depth, 1000 + max_depth);
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

	private double alphabeta(int depth, double alpha, double beta) {
		List<QubicImpl> next_moves = nextMoves();
		if (next_moves.size() == 0)
			return (1000 + depth) * winner();
		if (depth <= 0)
			return utility();

		sortMoves(next_moves);

		if (xTurn()) {
			double max = -1000 - depth;
			for (QubicImpl move : next_moves) {
				double evaluation = move.alphabeta(depth - 1, alpha, beta);
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
				double evaluation = move.alphabeta(depth - 1, alpha, beta);
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

	public UtilityFunction getUtilityFunction() {
		return _utility_function;
	}

	public void updateUtilityFunction(double learning_rate) {
		Integer winner = winner();
		if (winner == null || winner == 0)
			return;

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

		for (SquareType st : SquareType.values()) {
			double net_value = winner_spread.get(st) / winner_total - loser_spread.get(st) / loser_total;
			_utility_function.setValue(st,
					(1 - learning_rate) * _utility_function.getValue(st) + learning_rate * net_value);
		}
	}

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

	private List<QubicImpl> nextMoves() {
		List<QubicImpl> next_moves = new ArrayList<QubicImpl>();
		if (winner() == null)
			for (long move = 1L; move != 0; move <<= 1)
				if (((_x | _o) & move) == 0)
					next_moves.add(new QubicImpl(this, move));
		Collections.shuffle(next_moves);
		return next_moves;
	}

	private double utility() {
		double total = 0;
		for (long i = 1L; i != 0; i <<= 1) {
			if ((_x & i) != 0)
				total += _utility_function.getValue(i);
			else if ((_o & i) != 0)
				total -= _utility_function.getValue(i);
		}
		return total;
	}

	private static boolean hasWon(long player) {
		for (long win_pattern : WIN_PATTERNS)
			if ((player & win_pattern) == win_pattern)
				return true;
		return false;
	}

	private static void sortMoves(List<QubicImpl> next_moves) {
		Collections.sort(next_moves, new Comparator<QubicImpl>() {

			public int compare(QubicImpl q1, QubicImpl q2) {
				Double q1_value = _transposition_table.get(q1);
				Double q2_value = _transposition_table.get(q2);
				if (q2_value == null)
					return 1;
				if (q1_value == null)
					return -1;
				return q1_value.compareTo(q2_value);
			}
		});
	}

	@Override
	public boolean equals(Object other) {
		QubicImpl other_qubic = (QubicImpl) other;
		return _x == other_qubic._x && _o == other_qubic._o;
	}

}