import java.io.Serializable;

@SuppressWarnings("serial")
public class Move implements Serializable {
    public int startRow, startCol, endRow, endCol;
    public String playerColor;
    
    // Special move flags for network synchronization
    public boolean isEnPassant = false;
    public boolean isCastle = false;
    public Rook DAROOK = null;
    public int promotionType = 0; // 0: None, 1: Queen, 2: Rook, 3: Knight, 4: Bishop

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
        if (!p.getColor().trim().equalsIgnoreCase(playerColor.trim())) {
            System.out.println("DEBUG: Wrong color turn");
            return false;
        }

        // Reset global flags before checking piece logic
        GameStateManager.wantsToPassant = false;
        GameStateManager.wantsToCastle = false;

        try {
            boolean valid = p.checkLegalMove(realEnd);
            
            // Flag special moves based on GameStateManager results
            if (valid && GameStateManager.wantsToPassant) {
                this.isEnPassant = true;
            }
            if (valid && GameStateManager.wantsToCastle) {
                this.DAROOK = (Rook) GameStateManager.castleWith[1];
                this.isCastle = true;
            }

            // KING SAFETY CHECK
            if (p instanceof King) {
                if (GameStateManager.wouldBeInCheck((King) p, realEnd)) {
                    throw new IllegalStateException("DEBUG: Move would put the king in check!");
                }
            } else {
                Piece[] friendlyPieces = playerColor.trim().equalsIgnoreCase("White") ? 
                                        ChessBoard.getWhitePieces() : ChessBoard.getBlackPieces();
                for (Piece piece : friendlyPieces) {
                    if (piece instanceof King) {
                        // If king is in check, ensure this move resolves it
                        if (GameStateManager.isInCheck((King) piece)) {
                            if (!GameStateManager.wouldBeInCheck((King) piece, this)) {
                                return valid;
                            } else {
                                throw new IllegalStateException("DEBUG: King is in check!");
                            }
                        }
                    }
                }
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

            // 1. Handle En Passant Capture
            if (this.isEnPassant) {
                int direction = (p.getColor().equalsIgnoreCase("Black")) ? 1 : -1;
                Square victimSquare = ChessBoard.getSquare(endRow - direction, endCol);
                if (victimSquare != null) {
                    victimSquare.unOccupySquare();
                    System.out.println("DEBUG: En Passant Deletion Successful");
                }
                GameStateManager.wantsToPassant = false;
                GameStateManager.thereIsPassant = false;
            }

            // 2. Handle Castling
            if (this.isCastle) {
                p.Location.unOccupySquare();
                realEnd.occupySquare(p);
                p.setLocation(realEnd);

                if (DAROOK != null) {
                    int rookEndFile = (endCol == 6) ? 5 : 3;
                    Square rookEndSquare = ChessBoard.getSquare(startRow, rookEndFile);
                    
                    DAROOK.Location.unOccupySquare();
                    rookEndSquare.occupySquare(DAROOK);
                    DAROOK.setLocation(rookEndSquare);
                    DAROOK.hasMoved = true;
                }
                GameStateManager.wantsToCastle = false;
            } 
            // 3. Handle Standard Moves & Promotion
            else {
                if (p instanceof Pawn) {
                    if (Math.abs(startRow - endRow) == 2) {
                        GameStateManager.setPassantEligable(realEnd);
                    } else {
                        GameStateManager.thereIsPassant = false;
                    }
                    ((Pawn) p).hasMoved = true;
                    
                    // PROMOTION LOGIC
                    if ((endRow == 0 || endRow == 7) && promotionType > 0) {
                        ((Pawn) p).promote(promotionType);
                    }
                } else {
                    GameStateManager.thereIsPassant = false;
                }

                if (p instanceof King) ((King) p).hasMoved = true;
                if (p instanceof Rook) ((Rook) p).hasMoved = true;

                // Finalize piece placement
                if (realEnd.getOccupancy()) realEnd.unOccupySquare();
                realStart.unOccupySquare();
                realEnd.occupySquare(p);
                p.setLocation(realEnd);
            }

            // 4. CHECKMATE CHECK
            String enemyColor = p.getColor().equalsIgnoreCase("White") ? "Black" : "White";
            Piece[] enemyPieces = enemyColor.equalsIgnoreCase("White") ? 
                                 ChessBoard.getWhitePieces() : ChessBoard.getBlackPieces();

            for (Piece piece : enemyPieces) {
                if (piece instanceof King) {
                    if (GameStateManager.isCheckmate((King) piece)) {
                        GameStateManager.handleCheckmate(p.getColor());
                    }
                    break;
                }
            }
        }
    }
}