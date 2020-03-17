package main.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class NodeList {

    public int N;
    public int L;
    public ArrayList<Node> nodeList;
    public HashSet<String> nodeSet_String;

    public NodeList(int N, int L) {
        this.N = N;
        this.L = L;
        this.nodeList = new ArrayList<>();
        this.nodeSet_String = new HashSet<>();
        generateNodeList();
    }

    public ArrayList<Node> getList() {
        return this.nodeList;
    }

    private void generateNodeList() {
        this.nodeList.clear();
        Random random = new Random();
        int counter = 0;
        while (counter < N) {
            int this_i = random.nextInt(L);
            int this_j = random.nextInt(L);
            Node thisNode = new Node (this_i, this_j, counter);
            String nodeStr = thisNode.toString();
            if (!this.nodeSet_String.contains(nodeStr)) {
                this.nodeList.add(thisNode);
                this.nodeSet_String.add(nodeStr);
                counter++;
            }
        }
    }

    public Node getNode(int index) {
        return this.nodeList.get(index);
    }

    public int getSize() {
        return this.nodeList.size();
    }

    public int getN() {
        return this.N;
    }

    public int getL() {
        return this.L;
    }

    public PositionGraph nodeListToPositionGraph(int L) {
        int[][] positionGraph = new int[L][L];
        for (int i = 0; i < this.nodeList.size(); i++) {
            positionGraph[this.nodeList.get(i).getI()][this.nodeList.get(i).getJ()] = 1;
        }
        return new PositionGraph(positionGraph);
    }

    //show the densitty of the graph
    public int averageNeighbors(double r) {
        int sum = 0;
        for (int i = 0; i < N; i++) {
            sum += this.nodeList.get(i).numNodesInRange(this.nodeList, r);
        }
        return sum / N;
    }
}
