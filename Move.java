import java.io.Serializable;

@SuppressWarnings("serial")
public class Move implements Serializable {
    public int startRow, startCol, endRow, endCol;
    public String playerColor;
    public boolean isEnPassant = false;
    public boolean isCastle = false;
    public Rook DAROOK = null;
    public int promotionType = 0; 

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

        try {
            // Basic legal check from the piece itself
            return p.checkLegalMove(realEnd);
        } catch (Exception e) { 
            return false; 
        }
    }

    public void executeMove() {
        Square realStart = ChessBoard.getSquare(startRow, startCol);
        Square realEnd = ChessBoard.getSquare(endRow, endCol);
        if (realStart == null || !realStart.getOccupancy()) return;

        Piece p = realStart.getOccupant();
        
        // standard move logic
        if (realEnd.getOccupancy()) realEnd.unOccupySquare();
        realStart.unOccupySquare();
        realEnd.occupySquare(p);
        p.setLocation(realEnd);

        if (p instanceof Pawn) {
            ((Pawn) p).hasMoved = true;
            if (Math.abs(startRow - endRow) == 2) GameStateManager.setPassantEligable(realEnd);
            // Promotion logic
            if ((endRow == 0 || endRow == 7) && promotionType > 0) {
                ((Pawn) p).promote(promotionType);
            }
        }
    }
}