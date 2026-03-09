public class Pawn extends Piece {
	//
    public boolean hasMoved = false;
    public boolean enPassantEligible = false;

    public Pawn(String col, Square pos) {
        super(col, 2, pos); // Range starts at 2 for the first move
    }

    @Override
    public boolean checkLegalMove(Square destination) {
        // 1. Basic distance and direction math
        int fileDist = destination.getFile() - this.Location.getFile();
        int rankDist = destination.getRank() - this.Location.getRank();
        
        // Determine direction based on color
        int direction = this.Color.equals("White") ? 1 : -1;

        // 2. --- STRAIGHT MOVE (No Captures) ---
        if (fileDist == 0) {
            // One square forward: Square must be empty
            if (rankDist == direction) {
                return !destination.getOccupancy();
            }
            
            // Two squares forward: First move only, square and path must be empty
            if (!hasMoved && rankDist == 2 * direction) {
                Square middleSquare = ChessBoard.getBoard()[this.Location.getRank() + direction][this.Location.getFile()];
                return !destination.getOccupancy() && !middleSquare.getOccupancy();
            }
        }

        // 3. --- DIAGONAL CAPTURE ---
        if (Math.abs(fileDist) == 1 && rankDist == direction) {
            if (destination.getOccupancy()) {
                // Standard capture: must be an enemy
                return !destination.getOccupant().getColor().equals(this.Color);
            } 
            // Note: You can add En Passant logic here later
        }

        return false; 
    }

    /**
     * Replaces the pawn with a new piece type.
     * Note: This logic assumes your Square class has an occupySquare method.
     */
    public void promote(int pieceType) {
        Piece newPiece;
        switch (pieceType) {
            case 1: newPiece = new Queen(this.Color, this.Location); break;
            case 2: newPiece = new Rook(this.Color, this.Location); break;
            case 3: newPiece = new Bishop(this.Color, this.Location); break;
            case 4: newPiece = new Knight(this.Color, this.Location); break;
            default: newPiece = new Queen(this.Color, this.Location);
        }
        
        // Remove pawn and place new piece on the current square
        this.Location.occupySquare(newPiece);
    }
}