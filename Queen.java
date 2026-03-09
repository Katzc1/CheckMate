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
