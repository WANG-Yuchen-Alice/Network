package main.java;

import java.util.*;

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

    public int numNodesInRange(ArrayList<Node> nodeList, double r) {
        int num = 0;
        for (int i = 0; i < nodeList.size(); i++) {
            if (this.getId() != nodeList.get(i).getId() && this.distanceTo(nodeList.get(i)) <= r) {
                num++;
            }
        }
        return num;
    }

    public void addCompetitor(int id, String tag, Node sender) {
        this.competitors.add(id);
        this.tagMap.put(id, tag);
        double dis2 = 1 / Math.pow(distanceTo(sender), 2);
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

    public int chooseSenderByP() {
        System.out.println("Among all competitors: ");
        for (int i = 0; i < this.competitors.size(); i++) {
            System.out.print(this.competitors.get(i) + " " );
        }
        System.out.println();
        double accumulateP = 0.0;
        adjustCompetitors();// called after all p are stored
        if (this.distance2Sum == 0) {
            return -1;
        }
        ArrayList<Double> ctMap = new ArrayList<>();
        for (int i = 0; i < competitors.size(); i++) {
            ctMap.add(accumulateP);
            accumulateP += this.pMap.get(this.competitors.get(i)) / this.distance2Sum;
        } //in [thisctMap value, nextctMap value) --> this sender
        Random random = new Random();
        double mark = random.nextDouble();
        System.out.println("mark: " + mark);
        System.out.println("show ctMap: ");
        showctMap(ctMap);
        for (int i = 1; i < competitors.size(); i++) {
            if (ctMap.get(i) >= mark) {
                System.out.print(this.id + " chooses: " + this.competitors.get(i-1));
                System.out.println(this.tagMap.get(this.competitors.get(i-1)));
                return this.competitors.get(i-1);
            }
        }
        System.out.println(this.id + " choose the last one");
        return this.competitors.get(this.competitors.size() - 1);
    }

    public void adjustCompetitors() {
        double newSum = 0.0;
        ArrayList<Integer> temp = this.competitors;
        for (int i = 0; i < temp.size(); i++) {
            int thisCandidate = temp.get(i);
            if (pMap.containsKey(thisCandidate) && this.pMap.get(thisCandidate) < this.distance2Sum / 2.0) {
                pMap.remove(thisCandidate);
                tagMap.remove(thisCandidate);
                competitors.remove(i);
            }
            if (this.pMap.containsKey(thisCandidate)) {
                newSum += this.pMap.get(thisCandidate);
            }
        }
        this.distance2Sum = newSum; //WARNING: can be zero
    }

    public String chooseTag() {
        int sender = chooseSender();
        String chosenTag =  this.tagMap.get(sender);
        addTag(chosenTag);
        System.out.println(chosenTag + " " + "sender chosen: " + sender);
        return chosenTag;
    }

    public String chooseTagByP() {
        int sender = chooseSenderByP();
        if (sender == -1) {
            return "";
        }
        String chosenTag =  this.tagMap.get(sender);
        addTag(chosenTag);
        return chosenTag;
    }

    public void showctMap(ArrayList<Double> ctMap) {
        for (int i = 0; i < this.competitors.size(); i++) {
            System.out.println(this.competitors.get(i) + "  " + ctMap.get(i));
        }
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
