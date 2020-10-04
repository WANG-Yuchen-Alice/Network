package main.java;

import main.java.logic.DistributedBase;
import main.java.logic.DistributedRatio;
import main.java.tool.SignalGenerator;

import java.util.ArrayList;

public class tryMain {

    public static int density = 2;// average #neighbors
    public static int L = 20 ; //#length
    public static double A = (1.0) * L * L; // size of the graph
    public static double r = 3; //fixed radius
    public static int N; //#nodes
    public static int K = 2; //#signals
    public static int H = ((int)(Math.sqrt(2) * L) + 1) * K;  //max hops


    public static void main(String[] args) {
        int N = (int)Math.min(computeN(A, L, density, r), A);
        System.out.println("computed N: " + N);

        double res1 = 0;
        double res2 = 0;
        for (int i = 0; i < 5; i++) {
            NodeList nodeList = new NodeList(N, L, r);
            //nodeList.show();
            PositionGraph positionGraph = nodeList.nodeListToPositionGraph(L);
            //positionGraph.displayPositionGraph();
            ArrayList<String> signals = new ArrayList<>();
            generateSignals(signals, K);
            ArrayList<SensorNode> nodes = nodeList.toSensorNodeArrayList(r);


            DistributedRatio dr = new DistributedRatio(nodes, signals, L, r, H, 0.5);
            res1 += dr.run_res();

            nodeList = new NodeList(N, L, r);
            //nodeList.show();
            positionGraph = nodeList.nodeListToPositionGraph(L);
            //positionGraph.displayPositionGraph();
            signals = new ArrayList<>();
            generateSignals(signals, K);
            nodes = nodeList.toSensorNodeArrayList(r);

            DistributedBase db = new DistributedBase(nodes, signals, L, r, H, 0.5);
            res2 += db.run_res();

        }
        System.out.println("dr: " + res1 / 5.0);
        System.out.println("db: " + res2 / 5.0);

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
