package amazon_framework;

public class State {
    public byte[][] mat = null;
    public volatile double value = 2;
    public volatile double evalue = 2;
    public boolean is_op = false;
    public State parentState = null;
    public StateArray childStates = null;
    public final double gamma = 0.80;

    public synchronized void backPropagation(boolean win, int steps)
    {
        if (parentState == null)
            return;

        if (is_op == false) {
            if (win == true){
                value += 1 * Math.pow(gamma, steps);
                evalue -= 1 * Math.pow(gamma, steps);
                if (value < 0)
                    value = 0;
                if (evalue < 0)
                    evalue = 0;
                if (parentState != null)
                    parentState.backPropagation(win, ++steps);
            } else {
                value -= 1 * Math.pow(gamma, steps);
                evalue += 1 * Math.pow(gamma, steps);
                if (value < 0)
                    value = 0;
                if (evalue < 0)
                    evalue = 0;
                if (parentState != null)
                    parentState.backPropagation(win, ++steps);
            }
        } else {
            if (win == true){
                value -= 1 * Math.pow(gamma, steps);
                evalue += 1 * Math.pow(gamma, steps);
                if (value < 0)
                    value = 0;
                if (evalue < 0)
                    evalue = 0;
                if (parentState != null)
                    parentState.backPropagation(win, ++steps);
            } else {
                value += 1 * Math.pow(gamma, steps);
                evalue -= 1 * Math.pow(gamma, steps);
                if (value < 0)
                    value = 0;
                if (evalue < 0)
                    evalue = 0;
                if (parentState != null)
                    parentState.backPropagation(win, ++steps);
            }
        }
    }
}