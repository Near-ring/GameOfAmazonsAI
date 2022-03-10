package amazon_ai;

import static amazon_framework.Operations.*;

import java.util.Random;

import amazon_framework.*;

public class Greedy extends IntelligentAgent {

    public Greedy(byte[][] board, byte side) {
        super(board, side);
    }

    @Override
    public void calculate() {
        Random rint = new Random();
        MatrixArray nextStates = getPossibleStates(gameBoard, side);
        CoordinateArray valueList = new CoordinateArray();
        int size = nextStates.size();
        for (int i = 0; i < size; i++) {
            Coordinate value = new Coordinate(0, 0);
            value.y += numPossibleMoves(nextStates.at(i), side);
            value.y -= numPossibleMoves(nextStates.at(i), enemy_side);
            value.x = i;
            valueList.add(value);
        }
        valueList.sort_by_y();
        CoordinateArray betterList = valueList.back(1);
        if (betterList.isEmpty()) {
            newState = matrixCopy(gameBoard);
            return;
        }
        int x = betterList.at(rint.nextInt(betterList.size())).x;
        newState = matrixCopy(nextStates.at(x));
    }
}