package main.java.logic;

import main.java.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class StrongSigSends {

    public int N;
    public int L;
    public ArrayList<Node> nodeList;
    public ArrayList<String> signals;
    public HashMap<String, Integer> tagCounter; //tag - #receivers
    public HashMap<String, ArrayList<Integer>> receiverMap; //tag - its carrier
    public HashMap<String, HashSet<Integer>> receiverSetMap; //tag - its carrier in set
    public HashMap<String, Boolean> tagDone; //tag - whether it is done
    public int maxHop;
    public int doneTags;
    public double powerCount;

    public StrongSigSends(ArrayList<Node> nodeList, int L) {
        this.N = nodeList.size();
        this.L = L;
        this.nodeList = nodeList;
        signals = new ArrayList<>();
        tagCounter = new HashMap<>();
        receiverMap = new HashMap<>();
        receiverSetMap = new HashMap<>();
        tagDone = new HashMap<>();
        this.maxHop = 2 * (int)Math.sqrt(L); //average
        this.doneTags = 0;
        this.powerCount = 0.0;
    }

    public void run() {
        Random random = new Random();
        int startIndex = random.nextInt(N);

    }


}
