package main.java;

import java.util.Random;

/**
 * A position graph marks the physical position of all the nodes.
 * 0 / 1 represents whether there is a node in tha specific position.
 */
public class PositionGraph {
    public int N;
    public int[][] positionGraph;

    public PositionGraph(int[][] positionGraph) {
        this.N = positionGraph.length;
        this.positionGraph = positionGraph;
    }

    public void displayPositionGraph() {
        for (int i = 0; i < this.N * 2 - 1; i++) {
            System.out.print("=");
        }
        System.out.println();
        for (int i = 0; i < this.N; i++) {
            for (int j = 0; j < this.N; j++) {
                if (this.positionGraph[i][j] != -1) {
                    System.out.print("* ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
        for (int i = 0; i < this.N * 2 - 1; i++) {
            System.out.print("=");
        }
        System.out.println();
        for (int i = 0; i < this.N * 2 - 1; i++) {
            System.out.print("=");
        }
        System.out.println();
        for (int i = 0; i < this.N; i++) {
            for (int j = 0; j < this.N; j++) {
                if (this.positionGraph[i][j] != -1) {
                    System.out.print(this.positionGraph[i][j] + " ");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
        for (int i = 0; i < this.N * 2 - 1; i++) {
            System.out.print("=");
        }
        System.out.println();

    }

}
