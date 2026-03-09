public class Bishop extends Piece {
//


public Bishop(String col, Square pos) {

super(col, 8, pos);

}



public boolean sameDiagonal(Square destination) {
	//If the movement is the same for the vertical and horizontal, then it is on the same diagonal
	if(Location.getRankDistance(destination) == Location.getFileDistance(destination)) {
		return true;
	}
	
	return false;

}



public boolean isPathClear(Square destination) {

//If not on the same rank or file, move is illegal

if(!sameDiagonal(destination)) {

throw new IllegalStateException("Destination square is not a legal move!");

}



//To check all the squares in a line, we have to determine where to start and stop.

//Using min and max, we can be sure we start at the smaller value and end at the larger one

//It doesn't matter that it matches the exact direction of the bishop, so long as all the

//squares between the current location and destination are checked for occupancy

int startFile = Math.min(Location.getFile(), destination.getFile()) + 1;
int startRank = Math.min(Location.getFile(), destination.getFile()) + 1;

int endFile = Math.max(Location.getFile(), destination.getFile());
int endRank = Math.max(Location.getFile(), destination.getFile());

for(int i = startRank; i<endRank; i++) {
	
	for(int j = startFile; j < endFile; j++) {
		if(ChessBoard.getBoard()[i][j].isOccupied) {
			return false;
		}
	}
}



//If none of the squares in between are occupied, then the path isn't obstructed.

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

//Not applicable for bishop since it can move across the whole board


//Moving the piece must not put the king in check - WIP

//if(King.inCheck())



// BISHOP SPECIFC

//The destination must be diagonal of the starting square

if(!sameDiagonal(destination)) {
	return false;
}

//There cannot be any pieces in the way (diagonal)
if(!isPathClear(destination)) {
	return false;
}


return true;

}

}
