package amazon_ai;

import static amazon_framework.Operations.*;

import java.util.Scanner;

import amazon_framework.*;

public class Human extends IntelligentAgent {
    CoordinateArray actionList;

    public Human(byte[][] board, byte side) {
        super(board, side);
        actionList = new CoordinateArray();
    }

    public void calculate() {
        Scanner inp = new Scanner(System.in);
        byte[][] cm = matrixCopy(gameBoard);
        Coordinate src = new Coordinate(0, 0);
        Coordinate dst = new Coordinate(0, 0);
        Coordinate blk = new Coordinate(0, 0);
        while (true) {
            printf("\ninput x,y to move:");
            src.x = inp.nextInt();
            src.y = inp.nextInt();
            CoordinateArray vp = getAvailablePieces(cm, side);
            if (vp.contains(src))
                break;
            printf("\ninput x,y to move:");
        }
        actionList.add(src);

        byte[][] im = matrixCopy(cm);
        markVaildMoves(im, src);
        displayBoard(im);

        while (true) {
            printf("\ninput dst:");
            dst.x = inp.nextInt();
            dst.y = inp.nextInt();
            CoordinateArray vm = getValidMoves(cm, src);
            if (vm.contains(dst))
                break;
            printf("err\n");
        }
        actionList.add(dst);
        forceAction(cm, src, dst, MOVE);

        im = matrixCopy(cm);
        markVaildMoves(im, dst);
        displayBoard(im);

        while (true) {
            printf("\ninput block:");
            blk.x = inp.nextInt();
            blk.y = inp.nextInt();
            CoordinateArray vms = getValidMoves(cm, dst);
            if (vms.contains(blk))
                break;
            printf("err\n");
        }
        forceAction(cm, dst, blk, BLOCK);
        newState = matrixCopy(cm);
        actionList.add(blk);
        // inp.close();
    }
}