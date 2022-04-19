package amazon_framework;

public final class OrderedPair {
    public int x;
    public int y;

    public OrderedPair(int a, int b) {
        x = a;
        y = b;
    }

    public OrderedPair() {
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

    public boolean equals(OrderedPair p) {
        return p.x == this.x && p.y == this.y;
    }

    public double euclideanDist(OrderedPair p) {
        return Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2));
    }

    public double modulus() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}