package main.java;

import main.java.logic.RmaxGreedy;
import main.java.tool.SignalGenerator;

import java.util.ArrayList;

public class NetworkMain {

    public static int N = 100;//#nodes
    public static int L = 80; //#length
    public static int K = 3;//#signals

    public static void main(String[] args) {
        NodeList nodeList = new NodeList(N, L);
        PositionGraph positionGraph = nodeList.nodeListToPositionGraph(L);
        positionGraph.displayPositionGraph();
        ArrayList<String> signals = new ArrayList<>();
        generateSignals(signals, K);
        RmaxGreedy rmaxGreedy = new RmaxGreedy(nodeList.getList(), L);
        rmaxGreedy.setSignals(signals);
        //rmaxGreedy.runSingle();
        rmaxGreedy.runMultiple();
    }

    public static void generateSignals(ArrayList<String> signals, int K) {
        signals.clear();
        for (int i = 1; i <=K; i++) {
            signals.add(new SignalGenerator().generate(5));
        }
    }
}
