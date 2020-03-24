package main.java;

import main.java.logic.RmaxGreedy;
import main.java.tool.SignalGenerator;

import java.util.ArrayList;

public class NetworkMain {

    public static int density = 3;// average #neighbors
    public static int L = 3; //#length
    public static double A = (1.0) * L * L; // size of the graph
    public static double r = 1.5; //fixed radius
    public static int N; //#nodes
    public static int H = (int)(Math.sqrt(2) * L); //max hops
    public static int K = 2; //#signals

    public static void main(String[] args) {
        int N = (int)Math.min(computeN(A, L, density, r), A);
        System.out.println("computed N: " + N);
        System.out.println("N: " + N);
        NodeList nodeList = new NodeList(N, L, r);
        System.out.println("node list generated");
        PositionGraph positionGraph = nodeList.nodeListToPositionGraph(L);
        positionGraph.displayPositionGraph();
        ArrayList<String> signals = new ArrayList<>();
        generateSignals(signals, K);

        RmaxGreedy rmaxGreedy = new RmaxGreedy(nodeList.getList(), L, r);
        System.out.println("density: " + nodeList.averageNeighbors(rmaxGreedy.rmax));
        rmaxGreedy.setSignals(signals);
        System.out.println("signal set");
        //rmaxGreedy.runSingle();
        rmaxGreedy.runMultiple();
        //rmaxGreedy.runMultipleFullSender();
    }

    public static int computeN(double A, int L, int density, double r) {
        // N * pi * r^2 = A * (density + 1)
        return (int)((A * (density + 1)) / (Math.pow(r, 2) * Math.PI));
    }

    public static void generateSignals(ArrayList<String> signals, int K) {
        signals.clear();
        for (int i = 1; i <=K; i++) {
            signals.add(new SignalGenerator().generate(2));
        }
    }
}
