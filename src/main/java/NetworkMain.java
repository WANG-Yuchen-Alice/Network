package main.java;

import main.java.logic.RmaxGreedy;

public class NetworkMain {

    public static final int N = 10;

    public static void main(String[] args) {
        NodeList nodeList = new NodeList(N);
        PositionGraph positionGraph = nodeList.nodeListToPositionGraph();
        positionGraph.displayPositionGraph();
        RmaxGreedy rmaxGreedy = new RmaxGreedy(nodeList, positionGraph);
        rmaxGreedy.run();
    }
}
