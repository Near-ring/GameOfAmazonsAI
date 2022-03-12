package amazon_ai;

import static amazon_framework.Operations.*;
import amazon_framework.*;

public class HybridMCTS extends IntelligentAgent {
    int hybrid_hyperparam = 13;
    double max_sim_num;
    private final Thread[] thread_pool = new Thread[32];

    public HybridMCTS(byte[][] board, byte side) {
        super(board, side);
    }

    @Override
    public void calculate() {
        max_sim_num = 35 + 5 * hybrid_hyperparam;
        CoordinateArray valueArray = new CoordinateArray();
        int varr_size = hybrid_hyperparam - 2;
        int maxSimValue = -inf;
        byte[][] bestNode = null;

        // <Expansion>
        // Gets the possible states and calculates available moves for each child state
        MatrixArray childStates = getPossibleStates(gameBoard, side);
        final int size_i = childStates.size();
        for (int i = 0; i < size_i; i++) {
            Coordinate value = new Coordinate();
            value.y += numPossibleMoves(childStates.at(i), side);
            value.y -= numPossibleMoves(childStates.at(i), enemy_side);
            value.x = i;
            valueArray.add(value);
        }
        valueArray.sort_by_y(); // Get largest possible move state list, desending order
        varr_size = min(varr_size, 32);
        // </Expansion>

        // <Selection>
        CoordinateArray selectedNodeCor = valueArray.back(varr_size); // n most valuable moves
        if (selectedNodeCor.isEmpty()) {
            newState = gameBoard;
            return;
        }

        int[] simValue = new int[selectedNodeCor.size()];
        byte[][][] selectedNodeArray = new byte[selectedNodeCor.size()][][];
        for (int i = 0; i < selectedNodeCor.size(); i++) {
            selectedNodeArray[i] = childStates.at(selectedNodeCor.at(i).x);
        }
        // </Selection>

        // <Simulation>
        printf("Max Sim Value = %f\n", max_sim_num);
        printf("HybridMarkov is simulating possible states...\nValue: ");
        for (int i = 0; i < selectedNodeArray.length; i++) {
            final int j = i;
            thread_pool[j] = new Thread(new Runnable() {
                @Override
                public void run() {
                    simValue[j] = randomSimulation(toState(selectedNodeArray[j]));
                }
            });
        }
        for (int i = 0; i < selectedNodeArray.length; i++) {
            thread_pool[i].start();
        }
        for (int i = 0; i < selectedNodeArray.length; i++) {
            try {
                thread_pool[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // </Simulation>

        for (int i = 0; i < selectedNodeArray.length; i++) {
            if (simValue[i] > maxSimValue) {
                maxSimValue = simValue[i];
                bestNode = matrixCopy(selectedNodeArray[i]);
            }
            if (maxSimValue >= max_sim_num)
                break;
        }
        newState = bestNode;
        hybrid_hyperparam++;
    }

    public int randomSimulation(State s) {
        int value = 0;
        State currentState = s;
        HybridL shadow = new HybridL(s, side);
        HybridS eshadow = new HybridS(s, enemy_side);
        for (int j = 0; j < max_sim_num; j++) {
            currentState = s;
            while (true) {
                currentState.is_opponent = true;
                eshadow.update(currentState);
                eshadow.calculate(true); // Selection and Expansion
                currentState = eshadow.nextNode;
                if (eshadow.getAction().size() <= 0) {
                    value++;
                    eshadow.currentNode.backPropagation(false, 0); //Backpropagation
                    break;
                }
                currentState.is_opponent = false;
                shadow.update(currentState);
                shadow.calculate(false);
                currentState = shadow.nextNode;
                if (shadow.getAction().size() <= 0) {
                    value--;
                    shadow.currentNode.backPropagation(false, 0);
                    break;
                }
            }
        }
        printf("%d ", value);
        return value;
    }
}