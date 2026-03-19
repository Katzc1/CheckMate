public class Queen extends Piece {

//

public Queen(String col, Square pos) {

super(col, 8, pos);

}

//Borrowed from Rook

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


//Borrowed from Bishop

public boolean sameDiagonal(Square destination) {
	//If the movement is the same for the vertical and horizontal, then it is on the same diagonal
	if(Location.getRankDistance(destination) == Location.getFileDistance(destination)) {
		return true;
	}
	
	return false;

}



public boolean isDiagonalPathClear(Square destination) {

	//If not on the same rank or file, move is illegal

	if(!sameDiagonal(destination)) {

	throw new IllegalStateException("Destination square is not a legal move!");

	}

	//To check all the squares in a line, we have to determine where to start and stop.

	//Using min and max, we can be sure we start at the smaller value and end at the larger one

	//It doesn't matter that it matches the exact direction of the bishop, so long as all the

	//squares between the current location and destination are checked for occupancy
	    if (!sameDiagonal(destination)) return false;

	    int curRank = Location.getRank();
	    int curFile = Location.getFile();
	    int destRank = destination.getRank();
	    int destFile = destination.getFile();

	    //Determine the step direction (-1 or 1)
	    int rankStep = (destRank > curRank) ? 1 : -1;
	    int fileStep = (destFile > curFile) ? 1 : -1;

	    //Start checking from the first square after the current location
	    int checkRank = curRank + rankStep;
	    int checkFile = curFile + fileStep;

	    // Walk the diagonal until we hit the destination
	    while (checkRank != destRank && checkFile != destFile) {
	        if (ChessBoard.getBoard()[checkRank][checkFile].getOccupancy()) {
	            return false; // Path is blocked!
	        }
	        checkRank += rankStep;
	        checkFile += fileStep;
	    }

	    return true; // Path is clear
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

//Not applicable for queen since it can move across the whole board



//Moving the piece must not put the king in check - WIP

//if(King.inCheck())



// QUEEN SPECIFC

//The destination must not be a knight move away

if(((Location.getRankDistance(destination) == 2 && Location.getFileDistance(destination) == 1) || (Location.getRankDistance(destination) == 1 && Location.getFileDistance(destination) == 2))) {

return false;

}

//There cannot be any pieces in the way (diagonal or horizontal)
if(sameRank(destination) || sameFile(destination)) {
	if(!isPathClear(destination)) {
		return false;
	}
}

if(sameDiagonal(destination)) {
	if(!isDiagonalPathClear(destination)) {
		return false;
	}
}

return true;

}

}
