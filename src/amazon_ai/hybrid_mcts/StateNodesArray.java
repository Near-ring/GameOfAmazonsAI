package amazon_ai.hybrid_mcts;

import amazon_framework.MatrixArray;

import java.util.ArrayList;

public final class StateNodesArray extends ArrayList<StateNode> {
    StateNodesArray(MatrixArray m) {
        init(m);
    }

    public StateNode at(int index) {
        return this.get(index);
    }

    private void init(MatrixArray m) {
        StateNode sn;
        for (byte[][] b : m) {
            sn = new StateNode(b);
            this.add(sn);
        }
    }
}
