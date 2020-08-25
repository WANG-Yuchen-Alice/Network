package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SensorNode implements Comparable<SensorNode> {

    public int i;
    public int j;
    public int id; //position at the nodeList
    public String carriedSig;
    public ArrayList<Integer> targets; //list of default receivers within the range; in id
    public HashSet<String> signals_set;
    //public ArrayList<String> signals_arr;
    public int status; // 2 SEND; 0 RECEIVE; 1 RECEIVED IN THIS ROUND
    public int num; //current #available receivers
    //TODO: compute power consumption (r; dmax)

    public SensorNode(int i, int j, int id, double r) {
        this.i = i;
        this.j = j;
        this.id = id;
        carriedSig = "";
        this.targets = new ArrayList<Integer>();
        this.signals_set = new HashSet<String>();
        //this.signals_arr = new ArrayList<>();
        this.status = 0;
        this.num = 0;
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

    public String getCarriedSig(){
        return carriedSig;
    }

    public void setCarriedSig(String sig) {
        this.carriedSig = sig;
    }

    public ArrayList<Integer> getTargets() {
        return this.targets;
    }

    public int getNum() {
        return num;
    }

    public void addSignal(String signal) {
        this.signals_set.add(signal);
    }

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

    //============================initialize default receivers===============================

    //initialize default receivers
    public void initializeTargets(ArrayList<SensorNode> nodeList, double r) {
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
    //return true if a signal has reached the max receivers -> doneTags ++
    public boolean send(ArrayList<SensorNode> nodes, HashMap<String, Integer> tagCounter) {
        if (this.signals_set.isEmpty()) {
            System.out.println("error: sender with empty signal pool");
            return false;
        }
        if (this.carriedSig == "") {
            System.out.println(this.getId() + "this node does not even carry a signal");
            return false;
        }

        /*//pick the best signal to send
        ArrayList<String> signals = new ArrayList<>(this.signals_set);

        int max = -1;
        int index = 0;
        for (int i = 0; i < signals.size(); i++) {
            int res = countReceivers(nodes, signals.get(i));
            if (res > max) {
                max = res;
                index = i;
            }
        }
        String carriedSig = signals.get(index);*/

        System.out.print(this.id + "(" + this.carriedSig + ")" + " -> ");

        //send carriedSig to receivers
        for (int i = 0; i < this.targets.size(); i++) {
            int id = this.targets.get(i);
            SensorNode receiver = nodes.get(id);
            if (receiver.status == 0 && !receiver.signals_set.contains(this.carriedSig)) {
                receiver.addSignal(this.carriedSig);
                receiver.setStatus(1); //after receive, the receiver has its status altered to 1, so in the next round it will SEND
                //but it won't receive anymore
                System.out.print(id + " ");
                int newNum = tagCounter.get(this.carriedSig) + 1;
                tagCounter.put(this.carriedSig, newNum);
                if (newNum == nodes.size()) {
                    return true;
                }
            }
        }

        System.out.println();

        //the sender's status is still 2, but it will be removed from the sender queue
        return false;
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
    public void updateNum(ArrayList<SensorNode> nodes) {
        ArrayList<String> signals = new ArrayList<>(this.signals_set);
        int max = -1;
        int index = 0;
        for (int i = 0; i < signals.size(); i++) {
            int res = countReceivers(nodes, signals.get(i));
            if (res > max) {
                max = res;
                index = i;
            }
        }
        this.carriedSig = signals.get(index);
        this.num = max;
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