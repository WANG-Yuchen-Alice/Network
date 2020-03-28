package main.java;

import java.util.*;

public class NodeList {

    public int N;
    public int L;
    public double r;
    public ArrayList<Node> nodeList;
    public HashSet<String> nodeSet_String;
    public HashMap<Integer, ArrayList<Integer>> nodeMap; //create before each check connectivity
    public HashSet<Integer> mainIsland; //update in each check connectivity

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
        this.mainIsland = new HashSet<Integer>();
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
        //displayNodeMap();
        int cnt = checkConnectivity();
        System.out.println("initial N nodes cnt: " + cnt);

        if (cnt == N) {
            return;
        }
        counter = N; //the extra nodes' id start from N
        // keep generating new nodes until at least N nodes are connected
        while(cnt < N) { //the largest island should be N, otherwise there are unconnected nodes
            //System.out.println("not enough: " + cnt);
            //generate  a new node
            int this_i = random.nextInt(L);
            int this_j = random.nextInt(L);
            Node thisNode = new Node (this_i, this_j, counter);
            String nodeStr = thisNode.toString();
            while (this.nodeSet_String.contains(nodeStr)) {
                this_i = random.nextInt(L);
                this_j = random.nextInt(L);
                thisNode = new Node (this_i, this_j, counter);
                nodeStr = thisNode.toString();
            }
            this.nodeList.add(thisNode);
            this.nodeSet_String.add(nodeStr);
            //System.out.println("add: " + thisNode.getId());
            toHashMap();
            cnt = checkConnectivity();
            //System.out.println("after adding, cnt: " + cnt);
            counter++;
        }

        System.out.println("Now finally connected, padding done, total: " + this.nodeList.size());
        int extra = this.nodeList.size() - N; //after the previous step mainIsland is at least N

        //System.out.println("extra: " + extra);
        int k = 0;
        while(k < this.nodeList.size()) {
            Node pendingNode = this.nodeList.get(k);
            //System.out.println("pending: " + pendingNode.getId());
            this.nodeList.remove(k);
            toHashMap();
            if (checkConnectivity() >= N) {
                //System.out.println("this can be removed");
                extra--;
                this.nodeSet_String.remove(pendingNode.toString());
            } else {
                //System.out.println("this should not be removed");
                this.nodeList.add(k, pendingNode);
                k++;
            }
            if(extra == 0) {
                break;
            }

        }
        System.out.println("generation done. size: " + this.nodeList.size() + " cnt: " + this.mainIsland.size());

        convertOrder();

    }

    // nodeMap stores indices in nodeList
    public void toHashMap() {
        this.nodeMap.clear();
        for (int i = 0; i < this.nodeList.size(); i++) {
            Node thisNode = nodeList.get(i);
            this.nodeMap.put(i, thisNode.nodesInRange(this.nodeList, this.r));
        }
    }

    public int checkConnectivity() {
        int num = this.nodeMap.size();
        int ans = 0;
        int[] visited = new int[num];
        //initialize the visited to -1
        for (int i = 0; i < visited.length; i++) {
            visited[i] = -1;
        }
        int untouched = num;
        int marker = 1;
        int cnt = 0;
        int max = 0;
        int initial = 0;
        while(untouched > 0) {//there are still untouched nodes, need more rounds of bfs
            cnt = 0;
            initial = 0;
            LinkedList<Integer> queue = new LinkedList<Integer>();
            this.mainIsland.clear();
            for (int j = 0; j < N; j++) {
                if (visited[j] == -1) {
                    initial = j;
                    break;
                }
            }
            visited[initial] = marker;
            untouched--;
            queue.add(initial);
            mainIsland.add(this.nodeList.get(initial).getId());
            int s;
            while(queue.size() != 0) {
                s = queue.poll();
                Iterator<Integer> it = nodeMap.get(s).listIterator();
                while (it.hasNext())
                {
                    int n = it.next();
                    if (visited[n] == -1) //not visited before
                    {
                        visited[n] = marker;
                        untouched--;
                        queue.add(n);
                        mainIsland.add(this.nodeList.get(n).getId());
                    }
                }
            }

            for (int k = 0; k < visited.length; k++) {
                if (visited[k] == marker) {
                    //System.out.print(this.nodeList.get(k).getId() + " ");
                    cnt++;
                }
            }
            //System.out.println("cnt: " + cnt);

            max = Math.max(max, cnt);
            if (max >= N / 2) {
                ans = max;
                return ans; //when return, the mainIsland stores all elements in the mainIsland of the current graph
            } else {
                marker++;
                mainIsland.clear();
                continue;
            }
        }
        ans = max;
        return ans;
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

    // converts the generated connected node list to the natural id and indices
    public void convertOrder() {
        ArrayList<Node> newNodeList = new ArrayList<>();
        int num = this.nodeList.size();
        for (int i = 0; i < num; i++) {
            Node curNode = this.nodeList.get(i);
            newNodeList.add(new Node(curNode.getI(), curNode.getJ(), i));
        }
        this.nodeList = newNodeList;
    }

    public void displayNodeMap() {
        System.out.println("show the nodeMap");
        for (int i = 0; i < N; i++) {
            System.out.print(i + ": ");
            for (int j = 0; j < nodeMap.get(i).size(); j++) {
                System.out.print(nodeMap.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }
}
