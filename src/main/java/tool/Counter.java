package main.java.tool;

import main.java.NodeList;
import main.java.logic.RmaxFull;
import main.java.logic.RmaxGreedy;

import java.util.ArrayList;

public class Counter {

    public static int density = 7;// average #neighbors
    public static int L = 20; //#length
    public static double A = (1.0) * L * L; // size of the graph
    public static double r = 2; //fixed radius
    public static int N; //#nodes
    public static int H = (int)((Math.sqrt(2) * L)/2); //max hops
    public static int K = 5; //#signals

    public static void main(String[] args) {
        int N = (int) Math.min(computeN(A, L, density, r), A);
        int total = 0;
        for (int x = 1; x <= 10;x++) {
            System.out.println("=======trial" + x + "=========");

            NodeList nodeList = new NodeList(N, L, r);

            ArrayList<String> signals = new ArrayList<>();
            generateSignals(signals, K);

            RmaxGreedy rmaxGreedy = new RmaxGreedy(nodeList.getList(), L, r, H);
            rmaxGreedy.setSignals(signals);

            total += rmaxGreedy.runMultiple();
        }
        System.out.println("============================");
        System.out.println("N: " + N);
        System.out.println("Total: " + total);
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
