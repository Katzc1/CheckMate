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
		Square target = destination;
		int colorIndex = k.getColor().equals("White") ? 1 : 0;
		Piece[] totalPieces = colorIndex == 0 ? ChessBoard.getWhitePieces() : ChessBoard.getBlackPieces();
		for(Piece p : totalPieces) {
			try {
				if(p.checkLegalMove(target)) {
					return true;
				}
			} catch(IllegalStateException ex) {
				//Do absolutely nothing
			}
			
		}
		return false;
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
	
	public static boolean isCheckmate() {
		//To determine if you are in checkmate, we only need to check if the king is is in check, and that there are no legal moves that can get the king out of check.
		//First type of evasion: moving the king away from the check
		//Check all 8 squares around the king (if they exist) to see if the king can move to any of them
		return false;
	}
	
	public static void handleCheckmate() {
		//You are cooked
	}
	
	public static boolean isStalemate() {
		return false;
	}
	
	
	
	
}