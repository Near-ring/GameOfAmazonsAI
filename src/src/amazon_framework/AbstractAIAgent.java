package amazon_framework;

import static amazon_framework.Operations.*;

public abstract class AbstractAIAgent {
    /**
     * WHITE or BLACK
     */
    public final byte side;
    public final byte enemy_side;
    /**
     * current game board
     */
    protected byte[][] gameBoard;
    /**
     * new state(board) after AI action calculate()
     */
    protected byte[][] newState;

    public AbstractAIAgent(byte[][] board, byte side) {
        gameBoard = matrixCopy(board);
        this.side = side;
        enemy_side = side == WHITE ? BLACK : WHITE;
    }

    public byte[][] getNewState() {
        return newState;
    }

    /**
     * parse action from new state
     * API for caller (ex. main())
     *
     * @return array of action coordinate in one game turn
     */
    public OrderedPairList getAction() {
        return parseAction(gameBoard, newState);
    }

    /**
     * updates the gameBoard to get new game state
     * API for caller (ex. main())
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

    /**
     * overwrite this if clean up is needed after game is finished
     */
    public void destructor() {
    }
}
