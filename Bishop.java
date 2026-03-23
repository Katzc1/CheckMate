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
	public boolean isDiagonalPathClear(Square destination) {
	    // 1. Determine direction (1 or -1) for both axes
	    int rankStep = (destination.getRank() > Location.getRank()) ? 1 : -1;
	    int fileStep = (destination.getFile() > Location.getFile()) ? 1 : -1;
	    // 2. Start at the first square AFTER the Queen/Bishop
	    int checkRank = Location.getRank() + rankStep;
	    int checkFile = Location.getFile() + fileStep;
	    // 3. Walk the diagonal
	    // Stop once we are standing on the destination square
	    while (checkRank != destination.getRank() && checkFile != destination.getFile()) {
	       
	        // If we find any piece in the way, the path is blocked
	        if (ChessBoard.getSquare(checkRank, checkFile).getOccupancy()) {
	            return false;
	        }
	        // Increment both to stay on the diagonal
	        checkRank += rankStep;
	        checkFile += fileStep;
	    }
	    // If the loop finishes without returning false, the path is clear
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
		if (!isDiagonalPathClear(destination)) {
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



