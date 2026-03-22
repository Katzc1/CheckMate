import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChessBot {
    private Random random;

    public ChessBot() {
        this.random = new Random();
    }

    /**
     * @param board The current ChessBoard object
     * @param botColor "White" or "Black"
     * @return A random legal Move
     */
    public Move generateRandomMove(ChessBoard board, String botColor) {
        List<Move> allLegalMoves = new ArrayList<>();
        
        // Use the static getter from your ChessBoard class
        Square[][] grid = ChessBoard.getBoard();

        // 1. Scan the entire board (0 to 7)
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Square sq = grid[r][c];
                
                // 2. Check if there's a piece and if it belongs to the bot
                if (sq.getOccupancy() && sq.getOccupant().getColor().equals(botColor)) {
                    
                    // 3. Get moves for this specific piece
                    // Note: Your Piece.java needs the getValidMoves(board, sq) method
                    List<Move> pieceMoves = sq.getOccupant().getValidMoves(board, sq);
                    
                    if (pieceMoves != null) {
                        allLegalMoves.addAll(pieceMoves);
                    }
                }
            }
        }

        // 4. Safety check: If no moves found, the game is over
        if (allLegalMoves.isEmpty()) {
            return null; 
        }

        // 5. Pick one move at random from the list
        int randomIndex = random.nextInt(allLegalMoves.size());
        return allLegalMoves.get(randomIndex);
    }
}
