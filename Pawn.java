import java.util.List;

public class Pawn extends Piece {
	
    public boolean hasMoved = false;
    public int direction = 0;

    public Pawn(String col, Square pos) {
        super(col, 2, pos); // Range starts at 2 for the first move
    }
    
    @Override
    public boolean checkLegalMove(Square destination) {
    	//Thrown errors are self explanatory
        if (destination.isOccupied && destination.getOccupant().getColor().equals(this.Color)) {
        	throw new IllegalStateException("DEBUG: Target square is occupied by an ally piece!");
        }
        
        if (Location.equals(destination)) {
        	throw new IllegalStateException("DEBUG: Target square is same as current location!");
        }

      //For white, since 0,0 is in the top left, white's direction is technically negative. Just simplify the direction for easier math later.
        if(this.Color.equalsIgnoreCase("Black")){
        	this.direction = 1;
        } else {
        	this.direction = -1;
        }

        //Check the distance to the location
        int fileDist = destination.getFile() - this.Location.getFile();
        int rankDist = destination.getRank() - this.Location.getRank();
        
        //SPECIAL EN PASSANT LOGIC
        if (Math.abs(fileDist) == 1 && rankDist == this.direction) {
            if (this.checkEnPassant(destination)) {
            	//Tells the game state that the user wants to preform en passant
            	System.out.println("I know the problem is here idk how to fix it maybe ill touch myself bro");
            	GameStateManager.letMePassant();
                return true;
            }
        }
        
        //TYPICAL MOVE LOGIC
        
        //Moving straight forwards
        if (fileDist == 0) {
            // Two squares forward on the first move (only if havent moved yet
            
            	//If its trying to move more than 2 squares,
            	if(rankDist == 2 * direction) { 
            		//Make sure its the first move it tries to make.
            		if (!hasMoved) {
	            		// Check the square in the middle to see if it's occupied (since we know rankdist is 2*direction, we can add 1 of the inverse to get the square inbetween (ex add -1 * 1 to 2 and -1 * -1 (1) to -2)
	            		// And file stays the same
	            		if(ChessBoard.getSquare((destination.getRank() + ((-1)*direction)), (destination.getFile())).getOccupancy()) {
	            			throw new IllegalStateException("DEBUG: Pawn cannot jump over the pieces!");
	            		} else {
	            			//If the pawn moves 2 squares, it is vulnerable to an en passant capture for one move.
	            			
	            			return true;
	            		}
            		} else {
            			throw new IllegalStateException("DEBUG: Pawn cannot move in an illegal direction / an illegal amount of squares!");
            		}
            	} else {
            		 // One square forward
                    // Only if the pawn is moving in the correct direction (and since it is scaled to 1, we also know its the correct amount of squares
                    if (rankDist == direction) {
                    	if(!destination.getOccupancy()) {
                    		return true;
                    	} else {
                    		throw new IllegalStateException("DEBUG: Da pawns cannot headbutt da other paws twin");
                    	}
                        
                    } else { 
                    	throw new IllegalStateException("DEBUG: Pawn cannot move in an illegal direction / an illegal amount of squares!");
                    }
            	}

        } else {
        	//If the pawn isn't trying to move in a straight line, then make sure it's attempting to capture
            if (Math.abs(fileDist) == 1 && rankDist == direction) {
                if (destination.getOccupancy()) {
                    //Since it already passed the check to see if the destination is occupied by a friendly piece, we can assume that it will always check for an enemy piece
                	//The pawn is allowed to move diagonally in this case.
                	return true;
                } else {
                	throw new IllegalStateException("DEBUG: Pawn cannot move diagonally to an unoccupied square!");
                }
            } else {
            	throw new IllegalStateException("DEBUG: Pawn cannot move in an illegal direction / an illegal amount of squares!");
            }
        }
        
    }
    
   
    public boolean checkEnPassant(Square destination) {
    	Square victim = ChessBoard.getSquare(this.Location.getRank(), destination.getFile());
    	if(victim.getOccupancy()) {
    		if(victim.getOccupant() instanceof Pawn) {
        		//If that square has a pawn, is it vulnerable to en passant?
            	if(GameStateManager.thereIsPassant) {
            		if(GameStateManager.getWherePassant().equals(victim)) {
                		return true;
            		}
            	}
            }
    	}
	    
	    //No passant
	    return false;
    }
    
    

   

   
    public void promote(int pieceType) {
        Piece newPiece;
        if(pieceType == 1) {
        	newPiece = new Queen(this.Color, this.Location); 
        	 this.Location.occupySquare(newPiece);
        	
        }
        
        if(pieceType == 2) {
        	newPiece = new Rook(this.Color, this.Location); 
        	 this.Location.occupySquare(newPiece);
        	
        }
        
        if(pieceType == 3) {
        	newPiece = new Bishop(this.Color, this.Location); 
        	 this.Location.occupySquare(newPiece);
        	
        }
        
        if(pieceType == 4) {
        	newPiece = new Knight(this.Color, this.Location); 
        	 this.Location.occupySquare(newPiece);
        	
        }
            //Technically it cannot kill itself after this so we just have to kind of let it exist in the void without a purpose
            this.Location = null;
        
    }


	@Override
	public List<Move> getValidMoves(ChessBoard board, Square currentSquare) {
		// TODO Auto-generated method stub
		return null;
	}
}