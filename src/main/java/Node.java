package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class Node {

    public int i;
    public int j;
    public String message;
    public HashSet<String> tags;
    public int id;
    public ArrayList<Integer> competitors;
    public HashMap<Integer, String> tagMap;
    public HashMap<Integer, Double> pMap;
    public double distance2Sum;

    public Node(int i, int j, int id) {
        this.i = i;
        this.j = j;
        this.id = id;
        this.message = "";
        this.tags = new HashSet<>();
        this.competitors = new ArrayList<>();
        this.tagMap = new HashMap<>();
        this.pMap = new HashMap<>();
        this.distance2Sum = 0.0;
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

    public void addTag(String newTag) {
        this.tags.add(newTag);
    }

    public boolean isKnown(String tag) {
        return this.tags.contains(tag);
    }

    public double distanceTo(Node ano) {
        return Math.sqrt(Math.pow((this.i - ano.getI()), 2) + Math.pow(this.j - ano.getJ(), 2));
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

    public void addCompetitor(int id, String tag, Node sender) {
        this.competitors.add(id);
        this.tagMap.put(id, tag);
        double dis2 = Math.pow(distanceTo(sender), 2);
        this.pMap.put(id, dis2);
        this.distance2Sum += dis2;
    }

    public void clearCompetitor() {
        this.competitors.clear();
        this.tagMap.clear();
        this.pMap.clear();
        this.distance2Sum = 0.0;
    }

    public int chooseSender() {
        int res = -1;
        double bestP = 0.0;
        for (int i = 0; i < this.competitors.size(); i++) {
            double thisP = this.pMap.get(this.competitors.get(i)) / this.distance2Sum; //(0,1)
            if (i == 0 || thisP > bestP) {
                res = this.competitors.get(i);
                bestP = thisP;
            }
        }
        return res;
    }

    public String chooseTag() {
        String chosenTag =  this.tagMap.get(chooseSender());
        addTag(chosenTag);
        return chosenTag;
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
