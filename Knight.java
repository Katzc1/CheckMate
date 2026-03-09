public class Knight extends Piece {

	//
public Knight(String col, Square pos) {

super(col, Math.sqrt(5), pos);

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

//Moving the piece must not put the king in check - WIP

//if(King.inCheck())

//KNIGHT SPECIFIC

//Destination Square must not be outside of the spaces the piece can move

//For the knight, this actually covers all possible movement squares so no further investigation required

if(!((Location.getRankDistance(destination) == 2 && Location.getFileDistance(destination) == 1) || (Location.getRankDistance(destination) == 1 && Location.getFileDistance(destination) == 2))) {

return false;

}

//If all conditions are met, then it is a legal move.

//(Doesn't actually move the piece, just checks if move is legal)

return true;

}

}
