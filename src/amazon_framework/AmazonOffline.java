package amazon_framework;

import amazon_ai.amazons_minmax.TranslatedMinMax;
import amazon_ai.greedy.Greedy;
import amazon_ai.remote_cpp_agent.RemoteCPPAgent;

import static amazon_framework.Operations.*;

public class AmazonOffline {

    private final byte[][] gameBoard = new byte[10][10];

    public AmazonOffline() {
        gameInit();
    }

    public static void main(String[] args) {
        // Game Main Loop:
        AmazonOffline localGame = new AmazonOffline();
        localGame.startLocalGame();
    }

    private void gameInit() {
        gameBoard[3][0] = BLACK;
        gameBoard[0][3] = BLACK;
        gameBoard[6][0] = BLACK;
        gameBoard[9][3] = BLACK;
        gameBoard[0][6] = WHITE;
        gameBoard[3][9] = WHITE;
        gameBoard[9][6] = WHITE;
        gameBoard[6][9] = WHITE;
    }

    public void printBoard() {
        System.out.println("\n  0 1 2 3 4 5 6 7 8 9");
        for (int y = 0; y < BOARD_SIZE; y++) {
            System.out.printf("%d ", y);
            for (int x = 0; x < BOARD_SIZE; x++) {
                if (gameBoard[x][y] == EMPTY) {
                    System.out.print("  ");
                } else if (gameBoard[x][y] == BLACK) {
                    System.out.print("B ");
                } else if (gameBoard[x][y] == WHITE) {
                    System.out.print("W ");
                } else if (gameBoard[x][y] == BLOCK) {
                    System.out.print("X ");
                } else {
                    System.out.print("o ");
                }
            }
            System.out.println("");
        }
    }
    public void printBoard(byte[][] tempBoard) {
        System.out.println("\n  0 1 2 3 4 5 6 7 8 9");
        for (int y = 0; y < BOARD_SIZE; y++) {
            System.out.printf("%d ", y);
            for (int x = 0; x < BOARD_SIZE; x++) {
                if (tempBoard[x][y] == EMPTY) {
                    System.out.print("  ");
                } else if (tempBoard[x][y] == BLACK) {
                    System.out.print("B ");
                } else if (tempBoard[x][y] == WHITE) {
                    System.out.print("W ");
                } else if (tempBoard[x][y] == BLOCK) {
                    System.out.print("X ");
                } else {
                    System.out.print("o ");
                }
            }
            System.out.println("");
        }
    }

    public byte[][] getGameBoardClone() {
        return matrixCopy(gameBoard);
    }

    public void movePiece(OrderedPair src, OrderedPair dst, byte value) {
        byte[][] temp = gameBoard;
        OrderedPairList vms = getValidMoves(gameBoard, src);
        if (vms.contains(dst)) {
            if (value == MOVE) {
                gameBoard[dst.x][dst.y] = gameBoard[src.x][src.y];
                gameBoard[src.x][src.y] = EMPTY;
            }
            if (value == BLOCK) {
                gameBoard[dst.x][dst.y] = BLOCK;
            }
        } else {
            System.err.println("\nErr: =================Invalid move=================\nPlayer attempted to move from state A: \n");
            printBoard(temp);
            System.err.println("to state B: \n");
            printBoard();
            throw new UnsupportedOperationException();
        }
    }

    public void startLocalGame() {
        RemoteCPPAgent player1 = new RemoteCPPAgent(getGameBoardClone(), WHITE);
        Greedy player2 = new Greedy(getGameBoardClone(), BLACK);

        OrderedPairList action;
        long startTime, turnNum = 0;

        while (true) {
            // Player1
            printf("\n  Turn: %d Player 1:\n\n", turnNum);
            player1.update(getGameBoardClone());
            startTime = System.currentTimeMillis();
            player1.calculate();
            System.out.println("\nTime taken: " + (System.currentTimeMillis() - startTime));
            action = player1.getAction();
            if (action.size() <= 0) {
                printf("\nGAME END - Player2 wins!!!\n\n");
                break;
            }
            // Action 0 = src, Action 1 = dest
            movePiece(action.at(0), action.at(1), MOVE);
            movePiece(action.at(1), action.at(2), BLOCK);
            printBoard();

            // Player2
            printf("\n  Turn: %d Player 2:\n\n", turnNum++);
            player2.update(getGameBoardClone());
            startTime = System.currentTimeMillis();
            player2.calculate();
            System.out.println("\nTime taken: " + (System.currentTimeMillis() - startTime));
            action = player2.getAction();
            if (action.size() <= 0) {
                printf("\nGAME END - Player1 wins!!!\n\n");
                break;
            }
            movePiece(action.at(0), action.at(1), MOVE);
            movePiece(action.at(1), action.at(2), BLOCK);
            printBoard();
        }
        player1.destructor();
        player2.destructor();
    }
}