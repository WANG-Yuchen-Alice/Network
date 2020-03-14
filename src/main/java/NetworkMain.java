package main.java;

import main.java.logic.RmaxGreedy;

public class NetworkMain {

    public static int N = 100;
    public static int L = 80;

    public static void main(String[] args) {
        NodeList nodeList = new NodeList(N, L);
        PositionGraph positionGraph = nodeList.nodeListToPositionGraph(L);
        positionGraph.displayPositionGraph();
        RmaxGreedy rmaxGreedy = new RmaxGreedy(nodeList.getList(), L);
        rmaxGreedy.runSingle();
    }
}
