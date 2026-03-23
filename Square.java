import java.io.Serializable;
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
   	//We cannot directly delete an object in java, we can only make it eligable for garbage collection. We can do this by making the piece null
       this.onSquare = null;
       this.isOccupied = false;
   }
   public int getRankDistance(Square destination) {
       return Math.abs(this.rank - destination.getRank());
   }
   public int getFileDistance(Square destination) {
       return Math.abs(this.file - destination.getFile());
   }
  
 //Check if two squares share the same rank
 	public boolean sameRank(Square destination) {
 		if(this.getRank() == destination.getRank()) {
 			return true;
 		}
 		return false;
 	}
 	
 	//Check if two squares share the same file
 	public boolean sameFile(Square destination) {
 		if(this.getFile() == destination.getFile()) {
 			return true;
 		}
 		return false;
 	}
 	
 	public boolean sameDiagonal(Square destination) {
		//If the movement is the same for the vertical and horizontal, then it is on the same diagonal
		if(this.getRankDistance(destination) == this.getFileDistance(destination)) {
			return true;
		}
		return false;
	}
 	
 	public boolean equals(Square other) {
 	    return this.rank == other.getRank() && this.file == other.getFile();
 	}
}

