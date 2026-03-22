import java.util.List;

public class Rook extends Piece {


	public Rook(String col, Square pos) {
	
	super(col, 8, pos);
	
	}
	
	public boolean sameRank(Square destination) {
	
	if(Location.getRank() == destination.getRank()) {
	
	return true;
	
	}
	
	return false;
	
	}
	
	public boolean sameFile(Square destination) {
	
	if(Location.getFile() == destination.getRank()) {
	
	return true;
	
	}
	
	return false;
	
	}
	
	public boolean isPathClear(Square destination) {
	    // 1. Get coordinates
	    int curRank = Location.getRank();
	    int curFile = Location.getFile();
	    int destRank = destination.getRank();
	    int destFile = destination.getFile();

	    // 2. Determine directions
	    // If the ranks are the same, rankStep is 0. If files are same, fileStep is 0.
	    int rankStep = Integer.compare(destRank, curRank); // Returns 1, 0, or -1
	    int fileStep = Integer.compare(destFile, curFile);

	    // 3. Start checking from the square AFTER the current one
	    int checkRank = curRank + rankStep;
	    int checkFile = curFile + fileStep;

	    // 4. Walk until we hit the destination square
	    // We check BOTH because one will stay constant while the other moves
	    while (checkRank != destRank || checkFile != destFile) {
	        if (ChessBoard.getBoard()[checkRank][checkFile].getOccupancy()) {
	            return false; // Path is blocked!
	        }
	        checkRank += rankStep;
	        checkFile += fileStep;
	    }

	    return true;
	}
	
	//Override just saying "this is the child implementing the abstract method"
	
	//Helps in the case of a typo
	
	@Override
	
	public boolean checkLegalMove(Square destination){
	
	//Destination Square must not be occupied by a piece of the same color
	
	if(destination.isOccupied && destination.getOccupant().getColor() == this.Color) {
	
	return false;
	
	}
	
	//Destination Square must not be the same as the current Square
	
	if(Location.equals(destination) || this.Location.getTotalDistance(destination) == 0) {
	
	return false;
	
	}
	
	//Destination Square must not be outside of the spaces the piece can move
	
	//Not applicable for rook since it can move across the whole board
	
	// if(Math.abs(Location.getFileDistance(destination)) - spacesToMove < 0 || Location.getRankDistance(destination) - spacesToMove < 0) {
	
	// return false;
	
	// }
	
	//Moving the piece must not put the king in check - WIP
	
	//if(King.inCheck())
	
	// ROOK SPECIFC
	
	//The destination must be on the same rank or file
	
	if(!sameRank(destination) && !sameFile(destination)) {
	
	return false;
	
	}
	
	//There must not be any pieces in the way (On rank or file)
	
	if(!isPathClear(destination)) {
	
	return false;
	
	}
	
	return true;
	
	}

	@Override
	public List<Move> getValidMoves(ChessBoard board, Square currentSquare) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
