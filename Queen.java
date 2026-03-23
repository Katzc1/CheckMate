import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Queen extends Piece {

	public Queen(String col, Square pos) {
		super(col, 8, pos);
	}

	//Borrowed from Rook
	public boolean isPathClear(Square destination) {
		int direction;

		//In the case that the rook is moving along the same RANK:
		if (Location.sameRank(destination)) {
			//Find if the rook is moving left (1) or right (-1)
			//If distance is positive, the rook is moving to the left
			if (Location.getFile() - destination.getFile() > 0) {
				direction = -1;
			} else {
				//Otherwise the rook is moving to the right
				direction = 1;
			}
			//Now we start checking all the squares in between the location and destination using direction 
			//(AFTER the current location and BEFORE the destination since they don't matter)
			for (int i = (Location.getFile() + direction); i < destination.getFile(); i += direction) {
				if (ChessBoard.getSquare(Location.getRank(), i).getOccupancy()) {
					throw new IllegalStateException("DEBUG: Target square must not be obstructed by other pieces!");
				}
			}
		}

		//In the case that the rook is moving along the same FILE:
		if (Location.sameFile(destination)) {
			//Find if the rook is moving up (1) or down (-1)
			//If distance is positive, the rook is moving up
			if (Location.getRank() - destination.getRank() > 0) {
				direction = -1;
			} else {
				//Otherwise the rook is moving down
				direction = 1;
			}

			//Now we start checking all the squares in between the location and destination using direction 
			//(AFTER the current location and BEFORE the destination since they don't matter)
			for (int i = (Location.getRank() + direction); i < destination.getRank(); i += direction) {
				if (ChessBoard.getSquare(i, Location.getFile()).getOccupancy()) {
					throw new IllegalStateException("DEBUG: Horizontal path blocked!");
				}
			}
		}
		return true;
	}

	//Borrowed from Bishop
	public boolean isDiagonalPathClear(Square destination) {
		//Determine the direction of the movement ((-1,-1) is up left, (-1,1) is up right, (1,1) is down right, (1,-1) is down left)
		int rankStep, fileStep;
		if (destination.getRank() > Location.getRank()) {
			rankStep = 1;
		} else {
			rankStep = -1;
		}
		if (destination.getFile() > Location.getFile()) {
			fileStep = 1;
		} else {
			fileStep = -1;
		}

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
			throw new IllegalStateException("DEBUG: Target square cannot be the same as starting square!");
		}

		// QUEEN SPECIFC

		//The destination must not be a knight move away
		if (((Location.getRankDistance(destination) == 2 && Location.getFileDistance(destination) == 1)
				|| (Location.getRankDistance(destination) == 1 && Location.getFileDistance(destination) == 2))) {
			throw new IllegalStateException("DEBUG: Target square cannot be a knight move away!");
		}

		//There cannot be any pieces in the way 
		//(Horizontal)
		if (Location.sameRank(destination) || Location.sameFile(destination)) {
			//Only call if there is actually a path (aka its not a 1 square move
			if (Location.getTotalDistance(destination) > 1) {
				if (!isPathClear(destination)) {
					throw new IllegalStateException("DEBUG: Target square must not be horizontally obstructed by other pieces!");
				}
			}
		}
		//(Diagonal)
		if (Location.sameDiagonal(destination)) {
			if (!isDiagonalPathClear(destination)) {
				throw new IllegalStateException("DEBUG: Target square cannot be diagonally obstructed by other pieces!");
			}
		}

		return true;
	}

	@Override
	public List<Move> getValidMoves(ChessBoard board, Square currentSquare) {
	    List<Move> moves = new ArrayList<>();
	    int r = currentSquare.getRank();
	    int c = currentSquare.getFile();

	    // All 8 directions a Queen can slide: {rowStep, colStep}
	    int[][] directions = {
	        {1, 0}, {-1, 0}, {0, 1}, {0, -1}, // Rook-like (Straight)
	        {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // Bishop-like (Diagonal)
	    };

	    for (int[] d : directions) {
	        // Slide in each direction up to 7 squares away
	        for (int i = 1; i < 8; i++) {
	            int targetRank = r + (d[0] * i);
	            int targetFile = c + (d[1] * i);

	            Square target = ChessBoard.getSquare(targetRank, targetFile);

	            // If we hit the edge of the board, stop sliding in this direction
	            if (target == null) break;

	            try {
	                if (checkLegalMove(target)) {
	                    moves.add(new Move(currentSquare, target, this.Color));
	                }
	                
	                // If there's ANY piece on this square, we stop sliding.
	                // Your checkLegalMove handles if it was a valid capture or an ally block.
	                if (target.getOccupancy()) {
	                    break;
	                }
	            } catch (IllegalStateException e) {
	                // If we hit an obstruction or an ally piece, checkLegalMove throws an error.
	                // We catch it and stop sliding in this direction.
	                break;
	            }
	        }
	    }

	    return moves;
	}
}