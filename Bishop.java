import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Bishop extends Piece {

	//Constructor
	public Bishop(String col, Square pos) {
		super(col, 8, pos);
	}

	public boolean sameDiagonal(Square destination) {
		if (Location.getRankDistance(destination) == Location.getFileDistance(destination)) {
			return true;
		}
		return false;
	}

	public boolean isPathClear(Square destination) {
		//Determine the direction of the movement ((-1,-1) is up left, (-1,1) is up right, (1,1) is down right, (1,-1) is down left)
		int rankStep = (destination.getRank() > Location.getRank()) ? 1 : -1;
		int fileStep = (destination.getFile() > Location.getFile()) ? 1 : -1;

		//Start checking from the first square after the current location
		int checkRank = Location.getRank() + rankStep;
		int checkFile = Location.getFile() + fileStep;

		// Walk the diagonal until we hit the destination
		while (checkRank != destination.getRank() && checkFile != destination.getFile()) {
			if (ChessBoard.getBoard()[checkRank][checkFile].getOccupancy()) {
				return false;
			}
			checkRank += rankStep;
			checkFile += fileStep;
		}
		return true;
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
			throw new IllegalStateException("DEBUG: Target square cannot be the same as the current square!");
		}

		// BISHOP SPECIFC

		//The destination must be diagonal of the starting square
		if (!sameDiagonal(destination)) {
			throw new IllegalStateException("DEBUG: Target square must be on the same diagonal!");
		}

		//There cannot be any pieces in the way (diagonal)
		if (!isPathClear(destination)) {
			throw new IllegalStateException("DEBUG: Target square cannot be obstructed by other pieces!");
		}

		return true;
	}

	@Override
	public List<Move> getValidMoves(ChessBoard board, Square currentSquare) {
		List<Move> moves = new ArrayList<>();
		int r = currentSquare.getRank();
		int c = currentSquare.getFile();

		int[][] directions = { { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };

		for (int[] d : directions) {
			for (int i = 1; i < 8; i++) {
				int targetRank = r + (d[0] * i);
				int targetFile = c + (d[1] * i);

				Square target = ChessBoard.getSquare(targetRank, targetFile);

				if (target == null) break;

				try {
					if (checkLegalMove(target)) {
						moves.add(new Move(currentSquare, target, this.Color));
					}
				} catch (IllegalStateException e) {
					// Skip invalid moves found during scan
				}

				if (target.getOccupancy()) break;
			}
		}
		return moves;
	}
}