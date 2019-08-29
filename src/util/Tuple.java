package util;

public class Tuple<A, B> {

    private A firstElement;
    private B secondElement;

    public Tuple(A firstElement, B secondElement) {
        this.firstElement = firstElement;
        this.secondElement = secondElement;
    }

    public A first() {
        return this.firstElement;
    }

    public B second() {
        return this.secondElement;
    }
}
