package amazon_ai.hybrid_mcts;

import amazon_framework.AbstractAIAgent;
import amazon_framework.MatrixArray;
import amazon_framework.OrderedPair;
import amazon_framework.OrderedPairList;

import java.util.Random;

import static amazon_framework.Operations.*;

public final class HybridSimAgent extends AbstractAIAgent {
    StateNode currentNode;
    StateNode nextNode;
    int num;

    HybridSimAgent(StateNode inputState, byte side) {
        super(inputState.mat, side);
        currentNode = inputState;
        currentNode.mat = inputState.mat;
    }

    public void calculate(boolean is_opponent) {
        if (currentNode.childStates == null) {
            expand();
        } else {
            nodeSelection(is_opponent);
        }
    }

    public void nodeSelection(boolean opponent) {
        if (!opponent) {
            double sum = 0;
            for (StateNode s : currentNode.childStates) {
                sum += s.selfValue;
            }
            double cvalue = sum * Math.random();
            StateNode selectedState = null;
            sum = 0;
            for (StateNode s : currentNode.childStates) {
                sum += s.selfValue;
                if (sum >= cvalue) {
                    selectedState = s;
                    break;
                }
            }
            nextNode = selectedState;
        }

        if (opponent) {
            double sum = 0;
            for (StateNode s : currentNode.childStates) {
                sum += s.enemyValue;
            }
            double cvalue = sum * Math.random();
            StateNode selectedState = null;
            sum = 0;
            for (StateNode s : currentNode.childStates) {
                sum += s.enemyValue;
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
        OrderedPairList valueList = new OrderedPairList();
        final int size_i = nextNodes.size();
        for (int i = 0; i < size_i; i++) {
            OrderedPair value = new OrderedPair();
            value.x = i;
            value.y += numPossibleMoves(nextNodes.at(i), side);
            value.y -= numPossibleMoves(nextNodes.at(i), enemy_side);
            valueList.add(value);
        }
        valueList.sort_by_y();
        OrderedPairList betterList = valueList.back(32);

        if (betterList.isEmpty()) {
            nextNode = currentNode;
            return;
        }

        MatrixArray marr = new MatrixArray();

        for (int i = 0; i < betterList.size(); i++) {
            int index = betterList.at(i).x;
            marr.add(matrixCopy(nextNodes.at(index)));
        }

        StateNodesArray sarr = new StateNodesArray(marr);

        currentNode.childStates = sarr;

        for (StateNode s : currentNode.childStates) {
            s.parentState = currentNode;
        }

        Random rand = new Random();
        final int x = rand.nextInt(sarr.size());
        nextNode = sarr.at(x);
    }

    public void update(StateNode s) {
        currentNode = s;
    }

    @Override
    public OrderedPairList getAction() {
        return parseAction(currentNode.mat, nextNode.mat);
    }
}