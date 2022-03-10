package amazon_framework;

import java.util.ArrayList;
public class MatrixArray extends ArrayList<byte[][]>{
    public byte[][] at(int index) {
        return this.get(index);
    }
}