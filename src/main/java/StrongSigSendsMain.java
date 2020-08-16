package main.java;

import main.java.logic.StrongSigSends;
import main.java.tool.SignalGenerator;

import java.util.ArrayList;

public class StrongSigSendsMain {

    public static int density = 3;// average #neighbors
    public static int L = 10 ; //#length
    public static double A = (1.0) * L * L; // size of the graph
    public static double r = 2; //fixed radius
    public static int N; //#nodes
    public static int H = (int)((Math.sqrt(2) * L)/2);  //max hops
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
        ArrayList<SensorNode> nodes = nodeList.toSensorNodeArrayList(r);

        StrongSigSends sss = new StrongSigSends(nodes, signals, L, r);
        sss.run();
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
