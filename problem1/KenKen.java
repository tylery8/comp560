package comp560;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

// KenKen contains a 2d array of Squares that represents the KenKen puzzle as well
// as a set of Constraints that are imposed on the Squares (rows, columns, cages).
// KenKen contains public methods to solve the puzzle (through backtracking and local
// search) and print the puzzle. It is an Observer to all of the Constraints that it
// holds so it can keep an efficient running count of the number of violations.
public class KenKen implements Observer {

	private Square[][] _squares;
	private Collection<Constraint> _constraints = new HashSet<Constraint>();
	private int _violations = 0;
	
	public KenKen(char[][] layout, Map<Character, String> cages) {
		initiateBoard(layout.length);
		addRowColumnConstraints();
		createCages(layout, cages);
	}

	// IN:	int		-> the dimensions of the puzzle
	// OUT:	Nothing
	//
	// Creates and n by n array of Squares initialized to 0. Used in constructor
	private void initiateBoard(int n) {
		_squares = new Square[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				_squares[i][j] = new Square();
	}
	
	// IN:	Nothing
	// OUT:	Nothing
	//
	// Creates Constraint objects for each of the rows and columns. Used in constructor
	private void addRowColumnConstraints() {
		
		// Add a constraint on each row that will require all numbers to be different
		for (int i = 0; i < _squares.length; i++) {
			Constraint row = new Constraint(_squares.length);
			for (int j = 0; j < _squares.length; j++)
				row.addSquare(_squares[j][i]);
			_constraints.add(row);
			row.addObserver(this);
		}
		
		// Add a constraint on each column that will require all numbers to be different
		for (int i = 0; i < _squares.length; i++) {
			Constraint col = new Constraint(_squares.length);
			for (int j = 0; j < _squares.length; j++)
				col.addSquare(_squares[i][j]);
			_constraints.add(col);
			col.addObserver(this);
		}
	}
	
	// IN:	String[][]			-> n by n array of the layout of the cages in the puzzle
	//		Map<String, String>	-> map of what mathematical expression each
	//								character corresponds to (i.e. 'A' -> "6+")
	// OUT:	Nothing
	//
	// Creates Constraint objects for each of the cages. Used in constructor
	private void createCages(char[][] layout, Map<Character, String> cages) {
		int n = layout.length;
		Map<Character, Constraint> constraints = new HashMap<Character, Constraint>();
		
		// For each Square, map the letter in it to a mathematical Constraint
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				String s = cages.get(layout[i][j]);
				int num = 0;
				String operation = null;
				try {
					// Assumes it has no operation (single number cage)
					num = Integer.parseInt(s);
					operation = "."; // Dummy operation
				} catch (NumberFormatException e) {
					// Exception thrown -> It does have an operation
					num = Integer.parseInt(s.substring(0,s.length()-1));
					operation = s.substring(s.length()-1);
				}
				
				// If this letter has already been seen, just add the square to
				// the Constraint that was created before. Otherwise, create a
				// new Constraint for that letter
				if (constraints.containsKey(layout[i][j])) {
					constraints.get(layout[i][j]).addSquare(_squares[i][j]);
				} else {
					Constraint constraint = new Constraint(n, num, operation);
					constraint.addSquare(_squares[i][j]);
					constraints.put(layout[i][j], constraint);
					constraint.addObserver(this);
				}
			}
		_constraints.addAll(constraints.values());
	}
	
	// This is called when a violation is either created or fixed and it updates
	// the total accordingly
	@Override
	public void update(Observable constraint, Object violated) {
		if ((boolean) violated)
			_violations++;
		else
			_violations--;
	}
	
	// IN:	Nothing
	// OUT:	boolean		-> whether or not the puzzle is solved
	//
	// Checks to make sure no Constraints are violated and that all Squares are non-zero
	public boolean solved() {
		if (_violations != 0) return false;
		for (Constraint constraint : _constraints)
			if (constraint.hasNum(0)) 
				return false;
		return true;
	}

	// IN:	Nothing
	// OUT:	Nothing
	//
	// Prints out the puzzle in an n by n format
	public void print() {
		for (int i = 0; i < _squares.length; i++) {
			for (int j = 0; j < _squares.length; j++)
				System.out.print(_squares[j][i].getNumber() + " ");
			System.out.println();
		}
		System.out.println();
	}	

	// -----------------------------------------------------------------------
	//
	// BACKTRACKING
	// 
	// -----------------------------------------------------------------------
	
	// IN:	boolean		-> whether or not it is optimized backtracking
	// OUT:	String		-> the number of nodes reached while backtracking or
	//						a message stating that there were too many nodes
	//
	// Defaults the max number of nodes for either backtracking to be 100000
	public String backtrack(boolean optimized) {
		return backtrack(optimized, 100000);
	}
	
	// IN:	boolean		-> whether or not it is optimized backtracking
	//		long		-> the maximum number of nodes to explore before early
	//						termination
	// OUT:	String		-> the number of nodes reached while backtracking or
	//						a message stating that there were too many nodes
	//
	// Begins the recursive call for backtracking. It initializes a count of the
	// number of nodes and a list of all of the Squares. If it is optimized
	// backtracking, the list will later be sorted according to which squares are most
	// constricted, then by most constricting.
	public String backtrack(boolean optimized, long max_nodes) {
		
		// Initialize count pointer and list of squares
		long[] count = new long[] {0};
		List<Square> list = new LinkedList<Square>();
		for (int i = 0; i < _squares.length; i++)
			for (int j = 0; j < _squares.length; j++)
				list.add(_squares[i][j]);
		
		// Call recursive function
		backtrack(optimized, list, count, max_nodes);
		
		// It is either solved or has exceeded the max count. Returns appropriate response
		return (optimized ? "Optimized " : "") + "Backtracking: " + (solved() ? count[0] + " nodes" : "Exceeded " + max_nodes + " nodes");
	}
	
	// IN:	boolean		-> whether or not it is optimized backtracking
	//		List<Square>-> a list of the remaining Squares that have not yet been
	//						assigned a value
	//		long[]		-> a pointer to where the count of the number of nodes is stored
	//		long		-> the maximum number of nodes to explore before early
	//						termination
	// OUT:	boolean		-> whether or not backtracking is necessary (i.e. all future nodes
	//						lead to an error)
	//
	// Recursive backtracking method. Each call looks at each possibility for the next square
	// and chooses the first option that will not require backtracking. If optimized is true
	// the squares will be ordered by how constricted/constricting they are and only reasonable
	// options will be attempted for the number values.
	private boolean backtrack(boolean optimized, List<Square> list, long[] count, long max_nodes) {
		
		// If it has exceeded the maximum count, it should give up -> there is no
		// need to backtrack
		if (count[0] > max_nodes) return false;
		count[0]++;
		
		// If a constraint is currently violated, a mistake has already been
		// made -> backtracking is necessary
		if (_violations > 0) return true;
		
		// Since no constraints have been violated, an empty list of remaining Squares
		// implies that the puzzle has been solved -> backtracking is not necessary
		if (list.size() == 0) return false;
		
		// If this is optimized backtracking, sort the Squares to ensure that the
		// first Square is the most constricted (or most constricting for ties)
		if (optimized) Collections.sort(list);
		
		// Take the top square and try each number for it. If it is optimized backtracking,
		// a new node is only created for reasonable numbers.
		Square square = list.remove(0);
		for (int i = 1; i <= _squares.length; i++) {
			square.setNumber(i);
			
			if (optimized && _violations != 0) continue;
			
			// Recursive call. If backtracking is not necessary from the change that has been
			// made, then backtracking is not necessary from the current position.
			if (!backtrack(optimized, list, count, max_nodes))
				return false;
		}
		
		// Otherwise, backtracking is necessary from all possible changes -> backtracking is
		// necessary from the current position. Reset the Square and add it to the list of
		// remaining Squares
		square.setNumber(0);
		list.add(0, square);
		return true;
	}

	
	// -----------------------------------------------------------------------
	//
	// LOCAL SEARCH
	// 
	// -----------------------------------------------------------------------
	
	// IN:	Nothing
	// OUT:	String		-> the number of iterations required reached while running
	//						the local search or a message stating that there were
	//						too many iterations
	//
	// Defaults the max number of iterations for local search to be 100000
	public String localSearch() {
		return localSearch(100000);
	}
	
	// IN:	long		-> the maximum number of allowed iterations before early
	//						termination
	// OUT:	String		-> the number of iterations required reached while running
	//						the local search or a message stating that there were
	//						too many iterations
	//
	// Local search method. Begins with all Squares filled in semi-randomly and swaps
	// one Square at a time. The selected change is the one that will reduce the total
	// number of violated constraints by the most.
	public String localSearch(long max_iterations) {
		int count = 0;
		int max_swaps = _squares.length*_squares.length*(_squares.length-1)/2;
		
		// This loop will not terminate until a solution is found or the maximum iterations
		// has been reached.
		while(!solved() && count < max_iterations) {
			
			// Pick a semi-random "solution" and perform a local search at least "max swaps"
			// times before resetting (enough to do each swap once)
			scramble();
			for (int reset = 0; reset < max_swaps; reset++) {
				
				Square square1 = null;
				Square square2 = null;
				int min_violations = 3*_squares.length*_squares.length; // essentially MAX_VIOLATIONS
				
				// For each column (which contains numbers 1 through n, try swapping
				// each pair of numbers and save the Squares for the best swap.
				for (int i = 0; i < _squares.length; i++) {
					for (int j = 0; j < _squares.length-1; j++) {
						for (int k = j+1; k < _squares.length; k++) {
							swap(_squares[i][j], _squares[i][k]);
							
							if (_violations <= min_violations) {
								square1 = _squares[i][j];
								square2 = _squares[i][k];
								min_violations = _violations;
							}
							
							swap(_squares[i][j], _squares[i][k]);
						}
					}
				}
				
				// All swaps increase the total number of violations, so this is a
				// local min
				if (square1 == null) break;
				
				// Otherwise a change was found, so make that change and increment the count
				swap(square1, square2);
				count++;
			}
		}
		
		// It is either solved or has exceeded the max count. Returns appropriate response
		return "Local Search: " + (solved() ? count + " iterations" : "Exceeded " + max_iterations + " iterations");
	}
	
	// IN:	Nothing
	// OUT:	Nothing
	//
	// Randomly fills in the puzzle with numbers 1 through n, ensuring each column uses
	// the numbers 1 through n exactly once. Used for local search
	private void scramble() {
		List<Integer> numbers = new ArrayList<Integer>(_squares.length);
		for (int i = 1; i <= _squares.length; i++)
			numbers.add(i);
		for (int i = 0; i < _squares.length; i++) {
			Collections.shuffle(numbers);
			for (int j = 0; j < _squares.length; j++)
				_squares[i][j].setNumber(numbers.get(j));
		}
	}
	// IN:	Square		-> a first square
	//		Square		-> a second square
	// OUT:	Nothing
	//
	// Swaps the numbers of two Squares
	private void swap(Square square1, Square square2) {
		int tmp = square1.getNumber();
		square1.setNumber(square2.getNumber());
		square2.setNumber(tmp);
	}

}
