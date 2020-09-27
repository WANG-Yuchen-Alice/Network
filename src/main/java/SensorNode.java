package main.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class SensorNode implements Comparable<SensorNode> {

    public int i;
    public int j;
    public int id; //position at the nodeList
    public double r;
    public String carriedSig;
    public ArrayList<Integer> targets; //list of default receivers within the range rmax; in id //from closest to farest
    public ArrayList<Integer> neighbors; //list of potential competitors within in the range 2 * rmax; in id
    public HashSet<String> signals_set;
    //public ArrayList<String> signals_arr;
    public int status; // 2 SEND; 0 RECEIVE; 1 RECEIVED IN THIS ROUND
    public int num; //current #available receivers
    //TODO: compute power consumption (r; dmax)

    public HashSet<Integer> competitors;

    public SensorNode(int i, int j, int id, double r) {
        this.i = i;
        this.j = j;
        this.id = id;
        this.r = r;
        carriedSig = "";
        this.targets = new ArrayList<Integer>();
        this.neighbors = new ArrayList<Integer>();
        this.signals_set = new HashSet<String>();
        //this.signals_arr = new ArrayList<>();
        this.status = 0;
        this.num = 0;

        this.competitors = new HashSet<Integer>();
    }

    //===============================================Getters===================================

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public int getId() {
        return id;
    }

    public double getR() {
        return r;
    }

    public boolean isSender() {
        return this.status == 2;
    }

    public String getCarriedSig(){
        return carriedSig;
    }

    public ArrayList<Integer> getTargets() {
        return this.targets;
    }

    public int getNum() {
        return num;
    }

    //===============================================Setters===================================

    public void setStatus(int status) {
        if (status != 0 && status != 1 && status != 2) {
            System.out.println("Sensornode set status wrong: " + status);
        } else {
            this.status = status;
        }
    }

    //should only be used for debugging purpose
    public void setNum(int num) {
        this.num = num;
    }

    public void setCarriedSig(String sig) {
        this.carriedSig = sig;
    }

    public void setR(double r) {
        this.r = r;
    }

    public void addSignal(String signal) {
        this.signals_set.add(signal);
    }

    public void addCompetitor(int id) {
        this.competitors.add(id);
    }

    //clear competitors
    public void clearCompetitors() {
        this.competitors.clear();
    }

    //============================initialize default receivers===============================

    //initialize default receivers
    public void initializeTargets(ArrayList<SensorNode> nodeList, double r) {
        int thisId = this.getId();
        for (int i = 0; i < nodeList.size(); i++) {
            //does not compare with itself
            if (i == this.id) {
                continue;
            }
            if (this.getDistance(nodeList.get(i)) <= r) {
                this.targets.add(i);
                this.num++;
            }
        }

        Collections.sort(targets, new Comparator<Integer>(){
            @Override
            public int compare(Integer t1, Integer t2) {
                double dis1 = distance(nodeList.get(thisId), nodeList.get(t1));
                double dis2 = distance(nodeList.get(thisId), nodeList.get(t2));
                return (int)(dis1 - dis2);
            }

            public double distance(SensorNode a, SensorNode b) {
                return Math.sqrt(Math.pow((a.getI() - b.getI()), 2) + Math.pow((a.getJ() - b.getJ()), 2));
            }
        });
    }

    //initialize potential competitors
    public void initializeNeighbors(ArrayList<SensorNode> nodeList, double r) {
        for (int i = 0; i < nodeList.size(); i++) {
            //does not compare with itself
            if (i == this.id) {
                continue;
            }
            if (this.getDistance(nodeList.get(i)) <= 2 * r) {
                this.neighbors.add(i);
                this.num++;
            }
        }
    }

    //compute distance
    public double getDistance(SensorNode node) {
        double difI = node.getI() - this.i;
        double difJ = node.getJ() - this.j;
        return Math.sqrt(difI * difI + difJ * difJ);
    }

    public int countNeighbor(ArrayList<SensorNode> nodeList, double r) {
        int res = 0;
        for (int i = 0; i < nodeList.size(); i++) {
            //does not compare with itself
            if (i == this.id) {
                continue;
            }
            if (this.getDistance(nodeList.get(i)) < r) {
                res++;
            }
        }
        return res;
    }

    //================================================send======================================
    //return the sent signal if a signal has reached the max receivers -> doneTags ++
    //else return ""
    public String send(ArrayList<SensorNode> nodes, HashMap<String, Integer> tagCounter) {
        if (this.signals_set.isEmpty()) {
            System.out.println("error: sender with empty signal pool");
            return "";
        }
        if (this.carriedSig == "") {
            System.out.println(this.getId() + "this node does not even carry a signal");
            return "";
        }

        String sig = this.getCarriedSig();

        System.out.print(this.id + "(" + sig + ")" + " -> ");

        //send carriedSig to receivers
        for (int i = 0; i < this.targets.size(); i++) {
            int id = this.targets.get(i);
            SensorNode receiver = nodes.get(id);
            if (receiver.status == 0 && !receiver.signals_set.contains(sig)) {
                receiver.addSignal(sig);
                receiver.setStatus(1); //after receive, the receiver has its status altered to 1, so in the next round it will SEND
                //but it won't receive anymore
                System.out.print(id + " ");
                int newNum = tagCounter.get(sig) + 1;
                tagCounter.put(sig, newNum);
                if (newNum == nodes.size()) {
                    return sig;
                }
            }
        }

        System.out.println();

        //the sender's status is still 2, but it will be removed from the sender queue
        return "";
    }

    public void sendTo(SensorNode receiver, HashMap<String, Integer> tagCounter) {
        if (this.signals_set.isEmpty()) {
            System.out.println("error: sender with empty signal pool");
            return;
        }
        if (this.carriedSig == "") {
            System.out.println(this.getId() + "this node does not even carry a signal");
            return;
        }

        String sig = this.getCarriedSig();

        receiver.addSignal(sig);
        receiver.setStatus(1);
        receiver.setCarriedSig(sig);

        System.out.println(this.id + "(" + sig + ")" + " -> " + receiver.getId());

        int newNum = tagCounter.get(sig) + 1;
        tagCounter.put(sig, newNum);
    }

    //count receivers
    public int countReceivers(ArrayList<SensorNode> nodes, String sig) {
        int ans = 0;
        for (int i = 0; i < this.targets.size(); i++) {
            int id = this.targets.get(i);
            if (nodes.get(id).status == 0 && !nodes.get(id).signals_set.contains(sig)) { //in RECEIVE mode and does not contain sig
                ans ++;
            }
        }
        return ans;
    }

    //update num
    //re-count the number of new receivers
    public void updateNum(ArrayList<SensorNode> nodes, HashSet<String> doneTagSet) {
        ArrayList<String> signals = new ArrayList<>(this.signals_set);
        int max = -1;
        int index = 0;
        for (int i = 0; i < signals.size(); i++) {
            String thisSig = signals.get(i);
            if (doneTagSet.contains(thisSig)) {
                continue;
            }
            int res = countReceivers(nodes, thisSig);
            if (res > max) {
                max = res;
                index = i;
            }
        }
        this.carriedSig = signals.get(index);
        this.num = max;
    }

    //=================================================DistributedRatio=====================================

    //==========================================UpdateR====================================
    public void updateR(ArrayList<SensorNode> nodes, double rmax) {
        int senderCnt = 0;
        for (int i = 0; i < this.neighbors.size(); i++) {
            if (nodes.get(this.neighbors.get(i)).isSender()) {
                senderCnt ++;
            }
        }

        double r = rmax * ((this.neighbors.size() - senderCnt) * 1.0 / this.neighbors.size());
        double lowerLimit = this.getLowerLimit(nodes, this.getCarriedSig());
        if (r < 1) {
            double coin = Math.random();
            if (coin <= (1.0 / senderCnt)) {
                r = rmax;
            } else {
                r = 0;
            }
        }
        r = Math.max(r, lowerLimit);
        this.setR(r);
        System.out.println("node: " + this.getId() + " neighboring senders: " + senderCnt + " new r: " + r);
    }

    //TODO: lower limit: distance to the nearest unisited target (under carriedSignal)
    public double getLowerLimit(ArrayList<SensorNode> nodes, String sig) {
        return 0.0;
    }

    //===================================Competitors=================================
    public void reachOut(ArrayList<SensorNode> nodes) {
        for (int i = 0; i < this.targets.size(); i++) {
            SensorNode receiver = nodes.get(this.targets.get(i));
            if (receiver.status != 0 || receiver.signals_set.contains(this.carriedSig)) {
                continue;
            }
            receiver.addCompetitor(this.getId()); //only compete for possible receiver
        }
    }

    //choose the best competitor, return its id
    //if the receiving node is not open, return -1
    //TODO: threshold
    public int chooseSender(ArrayList<SensorNode> nodeList) {
        if (this.status != 0 || this.competitors.size() == 0) {
            return -1;
        }
        ArrayList<Integer> competitors = new ArrayList<>(this.competitors);
        double minDis = 500.0;
        int id = -1;
        for (int i = 0; i < competitors.size(); i++) {
            SensorNode competitor = nodeList.get(competitors.get(i));
            double dis = Math.sqrt(Math.pow((competitor.getI() - this.getI()), 2) + Math.pow((competitor.getJ() - this.getJ()), 2));
            if (i == 0 || dis < minDis) {
                minDis = dis;
                id = competitors.get(i);
            }
        }
        System.out.println("node: " + this.getId() + " chooses: " + id);
        return id;
    }

    //=======================================Prepare Signal=====================================
    //the node should carry a currently weakest signal
    public void updateCarriedSignal() {
        int range = this.signals_set.size();
        int chosenIndex = new Random().nextInt(range);
        this.setCarriedSig(new ArrayList<String>(this.signals_set).get(chosenIndex));
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure
     * {@code sgn(x.compareTo(y)) == -sgn(y.compareTo(x))}
     * for all {@code x} and {@code y}.  (This
     * implies that {@code x.compareTo(y)} must throw an exception iff
     * {@code y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code x.compareTo(y)==0}
     * implies that {@code sgn(x.compareTo(z)) == sgn(y.compareTo(z))}, for
     * all {@code z}.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
     * <i>signum</i> function, which is defined to return one of {@code -1},
     * {@code 0}, or {@code 1} according to whether the value of
     * <i>expression</i> is negative, zero, or positive, respectively.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(SensorNode o) {
        if (this.num > o.getNum()) {
            return -1;
        } else if (this.num < o.getNum()) {
            return 1;
        } else {
            return this.id - o.getId();
        }
    }

    public String toString() {
        return "[" + this.i + ", " + this.j + "]";
    }
}
