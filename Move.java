import java.io.Serializable;

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
        
        // DEBUG: See exactly what the computer is comparing
        System.out.println("Debug: Piece Color: " + p.getColor() + " | Player Color: " + playerColor);

        // 1. Check Color (Ignoring case and extra spaces)
        if (!p.getColor().trim().equalsIgnoreCase(playerColor.trim())) {
            System.out.println("Debug: Color Mismatch!");
            return false;
        }

        // 2. Check Piece Rules (Pawn/Rook/etc)
        boolean legal = p.checkLegalMove(realEnd);
        if (!legal) System.out.println("Debug: Piece rules say move is illegal");
        
        return legal;
    }

    // This performs the actual swap on the master board
    public void executeMove() {
        Square realStart = ChessBoard.getSquare(startRow, startCol);
        Square realEnd = ChessBoard.getSquare(endRow, endCol);

        if (realStart.getOccupancy()) {
            Piece p = realStart.getOccupant();
            
            if (p instanceof Pawn) {
                ((Pawn) p).hasMoved = true;
            }
            
            // Handle Capture logic locally
            if (realEnd.getOccupancy()) {
                realEnd.unOccupySquare();
            }

            realStart.unOccupySquare();
            realEnd.occupySquare(p);
            p.setLocation(realEnd);
        } 
    }
}