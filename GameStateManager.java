
public class GameStateManager {
	public static Square wherePassant;
	public static int moveTimer = 0;
	public static boolean thereIsPassant = false;
	public static boolean wantsToPassant = false;
	
	public static void setPassantEligable(Square s){
		wherePassant = s;
		moveTimer = 2;
		thereIsPassant = true;
	}
	
	public static Square getWherePassant() {
		return wherePassant;
		//YOU ARE NOT ALLOWEDD!!!!!!!!!!!!!!!!!!!!!!!!!!
	}
	
	public static void removePassantEligable(Square s){
		wherePassant = null;
		thereIsPassant = false;
	}
	
	public static void letMePassant() {
		wantsToPassant = true;
	}
	
	public static void tickMoveTimer() {
		moveTimer--;
	}
	
	public static boolean isCheckmate() {
		return false;
	}
	
	public static void handleCheckmate() {
		
	}
	
	public static boolean isStalemate() {
		return false;
	}
	
	
	
	
}
