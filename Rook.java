import java.util.List;

@SuppressWarnings("serial")
public class Rook extends Piece {

	//Constructor
	public Rook(String col, Square pos) {
		super(col, 8, pos);
	}

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
					throw new IllegalStateException("DEBUG: Target square must not be obstructed by other pieces!");
				}
			}
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
			throw new IllegalStateException("DEBUG: Target square cannot be the same as the destination square!");
		}

		//Moving the piece must not put the king in check - WIP
		//if(King.inCheck())

		// ROOK SPECIFC

		//The destination must be on the same rank or file
		if (!Location.sameRank(destination) && !Location.sameFile(destination)) {
			throw new IllegalStateException("DEBUG: Target square must be on the same rank or file as the current location!");
		}

		//There must not be any pieces in the way (On rank or file)
		//Only call if there is actually a path (aka its not a 1 square move
		if (Location.getTotalDistance(destination) > 1) {
			if (!isPathClear(destination)) {
				throw new IllegalStateException("DEBUG: Target square must not be obstructed by other pieces!");
			}
		}

		//If none of the conditions are violated, the move is legal and permitted to go through
		return true;
	}

	@Override
	public List<Move> getValidMoves(ChessBoard board, Square currentSquare) {
		// TODO Auto-generated method stub
		return null;
	}
}