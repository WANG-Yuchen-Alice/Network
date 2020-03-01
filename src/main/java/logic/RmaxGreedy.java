package main.java.logic;

import main.java.Node;
import main.java.tool.SignalGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Conditions: fixed position; one starting point; message tags counter
 * Goal: 95% received
 * Nest node: farest from the new receivers
 */
public class RmaxGreedy {
    public int N;
    public ArrayList<Node> nodeList;
    public double rmax;
    public HashMap<String, Integer> tagCounter;
    public int maxHop;

    public RmaxGreedy(ArrayList<Node> nodeList) {
        this.N = nodeList.size();
        this.nodeList = nodeList;
        this.rmax = 2 * Math.sqrt(N);
        tagCounter = new HashMap<>();
        this.maxHop = (int)Math.sqrt(N);
    }

    // WARNING: choose between single and multiple
    // start with one node, eeach time choose two more senders
    public void runSingle() {
        tagCounter.clear();
        System.out.println("RmaxGreedy_single is running");
        System.out.println("max hops: " + this.maxHop);
        System.out.println("goal: " + (int)(0.8*this.N));

        Random random = new Random();
        int startIndex = random.nextInt(N);

        //There is only one message
        String tag = "try1";
        tagCounter.put(tag, 1);
        this.nodeList.get(startIndex).addTag(tag);

        int hops = 1;

        ArrayList<Integer> receivers = getNewReceivers(startIndex, this.nodeList, tag, this.rmax);
        markNewReceivers(receivers, tag);

        findTwoAndProcess(startIndex, receivers, tag, hops);

        System.out.println("Done. " + " receivers: " + tagCounter.get(tag));

    }

    public void findTwoAndProcess(int oriIndex, ArrayList<Integer> receivers, String tag, int hops) {
        if (tagCounter.get(tag) > (int)(0.8 * this.N)) {
            System.out.println("Success");
            return;
        }
        if (hops >= this.maxHop) {
            System.out.println("Exceed maxHop: " + hops);
            return;
        }
        hops++;
        if (receivers.size() < 2) {
            System.out.println("less than 2");
            return;
        }
        int first = chooseGreedyBest(oriIndex, receivers, tag);
        System.out.println("#1: " + this.nodeList.get(first).getId());
        ArrayList<Integer> firstReceivers = getNewReceivers(first, this.nodeList, tag, this.rmax);
        int firstSize = firstReceivers.size();

        if (firstSize > 0) {
            markNewReceivers(firstReceivers, tag);
            int second = chooseGreedyBest(oriIndex, receivers, tag);
            System.out.println("#2: " + this.nodeList.get(second).getId());
            ArrayList<Integer> secondReceivers = getNewReceivers(second, this.nodeList, tag, this.rmax);
            int secondSize = secondReceivers.size();
            if (secondSize > 0) {
                markNewReceivers(secondReceivers, tag);
            }
            findTwoAndProcess(first, firstReceivers, tag, hops);
            if(secondSize > 0) {
                findTwoAndProcess(second, firstReceivers, tag, hops);
            }
        }
    }

    // for each tag each time choose one best next; messages are sent together at time stamp 0;
    // each node can only be sender / receiver
    public void runMultiple(int k) {
        System.out.println("RmaxGreedy_multiple is running");

        HashSet<String> sigSet = new HashSet<>();
        ArrayList<Node> senders = new ArrayList<>();
        SignalGenerator generator = new SignalGenerator();
        Random random = new Random();

        //generate a lits of  signals and original senders
        while(sigSet.size() < this.N) {
            sigSet.add(generator.generate(5));
        }
        ArrayList<String> signals = new ArrayList(sigSet);
    }

    public int chooseGreedyBest(int oriIndex, ArrayList<Integer> receiverPool, String tag) {
        int maxReceivers = -1;
        int index = -1;
        ArrayList<Integer> temp;
        for (int i = 0; i < receiverPool.size(); i++) {
            temp = getNewReceivers(receiverPool.get(i), this.nodeList, tag, this.rmax);
            int tempSize = temp.size();
            if (tempSize > maxReceivers) {
                index = i;
                maxReceivers = tempSize;
            }
        }
        //System.out.println("can reach: " + maxReceivers);
        return receiverPool.get(index);
    }

    // Trim original list to get only a list of new receivers, not decided (hence marked yet)
    public ArrayList<Integer> getNewReceivers(int index, ArrayList<Node> nodeList, String tag, double r) {
        ArrayList<Integer> temp = this.nodeList.get(index).nodesInRange(nodeList, r);
        ArrayList<Integer> receivers = new ArrayList<>();
        int i = 0;
        while(i < temp.size()) {
            if (index != temp.get(i) && !this.nodeList.get(temp.get(i)).isKnown(tag)) {
                receivers.add(temp.get(i));
            }
            i++;
        }
        System.out.println("for node " + this.nodeList.get(index).getId() + " the number of new receivers: " + receivers.size());
        for (int j = 0; j < receivers.size(); j++) {
            int thisIndex = receivers.get(j);
            System.out.print(this.nodeList.get(receivers.get(j)).getId() + " ");
        }
        System.out.println();

        return receivers;
    }

    // after the decision, mark the receiversLis
    public void markNewReceivers(ArrayList<Integer> receivers, String tag) {
        int cnt = receivers.size();
        for (int i = 0; i < receivers.size(); i++) {
            this.nodeList.get(receivers.get(i)).addTag(tag);
        }
        System.out.println("Mark new: " + cnt);
        int prevCnt = tagCounter.get(tag);
        int newCnt = prevCnt + cnt;
        tagCounter.put(tag, newCnt);
    }

    public Node chooseFarest(Node original, ArrayList<Node> receivers) {
        return original.getFarest(receivers);
    }
}
