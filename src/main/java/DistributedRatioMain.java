package main.java;

import main.java.logic.DistributedBase;
import main.java.logic.DistributedRatio;
import main.java.tool.SignalGenerator;

import java.util.ArrayList;

public class DistributedRatioMain {

    public static int density = 2;// average #neighbors
    public static int L = 10 ; //#length
    public static double A = (1.0) * L * L; // size of the graph
    public static double r = 3; //fixed radius
    public static int N; //#nodes
    public static int K = 2; //#signals
    public static int H = ((int)(Math.sqrt(2) * L) + 1) * K;  //max hops


    public static void main(String[] args) {
        int N = (int)Math.min(computeN(A, L, density, r), A);
        System.out.println("computed N: " + N);

        NodeList nodeList = new NodeList(N, L, r);
        //nodeList.show();
        PositionGraph positionGraph = nodeList.nodeListToPositionGraph(L);
        positionGraph.displayPositionGraph();
        ArrayList<String> signals = new ArrayList<>();
        generateSignals(signals, K);
        ArrayList<SensorNode> nodes = nodeList.toSensorNodeArrayList(r);

        DistributedRatio dr = new DistributedRatio(nodes, signals, L, r, H, 0.5);
        dr.run();
//        DistributedBase db = new DistributedBase(nodes, signals, L, r, H, 0.5);
//        db.run();
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
