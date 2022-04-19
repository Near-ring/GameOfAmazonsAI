package amazon_ai.hybrid_mcts;

public class StateNode {
    public final double gamma = 0.66;
    public byte[][] mat = null;
    public volatile double selfValue = 1;
    public volatile double enemyValue = 1;
    public boolean opponent = false;
    public StateNode parentState = null;
    public StateNodesArray childStates = null;

    StateNode(byte[][] x) {
        this.mat = x;
    }

    public synchronized void backPropagation(boolean win, int steps) {

        if (!opponent) {
            if (win) {
                selfValue += 1 * Math.pow(gamma, steps);
                enemyValue -= 1 * Math.pow(gamma, steps);
            } else {
                selfValue -= 1 * Math.pow(gamma, steps);
                enemyValue += 1 * Math.pow(gamma, steps);
            }
        }

        if (opponent) {
            if (win) {
                selfValue -= 1 * Math.pow(gamma, steps);
                enemyValue += 1 * Math.pow(gamma, steps);
            } else {
                enemyValue -= 1 * Math.pow(gamma, steps);
                selfValue += 1 * Math.pow(gamma, steps);
            }
        }

        if (selfValue < 0) {
            selfValue = 0;
        }
        if (enemyValue < 0) {
            enemyValue = 0;
        }

        if (parentState == null) {
            return;
        }
        parentState.backPropagation(win, ++steps);
    }
}