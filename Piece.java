//This class has been made abstract because there isn't a use case for a generic "Piece"

//The pieces on the board will always be child pieces such as pawns and rooks

public abstract class Piece {

//We define attributes every piece should have, and set the default color of the piece to Black

public String Color = "Black";

public double spacesToMove;

public Square Location;

//Constructor

public Piece(String col, double numSpc, Square loc) {

this.Color = col;

this.spacesToMove = numSpc;

this.Location = loc;

}

//Because the method is abstract, we can say that each child has to decide for itself

//how the method works. Because there is no universal legality for every piece we

//leave this as a placeholder until the child classes define it.

public abstract boolean checkLegalMove(Square destination);

//Other methods that each and every piece should be able to do.

public void setLocation(Square destination) {

Location = destination;

}

public Square getLocation() {

return Location;

}

public String getColor() {

return Color;

}

}
