package kenken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

// Squares contain one number and a list of Constraints that are imposed
// on it. Squares are an Observable and are observed by the Constraints 
// so that the Constraints only update when a relevant Square is changed
public class Square extends Observable implements Comparable<Square> {
	
	private int _number;
	private List<Integer> _options = new LinkedList<Integer>();
	private List<Constraint> _observers = new ArrayList<Constraint>();
	
	public Square(int n) {
		for (int i = n; i > 0; i--)
			_options.add(0, i);
	}
	
	public int getNumber() {
		return _number;
	}
	
	// Takes in a number and sets it as the current number for the Square. 
	// All relevant Constraints are notified of the change
	public void setNumber(int num) {
		int prev = _number;
		_number = num;
		for (Observer observer : _observers)
			observer.update(this, prev);
	}
	
	// Allows a Constraint to Observe the Square
	@Override
	public void addObserver(Observer o) {
		_observers.add((Constraint) o);
	}

	// Used to sort Squares for optimized backtrack method. The most
	// constricted squares (based on lack of freedom) will be at the
	// front of the list and the most constricting squares (restricting)
	// will be used to break ties
	@Override
	public int compareTo(Square other) {
		Collections.sort(_observers);
		Collections.sort(other._observers);
		for (int i = 0; i < _observers.size(); i++)
			if (_observers.get(i).freedom() != other._observers.get(i).freedom())
				return _observers.get(i).freedom() - other._observers.get(i).freedom();
		for (int i = 0; i < _observers.size(); i++)
			if (_observers.get(i).restricting() != other._observers.get(i).restricting())
				return _observers.get(i).restricting() - other._observers.get(i).restricting();
		return 0;
	}

}
