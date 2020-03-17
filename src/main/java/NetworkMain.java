package main.java;

import main.java.logic.RmaxGreedy;
import main.java.tool.SignalGenerator;

import java.util.ArrayList;

public class NetworkMain {

    public static int N = 50;//#nodes
    public static int L = 100; //#length
    public static int K = 3;//#signals

    public static void main(String[] args) {
        NodeList nodeList = new NodeList(N, L);
        PositionGraph positionGraph = nodeList.nodeListToPositionGraph(L);
        positionGraph.displayPositionGraph();
        ArrayList<String> signals = new ArrayList<>();
        generateSignals(signals, K);

        RmaxGreedy rmaxGreedy = new RmaxGreedy(nodeList.getList(), L);
        System.out.println("density: " + nodeList.averageNeighbors(rmaxGreedy.rmax));
        rmaxGreedy.setSignals(signals);
        System.out.println("signal set");
        //rmaxGreedy.runSingle();
        rmaxGreedy.runMultiple();
        //rmaxGreedy.runMultipleFullSender();
    }

    public static void generateSignals(ArrayList<String> signals, int K) {
        signals.clear();
        for (int i = 1; i <=K; i++) {
            signals.add(new SignalGenerator().generate(5));
        }
    }
}
