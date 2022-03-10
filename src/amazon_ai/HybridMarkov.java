package amazon_ai;

import static amazon_framework.Operations.*;
import amazon_framework.*;

public class HybridMarkov extends IntelligentAgent {
    int hybrid_hyperparam = 13;
    double max_sim_num;

    public HybridMarkov(byte[][] board, byte side) {
        super(board, side);
    }

    @Override
    public void calculate() {

        max_sim_num = 5 * hybrid_hyperparam;
        MatrixArray nextStates = getPossibleStates(gameBoard, side);
        CoordinateArray valueList = new CoordinateArray();

        // Gets the possible states and calculates available moves for each child state
        final int size_i = nextStates.size();
        for (int i = 0; i < size_i; i++) {
            Coordinate value = new Coordinate();
            value.y += numPossibleMoves(nextStates.at(i), side);
            value.y -= numPossibleMoves(nextStates.at(i), enemy_side);
            value.x = i;
            valueList.add(value);
        }
        valueList.sort_by_y(); // Get largest possible move state list, desending order
        int vlist_size = hybrid_hyperparam - 2;
        vlist_size = min(vlist_size, 32);
        CoordinateArray betterList = valueList.back(vlist_size); // n most valuable moves
        if (betterList.isEmpty()) {
            newState = gameBoard;
            return;
        }

        // Confidence Calculator
        int maxValue = -inf;
        byte[][] bestm = null;
        printf("Max Sim Value = %f\n", max_sim_num);

        int[] cvalue = new int[betterList.size()];
        byte[][][] selectedState = new byte[betterList.size()][][];
        // Find max of simulation
        printf("Hybrid is simulating possible states...\nValue: ");
        Thread[] queuedJobs = new Thread[betterList.size()];
        for (int i = 0; i < betterList.size(); i++) {
            selectedState[i] = nextStates.at(betterList.at(i).x);
        }
        for (int i = 0; i < betterList.size(); i++) {
            final byte[][] statePointer = selectedState[i];
            final int j = i;
            queuedJobs[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    cvalue[j] = randomSimulation(toState(statePointer));
                }
            });
        }
        for (Thread job : queuedJobs) {
            job.start();
        }
        for (Thread job : queuedJobs) {
            try {
                job.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        for (int i = 0; i < betterList.size(); i++) {
            if (cvalue[i] > maxValue) {
                maxValue = cvalue[i];
                bestm = matrixCopy(selectedState[i]);
            }
            if (maxValue >= max_sim_num)
                break;
        }
        newState = bestm;
        hybrid_hyperparam++;
    }

    public int randomSimulation(State s) {
        int value = 0;
        //byte[][] cb = null;
        State currentState = s;
        HybridL shadow = new HybridL(s, side);
        HybridS eshadow = new HybridS(s, enemy_side);
        for (int j = 0; j < max_sim_num; j++) {
            currentState = s;
            while (true) {
                currentState.is_op = true;
                eshadow.update(currentState);
                eshadow.calculate(true);
                currentState = eshadow.nextNode;
                if (eshadow.getAction().size() <= 0) {
                    value++;
                    eshadow.currentNode.backPropagation(false, 0);
                    break;
                }
                currentState.is_op = false;
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