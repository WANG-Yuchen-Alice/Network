package main.java;

import java.util.Random;

public class Graph {
    public int N;
    public int[][] positionGraph;

    public Graph (int N) {
        this.N = N;
        generateGraph(this.N);
    }

    public void generateGraph(int N) {
        Random random = new Random();
        int counter = 0;
        while (counter < N) {
            int this_i = random.nextInt(N);
            int this_j = random.nextInt(N);
            if (this.positionGraph[this_i][this_j] != 1) {
                counter++;
                this.positionGraph[this_i][this_j] = 1;
            }
        }
    }
}
