package main.java;

import main.java.logic.*;
import main.java.tool.SignalGenerator;

import java.util.ArrayList;

public class tryMain {

    public static int density = 3;// average #neighbors
    public static int L = 10 ; //#length
    public static double A = (1.0) * L * L; // size of the graph
    public static double r = 3; //fixed radius
    public static int N; //#nodes
    public static int K = 2; //#signals
    public static int H = ((int)(Math.sqrt(2) * L) + 1) * K;  //max hops


    public static void main(String[] args) {
        int N = (int)Math.min(computeN(A, L, density, r), A);
        System.out.println("computed N: " + N);

        double res1 = 0, cost1= 0;
        double res2 = 0, cost2 = 0;
        double res3 = 0;
        double res4 = 0;
        double res5 = 0;

        double[] res;

        for (int i = 0; i < 5; i++) {
            NodeList nodeList = new NodeList(N, L, r);
            //nodeList.show();
            PositionGraph positionGraph = nodeList.nodeListToPositionGraph(L);
            //positionGraph.displayPositionGraph();
            ArrayList<String> signals = new ArrayList<>();
            generateSignals(signals, K);
            ArrayList<SensorNode> nodes = nodeList.toSensorNodeArrayList(r);


            DistributedRatio dr = new DistributedRatio(nodes, signals, L, r, H, 0.5);
            res = dr.run_res();
            res1 += res[0];
            cost1 += res[1];

            nodeList = new NodeList(N, L, r);
            //nodeList.show();
            positionGraph = nodeList.nodeListToPositionGraph(L);
            //positionGraph.displayPositionGraph();
            signals = new ArrayList<>();
            generateSignals(signals, K);
            nodes = nodeList.toSensorNodeArrayList(r);

            DistributedBase db = new DistributedBase(nodes, signals, L, r, H, 0.5);
            res = db.run_res();
            res2 += res[0];
            cost2 += res[1];

            nodeList = new NodeList(N, L, r);
            //nodeList.show();
            positionGraph = nodeList.nodeListToPositionGraph(L);
            //positionGraph.displayPositionGraph();
            signals = new ArrayList<>();
            generateSignals(signals, K);
            nodes = nodeList.toSensorNodeArrayList(r);

            DistributedAlternative da = new DistributedAlternative(nodes, signals, L, r, H, 0.5);
            res3 += da.run_res();

            nodeList = new NodeList(N, L, r);
            //nodeList.show();
            positionGraph = nodeList.nodeListToPositionGraph(L);
            //positionGraph.displayPositionGraph();
            signals = new ArrayList<>();
            generateSignals(signals, K);
            nodes = nodeList.toSensorNodeArrayList(r);

            Darkroom dark = new Darkroom(nodes, signals, L, r, H, 0.5);
            res4 += dark.run_res();

            nodeList = new NodeList(N, L, r);
            //nodeList.show();
            positionGraph = nodeList.nodeListToPositionGraph(L);
            //positionGraph.displayPositionGraph();
            signals = new ArrayList<>();
            generateSignals(signals, K);
            nodes = nodeList.toSensorNodeArrayList(r);

            StrongSigSends ss = new StrongSigSends(nodes, signals, L, r, H);
            res5 += ss.run_res();

        }
        System.out.println("ss: " + res5 / 5.0);
        System.out.println("dr: " + res1 / 5.0 + " @" + cost1/5.0);
        System.out.println("db: " + res2 / 5.0 + " @" + cost2/5.0);
        System.out.println("da: " + res3 / 5.0);
        System.out.println("dark: " + res4 / 5.0);

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
