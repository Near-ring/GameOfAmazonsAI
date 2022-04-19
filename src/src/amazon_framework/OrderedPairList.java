package amazon_framework;

import java.util.ArrayList;

import static amazon_framework.Operations.max;

public final class OrderedPairList extends ArrayList<OrderedPair> {
    public boolean contains(OrderedPair c) {
        boolean isContain = false;
        final int size = this.size();
        for (int i = 0; i < size; i++) {
            if (this.get(i).equals(c)) {
                isContain = true;
                break;
            }
        }
        return isContain;
    }

    public OrderedPair at(int index) {
        return this.get(index);
    }

    public void sort_by_x() {
        this.sort((o1, o2) -> {
            if (o1.x == o2.x) {
                return 0;
            }
            return o1.x < o2.x ? -1 : 1;
        });
    }

    public void sort_by_y() {
        this.sort((o1, o2) -> {
            if (o1.y == o2.y) {
                return 0;
            }
            return o1.y < o2.y ? -1 : 1;
        });
    }

    /**
     * @param n
     * @return back n elements of an array
     */
    public OrderedPairList back(int n) {
        OrderedPairList r = new OrderedPairList();
        int end = this.size() - n;
        end = max(end, 0);
        for (int i = this.size() - 1; i >= end; i--) {
            r.add(this.at(i));
        }
        return r;
    }

    public OrderedPairList front(int n) {
        OrderedPairList f = new OrderedPairList();
        if (n > this.size()) {
            n = this.size();
        }
        for (int i = 0; i < n; i++) {
            f.add(this.at(i));
        }
        return f;
    }

    public String toString() {
        if (!this.isEmpty()) {
            return (
                "Source: " + this.at(0).toString() +
                "\nDestination: " + this.at(1).toString() +
                "\nArrow: " + this.at(2).toString() + "\n"
            );
        } else {
            return null;
        }
    }
}