public class King extends Piece {


public King(String col, Square pos) {

super(col, 1, pos);

}

public boolean hasMoved = false;

public boolean inCheck;

public boolean wouldBeInCheck(Square destination) {

return false;

}

public boolean getCheckStatus() {

return inCheck;

}

//Override just saying "this is the child implementing the abstract method"

//Helps in the case of a typo

@Override

public boolean checkLegalMove(Square destination){

//Destination Square must not be occupied by a piece of the same color

if(destination.isOccupied && destination.getPieceOnSquare().getColor() == this.Color) {

return false;

}

//Destination Square must not be the same as the current Square

if(Location.equals(destination) || this.Location.getTotalDistance(destination) == 0) {

return false;

}

//Destination Square must not be outside of the spaces the piece can move

if(Math.abs(Location.getFileDistance(destination)) - spacesToMove < 0 || Location.getRankDistance(destination) - spacesToMove < 0) {

return false;

}

// KING SPECIFC

//The destination must not put the king in check

if(wouldBeInCheck(destination)) {

return false;

}

return true;

}

}
