package main.java;

import main.java.logic.RmaxFull;
import main.java.logic.RmaxGreedy;
import main.java.tool.SignalGenerator;

import java.util.ArrayList;

public class NetworkMain {

    public static int density = 5;// average #neighbors
    public static int L = 20 ; //#length
    public static double A = (1.0) * L * L; // size of the graph
    public static double r = 2; //fixed radius
    public static int N; //#nodes
    public static int H = (int)((Math.sqrt(2) * L)/2); //max hops
    public static int K = 5; //#signals

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

        //RmaxFull rmaxFull = new RmaxFull(nodeList.getList(), L, r, H);
        /*System.out.println("density: " + nodeList.averageNeighbors(rmaxFull.rmax));
        rmaxFull.setSignals(signals);
        System.out.println("signal set");*/
        RmaxGreedy rmaxGreedy = new RmaxGreedy(nodeList.getList(), L, r, H);
        System.out.println("density: " + nodeList.averageNeighbors(rmaxGreedy.rmax));
        rmaxGreedy.setSignals(signals);
        System.out.println("signal set");
        //rmaxGreedy.runSingle();
        //rmaxGreedy.runMultiple();
        int ans = rmaxGreedy.runMultiple();
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
