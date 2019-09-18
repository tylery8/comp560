package comp560;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

// Constraints impose a constraint on a given list of squares. This can
// be a row/column constraint (no repeated numbers) or a mathematical
// constraint for cages. This class servers as an Observer to Squares
// so that it only updates when a Square is changed. It is also Observed
// by KenKen and updates KenKen when its violation status changes
public class Constraint extends Observable implements Observer, Comparable<Constraint> {
	
	private List<Square> _squares = new ArrayList<Square>(); 
	private int[] _nums; // Keeps track of how many of each number is included 
	private int _result;
	private String _operator;
	private boolean _violated;
	private List<Observer> _observers = new ArrayList<Observer>();
	
	// Used for row/column constraints
	public Constraint(int n) {
		this(n, 0, null);
	}
	
	// Used to initialize a cage
	public Constraint(int n, int result, String operator) {
		_nums = new int[n+1];
		_result = result;
		_operator = operator;
	}
	
	// Returns whether or not the constraint is violated
	public boolean isViolated() {
		return _violated;
	}
	
	// Returns the number of currently undetermined Squares involved in
	// the Constraint (used as a measurement of most constrained)
	public int freedom() {
		return _nums[0];
	}
	
	// Returns the number of currently determined Squares involved in
	// the Constraint (used as a measurement of most constraining)
	public int restricting() {
		return _squares.size() - _nums[0];
	}
	
	// Returns whether or not the Constraint is a cage
	public boolean isCage() {
		return _operator != null;
	}
	
	// Returns whether or not the given number is currently in the Constraint
	public boolean hasNum(int num) {
		return _nums[num] > 0;
	}
	
	// Adds a square to the Constraint and updates all totals
	public void addSquare(Square square) {
		_squares.add(square);
		square.addObserver(this);
		_nums[square.getNumber()]++;
		update(square, square.getNumber());
	}

	// Updates totals and _violation because a square has been changed or added
	@Override
	public void update(Observable observable, Object prev) {
		boolean was_violated = _violated;
		_violated = calculateViolation((Square) observable, (Integer) prev);
		
		// Update KenKen if its status has changed
		for (Observer observer : _observers)
			if (was_violated != _violated)
				observer.update(this, _violated);
	}
	
	// Updates number counts and returns whether there is a violation
	private boolean calculateViolation(Square square, int prev) {
		// Keeping track of the count for each number
		_nums[square.getNumber()]++;
		_nums[prev]--;
		
		if (_operator == null) { // This is just a row/column check so no math required
			for (int i = 1; i < _nums.length; i++)
				if (_nums[i] > 1) return true; // A number is repeated so it's violated
			return false;
		}
		
		
		
		// Math check, handles each operation individually
		if (_operator.equals("+")) {
			int result = 0;
			for (int i = 0; i < _squares.size(); i++)
				result += _squares.get(i).getNumber();
			if (_result - result < _nums[0]) return true;
			return _nums[0] == 0 && result != _result;
		} else if (_operator.equals("-")) {
			if (_nums[0] > 0) return false;
			return Math.max(_squares.get(0).getNumber() - _squares.get(1).getNumber(), _squares.get(1).getNumber() - _squares.get(0).getNumber()) != _result;
		} else if (_operator.equals("*")) {
			int result = 1;
			for (int i = 0; i < _squares.size(); i++)
				result *= _squares.get(i).getNumber();
			if (result > _result) return true;
			return _nums[0] == 0 && result != _result;
		} else if (_operator.equals("/")) {
			if (_nums[0] > 0) return false;
			return Math.max(_squares.get(0).getNumber() / _squares.get(1).getNumber(), _squares.get(1).getNumber() / _squares.get(0).getNumber()) != _result;
		} else {
			return _nums[0] == 0 && _squares.get(0).getNumber() != _result;
		}
		
		
		
//		if (_nums[0] > 0) return false; // Region is still unfinished -> math is not yet violated
//		
//		// Region is finished, so verify that it does not violate the math requirements
//		// Beginning with max number to handle subtraction, division, or single number
//		int result = 0;
//		int max_index = 0;
//		for (int i = 0; i < _squares.size(); i++)
//			if (_squares.get(i).getNumber() > result) {
//				result = _squares.get(i).getNumber();
//				max_index = i;
//			}
//		
//		// Combine with rest of squares according to operation
//		for (int i = 0; i < _squares.size(); i++)
//			if (i != max_index)
//				switch(_operator) {
//				case "+":
//					result += _squares.get(i).getNumber();
//					break;
//				case "-":
//					result -= _squares.get(i).getNumber();
//					break;
//				case "*":
//					result *= _squares.get(i).getNumber();
//					break;
//				case "/":
//					result /= _squares.get(i).getNumber();
//					break;
//				}
//
//		return result != _result; // Numbers do not combine to expected result -> it's a violation
	}

	// Orders Constraints according to how restricted they are to aid sorting Squares
	@Override
	public int compareTo(Constraint other) {
		return freedom() - other.freedom();
	}
	
	// Allows KenKen to Observe it
	@Override
	public void addObserver(Observer o) {
		_observers.add((KenKen) o);
	}

}
