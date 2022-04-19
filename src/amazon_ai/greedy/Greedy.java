package amazon_ai.greedy;

import amazon_framework.AbstractAIAgent;
import amazon_framework.MatrixArray;
import amazon_framework.OrderedPair;
import amazon_framework.OrderedPairList;

import java.util.Random;

import static amazon_framework.Operations.*;

public class Greedy extends AbstractAIAgent {

    public Greedy(byte[][] board, byte side) {
        super(board, side);
    }

    @Override
    public void calculate() {
        Random rint = new Random();
        MatrixArray nextStates = getPossibleStates(gameBoard, side);
        OrderedPairList valueList = new OrderedPairList();
        int size = nextStates.size();
        for (int i = 0; i < size; i++) {
            OrderedPair value = new OrderedPair(0, 0);
            value.y += numPossibleMoves(nextStates.at(i), side);
            value.y -= numPossibleMoves(nextStates.at(i), enemy_side);
            value.x = i;
            valueList.add(value);
        }
        valueList.sort_by_y();
        OrderedPairList betterList = valueList.back(1);
        if (betterList.isEmpty()) {
            newState = matrixCopy(gameBoard);
            return;
        }
        int x = betterList.at(rint.nextInt(betterList.size())).x;
        newState = matrixCopy(nextStates.at(x));
    }
}