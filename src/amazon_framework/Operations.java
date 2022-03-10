package amazon_framework;

/**
 * public static only
 */
public final class Operations {
    private Operations() {
    }

    public static final byte EMPTY = 0;
    public static final byte MOVE = 0;
    public static final byte BLACK = 1;
    public static final byte WHITE = 2;
    public static final byte BLOCK = 3;
    public static final byte BLACKPLAYER = BLACK;
    public static final byte WHITEPLAYER = WHITE;
    public static final int inf = 1_000_000_000;

    public static void helloWord() {
        System.out.println("\nHello Word!");
    }

    /**
     * See printf(); https://www.cplusplus.com/reference/cstdio/printf/
     * Example:
     * printf("var1 = %d, var2 = %f\n", int1, float2);
     * 
     * @param str
     * @param vars
     */
    public static void printf(String str, Object... vars) {
        System.out.printf(str, vars);
    }

    /**
     * @param board  gameBoard
     * @param player player to search on board
     * @return An ArrayList of coordinate for a given player e.x [(x1,y1),(x2,y2)]
     */
    public static CoordinateArray getAvailablePieces(byte[][] board, byte player) {
        CoordinateArray availablePieces = new CoordinateArray();
        for (int x = 0; x < 10; x++)
            for (int y = 0; y < 10; y++)
                if (board[x][y] == player)
                    availablePieces.add(new Coordinate(x, y));
        return availablePieces;
    }

    /**
     * Gets all valid moves for a piece on a board
     * 
     * @param board byte[][] board
     * @param piece Coordinate[] piece
     * @return CoordinateArray of valid moves
     */
    public static CoordinateArray getValidMoves(byte[][] board, Coordinate piece) {
        CoordinateArray validMoves = new CoordinateArray();
        final int x = piece.x;
        final int y = piece.y;
        int i, j;
        for (i = x - 1; i > -1; i--) {// get +x
            if (board[i][y] == EMPTY)
                validMoves.add(new Coordinate(i, y));
            else
                break;
        }
        for (i = x + 1; i < 10; i++) {// get -x
            if (board[i][y] == EMPTY)
                validMoves.add(new Coordinate(i, y));
            else
                break;
        }
        for (i = y + 1; i < 10; i++) {// get +y
            if (board[x][i] == EMPTY)
                validMoves.add(new Coordinate(x, i));
            else
                break;
        }
        for (i = y - 1; i > -1; i--) {// get -y
            if (board[x][i] == EMPTY)
                validMoves.add(new Coordinate(x, i));
            else
                break;
        }
        // get xy 1
        j = y;
        for (i = x + 1; i < 10; i++) {
            j--;
            if (j < 0)
                break;
            if (i == 9 || j == 0) {
                if (board[i][j] == EMPTY)
                    validMoves.add(new Coordinate(i, j));
                break;
            }
            if (board[i][j] == EMPTY)
                validMoves.add(new Coordinate(i, j));
            else
                break;
        }
        // get xy 2
        j = y;
        for (i = x - 1; i > -1; i--) {
            j++;
            if (j > 9)
                break;
            if (i == 0 || j == 9) {
                if (board[i][j] == EMPTY)
                    validMoves.add(new Coordinate(i, j));
                break;
            }
            if (board[i][j] == EMPTY)
                validMoves.add(new Coordinate(i, j));
            else
                break;
        }
        // get xy 3
        j = y;
        for (i = x + 1; i < 10; i++) {
            j++;
            if (j > 9)
                break;
            if (i == 9 || j == 9) {
                if (board[i][j] == EMPTY)
                    validMoves.add(new Coordinate(i, j));
                break;
            }
            if (board[i][j] == EMPTY)
                validMoves.add(new Coordinate(i, j));
            else
                break;
        }
        // get xy 4
        j = y;
        for (i = x - 1; i > -1; i--) {
            j--;
            if (j < 0)
                break;
            if (i == 0 || j == 0) {
                if (board[i][j] == EMPTY)
                    validMoves.add(new Coordinate(i, j));
                break;
            }
            if (board[i][j] == EMPTY)
                validMoves.add(new Coordinate(i, j));
            else
                break;
        }
        return validMoves;
    }

    /**
     * force a move on given game board copy
     * 
     * @param board any game board copy
     * @param src   source Coordinate
     * @param dst   destination Coordinate
     * @param vaule MOVE: move piece from src to dst, BLOCK: place a block in dst
     */
    public static void forceAction(byte[][] board, Coordinate src, Coordinate dst, byte value) {
        if (value == MOVE) {
            board[dst.x][dst.y] = board[src.x][src.y];
            board[src.x][src.y] = EMPTY;
        }
        if (value == BLOCK) {
            board[dst.x][dst.y] = BLOCK;
        }
    }

    public static void displayBoard(byte[][] board) {
        System.out.println("  0 1 2 3 4 5 6 7 8 9");
        for (int y = 0; y < 10; y++) {
            System.out.printf("%d ", y);
            for (int x = 0; x < 10; x++) {
                if (board[x][y] == EMPTY)
                    System.out.print("  ");
                else if (board[x][y] == BLACK)
                    System.out.print("B ");
                else if (board[x][y] == WHITE)
                    System.out.print("W ");
                else if (board[x][y] == BLOCK)
                    System.out.print("X ");
                else
                    System.out.print("o ");
            }
            System.out.println("");
        }
    }

    /**
     * ONLY use for TEST COPY of board
     * mark all vaild moves for Coordinate p,
     * call displayBoard() after to display
     * 
     * @param board
     * @param p
     */
    public static void markVaildMoves(byte[][] board, Coordinate p) {
        CoordinateArray vms = getValidMoves(board, p);
        for (Coordinate c : vms) {
            board[c.x][c.y] = -1;
        }
    }

    /**
     * Gets an array of all possible states in one turn for a player
     * of a given state(board)
     * 
     * @param board      state to expand
     * @param playerSide
     * @return array of child state of given state
     */
    public static MatrixArray getPossibleStates(byte[][] board, byte playerSide) {
        MatrixArray possibleStates = new MatrixArray();
        CoordinateArray availablePieces = getAvailablePieces(board, playerSide);
        final int size_i = availablePieces.size();
        for (int i = 0; i < size_i; i++) {
            Coordinate piece = availablePieces.at(i);
            CoordinateArray validMoves = getValidMoves(board, piece);
            final int size_j = validMoves.size();
            for (int j = 0; j < size_j; j++) {
                Coordinate dst = validMoves.at(j);
                byte[][] board_j = matrixCopy(board);
                forceAction(board_j, piece, dst, MOVE);
                CoordinateArray validBlock = getValidMoves(board_j, dst);
                final int size_k = validBlock.size();
                for (int k = 0; k < size_k; k++) {
                    Coordinate block_dst = validBlock.at(k);
                    byte[][] board_k = matrixCopy(board_j);
                    forceAction(board_k, null, block_dst, BLOCK);
                    possibleStates.add(board_k);
                }
            }
        }
        return possibleStates;
    }

    /**
     * 
     * @param board
     * @param side
     * @return number of possible moves (for pieces) for a player in next turn
     */
    public static int numPossibleMoves(byte[][] board, byte side) {
        int num = 0;
        CoordinateArray ap = getAvailablePieces(board, side);
        for (Coordinate pieceLocation : ap)
            num += getValidMoves(board, pieceLocation).size();
        return num;
    }

    public static byte[][] matrixCopy(byte[][] old) {
        byte[][] cb = new byte[old.length][old.length];
        for (int i = 0; i < old.length; i++) {
            for (int j = 0; j < old[i].length; j++) {
                cb[i][j] = old[i][j];
            }
        }
        return cb;
    }

    /**
     * Parse action from game board before and after Player's action
     * array[0] = coordinate of piece to move
     * array[1] = coordinate of piece destination
     * array[2] = coordinate of the block that piece placed
     * 
     * @param oldb Game board before player action
     * @param newb Game board after player action
     * @return array of action Coordinate
     */
    public static CoordinateArray parseAction(byte[][] oldb, byte[][] newb) {
        CoordinateArray actionArray = new CoordinateArray();
        Coordinate move = null;
        Coordinate destination = null;
        Coordinate block = null;

        for (int x = 0; x < oldb.length; x++) {
            for (int y = 0; y < oldb.length; y++) {
                if (oldb[x][y] != newb[x][y]) {
                    if (newb[x][y] == BLOCK) {
                        block = new Coordinate(x, y);
                    } else if (newb[x][y] == BLACK || newb[x][y] == WHITE) {
                        destination = new Coordinate(x, y);
                    } else if (oldb[x][y] == BLACK || oldb[x][y] == WHITE) {
                        move = new Coordinate(x, y);
                    }
                }
            }
        }
        if (move == null && destination == null && block == null) {
        } else if (move == null) {
            move = block;
            actionArray.add(move);
            actionArray.add(destination);
            actionArray.add(block);
        } else {
            actionArray.add(move);
            actionArray.add(destination);
            actionArray.add(block);
        }
        return actionArray;
    }

    /**
     * Note: Assumes that this function is called by the player that just made a
     * move
     * 
     * @param board  Gameboard
     * @param player Current player
     * @return 1 if current player wins, -1 if lose, 0 if not terminal state
     */
    public static int terminalState(byte[][] board, byte player) {
        boolean currentHasAvailableMoves = false;
        boolean opponentHasAvailableMoves = false;
        CoordinateArray availablePieces;
        byte opponent = (player == WHITE) ? BLACK : WHITE;

        // Check opponent
        availablePieces = getAvailablePieces(board, opponent);
        for (Coordinate pieceLocation : availablePieces) {
            opponentHasAvailableMoves |= !(getValidMoves(board, pieceLocation).size() <= 0);
        }
        if (!opponentHasAvailableMoves)
            return 1;

        // Check self
        availablePieces = getAvailablePieces(board, player);
        for (Coordinate pieceLocation : availablePieces) {
            currentHasAvailableMoves |= !(getValidMoves(board, pieceLocation).size() <= 0);
        }
        if (!currentHasAvailableMoves)
            return -1;
        return 0;
    }

    public static State toState(byte[][] x) {
        State state = new State();
        state.mat = x;
        return state;
    }
    
    public static StateArray toStateArray(MatrixArray m)
    {
        StateArray sa = new StateArray();
        for(byte[][] b : m){
            State s = new State();
            s.mat = b;
            sa.add(s);
        }
        return sa;
    }

    public static int max(int a, int b) {
        if (a > b)
            return a;
        else
            return b;
    }

    public static int min(int a, int b) {
        if (a < b)
            return a;
        else
            return b;
    }

    public static double max(double a, double b) {
        if (a > b)
            return a;
        else
            return b;
    }

    public static double min(double a, double b) {
        if (a < b)
            return a;
        else
            return b;
    }
}
