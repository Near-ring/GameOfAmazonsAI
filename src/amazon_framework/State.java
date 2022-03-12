package amazon_framework;

public class State {
    public byte[][] mat = null;
    public volatile double value_p1 = 1;
    public volatile double value_p2 = 1;
    public boolean is_opponent = false;
    public State parentState = null;
    public StateArray childStates = null;
    public final double gamma = 0.50;

    public synchronized void backPropagation(boolean win, int steps) {
        if (parentState == null)
            return;

        if (is_opponent == false) {
            if (win == true) {
                value_p1 += 1 * Math.pow(gamma, steps);
                value_p2 -= 1 * Math.pow(gamma, steps);
                parentState.backPropagation(win, ++steps);
            } else {
                if (steps==0)
                    value_p1 = 0;
                else
                    value_p1 -= 1 * Math.pow(gamma, steps);
                value_p2 += 1 * Math.pow(gamma, steps);
                parentState.backPropagation(win, ++steps);
            }
        }

        if (is_opponent == true) {
            if (win == true) {
                value_p1 -= 1 * Math.pow(gamma, steps);
                value_p2 += 1 * Math.pow(gamma, steps);
                parentState.backPropagation(win, ++steps);
            } else {
                if (steps == 0)
                    value_p2 = 0;
                else
                    value_p2 -= 1 * Math.pow(gamma, steps);
                value_p1 += 1 * Math.pow(gamma, steps);
                parentState.backPropagation(win, ++steps);
            }
        }

        if (value_p1 < 0)
            value_p1 = 0;
        if (value_p2 < 0)
            value_p2 = 0;
    }
}