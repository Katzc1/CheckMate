import java.io.Serializable;
//
@SuppressWarnings("serial")
public class Square implements Serializable {
    public int rank;
    public int file;
    public String Color;
    public boolean isOccupied;
    public Piece onSquare;

    
    public Square(int row, int column) {
        this.rank = row;
        this.file = column;
    }
    
    
 

    public double getTotalDistance(Square destination) {
        return Math.sqrt(Math.pow(this.rank - destination.getRank(), 2) + Math.pow(this.file - destination.getFile(), 2));
    }

    public int getRank() {
    	return rank; 
    }
    public int getFile() { 
    	return file; 
    
    }
    public Piece getPieceOnSquare() { 
    	return onSquare; 
    }
    
    public boolean getOccupancy() {
    	return isOccupied;
    }
    
    public Piece getOccupant() { 
    	return onSquare; 
    }

    public void occupySquare(Piece p) {
        this.onSquare = p;
        this.isOccupied = true;
    }

    public void unOccupySquare() {
        this.onSquare = null;
        this.isOccupied = false;
    }

    public int getRankDistance(Square destination) {
        return Math.abs(this.rank - destination.getRank());
    }

    public int getFileDistance(Square destination) {
        return Math.abs(this.file - destination.getFile());
    }
}