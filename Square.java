
import java.io.Serializable;

@SuppressWarnings("serial")
public class Square implements Serializable {
	
//Rank and file: coordinates as to where the square is

public int rank;

//In this case both are represented as integers while the names of the squares denote

//Their actual algebraic chess notation (e5 vs 5 5)

public int file;

public String Color;

//Having a boolean to check if the square is occupied rather than relying on

//Whether or not onSquare is null is safer. Don't really want to mess with null and boolean

//checks are easier than checking if it is equal to null

public boolean isOccupied;

public Piece onSquare;


public Square(int row, int column) {

this.rank = row;

this.file = column;
}





//Constructor for when the square is empty (technically dont need the bool in this case

//But kept for clarity

public Square(int row, int column, String col, boolean occ) {

this.rank = row;

this.file = column;

this.Color = col;

this.isOccupied = occ;

}

//Constructor for when the square is occupied

public Square(int row, int column, String col, boolean occ, Piece p) {

this.rank = row;

this.file = column;

this.Color = col;

this.isOccupied = occ;

this.onSquare = p;

}

public int getRank() {

return rank;

}

public int getFile() {

return file;

}

public String getColor() {

return Color;

}

public boolean getOccupancy() {

return isOccupied;

}

//Just in case we need to know exactly what piece is on the square

public Piece getPieceOnSquare() {

//Can't know what piece is on the square if the square isn't occupied

if(!isOccupied) {

throw new IllegalStateException("Square isn't occupied!");

}

return onSquare;

}

public void occupySquare(Piece p) {

//Can't occupy a square that is already occupied

if(isOccupied) {

throw new IllegalStateException("Square already occupied!");

}

onSquare = p;

isOccupied = true;

}

public void unOccupySquare() {

//Can't unoccupy a square that is already unoccupied

if(!isOccupied) {

throw new IllegalStateException("Square already unoccupied!");

}

onSquare = null;

isOccupied = false;

}

public double getTotalDistance(Square destination) {

//Using typical distance formula, storing signed

return Math.sqrt((this.rank - destination.getRank())^2 + (this.file - destination.getFile())^2);

}

//We can just convert both to an absolute value to make the legality check easier.

public int getRankDistance(Square destination) {

return Math.abs(this.rank - destination.getRank());

}

//Will have to check for pawn legality seperately

public int getFileDistance(Square destination) {

return Math.abs(this.file - destination.getFile());

}

}