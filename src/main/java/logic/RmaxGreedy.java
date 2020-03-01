package main.java.logic;

import main.java.Node;
import main.java.NodeList;
import main.java.PositionGraph;

import java.util.ArrayList;
import java.util.HashMap;
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
    public HashMap<String, Integer> tagCounter = new HashMap<>();
    public int maxHop;

    public RmaxGreedy(NodeList nodeList, PositionGraph positionGraph) {
        this.N = nodeList.getSize();
        this.nodeList = nodeList;
        this.positionGraph = positionGraph;
        this.rmax = 0.5 * Math.sqrt(N);
        this.maxHop = (int)Math.sqrt(N);
    }

    public void setRmax(double newR) {
        this.rmax = newR;
    }

    public void run() {
        System.out.println("RmaxGreedy is running");
        System.out.println("max hops: " + this.maxHop);
        this.positionGraph.displayPositionGraph();
        Random random = new Random();
        Node thisNode = nodeList.getNode(random.nextInt(N));

        //There is only one message
        String tag = "try1";
        tagCounter.put(tag, 1);

        int hops = 1;
        boolean failed = false;

        ArrayList<Node> receivers = thisNode.nodesInRange(this.nodeList.getList(), this.rmax);
        getNewReceivers(receivers, tag);
        System.out.println("In hop #" + hops + ", #new receivers = " + receivers.size());
        markNewReceivers(receivers, tag);
        while (tagCounter.get(tag) >= (int)0.95 * this.N) {
            hops++;
            if (hops > this.maxHop) {
                System.out.println("maxTime exceeded");
                failed = true;
                break;
            }
            if (receivers.size() <= 0) {
                failed = true;
                break;
            }
            thisNode = chooseFarest(thisNode, receivers);
            receivers = thisNode.nodesInRange(this.nodeList.getList(), this.rmax);
            getNewReceivers(receivers, tag);
            System.out.println("In hop #" + hops + ", #new receivers = " + receivers.size());
            markNewReceivers(receivers, tag);
        }
        if(failed) {
            System.out.println("Failed to meet the standard, there are only: " + tagCounter.get(tag) + " receivers");
        } else {
            System.out.println("Succeed. #hops: " + hops);
        }
    }

    public Node chooseFarest(Node original, ArrayList<Node> receivers) {
        return original.getFarest(receivers);
    }

    // Trim original list to get only a list of new receivers, not decided (hence marked yet)
    public void getNewReceivers(ArrayList<Node> receivers, String tag) {
        int i = 0;
        while(i < receivers.size()) {
            if (receivers.get(i).isKnown(tag)) {
                receivers.remove(i);
            }
            i++;
        }
    }

    // after the decision, mark the receivers
    public void markNewReceivers(ArrayList<Node> receivers, String tag) {
        for (int i = 0; i < receivers.size(); i++) {
            receivers.get(i).addTag(tag);
        }
        int prevCnt = tagCounter.get(tag);
        int newCnt = prevCnt + receivers.size();
        tagCounter.put(tag, newCnt);
    }

}
