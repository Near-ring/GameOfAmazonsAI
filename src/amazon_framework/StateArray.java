package amazon_framework;

import java.util.ArrayList;

public class StateArray extends ArrayList<State>{
    public State at(int index) {
        return this.get(index);
    }
}
