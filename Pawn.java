import java.util.List;

@SuppressWarnings("serial")
public class Pawn extends Piece {
    
    public boolean hasMoved = false;
    public int direction;

    public Pawn(String col, Square pos) {
        super(col, 2, pos); 
        // Set direction once during creation so it's always reliable
        this.direction = col.equalsIgnoreCase("Black") ? 1 : -1;
    }
    
    @Override
    public boolean checkLegalMove(Square destination) {
        // 1. Basic Occupancy Checks
        if (destination.getOccupancy() && destination.getOccupant().getColor().equals(this.Color)) {
            throw new IllegalStateException("DEBUG: Target square is occupied by an ally piece!");
        }
        
        if (Location.equals(destination)) {
            throw new IllegalStateException("DEBUG: Target square is same as current location!");
        }

        // Check the distance to the location
        int fileDist = destination.getFile() - this.Location.getFile();
        int rankDist = destination.getRank() - this.Location.getRank();
        
        // 2. SPECIAL EN PASSANT LOGIC
        // If moving diagonally to an empty square, check if it's a valid En Passant
        if (Math.abs(fileDist) == 1 && rankDist == this.direction && !destination.getOccupancy()) {
            if (this.checkEnPassant(destination)) {
                GameStateManager.letMePassant();
                return true;
            }
        }
        
        // 3. TYPICAL MOVE LOGIC
        
        // Moving straight forwards
        if (fileDist == 0) {
            // Case A: Two squares forward
            if (rankDist == 2 * direction) { 
                if (!hasMoved) {
                    // Check the square in the middle (jump-over check)
                    Square middleSquare = ChessBoard.getSquare(this.Location.getRank() + direction, this.Location.getFile());
                    if (middleSquare.getOccupancy()) {
                        throw new IllegalStateException("DEBUG: Pawn cannot jump over pieces!");
                    }
                    if (destination.getOccupancy()) {
                        throw new IllegalStateException("DEBUG: Destination is blocked!");
                    }
                    return true;
                } else {
                    throw new IllegalStateException("DEBUG: Pawn can only move 2 squares on its first move!");
                }
            } 
            // Case B: One square forward
            else if (rankDist == direction) {
                if (!destination.getOccupancy()) {
                    return true;
                } else {
                    throw new IllegalStateException("DEBUG: Pawn is blocked by another piece!");
                }
            } else { 
                throw new IllegalStateException("DEBUG: Illegal forward movement distance!");
            }

        } 
        // 4. DIAGONAL CAPTURE LOGIC (Non-En Passant)
        else if (Math.abs(fileDist) == 1 && rankDist == direction) {
            if (destination.getOccupancy()) {
                // Already checked for ally pieces at the start, so this is an enemy capture
                return true;
            } else {
                throw new IllegalStateException("DEBUG: Pawn cannot move diagonally unless capturing!");
            }
        } else {
            throw new IllegalStateException("DEBUG: Illegal move pattern for Pawn!");
        }
    }
    
    public boolean checkEnPassant(Square destination) {
        // The victim is always on the same rank as the attacker and same file as the destination
        Square victim = ChessBoard.getSquare(this.Location.getRank(), destination.getFile());
        
        if (victim != null && victim.getOccupancy()) {
            if (victim.getOccupant() instanceof Pawn) {
                // Verify the GameStateManager currently considers this square vulnerable
                if (GameStateManager.thereIsPassant && victim.equals(GameStateManager.getWherePassant())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void promote(int pieceType) {
        Piece newPiece;
        switch(pieceType) {
            case 1: newPiece = new Queen(this.Color, this.Location); break;
            case 2: newPiece = new Rook(this.Color, this.Location); break;
            case 3: newPiece = new Bishop(this.Color, this.Location); break;
            case 4: newPiece = new Knight(this.Color, this.Location); break;
            default: newPiece = new Queen(this.Color, this.Location); break;
        }
        this.Location.occupySquare(newPiece);
        this.Location = null; // Retire the pawn object
    }

    @Override
    public List<Move> getValidMoves(ChessBoard board, Square currentSquare) {
        // To be implemented for AI/Bot move generation later
        return null;
    }
}