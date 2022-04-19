package amazon_ai.hybrid_mcts;

import amazon_framework.*;

import java.util.concurrent.LinkedBlockingQueue;

import static amazon_framework.Operations.*;

public final class HybridMCTS extends AbstractAIAgent {
    public final int num_threads = Runtime.getRuntime().availableProcessors();
    int hybrid_hyperparam = 17;
    double max_sim_num;
    ThreadPool thread_pool = new ThreadPool(32, 32, new LinkedBlockingQueue<Runnable>());

    public HybridMCTS(byte[][] board, byte side) {
        super(board, side);
    }

    @Override
    public void destructor() {
        thread_pool.shutdown();
    }

    @Override
    public void calculate() {
        max_sim_num = -70 + 10 * hybrid_hyperparam;
        OrderedPairList valueArray = new OrderedPairList();
        int varr_size = hybrid_hyperparam;
        int maxSimValue = -INF;
        byte[][] bestNode = null;

        // <Expansion>
        // Gets the possible states and calculates available moves for each child state
        MatrixArray childStates = getPossibleStates(gameBoard, side);
        final int size_i = childStates.size();
        for (int i = 0; i < size_i; i++) {
            OrderedPair value = new OrderedPair();
            value.y += numPossibleMoves(childStates.at(i), side);
            value.y -= numPossibleMoves(childStates.at(i), enemy_side);
            value.x = i;
            valueArray.add(value);
        }
        valueArray.sort_by_y(); // Get largest possible move state list, desending order
        varr_size = min(hybrid_hyperparam, 32);
        // </Expansion>

        // <Selection>
        // n most valuable moves
        OrderedPairList selectedNodeCor = valueArray.back(varr_size);
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
            thread_pool.future[j] = thread_pool.submit(() -> {
                simValue[j] = randomSimulation(new StateNode(selectedNodeArray[j]));
            });
        }
        thread_pool.join_all();
        // </Simulation>

        for (int i = 0; i < selectedNodeArray.length; i++) {
            if (simValue[i] > maxSimValue) {
                maxSimValue = simValue[i];
                bestNode = matrixCopy(selectedNodeArray[i]);
            }
            if (maxSimValue >= max_sim_num) {
                break;
            }
        }
        newState = bestNode;
        hybrid_hyperparam++;
    }

    public int randomSimulation(StateNode s) {
        int value = 0;
        StateNode currentState = s;
        HybridSimAgent shadow = new HybridSimAgent(s, side);
        HybridSimAgent eshadow = new HybridSimAgent(s, enemy_side);
        for (int j = 0; j < max_sim_num; j++) {
            currentState = s;
            while (true) {
                currentState.opponent = true;
                eshadow.update(currentState);
                // Selection and Expansion
                eshadow.calculate(true);
                currentState = eshadow.nextNode;
                if (eshadow.getAction().size() <= 0) {
                    value++;
                    // Backpropagation
                    eshadow.currentNode.backPropagation(false, 0);
                    break;
                }
                currentState.opponent = false;
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