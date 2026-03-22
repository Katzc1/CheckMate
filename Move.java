import java.io.Serializable;

@SuppressWarnings("serial")
public class Move implements Serializable {
    // Use primitive ints for the network transfer. 
    // These never change or "clone" incorrectly.
    public int startRow, startCol, endRow, endCol;
    public String playerColor; 

    public Move(Square location, Square destination, String playerColor) {
        this.startRow = location.getRank();
        this.startCol = location.getFile();
        this.endRow = destination.getRank();
        this.endCol = destination.getFile();
        this.playerColor = playerColor;
    }
    
    public int getStartRow() {
    	return startRow;
    }
    
    public int getStartCol() {
    	return startCol;
    }
    
    public int getEndRow() {
    	return endRow;
    }
    
    public int getEndCol() {
    	return endCol;
    }

    // This checks if the move is valid BEFORE sending
    public boolean checkValidMove() {
        Square realStart = ChessBoard.getSquare(startRow, startCol);
        Square realEnd = ChessBoard.getSquare(endRow, endCol);

        if (!realStart.getOccupancy()) {
            System.out.println("Debug: Start square is empty");
            return false;
        }
        
        Piece p = realStart.getOccupant();
        
        // 1. Check Color 
        if (!p.getColor().trim().equalsIgnoreCase(playerColor.trim())) {
            System.out.println("Debug: Color Mismatch!");
            return false;
        }

        // 2. CRITICAL: Reset the En Passant intent flag before checking logic
        // This prevents "ghost" captures from previous UI interactions
        GameStateManager.wantsToPassant = false;

        // 3. Check Piece Rules (Pawn/Rook/etc)
        try {
            // This call will set wantsToPassant to true ONLY if it's a valid En Passant
            return p.checkLegalMove(realEnd);
        } catch (IllegalStateException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        
        //TO ADD: CHECK IF THE MOVE WOULD PUT THE KING IN CHECK
    }

    // This performs the actual swap on the master board
    public void executeMove() {
        Square realStart = ChessBoard.getSquare(startRow, startCol);
        Square realEnd = ChessBoard.getSquare(endRow, endCol);

        if (realStart.getOccupancy()) {
            Piece p = realStart.getOccupant();
            
            if (p instanceof Pawn) {
                // 1. CAPTURE LOGIC
                if (GameStateManager.wantsToPassant) {
                    // Get the square we marked as vulnerable in the previous turn
                    Square victimSquare = GameStateManager.getWherePassant();
                    if (victimSquare != null) {
                        System.out.println("DEBUG: EN PASSANT SUCCESS! Deleting victim at " + victimSquare.getRank());
                        victimSquare.unOccupySquare();
                    }
                    // Reset flags
                    GameStateManager.wantsToPassant = false;
                    GameStateManager.thereIsPassant = false;
                } 
                // 2. DOUBLE JUMP LOGIC (Set for next turn)
                else if (Math.abs(startRow - endRow) == 2) {
                    GameStateManager.setPassantEligable(realEnd);
                }
                // 3. NORMAL MOVE (Clear old passant)
                else {
                    GameStateManager.thereIsPassant = false;
                }

                ((Pawn) p).hasMoved = true;
            }

            // Standard Capture/Move Logic
            if (realEnd.getOccupancy()) {
                realEnd.unOccupySquare();
            }

            realStart.unOccupySquare();
            realEnd.occupySquare(p);
            p.setLocation(realEnd);
        }
    }
}