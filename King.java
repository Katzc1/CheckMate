public class King extends Piece {

//
public King(String col, Square pos) {

super(col, 1, pos);

}

public boolean hasMoved = false;

public boolean inCheck;

public boolean wouldBeInCheck(Square destination) {
	for(int i = 0; i < 8; i++) {
		for(int j = 0; j<8; j++) {
			Square currentSquare = ChessBoard.getBoard()[i][j];
			if(currentSquare.getOccupancy()) {
				if(currentSquare.getOccupant().getColor() != Color) {
					//Note: need to handle king vs king interactions
					if(currentSquare.getOccupant().checkLegalMove(destination)) {
						return true;
					}
				}
			}
		}
	}

	return false;

}

public boolean getCheckStatus() {

return inCheck;

}


public boolean isStalemate() {
	if(inCheck) {
		return false;
	}

	int currentRank = Location.getRank();
	int currentFile = Location.getFile();
	
		//Need to check all squares around the king for legal moves.
	//(Rank+1, File), (Rank+1, File+1), (Rank, File+1), (Rank-1, File +1), (Rank-1, File), (Rank-1, File-1), (Rank, File-1), (Rank+1, File-1)
	if(ChessBoard.squareExists(Location, currentRank+1, currentFile)) {
		if(checkLegalMove(ChessBoard.getBoard()[currentRank+1][currentFile])){
			return false;
		}
	}
	
	if(ChessBoard.squareExists(Location, currentRank+1, currentFile+1)) {
		if(checkLegalMove(ChessBoard.getBoard()[currentRank+1][currentFile+1])){
			return false;
		}
	}
	
	if(ChessBoard.squareExists(Location, currentRank, currentFile+1)) {
		if(checkLegalMove(ChessBoard.getBoard()[currentRank][currentFile+1])){
			return false;
		}
	}
	
	if(ChessBoard.squareExists(Location, currentRank-1, currentFile+1)) {
		if(checkLegalMove(ChessBoard.getBoard()[currentRank-1][currentFile+1])){
			return false;
		}
	}
	
	if(ChessBoard.squareExists(Location, currentRank-1, currentFile)) {
		if(checkLegalMove(ChessBoard.getBoard()[currentRank-1][currentFile])){
			return false;
		}
	}
	
	if(ChessBoard.squareExists(Location, currentRank-1, currentFile-1)) {
		if(checkLegalMove(ChessBoard.getBoard()[currentRank-1][currentFile-1])){
			return false;
		}
	}
	
	if(ChessBoard.squareExists(Location, currentRank, currentFile-1)) {
		if(checkLegalMove(ChessBoard.getBoard()[currentRank][currentFile-1])){
			return false;
		}
	}
	
	if(ChessBoard.squareExists(Location, currentRank+1, currentFile-1)) {
		if(checkLegalMove(ChessBoard.getBoard()[currentRank+1][currentFile-1])){
			return false;
		}
	}

	return true;
}



//Override just saying "this is the child implementing the abstract method"

//Helps in the case of a typo

@Override

public boolean checkLegalMove(Square destination){

//Destination Square must not be occupied by a piece of the same color

if(destination.isOccupied && destination.getOccupant().getColor() == this.Color) {
	throw new IllegalStateException("DEBUG: Target square is occupied by an ally piece!");
}

//Destination Square must not be the same as the current Square

if(Location.equals(destination) || this.Location.getTotalDistance(destination) == 0) {

	throw new IllegalStateException("DEBUG: Target square is same as starting square!");

}

//KING SPECIFC

//NOTE: MAKE SURE CASTLING LOGIC IS BEFORE THIS CHECK

//Castling logic
if(!hasMoved) {
	//You are allowed to castle if nothing is in the way...
}

//Destination Square must not be outside of the spaces the piece can move
if(Math.abs(Location.getFileDistance(destination)) > spacesToMove || Location.getRankDistance(destination) > spacesToMove) {

	throw new IllegalStateException("DEBUG: Target square is outisde of the piece's movement range!");

}



//The destination must not put the king in check

//if(wouldBeInCheck(destination)) {
//
//	throw new IllegalStateException("DEBUG: Target square would put the king in check!");
//
//}


return true;

}

}
