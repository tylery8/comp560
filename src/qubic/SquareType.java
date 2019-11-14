package qubic;

public enum SquareType {
	
	CENTER, CORNER, EDGE, FACE;
	
	// CENTER: One of the 8 squares in the very center of the cube
	// CORNER: One of the 8 squares on the very outside tips of the cube
	// EDGE: One of the 24 squares that run along the shortest path between two corners
	// FACE: One of the 24 squares directly adjacent to the middle squares 
	
	private static final SquareType[] LAYOUT = new SquareType[] {
			SquareType.CORNER, SquareType.EDGE, SquareType.EDGE, SquareType.CORNER,
			SquareType.EDGE, SquareType.FACE, SquareType.FACE, SquareType.EDGE,
			SquareType.EDGE, SquareType.FACE, SquareType.FACE, SquareType.EDGE,
			SquareType.CORNER, SquareType.EDGE, SquareType.EDGE, SquareType.CORNER,
			
			SquareType.EDGE, SquareType.FACE, SquareType.FACE, SquareType.EDGE,
			SquareType.FACE, SquareType.CENTER, SquareType.CENTER, SquareType.FACE,
			SquareType.FACE, SquareType.CENTER, SquareType.CENTER, SquareType.FACE,
			SquareType.EDGE, SquareType.FACE, SquareType.FACE, SquareType.EDGE,
			
			SquareType.EDGE, SquareType.FACE, SquareType.FACE, SquareType.EDGE,
			SquareType.FACE, SquareType.CENTER, SquareType.CENTER, SquareType.FACE,
			SquareType.FACE, SquareType.CENTER, SquareType.CENTER, SquareType.FACE,
			SquareType.EDGE, SquareType.FACE, SquareType.FACE, SquareType.EDGE,
			
			SquareType.CORNER, SquareType.EDGE, SquareType.EDGE, SquareType.CORNER,
			SquareType.EDGE, SquareType.FACE, SquareType.FACE, SquareType.EDGE,
			SquareType.EDGE, SquareType.FACE, SquareType.FACE, SquareType.EDGE,
			SquareType.CORNER, SquareType.EDGE, SquareType.EDGE, SquareType.CORNER
	};
	
	public static SquareType valueOf(long l) {
		return LAYOUT[Long.numberOfTrailingZeros(l)];
	}

}
