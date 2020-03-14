package main.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Node {

    public int i;
    public int j;
    public String message;
    public HashSet<String> tags;
    public int id;

    public Node(int i, int j, int id) {
        this.i = i;
        this.j = j;
        this.id = id;
        this.message = "";
        this.tags = new HashSet<>();
    }

    public Node(int i, int j, String message) {
        this.i = i;
        this.j = j;
        this.message = message;
        this.tags = new HashSet<>();
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public int getId() {
        return id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void addTag(String newTag) {
        this.tags.add(newTag);
    }

    public boolean isKnown(String tag) {
        return this.tags.contains(tag);
    }

    public double distanceTo(Node ano) {
        return Math.sqrt(Math.pow((this.i - ano.getI()), 2) + Math.pow(this.j - ano.getJ(), 2));
    }

    // Returns the farest node from the receiver list
    public Node getFarest(ArrayList<Node> nodeList) {
        double max = this.distanceTo(nodeList.get(0));
        int mark = 0;
        for (int i = 1; i < nodeList.size(); i++) {
            double thisDistance = this.distanceTo(nodeList.get(i));
            if (thisDistance > max) {
                max = thisDistance;
                mark = i;
            }
        }
        return nodeList.get(mark);
    }

    // Returns nodes within in the range r (including old receivers; no repetition)
    // contains the indices
    public ArrayList<Integer> nodesInRange(ArrayList<Node> nodeList, double r) {
        ArrayList<Integer> resList = new ArrayList<>();
        for (int i = 0; i < nodeList.size(); i++) {
            if (this.getId() != nodeList.get(i).getId() && this.distanceTo(nodeList.get(i)) <= r) {
                resList.add(i);
            }
        }
        return resList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return i == node.i &&
                j == node.j &&
                id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j, message, tags, id);
    }

    public String toString() {
        return "[" + this.i + ", " + this.j + "]";
    }
}
