package amazon_framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CoordinateArray extends ArrayList<Coordinate> {
    public boolean contains(Coordinate c) {
        boolean isContain = false;
        final int size = this.size();
        for (int i = 0; i < size; i++) {
            if (this.get(i).equals(c)) {
                isContain |= true;
                break;
            }
        }
        return isContain;
    }

    public Coordinate at(int index) {
        return this.get(index);
    }

    public void sort_by_x()
    {
        Collections.sort(this, new Comparator<Coordinate>() {
            public int compare(Coordinate o1, Coordinate o2) {
                if (o1.x == o2.x)
                    return 0;
                return o1.x < o2.x ? -1 : 1;
            }
        });
    }

    public void sort_by_y()
    {
        Collections.sort(this, new Comparator<Coordinate>() {
            public int compare(Coordinate o1, Coordinate o2) {
                if (o1.y == o2.y)
                    return 0;
                return o1.y < o2.y ? -1 : 1;
            }
        });
    }

    /**
     * 
     * @param n
     * @return back n elements of an array
     */
    public CoordinateArray back(int n){
        CoordinateArray r = new CoordinateArray();
        //n = n < 0 ? -n : n;
        int end = this.size() - n;
        end = end < 0 ? 0 : end;
        for (int i = this.size() - 1; i >= end; i--){
            r.add(this.at(i));
        }
        return r;
    }

    public CoordinateArray front(int n){
        CoordinateArray f = new CoordinateArray();
        if (n > this.size())
            n = this.size();
        for (int i = 0; i < n; i++){
            f.add(this.at(i));
        }
        return f;
    }
}