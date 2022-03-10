package amazon_framework;

import java.lang.Math;

public class Coordinate {
    public int x;
    public int y;

    public Coordinate(int a, int b) {
        x = a;
        y = b;
    }

    public Coordinate() {
    }

    @Override
    public String toString() {
        String str = "(";
        str += String.valueOf(x);
        str += ",";
        str += String.valueOf(y);
        str += ") ";
        return str;
    }

    public boolean equals(Coordinate p) {
        if (p.x == this.x && p.y == this.y)
            return true;
        else
            return false;
    }

    public double euclideanDist(Coordinate p) {
        return Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2));
    }

    public double modulus() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}