import java.util.ArrayList;
import java.util.List;
public class GameStateManager {
	public static Square wherePassant;
	public static int moveTimer = 0;
	public static boolean thereIsPassant = false;
	public static boolean wantsToPassant = false;
	public static boolean wantsToCastle = false;
	public static Piece[] castleWith = new Piece[2];
	
	public static void setPassantEligable(Square s){
		wherePassant = s;
		moveTimer = 2;
		thereIsPassant = true;
	}
	
	public static Square getWherePassant() {
		return wherePassant;
	}
	
	public static void removePassantEligable(Square s){
		wherePassant = null;
		thereIsPassant = false;
	}
	
	public static void letMePassant() {
		wantsToPassant = true;
	}
	
	public static void letMeCastle(King k, Rook r) {
		castleWith[0] = k;
		castleWith[1] = r;
		wantsToCastle = true;
	}
	
	public static Piece[] whereCastle() {
		return castleWith;
	}
	
	public static void tickMoveTimer() {
		moveTimer--;
	}
	
	public static boolean isInCheck(King k) {
		Square target = k.getLocation();
		int colorIndex = k.getColor().equals("White") ? 1 : 0;
		Piece[] totalPieces = colorIndex == 0 ? ChessBoard.getWhitePieces() : ChessBoard.getBlackPieces();
		for(Piece p : totalPieces) {
			try {
				if(p.checkLegalMove(target)) {
					return true;
				}
			} catch(IllegalStateException ex) {
			}
			
		}
		return false;
	}
	
	public static boolean wouldBeInCheck(King k, Square destination) {
	    // 1. Save the piece that is currently on the destination (the victim)
	    Piece victim = destination.getOccupant();
	    Square originalLocation = k.getLocation();
	    // 2. Mock the move
	    originalLocation.unOccupySquare();
	    destination.occupySquare(k);
	    // IMPORTANT: Even if there was an enemy there, occupySquare replaces it
	   
	    // 3. Check if any enemy can now hit this destination
	    boolean result = false;
	    Piece[] enemies = k.getColor().equals("White") ? ChessBoard.getBlackPieces() : ChessBoard.getWhitePieces();
	   
	    for (Piece p : enemies) {
	        // Skip the victim! They are dead/captured and can't check the king
	        if (p == victim) continue;
	       
	        try {
	            if (p.checkLegalMove(destination)) {
	                result = true;
	                break;
	            }
	        } catch (IllegalStateException e) {}
	    }
	    // 4. RESET everything exactly as it was
	    destination.occupySquare(victim); // Put the victim back (if null, it stays null)
	    if (victim == null) destination.isOccupied = false; // Ensure occupancy flag is correct
	    originalLocation.occupySquare(k);
	   
	    return result;
	}
	
	public static boolean wouldBeInCheck(King k, Move m) {
		Square target = k.getLocation();
		int colorIndex = k.getColor().equals("White") ? 1 : 0;
		boolean result = false;
		
		//Update board state to reflect the theoretical move
		Piece possiblePinnedPiece = ChessBoard.getSquare(m.startRow, m.startCol).getOccupant();
		ChessBoard.getSquare(m.endRow, m.endCol).occupySquare(possiblePinnedPiece);
		ChessBoard.getSquare(m.startRow, m.startCol).unOccupySquare();
		
		Piece[] totalPieces = colorIndex == 0 ? ChessBoard.getWhitePieces() : ChessBoard.getBlackPieces();
		for(Piece p : totalPieces) {
			try {
				if(p.checkLegalMove(target)) {
					
					result = true;
				}
			} catch(IllegalStateException ex) {
				//Do absolutely nothing
			}
			
		}
		//before providing the result, reinstate the previous board state before the theoretical move
		ChessBoard.getSquare(m.startRow, m.startCol).occupySquare(possiblePinnedPiece);
		ChessBoard.getSquare(m.endRow, m.endCol).unOccupySquare();
		return result;
	}
	
	
	
	
	
	public static List<Piece> getAttackers(King k) {
	    List<Piece> attackers = new java.util.ArrayList<>();
	    Piece[] enemies = k.getColor().equalsIgnoreCase("White") ? ChessBoard.getBlackPieces() : ChessBoard.getWhitePieces();
	    for (Piece p : enemies) {
	        try {
	            if (p.checkLegalMove(k.getLocation())) {
	                attackers.add(p);
	            }
	        } catch (IllegalStateException e) {
	            // Not attacking the king
	        }
	    }
	    return attackers;
	}
	public static boolean isCheckmate(King k) {
		
		//You are only checkmated if the king is in check, the king can't move out of the check, there is no piece that can capture the attacking piece, and there is no piece to block.
   if (!isInCheck(k)) return false;
   List<Piece> attackers = getAttackers(k);
   Piece[] allies = k.getColor().equalsIgnoreCase("White") ? ChessBoard.getWhitePieces() : ChessBoard.getBlackPieces();
   //ESCAPE: Can the King move?
   int[] range = {-1, 0, 1};
   for (int r : range) {
       for (int f : range) {
           if (r == 0 && f == 0) continue;
           int targetRank = k.getLocation().getRank() + r;
           int targetFile = k.getLocation().getFile() + f;
           if (targetRank >= 0 && targetRank <= 7 && targetFile >= 0 && targetFile <= 7) {
               Square dest = ChessBoard.getSquare(targetRank, targetFile);
               if (dest != null) {
                   Move m = new Move(k.getLocation(), dest, k.getColor());
                   if (m.checkValidMove()) return false;
               }
           }
       }
   }
   if (attackers.size() > 1) return true;
   //CAPTURE: Can we kill the lone attacker?
   Piece loneAttacker = attackers.get(0);
   for (Piece p : allies) {
       Move m = new Move(p.getLocation(), loneAttacker.getLocation(), p.getColor());
       if (m.checkValidMove()) return false;
   }
   //BLOCK: Can we step in front of the lone attacker?
   if (loneAttacker instanceof Rook || loneAttacker instanceof Bishop || loneAttacker instanceof Queen) {
       for (int row = 0; row < 8; row++) {
           for (int col = 0; col < 8; col++) {
               Square potBlock = ChessBoard.getSquare(row, col);
              
               if (potBlock != null && !potBlock.getOccupancy()) {
                   boolean onPath = false;
                   try {
                       // We check if the ATTACKER has a clear path to the KING,
                       // and if the KING has a clear path to the potential BLOCK SQUARE.
                      
                       // CASE: ROOK OR QUEEN (Straight lines)
                       if ((loneAttacker instanceof Rook || loneAttacker instanceof Queen) &&
                           (k.getLocation().sameRank(potBlock) && loneAttacker.getLocation().sameRank(potBlock) ||
                            k.getLocation().sameFile(potBlock) && loneAttacker.getLocation().sameFile(potBlock))) {
                          
                           // Cast the attacker to Rook to use its isPathClear method
                           boolean clearToKing = (loneAttacker instanceof Rook) ?
                               ((Rook)loneAttacker).isPathClear(k.getLocation()) :
                               ((Queen)loneAttacker).isPathClear(k.getLocation());
                              
                           //check distance alignment.
                           if (clearToKing) {
                               double distToAttacker = k.getLocation().getTotalDistance(loneAttacker.getLocation());
                               double distToBlock = k.getLocation().getTotalDistance(potBlock);
                               if (distToAttacker > distToBlock) {
                                   onPath = true;
                               }
                           }
                       }
                       // CASE: BISHOP OR QUEEN (Diagonals)
                       else if ((loneAttacker instanceof Bishop || loneAttacker instanceof Queen) &&
                                k.getLocation().sameDiagonal(potBlock) && loneAttacker.getLocation().sameDiagonal(potBlock)) {
                          
                           boolean clearToKing = (loneAttacker instanceof Bishop) ?
                               ((Bishop)loneAttacker).isDiagonalPathClear(k.getLocation()) :
                               ((Queen)loneAttacker).isDiagonalPathClear(k.getLocation());
                              
                           if (clearToKing) {
                               double distToAttacker = k.getLocation().getTotalDistance(loneAttacker.getLocation());
                               double distToBlock = k.getLocation().getTotalDistance(potBlock);
                               if (distToAttacker > distToBlock) {
                                   onPath = true;
                               }
                           }
                       }
                   } catch (Exception e) {}
                   if (onPath) {
                       for (Piece p : allies) {
                           if (p instanceof King) continue;
                           Move m = new Move(p.getLocation(), potBlock, p.getColor());
                           if (m.checkValidMove()) return false;
                       }
                   }
               }
           }
       }
   }
   return true;
}
	
	public static void handleCheckmate() {
		System.out.println("CHECKMATE! GAME OVER!!!!!");
		System.exit(0);
	}
	
	
	
	
}



