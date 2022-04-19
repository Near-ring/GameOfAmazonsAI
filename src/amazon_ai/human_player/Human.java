package amazon_ai.human_player;

import amazon_framework.AbstractAIAgent;
import amazon_framework.OrderedPair;
import amazon_framework.OrderedPairList;

import java.util.Scanner;

import static amazon_framework.Operations.*;

public class Human extends AbstractAIAgent {
    OrderedPairList actionList;
    Scanner input = new Scanner(System.in);

    public Human(byte[][] board, byte side) {
        super(board, side);
        actionList = new OrderedPairList();
    }

    @Override
    public void destructor() {
        input.close();
    }

    @Override
    public void calculate() {
        byte[][] cm = matrixCopy(gameBoard);
        OrderedPair src = new OrderedPair();
        OrderedPair dst = new OrderedPair();
        OrderedPair blk = new OrderedPair();
        while (true) {
            printf("\ninput x,y to move:");
            src.x = input.nextInt();
            src.y = input.nextInt();
            OrderedPairList vp = getAvailablePieces(cm, side);
            if (vp.contains(src)) {
                break;
            }
            printf("\ninput x,y to move:");
        }
        actionList.add(src);

        byte[][] im = matrixCopy(cm);
        markValidMoves(im, src);
        displayBoard(im);

        while (true) {
            printf("\ninput dst:");
            dst.x = input.nextInt();
            dst.y = input.nextInt();
            OrderedPairList vm = getValidMoves(cm, src);
            if (vm.contains(dst)) {
                break;
            }
            printf("err\n");
        }
        actionList.add(dst);
        forceAction(cm, src, dst, MOVE);

        im = matrixCopy(cm);
        markValidMoves(im, dst);
        displayBoard(im);

        while (true) {
            printf("\ninput block:");
            blk.x = input.nextInt();
            blk.y = input.nextInt();
            OrderedPairList vms = getValidMoves(cm, dst);
            if (vms.contains(blk)) {
                break;
            }
            printf("err\n");
        }
        forceAction(cm, dst, blk, BLOCK);
        newState = matrixCopy(cm);
        actionList.add(blk);
    }
}