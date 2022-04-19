package amazon_framework;

/**
 * public static only
 */
public final class Operations {
    public static final byte EMPTY = 0;
    public static final byte MOVE = 0;
    public static final byte BLACK = 1;
    public static final byte WHITE = 2;
    public static final byte BLOCK = 3;
    public static final int INF = 1_000_000_000;
    public static final int BOARD_SIZE = 10;
    public static final int MAX_LEN = BOARD_SIZE - 1;

    private Operations() {
        System.err.println("\nIllegal Operation: Instantiation of utility class is disallowed.\n");
        throw new UnsupportedOperationException();
    }

    public static void helloWord() {
        Operations a = new Operations();
        System.out.println("\nHello Word!");
    }

    /**
     * See printf(); https://www.cplusplus.com/reference/cstdio/printf/
     * Example:
     * printf("var1 = %d, var2 = %f\n", int1, float2);
     */
    public static void printf(String str, Object... vars) {
        System.out.printf(str, vars);
    }

    /**
     * @param board  gameBoard
     * @param player player to search on board
     * @return An ArrayList of coordinate for a given player e.x [(x1,y1),(x2,y2)]
     */
    public static OrderedPairList getAvailablePieces(byte[][] board, byte player) {
        final OrderedPairList availablePieces = new OrderedPairList();
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (board[x][y] == player) {
                    availablePieces.add(new OrderedPair(x, y));
                }
            }
        }
        return availablePieces;
    }

    /**
     * Gets all valid moves for a piece on a board
     *
     * @param board byte[][] board
     * @param piece Coordinate[] piece
     * @return CoordinateArray of valid moves
     */
    public static OrderedPairList getValidMoves(byte[][] board, OrderedPair piece) {
        final OrderedPairList validMoves = new OrderedPairList();
        final int x = piece.x;
        final int y = piece.y;
        int i, j;
        for (i = x - 1; i > -1 && board[i][y] == EMPTY; --i) {
            validMoves.add(new OrderedPair(i, y));
        }

        for (i = x + 1; i < BOARD_SIZE && board[i][y] == EMPTY; ++i) {
            validMoves.add(new OrderedPair(i, y));
        }

        for (i = y + 1; i < BOARD_SIZE && board[x][i] == EMPTY; ++i) {
            validMoves.add(new OrderedPair(x, i));
        }

        for (i = y - 1; i > -1 && board[x][i] == EMPTY; --i) {
            validMoves.add(new OrderedPair(x, i));
        }

        j = y;
        for (i = x + 1; i < BOARD_SIZE; ++i) {
            --j;
            if (j < 0 || board[i][j] != EMPTY) {
                break;
            }
            validMoves.add(new OrderedPair(i, j));
        }
        j = y;
        for (i = x - 1; i > -1; --i) {
            ++j;
            if (j > MAX_LEN || board[i][j] != EMPTY) {
                break;
            }
            validMoves.add(new OrderedPair(i, j));
        }
        j = y;
        for (i = x + 1; i < BOARD_SIZE; ++i) {
            ++j;
            if (j > MAX_LEN || board[i][j] != EMPTY) {
                break;
            }
            validMoves.add(new OrderedPair(i, j));
        }
        j = y;
        for (i = x - 1; i > -1; --i) {
            --j;
            if (j < 0 || board[i][j] != EMPTY) {
                break;
            }
            validMoves.add(new OrderedPair(i, j));
        }
        return validMoves;
    }

    public static int pieceMoveNum(byte[][] board, OrderedPair piece) {
        int num = 0;
        final int x = piece.x;
        final int y = piece.y;
        int i, j;
        for (i = x - 1; i > -1 && board[i][y] == EMPTY; --i) {
            num++;
        }

        for (i = x + 1; i < BOARD_SIZE && board[i][y] == EMPTY; ++i) {
            num++;
        }

        for (i = y + 1; i < BOARD_SIZE && board[x][i] == EMPTY; ++i) {
            num++;
        }

        for (i = y - 1; i > -1 && board[x][i] == EMPTY; --i) {
            num++;
        }

        j = y;
        for (i = x + 1; i < BOARD_SIZE; ++i) {
            --j;
            if (j < 0 || board[i][j] != EMPTY) {
                break;
            }
            num++;
        }
        j = y;
        for (i = x - 1; i > -1; --i) {
            ++j;
            if (j > MAX_LEN || board[i][j] != EMPTY) {
                break;
            }
            num++;
        }
        j = y;
        for (i = x + 1; i < BOARD_SIZE; ++i) {
            ++j;
            if (j > MAX_LEN || board[i][j] != EMPTY) {
                break;
            }
            num++;
        }
        j = y;
        for (i = x - 1; i > -1; --i) {
            --j;
            if (j < 0 || board[i][j] != EMPTY) {
                break;
            }
            num++;
        }
        return num;
    }

    /**
     * force a move on given game board copy
     *
     * @param board any game board copy
     * @param src   source Coordinate
     * @param dst   destination Coordinate
     * @param value MOVE: move piece from src to dst, BLOCK: place a block in dst
     */
    public static void forceAction(byte[][] board, OrderedPair src, OrderedPair dst, byte value) {
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
        for (int y = 0; y < BOARD_SIZE; y++) {
            System.out.printf("%d ", y);
            for (int x = 0; x < BOARD_SIZE; x++) {
                if (board[x][y] == EMPTY) {
                    System.out.print("  ");
                } else if (board[x][y] == BLACK) {
                    System.out.print("B ");
                } else if (board[x][y] == WHITE) {
                    System.out.print("W ");
                } else if (board[x][y] == BLOCK) {
                    System.out.print("X ");
                } else {
                    System.out.print("o ");
                }
            }
            System.out.println("");
        }
    }

    /**
     * ONLY use for TEST COPY of board
     * mark all valid moves for Coordinate p,
     * call displayBoard() after to display
     */
    public static void markValidMoves(byte[][] board, OrderedPair p) {
        final OrderedPairList vms = getValidMoves(board, p);
        for (OrderedPair c : vms) {
            board[c.x][c.y] = -1;
        }
    }

    /**
     * Gets an array of all possible states in one turn for a player
     * of a given state(board)
     *
     * @param board state to expand
     * @return array of child state of given state
     */
    public static MatrixArray getPossibleStates(byte[][] board, byte playerSide) {
        final MatrixArray possible_states = new MatrixArray();
        final OrderedPairList available_pieces = getAvailablePieces(board, playerSide);
        final int size_i = available_pieces.size();

        OrderedPair piece = null, dst = null;
        OrderedPairList validMoves = null, validBlock = null;

        for (int i = 0; i < size_i; i++) {
            piece = available_pieces.get(i);
            validMoves = getValidMoves(board, piece);
            final int size_j = validMoves.size();

            for (int j = 0; j < size_j; j++) {
                dst = validMoves.get(j);
                byte[][] boardJ = matrixCopy(board);
                forceAction(boardJ, piece, dst, MOVE);
                validBlock = getValidMoves(boardJ, dst);
                final int size_k = validBlock.size();

                for (int k = 0; k < size_k; k++) {
                    byte[][] boardK = matrixCopy(boardJ);
                    forceAction(boardK, null, validBlock.get(k), BLOCK);
                    possible_states.add(boardK);
                }
            }
        }
        return possible_states;
    }

    /**
     * @param board
     * @param side
     * @return number of possible moves (for pieces) for a player in next turn
     */
    public static int numPossibleMoves(byte[][] board, byte side) {
        int num = 0;
        final OrderedPairList ap = getAvailablePieces(board, side);
        for (OrderedPair pieceLocation : ap) {
            num += pieceMoveNum(board,pieceLocation);
        }
        return num;
    }

    public static byte[][] matrixCopy(byte[][] old) {
        final byte[][] cb = new byte[old.length][old.length];
        for (int i = 0; i < old.length; i++) {
            System.arraycopy(old[i], 0, cb[i], 0, old.length);
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
    public static OrderedPairList parseAction(byte[][] oldb, byte[][] newb) {
        final OrderedPairList action_array = new OrderedPairList();
        OrderedPair move = null;
        OrderedPair destination = null;
        OrderedPair block = null;

        for (int x = 0; x < oldb.length; x++) {
            for (int y = 0; y < oldb.length; y++) {
                if (oldb[x][y] != newb[x][y]) {
                    if (newb[x][y] == BLOCK) {
                        block = new OrderedPair(x, y);
                        continue;
                    }
                    if (newb[x][y] != EMPTY) {
                        destination = new OrderedPair(x, y);
                        continue;
                    }
                    move = new OrderedPair(x, y);
                }
            }
        }
        if (block != null) {
            if (move == null) {
                move = block;
            }
            action_array.add(move);
            action_array.add(destination);
            action_array.add(block);
        }
        return action_array;
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
        OrderedPairList availablePieces;
        final byte opponent = (player == WHITE) ? BLACK : WHITE;

        // Check opponent
        availablePieces = getAvailablePieces(board, opponent);
        for (OrderedPair pieceLocation : availablePieces) {
            opponentHasAvailableMoves |= (getValidMoves(board, pieceLocation).size() > 0);
        }
        if (!opponentHasAvailableMoves) {
            return 1;
        }

        // Check self
        availablePieces = getAvailablePieces(board, player);
        for (OrderedPair pieceLocation : availablePieces) {
            currentHasAvailableMoves |= (getValidMoves(board, pieceLocation).size() > 0);
        }
        if (!currentHasAvailableMoves) {
            return -1;
        }
        return 0;
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    public static int min(int a, int b) {
        return a < b ? a : b;
    }

    public static double max(double a, double b) {
        return a > b ? a : b;
    }

    public static double min(double a, double b) {
        return a < b ? a : b;
    }
}
