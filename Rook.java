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
	
	//If not on the same rank or file, move is illegal
	
	if(!sameRank(destination) && !sameFile(destination)) {
	
	throw new IllegalStateException("Destination square is not a legal move!");
	
	}
	
	if(sameRank(destination)) {
	
	//To check all the squares in a line, we have to determine where to start and stop.
	
	//Using min and max, we can be sure we start at the smaller value and end at the larger one
	
	//It doesn't matter that it matches the exact direction of the rook, so long as all the
	
	//squares between the current location and destination are checked for occupancy
	
	int startFile = Math.min(Location.getFile(), destination.getFile()) + 1;
	
	//We add 1 so that we dont check the same square we're on.
	
	int endFile= Math.max(Location.getFile(), destination.getFile());
	
	for(int i = startFile; i < endFile; i++) {
	
	//Iterate through board coordinates (staying on the same rank, and iterating files)
	
	if(ChessBoard.getBoard()[Location.getRank()][i].isOccupied) {
	
	//If any of the squares IN BETWEEN are occupied, then the path is not clear.
	
	//Note that this does not check if the destination square is occupied, as that is handled separately.
	
	return false;
	
	}
	
	}
	
	}
	
	//Same thing but keep file and iterate rank.
	
	if(sameFile(destination)) {
	
	int startRank = Math.min(Location.getRank(), destination.getRank()) + 1;
	
	int endRank = Math.max(Location.getRank(), destination.getRank());
	
	for(int i = startRank; i < endRank; i++) {
	
	if(ChessBoard.getBoard()[i][Location.getFile()].isOccupied) {
	
	return false;
	
	}
	
	}
	
	}
	
	//If none of the squares in between are occupied, then the path isn't obstructed.
	
	return true;
	//
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
	
}
