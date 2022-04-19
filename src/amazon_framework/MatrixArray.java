package amazon_framework;

import java.util.ArrayList;

public final class MatrixArray extends ArrayList<byte[][]> {
    public byte[][] at(int index) {
        return this.get(index);
    }
}