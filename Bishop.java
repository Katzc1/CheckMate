import java.util.ArrayList;
import java.util.List;

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



@Override
public List<Move> getValidMoves(ChessBoard board, Square currentSquare) {
    List<Move> moves = new ArrayList<>();
    int r = currentSquare.getRank();
    int c = currentSquare.getFile();

    // The four diagonal directions: {rowStep, colStep}
    int[][] directions = {
        {1, 1},   // Down-Right
        {1, -1},  // Down-Left
        {-1, 1},  // Up-Right
        {-1, -1}  // Up-Left
    };

    for (int[] d : directions) {
        // Check squares in this direction until we hit the edge of the board
        for (int i = 1; i < 8; i++) {
            int targetRank = r + (d[0] * i);
            int targetFile = c + (d[1] * i);

            Square target = ChessBoard.getSquare(targetRank, targetFile);

            // If the square is off-board, stop looking in this direction
            if (target == null) break;

            // Use your existing logic to see if this specific square is a valid move
            if (checkLegalMove(target)) {
                moves.add(new Move(currentSquare, target, this.Color));
            }

            // IMPORTANT: If there is ANY piece on this square, we stop "sliding" 
            // because a Bishop cannot jump over pieces.
            if (target.getOccupancy()) {
                break;
            }
        }
    }

    return moves;
}

}
