import java.util.List;

public class Pawn extends Piece {
	//
    public boolean hasMoved = false;
    public boolean enPassantEligible = false;

    public Pawn(String col, Square pos) {
        super(col, 2, pos); // Range starts at 2 for the first move
    }
    
    
    @Override
    public boolean checkLegalMove(Square destination) {
        // 1. Basic Safety Checks
        if (destination.isOccupied && destination.getOccupant().getColor().equals(this.Color)) return false;
        if (Location.equals(destination)) return false;

        // 2. COORDINATE MATH
        // In your board: Row (Rank) is vertical, Column (File) is horizontal
        int fileDist = destination.getFile() - this.Location.getFile();
        int rankDist = destination.getRank() - this.Location.getRank();
        
        // DIRECTION FIX: 
        // White is at row 6, moving to row 4. Dist = 4 - 6 = -2.
        // So for White, direction is -1. For Black (row 1 to 3), direction is 1.
        int direction = this.Color.equalsIgnoreCase("White") ? -1 : 1;
        int startingRow = this.Color.equalsIgnoreCase("White") ? 6 : 1;

        // 3. STRAIGHT MOVE (No Captures)
        if (fileDist == 0) {
            // One square forward
            if (rankDist == direction) {
                return !destination.getOccupancy();
            }
            
            // Two squares forward (e2 to e4)
            if (!hasMoved && rankDist == 2 * direction) {
                // Check the square in the middle (e3)
                Square middleSquare = ChessBoard.getBoard()[this.Location.getRank() + direction][this.Location.getFile()];
                return !destination.getOccupancy() && !middleSquare.getOccupancy();
            }
        }

        // 4. DIAGONAL CAPTURE
        if (Math.abs(fileDist) == 1 && rankDist == direction) {
            if (destination.getOccupancy()) {
                return !destination.getOccupant().getColor().equals(this.Color);
            }
        }

        return false; 
    }

   
    public void promote(int pieceType) {
        Piece newPiece;
        if(pieceType == 1) {
        	newPiece = new Queen(this.Color, this.Location); 
        	 this.Location.occupySquare(newPiece);
        	
        }
        
        if(pieceType == 2) {
        	newPiece = new Rook(this.Color, this.Location); 
        	 this.Location.occupySquare(newPiece);
        	
        }
        
        if(pieceType == 3) {
        	newPiece = new Bishop(this.Color, this.Location); 
        	 this.Location.occupySquare(newPiece);
        	
        }
        
        if(pieceType == 4) {
        	newPiece = new Knight(this.Color, this.Location); 
        	 this.Location.occupySquare(newPiece);
        	
        }
            //Technically it cannot kill itself after this so we just have to kind of let it exist in the void without a purpose
            this.Location = null;
        
    }


	@Override
	public List<Move> getValidMoves(ChessBoard board, Square currentSquare) {
		// TODO Auto-generated method stub
		return null;
	}
}
