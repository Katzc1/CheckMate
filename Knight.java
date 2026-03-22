import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Knight extends Piece {

	public Knight(String col, Square pos) {
		super(col, Math.sqrt(5), pos);
	}

	//Override just saying "this is the child implementing the abstract method"
	@Override
	public boolean checkLegalMove(Square destination) {

		//Destination Square must not be occupied by a piece of the same color
		if (destination.isOccupied && destination.getOccupant().getColor() == this.Color) {
			throw new IllegalStateException("DEBUG: Target square is occupied by an ally piece!");
		}

		//Destination Square must not be the same as the current Square
		if (Location.equals(destination) || this.Location.getTotalDistance(destination) == 0) {
			throw new IllegalStateException("DEBUG: Target square cannot be the same as starting square");
		}

		//KNIGHT SPECIFIC

		//Destination Square must not be outside of the spaces the piece can move
		//For the knight, this actually covers all possible movement squares so no further investigation required
		if (!((Location.getRankDistance(destination) == 2 && Location.getFileDistance(destination) == 1)
				|| (Location.getRankDistance(destination) == 1 && Location.getFileDistance(destination) == 2))) {
			throw new IllegalStateException("DEBUG: Target square is not an L shape!");
		}

		//If all conditions are met, then it is a legal move.
		return true;
	}

	@Override
	public List<Move> getValidMoves(ChessBoard board, Square currentSquare) {
		List<Move> moves = new ArrayList<>();

		// These are the only 8 relative positions a knight can move to
		int[][] offsets = { { -2, -1 }, { -2, 1 }, { 2, -1 }, { 2, 1 }, { -1, -2 }, { -1, 2 }, { 1, -2 }, { 1, 2 } };

		for (int[] o : offsets) {
			int targetRank = currentSquare.getRank() + o[0];
			int targetFile = currentSquare.getFile() + o[1];

			Square target = ChessBoard.getSquare(targetRank, targetFile);

			if (target != null) {
				try {
					if (checkLegalMove(target)) {
						moves.add(new Move(currentSquare, target, this.Color));
					}
				} catch (IllegalStateException e) {
					// Skip invalid L-shapes or occupied squares
				}
			}
		}
		return moves;
	}
}