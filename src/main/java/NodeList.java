package main.java;

import java.util.*;

public class NodeList {

    public int N;
    public int L;
    public double r;
    public ArrayList<Node> nodeList;
    public HashSet<String> nodeSet_String;
    public  HashMap<Integer, ArrayList<Integer>> nodeMap;

    public NodeList(int N, int L) {
        this.N = N;
        this.L = L;
        this.nodeList = new ArrayList<>();
        this.nodeSet_String = new HashSet<>();
        generateNodeList();
    }

    // overloaded constructor that makes the graph connected
    public NodeList(int N, int L, double r) {
        this.N = N;
        this.L = L;
        this.r = r;
        this.nodeList = new ArrayList<Node>();
        this.nodeSet_String = new HashSet<String>();
        this.nodeMap = new HashMap<Integer, ArrayList<Integer>>();
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
        for (int i = 0; i < N; i++) {
            Node thisNode = this.nodeList.get(i);
            while (thisNode.nodesInRange(this.nodeList, this.r).size() == 0) {
                this.nodeSet_String.remove(thisNode.toString());
                int newI = random.nextInt(L);
                int newJ = random.nextInt(L);
                int thisId = this.nodeList.get(i).getId();
                thisNode = new Node (newI, newJ, thisId);
                String nodeStr = thisNode.toString();
                if (!this.nodeSet_String.contains(nodeStr)) {
                    this.nodeList.set(i, thisNode);
                    this.nodeSet_String.add(nodeStr);
                }
            }
        }
        toHashMap();
        checkConnectivity();
    }

    public void toHashMap() {
        for (int i = 0; i < this.nodeList.size(); i++) {
            Node thisNode = nodeList.get(i);
            this.nodeMap.put(thisNode.getId(), thisNode.nodesInRange(this.nodeList, this.r));
        }
    }

    public void checkConnectivity() {
        boolean[] visited = new boolean[this.N];
        int cnt = 0;
        LinkedList<Integer> queue = new LinkedList<Integer>();
        visited[0] = true;
        queue.add(0);
        int s;
        while (queue.size() != 0)
        {
            s = queue.poll();
            Iterator<Integer> i = nodeMap.get(s).listIterator();
            while (i.hasNext())
            {
                int n = i.next();
                if (!visited[n])
                {
                    visited[n] = true;
                    queue.add(n);
                }
            }
        }
        for (int i = 0; i < visited.length; i++) {
            if (visited[i]) {
                cnt++;
            }
        }
        System.out.println("connected node: " + cnt);
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

    //show the density of the graph
    public int averageNeighbors(double r) {
        int sum = 0;
        for (int i = 0; i < N; i++) {
            sum += this.nodeList.get(i).numNodesInRange(this.nodeList, r);
        }
        return sum / N;
    }
}
