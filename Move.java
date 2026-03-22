import java.io.Serializable;

@SuppressWarnings("serial")
public class Move implements Serializable {
    public int startRow, startCol, endRow, endCol;
    public String playerColor; 
    public boolean isEnPassant = false; // NEW: Travels over the network

    public Move(Square location, Square destination, String playerColor) {
        this.startRow = location.getRank();
        this.startCol = location.getFile();
        this.endRow = destination.getRank();
        this.endCol = destination.getFile();
        this.playerColor = playerColor;
    }

    public boolean checkValidMove() {
        Square realStart = ChessBoard.getSquare(startRow, startCol);
        Square realEnd = ChessBoard.getSquare(endRow, endCol);
        if (realStart == null || !realStart.getOccupancy()) return false;
        
        Piece p = realStart.getOccupant();
        if (!p.getColor().trim().equalsIgnoreCase(playerColor.trim())) return false;

        GameStateManager.wantsToPassant = false; // Reset before check
        try {
            boolean valid = p.checkLegalMove(realEnd);
            // If the piece logic says this is an En Passant, flag this move object!
            if (valid && GameStateManager.wantsToPassant) {
                this.isEnPassant = true;
            }
            return valid;
        } catch (IllegalStateException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }

    public void executeMove() {
        Square realStart = ChessBoard.getSquare(startRow, startCol);
        Square realEnd = ChessBoard.getSquare(endRow, endCol);

        if (realStart != null && realStart.getOccupancy()) {
            Piece p = realStart.getOccupant();
            
            // Handle En Passant Capture (Using the internal flag for network sync)
            if (this.isEnPassant) {
                // Victim is always behind the destination
                int direction = (p.getColor().equalsIgnoreCase("Black")) ? 1 : -1;
                Square victimSquare = ChessBoard.getSquare(endRow - direction, endCol);
                if (victimSquare != null) {
                    victimSquare.unOccupySquare();
                    System.out.println("DEBUG: En Passant Deletion Successful");
                }
                GameStateManager.wantsToPassant = false;
                GameStateManager.thereIsPassant = false;
            }

            // Standard Pawn State Logic
            if (p instanceof Pawn) {
                if (Math.abs(startRow - endRow) == 2) {
                    GameStateManager.setPassantEligable(realEnd);
                } else {
                    GameStateManager.thereIsPassant = false;
                }
                ((Pawn) p).hasMoved = true;
            } else {
                GameStateManager.thereIsPassant = false;
            }

            // Finalize the move
            if (realEnd.getOccupancy()) realEnd.unOccupySquare();
            realStart.unOccupySquare();
            realEnd.occupySquare(p);
            p.setLocation(realEnd);
        }
    }
}