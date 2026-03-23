import java.io.Serializable;
@SuppressWarnings("serial")
public class Move implements Serializable {
   public int startRow, startCol, endRow, endCol;
   public String playerColor;
   //Both en passant and castling impact mutliple different squares, so they must be stored over the network.
   public boolean isEnPassant = false;
   public boolean isCastle = false;
   public Rook DAROOK = null;
   public Move(Square location, Square destination, String playerColor) {
       this.startRow = location.getRank();
       this.startCol = location.getFile();
       this.endRow = destination.getRank();
       this.endCol = destination.getFile();
       this.playerColor = playerColor;
   }
  
   private void checkOpponentCheckmate(String currentMovingColor) {
       String opponentColor = currentMovingColor.equalsIgnoreCase("White") ? "Black" : "White";
       Piece[] opponentPieces = opponentColor.equalsIgnoreCase("White") ?
                                ChessBoard.getWhitePieces() : ChessBoard.getBlackPieces();
      
       for (Piece piece : opponentPieces) {
           if (piece instanceof King) {
               if (GameStateManager.isCheckmate((King) piece)) {
                   GameStateManager.handleCheckmate();
               }
               return;
           }
       }
   }
   public boolean checkValidMove() {
       Square realStart = ChessBoard.getSquare(startRow, startCol);
       Square realEnd = ChessBoard.getSquare(endRow, endCol);
       if (realStart == null || !realStart.getOccupancy()) return false;
      
       Piece p = realStart.getOccupant();
       if (!p.getColor().trim().equalsIgnoreCase(playerColor.trim())) {
       	System.out.println("DEBUG: Wrong color twin");
       	return false;
       }
      
       //Reset before checked
       GameStateManager.wantsToPassant = false;
       GameStateManager.wantsToCastle = false;
       try {
           boolean valid = p.checkLegalMove(realEnd);
           // If the piece logic says this is an En Passant, flag this move object!
           if (valid && GameStateManager.wantsToPassant) {
               this.isEnPassant = true;
           }
          
           if (valid && GameStateManager.wantsToCastle) {
           	DAROOK = (Rook)GameStateManager.castleWith[1];
           	this.isCastle = true;
           }
           //Now, would the move put the king in check??
           if(p instanceof King) {
           	if(GameStateManager.wouldBeInCheck((King)p, realEnd)) {
           		throw new IllegalStateException("DEBUG: Move would put the king in check!");
           	}
           } else {
           	Piece[] friendlyPieces = playerColor.trim().equalsIgnoreCase("White") ? ChessBoard.getWhitePieces() : ChessBoard.getBlackPieces();
           	for(Piece piece : friendlyPieces) {
           		if(piece instanceof King) {
           			if(GameStateManager.isInCheck((King)piece)) {
           				if(!GameStateManager.wouldBeInCheck((King)piece, this)) {
           					//This is to allow moves that get the king out of check!
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
          
           //Handle castling
           if(this.isCastle) {
               // Move the King
               p.Location.unOccupySquare();
               realEnd.occupySquare(p);
               p.setLocation(realEnd);
              
               //Move the Rook
               if (DAROOK != null) {
                   int direction = (startCol - endCol > 0) ? 1 : -1;
                   int rookEndFile = (endCol == 6) ? 5 : 3;
                   Square rookEndSquare = ChessBoard.getSquare(startRow, rookEndFile);
                  
                   DAROOK.Location.unOccupySquare(); // Use DAROOK directly
                   rookEndSquare.occupySquare(DAROOK);
                   DAROOK.setLocation(rookEndSquare);
                   DAROOK.hasMoved = true;
               }
              
               // Reset the global flag so it doesn't interfere with next turn
               GameStateManager.wantsToCastle = false;
           } else {
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
              
               //King logic (for castling)
               if(p instanceof King) {
               	((King) p).hasMoved = true;
               }
              
               //Rook logic (for castling)
               if(p instanceof Rook) {
               	((Rook) p).hasMoved = true;
               }
               // Finalize the move
               if (realEnd.getOccupancy()) realEnd.unOccupySquare();
               realStart.unOccupySquare();
               realEnd.occupySquare(p);
               p.setLocation(realEnd);
              
           }
           //CHECKMATE CHECK
           String enemyColor = p.getColor().equalsIgnoreCase("White") ? "Black" : "White";
          
           //Find the enemy King and check if they are checkmated
           Piece[] enemyPieces = enemyColor.equalsIgnoreCase("White") ? ChessBoard.getWhitePieces() : ChessBoard.getBlackPieces();
          
           for (Piece piece : enemyPieces) {
               if (piece instanceof King) {
                   if (GameStateManager.isCheckmate((King) piece)) {
                       GameStateManager.handleCheckmate();
                   }
                   break;
               }
           }
       }
   }
}



