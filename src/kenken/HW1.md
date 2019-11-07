#Report

<h1>Group Members:</h1>
<hr>
<p>Tyler Youngberg</p>
<br>
<h1>Backtracking Method:</h1>
<hr>
<p><i>Highest Success: 8x8, sometimes 9x9</i></p>
<p>This method was implemented through a recursive function. It begins with the puzzle filled with zeroes and fills in one number at each step. The simple version of this simply tries the numbers in order from 1 to n and goes through the squares in order row by row. At each step, the function decides whether to backtrack based on the following conditions:</p>
<p><i>Bactrack iff:
<li>This node violates a constraint
<li>All future nodes need to backtrack</i></p>
<p>The first condition is essentially the base case for the recursive function while the second condition is the recursive call.</p>
<p>This simple version of backtracking was improved by changing the selection order of the squares and which numbers are tried for each square. Instead of being selected in row by row order, the squares were ordered according to which square was the "most constricted". For example, if a square can only be two different numbers (all other numbers are already in the row, for example) and all other squares can be at least three different numbers, then the former square is selected. In the case of ties, the "most constricting" square is selected (the one that has the most open squares in the same row/column/cage as it).</p>
<p>Secondly, the backtracking method was optimized through "forward checking" to ensure that only reasonable numbers are tried for each square. For example, if the cage the square is in already contains a given number, then that number is not tried for that square. Rows, columns, and addition and multiplication cages were taken into consideration for this optimization.</p>
<br>
<h1>Local Search Method</h1>
<hr>
<p><i>Highest Success: usually 6x6</i></p>
<p>Unlike the backtracking method, the local search method begins with the puzzle completely filled in with numbers 1 through n. In order to begin near somewhere near the global minimum, the puzzle is not filled in completely randomly. Instead, it ensures that each column already contain the numbers 1 through n exactly once. The only constraints that are violated at this point are the rows and the cages. To rearrange the puzzle and step towards the global minimum, the function swaps the numbers of squares within the same column so that column constraints are never violated. At each step, there are n*n*(n-1)/2 choices for which swap to make, and this choice is guided by maximizing the following utility function:</p>
<p><i>U(puzzle) = -v, where v is the number of violated constraints</i></p>
<p><i>Note: this is equivalent to minimizing the negation of this utility function, or simply minimizing the number of violations</i></p>
<p>At each step, the function makes whichever swap has the highest utility. Sometimes, this can result in the puzzle getting stuck at a local minimum that is not the global minimum (or solution). When this happens, the function will simply reset to a new starting position (where each column has 1 through n exactly once) and begin again.</p>