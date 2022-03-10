package amazon_ai;

import static amazon_framework.Operations.*;
import amazon_framework.*;

public abstract class IntelligentAgent {
    protected final byte side; //WHITE or BLACK
    protected final byte enemy_side;
    protected byte[][] gameBoard; //current game board
    protected byte[][] newState; //new state(board) after AI action calculate()
    
    public IntelligentAgent(byte[][] board, byte side) {
        gameBoard = matrixCopy(board);
        this.side = side;
        enemy_side = side == WHITE ? BLACK : WHITE;
    }

    /**
     * parse action from new state
     * API for caller (ex. main())
     * @return array of action coordinate in one game turn
     */
    public CoordinateArray getAction(){
        return parseAction(gameBoard, newState);
    }
    /**
     * updates the gameBoard to get new game state
     * API for caller (ex. main())
     * @param board
     */
    public void update(byte[][] board) {
        gameBoard = matrixCopy(board);
    }

    /**
     * overwrite this in AI to update your move and write it in newState[][]
     */
    public void calculate() {
        newState = matrixCopy(gameBoard);
    }
}
