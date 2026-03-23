import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("serial")
public class King extends Piece {
	public King(String col, Square pos) {
		super(col, 1, pos);
	}
	public boolean hasMoved = false;
	public Piece[] castlingWith = new Piece[2];
	public boolean canCastle(Square s) {
		Piece[] pieces = this.Color.equalsIgnoreCase("White") ? ChessBoard.getWhitePieces() : ChessBoard.getBlackPieces();
		//Store the rook in a piece array of size 1 so we don't have to initialize a ghost rook
		Rook[] rook = new Rook[1];
		//1 means left rook, -1 means right rook
		int direction = (Location.getFile() - s.getFile() > 0) ? 1 : -1;
		for(Piece p : pieces) {
			//since you could castle with either rook, we need to find the rook in the correct direction of the move
			if(p instanceof Rook) {
				if(Location.getFile() - p.getLocation().getFile() > 0 && direction == 1 ) {
					rook[0] = (Rook)p;
				} else if(Location.getFile() - p.getLocation().getFile() < 0 && direction == -1) {
					rook[0] = (Rook)p;
				} else {
					throw new IllegalStateException("DEBUG: No valid rook exists!");
				}
			}
		}
		
		if(rook[0].hasMoved) {
			throw new IllegalStateException("DEBUG: YOU DONE MOVED YO ROOK AND THOUGHT I WOULDNT NOTICE");
		}
		
		if(this.hasMoved) {
			throw new IllegalStateException("DEBUG: YOU DONE MOVED YO KING AND THOUGHT I WOULDNT NOTICE");
		}
		
		if(!rook[0].isPathClear(this.Location)) {
			throw new IllegalStateException("DEBUG: You cannot castle through your own pieces!");
		}
		castlingWith[0] = this;
		castlingWith[1] = rook[0];
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
			throw new IllegalStateException("DEBUG: Target square is same as starting square!");
		}
		//KING SPECIFC
		//NOTE: MAKE SURE CASTLING LOGIC IS BEFORE THIS CHECK
		//Castling logic
		//Make sure we only check for castling if the king is trying to move 2 squares horizontally
		if(Location.getFileDistance(destination) == 2) {
			//Since it has multiple reasons to fail, we capture all of them and re throw them for the main move method
			try {
				if(this.canCastle(destination)) {
					//We tell the GameStateManager where the castle is.
					GameStateManager.letMeCastle((King)castlingWith[0], (Rook)castlingWith[1]);
					return true;
				}
				
			} catch(IllegalStateException ex) {
				//Perpetuate the error and its message to that we can print it to the console later on
				throw ex;
			}
		}
		
		
		//Destination Square must not be outside of the spaces the piece can move
		if (Math.abs(Location.getFileDistance(destination)) > spacesToMove || Location.getRankDistance(destination) > spacesToMove) {
			throw new IllegalStateException("DEBUG: Target square is outisde of the piece's movement range!");
		}
		return true;
	}
	@Override
	public List<Move> getValidMoves(ChessBoard board, Square currentSquare) {
		List<Move> moves = new ArrayList<>();
		int r = currentSquare.getRank();
		int c = currentSquare.getFile();
		// The 8 squares surrounding the King
		int[][] kingOffsets = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };
		for (int[] offset : kingOffsets) {
			int targetRank = r + offset[0];
			int targetFile = c + offset[1];
			Square target = ChessBoard.getSquare(targetRank, targetFile);
			if (target != null) {
				try {
					if (checkLegalMove(target)) {
						moves.add(new Move(currentSquare, target, this.Color));
					}
				} catch (IllegalStateException e) {
					// Skip invalid moves
				}
			}
		}
		return moves;
	}
}

