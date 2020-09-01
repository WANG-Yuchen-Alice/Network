package main.java;

import java.util.*;

public class Node {

    public int i;
    public int j;
    public int id;
    public String message;
    public HashSet<String> tags; //store all the tags known by this node
    public ArrayList<Integer> targets; //potential receivers within the rmax range
    public ArrayList<Integer> nextReceivers; //receivers for the coming round (update together with the carriedSignal)
    public ArrayList<Integer> competitors;
    public HashMap<Integer, String> tagMap; //store competitor - tag pairs
    public HashMap<Integer, Double> pMap; //store competitor - 1/d^2 pairs
    public double distance2Sum; //store sum (1/d^2)
    public String carriedTag;
    public int status; //0 RECEIVE, 1 SEND

    public Node(int i, int j, int id) {
        this.i = i;
        this.j = j;
        this.id = id;
        this.message = "";
        this.tags = new HashSet<>();
        this.targets = new ArrayList<>();
        this.nextReceivers = new ArrayList<>();
        this.competitors = new ArrayList<>();
        this.tagMap = new HashMap<>();
        this.pMap = new HashMap<>();
        this.distance2Sum = 0.0;
        this.status = 0;//by default, a node is ready to receive
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

    public void setStatus(int status) {
        if (status!= 1 && status != 0) {
            System.out.println("####wrong status!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        this.status = status;
    }

    public void setCarriedTag(String tag) {
        this.carriedTag = tag;
    }

    public void setTargets(ArrayList<Integer> targets) {
        this.targets = targets;
    }

    public void addTag(String newTag) {
        this.tags.add(newTag);
    }

    public boolean isKnown(String tag) {
        return this.tags.contains(tag);
    }

    public boolean isDone(int K) {
        return this.tags.size() == K;
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

    public void updateCarriedTagAndReceivers(ArrayList<Node> nodes, double r) {
        boolean success = false;
        ArrayList<String> sigs = new ArrayList<>(this.tags);
        int max = -1;
        for (int i = 0; i < sigs.size(); i++) {
            String str = sigs.get(i);
            int cnt = 0;
            ArrayList<Integer> possibleReceivers = new ArrayList<>();
            for (int j = 0; j < this.targets.size(); j++) {
                int thatId = this.targets.get(j);
                //if a receiver is RECEIVER and does not know the signal yet, count it as a potential receivers
                if (nodes.get(thatId).status == 0 && !nodes.get(thatId).isKnown(str)) {
                    cnt ++;
                    possibleReceivers.add(thatId);
                }
            }
            if (cnt > max) {
                success = true;
                max = cnt;
                this.setCarriedTag(str);
                this.nextReceivers = possibleReceivers;
            }
        }
        if (this.nextReceivers.size() <= 0) {
            //System.out.println(this.id + " Empty potential receivers");
        }
        if (!success) {
            System.out.println("Failure in set carried signal / receivers");
        }
    }

    //choose to carry the signal that is the least popular
    public String chooseTagToSend(ArrayList<String> signals, HashMap<String, Integer> tagCounter) {
        if (this.tags.size() == 0) {
            return "";
        }
        int min = Integer.MAX_VALUE;
        String res = "";
        for (int i = 0; i < signals.size(); i++) {
            String sig = signals.get(i);
            if (this.tags.contains(sig)) {
                if (tagCounter.get(sig) < min) {
                    min = tagCounter.get(sig);
                    res = sig;
                }
            }
        }
        return res;
    }

    public void addCompetitor(int id, String tag, Node sender) {
        this.competitors.add(id);
        //this.tagMap.put(id, tag);
        double dis2 = 1 / Math.pow(distanceTo(sender), 2);
        this.pMap.put(id, dis2);
        this.distance2Sum += dis2;
    }

    public void clearCompetitor() {
        this.competitors.clear();
        //this.tagMap.clear();
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
        if (bestP >= 2*(0.1)/3) { //TODO: check threshold
            return res;
        } else {
            return -1;
        }
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
        if (sender < 0) {
            return "";
        }
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

    /**
     * The page shown to the user.
     */
    public enum Status {
        SEND,
        RECEIVE,
        DONE
    }
}
