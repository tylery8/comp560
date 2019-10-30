package qubic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QubicImpl implements Qubic {
	
	private long _x;
	private long _o;
	private UtilityFunction _utility_function;
	
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
		for (long taken = _x|_o; taken != 0; taken &= taken-1)
			count++;
		return count % 2 == 0;
	}
	
	public Double winner() {
		if (hasWon(_x))
			return 1.0;
		if (hasWon(_o))
			return -1.0;
		if (~(_x|_o) == 0)
			return 0.0;
		return null;
	}
	
	public Qubic move(int plane, int line, int index) {
		if (plane < 0 || line < 0 || index < 0 || plane > 3 || line > 3 || index > 3)
			throw new IllegalArgumentException();
		return move(1L << (16*plane + 4*line + index));
	}
	
	private Qubic move(long move) {
		if (((_x|_o)&move) == 0 && winner() == null)
			return new QubicImpl(this, move);
		throw new IllegalArgumentException();
	}
	
	public Qubic move(int depth) {
		QubicImpl best = null;
		double best_eval = 0;
		for (QubicImpl move : nextMoves()) {
			double move_eval = move.alphabeta(depth - 1, -1000 - depth, 1000 + depth);
			if (best == null || xTurn() && move_eval > best_eval || !xTurn() && move_eval < best_eval) {
				best = move;
				best_eval = move_eval;
			}
		}
		return best;
	}
	
	private double alphabeta(int depth, double alpha, double beta) {
		if (depth < 0)
			return 0;
		List<QubicImpl> next_moves = nextMoves();
		if (next_moves.size() == 0)
			return (1000+depth)*winner();
		if (depth == 0)
			return utility();

		if (xTurn()) {
			double max = -1000 - depth;
			for (QubicImpl move : next_moves) {
				double evaluation = move.alphabeta(depth - 1, alpha, beta);
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
		Double reward = winner();
		if (reward == null || reward == 0)
			return;
		
		long winner_pattern = reward > 0 ? _x : _o;
		long loser_pattern = reward < 0 ? _x : _o;
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
			double winner_value = winner_spread.get(st)/winner_total;
			double loser_value = loser_spread.get(st)/loser_total;
			double net_value = winner_value - loser_value;
			_utility_function.setValue(st, (1 - learning_rate) * _utility_function.getValue(st) + learning_rate * net_value);
		}
	}

	public void print() {
		String out = "";
		for (int i = 0; i < 64; i++) {
			if (((_x >>> i) & 1) == 1) {
				out += "X";
			} else if (((_o >>> i) & 1) == 1) {
				out += "O";
			} else {
				out += " ";
			}
		}
		
		for (int i = 3; i >= 0; i--) {
			for (int j = 3; j >= 0; j--) {
				for (int k = 0; k < 4; k++) {
					System.out.print(out.charAt(16*i+4*j+k));
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
				if (((_x|_o)&move) == 0)
					next_moves.add(new QubicImpl(this, move));
		Collections.shuffle(next_moves);
		return next_moves;
	}
	
	private double utility() {
		double total = 0;
		for (long i = 1L; i != 0; i <<= 1) {
			if ((i&_x) != 0)
				total += _utility_function.getValue(i);
			else if ((i&_o) != 0)
				total -= _utility_function.getValue(i);
		}
		return total;
	}
	
	private static boolean hasWon(long board) {
		
		// Lines
		long x_line = 0b1111L;
		long y_line = 0b1000100010001L;
		long z_line = 0b1000000000000000100000000000000010000000000000001L;
		
		for (int i = 0; i < 16; i++) {
			
			if ((board & x_line) == x_line || (board & y_line) == y_line || (board & z_line) == z_line)
				return true;
			
			x_line <<= 4;
			y_line <<= i % 4 == 3 ? 13 : 1;
			z_line <<= 1;
		}
		
		// Plane diagonals
		long xy_diag1 = 0b1000010000100001L;
		long xy_diag2 = 0b1001001001000L;
		long xz_diag1 = 0b1000000000000000010000000000000000100000000000000001L;
		long xz_diag2 = 0b1000000000000001000000000000001000000000000001000L;
		long yz_diag1 = 0b1000000000000000000010000000000000000000100000000000000000001L;
		long yz_diag2 = 0b1000000000001000000000001000000000001000000000000L;
		
		for (int i = 0; i < 4; i++) {
			
			if ((board & xy_diag1) == xy_diag1 || (board & xz_diag1) == xz_diag1 || (board & yz_diag1) == yz_diag1 ||
					(board & xy_diag2) == xy_diag2 || (board & xz_diag2) == xz_diag2 || (board & yz_diag2) == yz_diag2)
				return true;
			
			xy_diag1 <<= 16;
			xy_diag2 <<= 16;
			xz_diag1 <<= 4;
			xz_diag2 <<= 4;
			yz_diag1 <<= 1;
			yz_diag2 <<= 1;
		}
		
		// Cube diagonals
		long xyz_diag1 = 0b1000000000000000000001000000000000000000001000000000000000000001L;
		long xyz_diag2 = 0b1000000000000000000100000000000000000010000000000000000001000L;
		long xyz_diag3 = 0b1000000000000100000000000010000000000001000000000000L;
		long xyz_diag4 = 0b1000000000010000000000100000000001000000000000000L;
		
		return (board & xyz_diag1) == xyz_diag1 || (board & xyz_diag2) == xyz_diag2 || (board & xyz_diag3) == xyz_diag3 || (board & xyz_diag4) == xyz_diag4;
	}

}
