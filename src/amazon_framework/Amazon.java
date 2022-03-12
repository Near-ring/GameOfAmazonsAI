package amazon_framework;

import static amazon_framework.Operations.*;


import amazon_ai.*;

public class Amazon {

    private byte[][] gameBoard = new byte[10][10];

    public void gameInit() {
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
        for (int y = 0; y < 10; y++) {
            System.out.printf("%d ", y);
            for (int x = 0; x < 10; x++) {
                if (gameBoard[x][y] == EMPTY)
                    System.out.print("  ");
                else if (gameBoard[x][y] == BLACK)
                    System.out.print("B ");
                else if (gameBoard[x][y] == WHITE)
                    System.out.print("W ");
                else if (gameBoard[x][y] == BLOCK)
                    System.out.print("X ");
                else
                    System.out.print("o ");
            }
            System.out.println("");
        }
    }

    public byte[][] getGameBoardClone() {
        return matrixCopy(gameBoard);
    }

    public void movePiece(Coordinate src, Coordinate dst, byte value) {
        CoordinateArray vms = getValidMoves(gameBoard, src);
        if (vms.contains(dst)) {
            if (value == MOVE) {
                gameBoard[dst.x][dst.y] = gameBoard[src.x][src.y];
                gameBoard[src.x][src.y] = EMPTY;
            }
            if (value == BLOCK) {
                gameBoard[dst.x][dst.y] = BLOCK;
            }
        } else {
            System.err.println("Invaild move");
        }
    }

    public void gameLoop() {
        Greedy player1 = new Greedy(getGameBoardClone(), BLACK);
        HybridMCTS player2 = new HybridMCTS(getGameBoardClone(), WHITE);

        CoordinateArray action;
        long startTime;
        int num = 0;
        while (true) {
            // Player1
            printf("\n  Turn: %d Player 1:\n\n", num);
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
            printf("\n  Turn: %d Player 2:\n\n", num++);
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
            System.gc();
        }
    }

    public static void main(String[] args) {
        // Game Main Loop:
        Amazon localGame = new Amazon();
        localGame.gameInit();
        localGame.gameLoop();

        // Test:
        // byte[][] board = localGame.getGameBoardClone();
        // long startTime, endTime;
        // startTime = System.currentTimeMillis();
        // endTime = System.currentTimeMillis();
        // printf("\n1. Total execution time: %d\n", (endTime - startTime));
        // startTime = System.currentTimeMillis();
        // endTime = System.currentTimeMillis();
        // printf("\n2. Total execution time: %d\n", (endTime - startTime));
        // System.out.println(ClassLayout.parseInstance(board).toPrintable());
    }
}