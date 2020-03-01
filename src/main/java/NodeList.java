package main.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class NodeList {

    public int N;
    public ArrayList<Node> nodeList;
    public HashSet<String> nodeSet_String;

    public NodeList(int N) {
        this.N = N;
        this.nodeList = new ArrayList<>();
        this.nodeSet_String = new HashSet<>();
        generateNodeList();
    }

    public ArrayList<Node> getList() {
        return this.nodeList;
    }

    private void generateNodeList() {
        this.nodeList.clear();
        N = this.N;
        Random random = new Random();
        int counter = 0;
        while (counter < N) {
            int this_i = random.nextInt(N);
            int this_j = random.nextInt(N);
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

    public PositionGraph nodeListToPositionGraph() {
        int[][] positionGraph = new int[N][N];
        for (int i = 0; i < this.nodeList.size(); i++) {
            positionGraph[this.nodeList.get(i).getI()][this.nodeList.get(i).getJ()] = 1;
        }
        return new PositionGraph(positionGraph);
    }
}
