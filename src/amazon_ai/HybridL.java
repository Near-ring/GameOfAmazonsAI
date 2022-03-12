package amazon_ai;

import static amazon_framework.Operations.*;

import java.util.Random;

import amazon_framework.*;

public class HybridL extends IntelligentAgent {
    State currentNode;
    State nextNode;

    public HybridL(State inputState, byte side) {
        super(inputState.mat, side);
        currentNode = inputState;
        currentNode.mat = inputState.mat;
        nextNode = new State();
    }

    public void calculate(boolean is_opponent) {
        if (currentNode.childStates == null)
            expand();
        else
            nodeSelection(is_opponent);
    }

    public void nodeSelection(boolean is_opponent) {
        if (is_opponent == false){
            double sum = 0;
            for (State s : currentNode.childStates) {
                sum += s.value_p1;
            }
            double cvalue = sum * Math.random();
            State selectedState = null;
            sum = 0;
            for (State s : currentNode.childStates) {
                sum += s.value_p1;
                if (sum >= cvalue) {
                    selectedState = s;
                    break;
                }
            }
            nextNode = selectedState;
        }

        if (is_opponent == true) {
            double sum = 0;
            for (State s : currentNode.childStates) {
                sum += s.value_p2;
            }
            double cvalue = sum * Math.random();
            State selectedState = null;
            sum = 0;
            for (State s : currentNode.childStates) {
                sum += s.value_p2;
                if (sum >= cvalue) {
                    selectedState = s;
                    break;
                }
            }
            nextNode = selectedState;
        }
    }

    public void expand() {
        MatrixArray nextNodes = getPossibleStates(currentNode.mat, side);
        CoordinateArray valueList = new CoordinateArray();
        final int size_i = nextNodes.size();
        for (int i = 0; i < size_i; i++) {
            Coordinate value = new Coordinate();
            value.x = i;
            value.y += numPossibleMoves(nextNodes.at(i), side);
            value.y -= numPossibleMoves(nextNodes.at(i), enemy_side);
            valueList.add(value);
        }
        valueList.sort_by_y();
        CoordinateArray betterList = valueList.back(11);

        if (betterList.isEmpty()) {
            nextNode = currentNode;
            return;
        }

        MatrixArray marr = new MatrixArray();

        for (int i = 0; i < betterList.size(); i++) {
            int index = betterList.at(i).x;
            marr.add(matrixCopy(nextNodes.at(index)));
        }

        StateArray sarr = toStateArray(marr);

        currentNode.childStates = sarr;

        for (State s : currentNode.childStates) {
            s.parentState = currentNode;
        }

        Random rand = new Random();
        final int x = rand.nextInt(sarr.size());
        nextNode = sarr.at(x);
    }

    public void update(State s) {
        currentNode = s;
    }

    @Override
    public CoordinateArray getAction(){
        return parseAction(currentNode.mat, nextNode.mat);
    }
}