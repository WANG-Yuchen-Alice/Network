package main.java.logic;

import main.java.Node;
import main.java.NodeList;
import main.java.PositionGraph;
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
    public NodeList nodeList;
    public PositionGraph positionGraph;
    public double rmax;
    public HashMap<String, Integer> tagCounter;
    public int maxHop;

    public RmaxGreedy(NodeList nodeList, PositionGraph positionGraph) {
        this.N = nodeList.getSize();
        this.nodeList = nodeList;
        this.positionGraph = positionGraph;
        this.rmax = Math.sqrt(N);
        tagCounter = new HashMap<>();
        this.maxHop = (int)Math.sqrt(N);
    }

    public void setRmax(double newR) {
        this.rmax = newR;
    }

    // WARNING: choose between single and multiple
    // start with one node, eeach time choose two more senders
    public void runSingle() {
        System.out.println("RmaxGreedy_single is running");
        System.out.println("max hops: " + this.maxHop);
        System.out.println("goal: " + (int)(0.8*this.N));
        this.positionGraph.displayPositionGraph();

        Random random = new Random();
        Node thisNode = this.nodeList.getNode(random.nextInt(N));

        //There is only one message
        String tag = "try1";
        tagCounter.put(tag, 1);

        int hops = 1;
        boolean failed = false;

        ArrayList<Node> receivers = getNewReceivers(thisNode, this.nodeList.getList(), tag, this.rmax);
        markNewReceivers(receivers, tag);

        findTwoAndProcess(thisNode, receivers, tag, hops);

        System.out.println("Done. " + " receivers: " + tagCounter.get(tag));

    }

    public void findTwoAndProcess(Node thisNode, ArrayList<Node> receivers, String tag, int hops) {
        if (tagCounter.get(tag) > (int)(0.8 * this.N) || receivers.size() <= 0) {
            return;
        }
        if (hops >= this.maxHop) {
            System.out.println("Exceed maxHop: " + hops);
            return;
        }
        hops++;
        Node[] bestTwo = chooseGreedyBest(thisNode, receivers, tag);
        Node first = bestTwo[0];
        Node second = bestTwo[1];
        ArrayList<Node> firstReceivers = getNewReceivers(first, this.nodeList.getList(), tag, this.rmax);
        markNewReceivers(firstReceivers, tag);
        ArrayList<Node> secondReceivers = getNewReceivers(second, this.nodeList.getList(), tag, this.rmax);
        markNewReceivers(secondReceivers, tag);

        findTwoAndProcess(first, firstReceivers, tag, hops);
        findTwoAndProcess(second, firstReceivers, tag, hops);
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

    public Node[] chooseGreedyBest(Node thisNode, ArrayList<Node> receiverPool, String tag) {
        int maxReceiver = -1, secondReceiver = -1;
        int index1 = -1, index2 = -1;
        ArrayList<Node> temp = new ArrayList<>();
        for (int i = 0; i < receiverPool.size(); i++) {
            Node candidate = receiverPool.get(i);
            temp = getNewReceivers(candidate, this.nodeList.getList(), tag, this.rmax);
            // System.out.println("test, #rec: " + temp.size());
            int tempSize = temp.size();
            if (tempSize > maxReceiver) {
                index2 = index1;
                index1 = i;
                secondReceiver = maxReceiver;
                maxReceiver = tempSize;
            } else if (tempSize > secondReceiver) {
                secondReceiver = tempSize;
                index2 = i;
            }

        }
        Node[] nodes = {receiverPool.get(index1), receiverPool.get(index2)};
        return nodes;
    }

    // Trim original list to get only a list of new receivers, not decided (hence marked yet)
    public ArrayList<Node> getNewReceivers(Node thisNode, ArrayList<Node> nodeList, String tag, double r) {
        ArrayList<Node> receivers = thisNode.nodesInRange(nodeList, r);
        int i = 0;
        while(i < receivers.size()) {
            if (receivers.get(i).isKnown(tag)) {
                receivers.remove(i);
            }
            i++;
        }
        return receivers;
    }

    // after the decision, mark the receivers
    public void markNewReceivers(ArrayList<Node> receivers, String tag) {
        int cnt = 0;
        for (int i = 0; i < receivers.size(); i++) {
            if (!receivers.get(i).isKnown(tag)) {
                receivers.get(i).addTag(tag);
                cnt++;
            }
        }
        int prevCnt = tagCounter.get(tag);
        int newCnt = prevCnt + cnt;
        tagCounter.put(tag, newCnt);
    }

    public Node chooseFarest(Node original, ArrayList<Node> receivers) {
        return original.getFarest(receivers);
    }
}
