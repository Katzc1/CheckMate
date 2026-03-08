public class Pawn extends Piece {

//PLACEHOLDER: PLEASE DELETE LATER!!!!!!

//

//

//

Square[][] board = new Square[8][8];

//

//

//

//DELETE TS!!!!!!!!!!!!

public Pawn(String col, Square pos) {

super(col, 2, pos);

}

public boolean hasMoved = false;

public boolean enPassantEligible = false;

public void promote(int pieceType) {

//Find some way to delete this pawn

//1 == queen

if(pieceType == 1) {

Queen q = new Queen(Color, Location);

}

//2 == rook

if(pieceType == 2) {

Rook r = new Rook(Color, Location);

}

//3 == bishop

if(pieceType == 3) {

Bishop b = new Bishop(Color, Location);

}

//4 == knight

if(pieceType == 4) {

Knight n = new Knight(Color, Location);

}

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

//For pawns, if the pawn has been moved it can only move 1 square after that.

if(hasMoved) {

this.spacesToMove = 1;

}

if(Math.abs(Location.getFileDistance(destination)) - spacesToMove < 0 || Location.getRankDistance(destination) - spacesToMove < 0) {

return false;

}

// PAWN SPECIFC

//The pawn cannot move diagonally (except for captures)

//The pawn cannot move backwards.

//The pawn cannot move into other pawns.

return true;

}

}