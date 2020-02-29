package main.java;

import java.util.Random;

/**
 * A position graph marks the physical position of all the nodes.
 * 0 / 1 represents whether there is a node in tha specific position.
 */
public class PositionGraph {
    public int N;
    public int[][] positionGraph;

    public PositionGraph(int N) {
        this.N = N;
        this.positionGraph = new int[N][N];
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
