package main.java;

import main.java.logic.RmaxGreedy;

public class NetworkMain {

    public static final int N = 50;

    public static void main(String[] args) {
        NodeList nodeList = new NodeList(N);
        PositionGraph positionGraph = nodeList.nodeListToPositionGraph();
        RmaxGreedy rmaxGreedy = new RmaxGreedy(nodeList, positionGraph);
        rmaxGreedy.runSingle();
    }
}
