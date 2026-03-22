@SuppressWarnings("serial")
public class Bishop extends Piece {
	
	//Constructor
	public Bishop(String col, Square pos) {
		super(col, 8, pos);
	}
	
	public boolean isPathClear(Square destination) {
	    //Determine the direction of the movement ((-1,-1) is up left, (-1,1) is up right, (1,1) is down right, (1,-1) is down left)
	    int rankStep, fileStep;
	    if(destination.getRank() > Location.getRank()) {
	    	rankStep = 1;
	    } else {
	    	rankStep = -1;
	    }
	    if(destination.getFile() > Location.getFile()) {
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
	public boolean checkLegalMove(Square destination){
	
	//Destination Square must not be occupied by a piece of the same color
	if(destination.isOccupied && destination.getOccupant().getColor() == this.Color) {
		throw new IllegalStateException("DEBUG: Target square is occupied by an ally piece!");
	}
	
	//Destination Square must not be the same as the current Square
	if(Location.equals(destination) || this.Location.getTotalDistance(destination) == 0) {
		throw new IllegalStateException("DEBUG: Target square cannot be the same as the current square!");
	}
	
	// BISHOP SPECIFC
	
	//The destination must be diagonal of the starting square
	if(!Location.sameDiagonal(destination)) {
		throw new IllegalStateException("DEBUG: Target square must be on the same diagonal!");
	}
	
	//There cannot be any pieces in the way (diagonal)
	if(!isPathClear(destination)) {
		throw new IllegalStateException("DEBUG: Target square cannot be obstructed by other pieces!");
	}
	
	return true;
	}
}
