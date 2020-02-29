package main.java;

public class Node {

    public int i;
    public int j;
    public String message;
    public String tag;

    public Node(int i, int j) {
        this.i = i;
        this.j = j;
        this.message = "";
        this.tag = "";
    }

    public Node(int i, int j, String message) {
        this.i = i;
        this.j = j;
        this.message = message;
        this.tag = "";
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public String getTag() {
        return tag;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public double distanceTo(Node ano) {
        return Math.sqrt(Math.pow((this.i - ano.getI()), 2) + Math.pow(this.j - ano.getJ(), 2));
    }


}
