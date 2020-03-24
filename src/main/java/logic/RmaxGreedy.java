package main.java.logic;

import main.java.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Conditions: fixed position; one starting point; message tags counter
 * Goal: 100% received
 * Nest node: farest from the new receivers
 */
public class RmaxGreedy {
    public int N;
    public int L;
    public ArrayList<Node> nodeList;
    public double rmax;
    public ArrayList<String> signals;
    public HashMap<String, Integer> tagCounter; //tag - #receivers
    public HashMap<String, ArrayList<Integer>> receiverMap; //tag - its carrier
    public HashMap<String, HashSet<Integer>> receiverSetMap; //tag - its carrier in set
    public HashMap<String, Boolean> tagDone; //tag - whether it is done
    public int maxHop;
    public int doneTags;
    public int goal;

    public RmaxGreedy(ArrayList<Node> nodeList, int L, double r) {
        this.N = nodeList.size();
        this.L = L;
        this.nodeList = nodeList;
        this.rmax = r;

        signals = new ArrayList<>();
        tagCounter = new HashMap<>();
        receiverMap = new HashMap<>();
        receiverSetMap = new HashMap<>();
        tagDone = new HashMap<>();
        this.maxHop = 2* (int)(Math.sqrt(2) * L); //average
        this.doneTags = 0;
    }

    public void initStorage() {
        this.tagCounter.clear();
        this.receiverMap.clear();
        this.receiverSetMap.clear();
        this.tagDone.clear();
    }

    public void setSignals(ArrayList<String> signals) {
        this.signals.clear();
        this.tagCounter.clear();
        for (int i = 0; i < signals.size(); i++) {
            this.signals.add(signals.get(i));
            this.tagCounter.put(signals.get(i), 0);
            this.tagDone.put(signals.get(i), false);
        }
    }

    // WARNING: choose between single and multiple
    // start with one node, each time choose two more senders
    public void runSingle() {
        tagCounter.clear();
        System.out.println("RmaxGreedy_single is running");
        System.out.println("max hops: " + this.maxHop);
        System.out.println("goal: " + N);

        Random random = new Random();
        int startIndex = random.nextInt(N);

        //There is only one message
        String tag = "try1";
        tagCounter.put(tag, 1);
        receiverMap.put(tag, new ArrayList<Integer>());
        receiverSetMap.put(tag, new HashSet<Integer>());
        this.nodeList.get(startIndex).addTag(tag);

        int hops = 1;

        ArrayList<Integer> receivers = getNewReceivers(startIndex, this.nodeList, tag, this.rmax);
        addReceiversToMap(receivers, tag);
        markNewReceivers(receivers, tag);

        findNextAndProcess(tag, hops);

        System.out.println("Done. " + " receivers: " + tagCounter.get(tag) + "\n hops: " + hops);

    }

    public void addReceiversToMap(ArrayList<Integer> receivers, String tag) {
        for (int i = 0; i < receivers.size(); i++) {
            int thisId = receivers.get(i);
            if (!this.receiverSetMap.get(tag).contains(thisId)) {
                this.receiverSetMap.get(tag).add(thisId);
                this.receiverMap.get(tag).add(thisId);
            }
        }
    }

    public void findNextAndProcess(String tag, int hops) {
        if (tagCounter.get(tag) > (int)(0.9 * this.N)) {
            System.out.println("Success");
            return;
        }
        if (hops >= this.maxHop) {
            System.out.println("Exceed maxHop: " + hops);
            return;
        }
        hops++;
        int first = chooseGreedyBest(tag);
        if (first == -1) {
            return;
        }
        System.out.println("#1: " + this.nodeList.get(first).getId());
        ArrayList<Integer> firstReceivers = getNewReceivers(first, this.nodeList, tag, this.rmax);
        markNewReceivers(firstReceivers, tag);
        addReceiversToMap(firstReceivers, tag);

        findNextAndProcess(tag, hops);
    }

    // for each tag each time choose one best next; messages are sent together at time stamp 0;
    // each node can only be sender / receiver
    public void runMultiple() {
        this.initStorage();
        System.out.println("RmaxGreedy_multiple is running");
        System.out.println("max hops: " + this.maxHop);
        this.goal = N;
        System.out.println("goal: " + goal);
        HashSet<Integer> oriSenders = new HashSet<>();
        int num = this.signals.size();
        Random random = new Random();

        // prepare the original senders
        for(int i = 0; i < num; i++) {
            String thisTag = signals.get(i);
            receiverMap.put(thisTag, new ArrayList<Integer>());
            receiverSetMap.put(thisTag, new HashSet<Integer>());
            int thisIndex = random.nextInt(N);
            while(oriSenders.contains(thisIndex)) {
                thisIndex = random.nextInt(N);
            }
            oriSenders.add(thisIndex);
            receiverMap.get(thisTag).add(thisIndex);
            receiverSetMap.get(thisTag).add(thisIndex);
            tagCounter.put(thisTag, 1);
            tagDone.put(thisTag, false);
            this.nodeList.get(thisIndex).addTag(thisTag); //all original senders "has known" the corresponding tag
        }

        displayReceiverMap();

        findNextGroupAndProcess(1);

        System.out.println("Done. ");
        reportTagCounter();

    }

    // for each tag each time everyone send next round; messages are sent together at time stamp 0;
    // each node can only be sender / receiver
    public void runMultipleFullSender() {
        this.initStorage();
        System.out.println("RmaxGreedy_multiple_fullSender is running");
        System.out.println("max hops: " + this.maxHop);

        System.out.println("goal: " + this.goal);

        HashSet<Integer> oriSenders = new HashSet<>();
        int num = this.signals.size();
        Random random = new Random();

        // prepare the original senders
        for(int i = 0; i < num; i++) {
            String thisTag = signals.get(i);
            receiverMap.put(thisTag, new ArrayList<Integer>());
            receiverSetMap.put(thisTag, new HashSet<Integer>());
            int thisIndex = random.nextInt(N);
            while(oriSenders.contains(thisIndex)) {
                thisIndex = random.nextInt(N);
            }
            oriSenders.add(thisIndex);
            receiverMap.get(thisTag).add(thisIndex);
            receiverSetMap.get(thisTag).add(thisIndex);
            tagCounter.put(thisTag, 1);
            tagDone.put(thisTag, false);
            this.nodeList.get(thisIndex).addTag(thisTag); //all original senders "has known" the corresponding tag
        }

        findNextGroupAndProcess_full(1);

        System.out.println("Done. ");
        reportTagCounter();

    }

    public void findNextGroupAndProcess(int hops) {
        if (hops > this.maxHop) {
            System.out.println("exceed max hops.");
            return;
        }

        int num = this.signals.size();
        if (this.doneTags >= num) {
            System.out.println("all tags are done.");
            return;
        }

        //receiverPool and receiverSer are for this round only
        ArrayList<Integer> receiverPool = new ArrayList<>();
        HashSet<Integer> receiverSet = new HashSet<>();
        for (int i = 0; i < num; i++) {
            String thisTag = signals.get(i);
            if (this.tagDone.get(thisTag)) {
                continue;
            }
            //if done for this tag, mark as true and proceed to the next tag
            if (receiverSetMap.get(thisTag).size() >= goal) {
                tagDone.put(thisTag, true);
                this.doneTags++;
                continue;
            }
            //if not done yet, from all available receivers of this tag, choose one with the most number of new
            //receivers, store the list of new receivers into the temp arrayList and add the sender-tag pair into the
            //competitor list of the receiving node
            //After all tags are processed, decide for the receivers for each tag
            int nextSender = chooseGreedyBest(thisTag);
            if (nextSender == -1) {
                tagDone.put(thisTag, true);
                this.doneTags++;
                continue;
            }
            //update the receiverPool, and let the nodes in the receiverPool know its competing senders
            addToPoolAndCompetitor(receiverPool, receiverSet, getNewReceivers(nextSender, this.nodeList, thisTag,
                    this.rmax), nextSender, thisTag);

        }
        //go through the receiverPool and decide for the signal for each pending receivers
        //each receiver confirms will choose one sender and take his signal
        //update tagCounter, receieverMap, receiverSetMap as well
        processReceiverPool(receiverPool);
        hops++;
        displayReceiverMap();
        System.out.println("next hop: " + hops);
        findNextGroupAndProcess(hops);
    }

    /**
     * Multiple senders. In each round every one sends. Receivers decide by possibility.
     * @param hops #hops for the current round (starts from 1)
     */
    public void findNextGroupAndProcess_full(int hops) {


        if (hops > this.maxHop) {
            System.out.println("exceed max hops.");
            return;
        }

        int num = this.signals.size(); //# signals
        if (this.doneTags >= num) {
            System.out.println("all tags are done.");
            return;
        }
        //receiverPool and receiverSer are for this round only
        ArrayList<Integer> receiverPool = new ArrayList<>();
        HashSet<Integer> receiverSet = new HashSet<>();
        for (int i = 0; i < num; i++) {
            String thisTag = signals.get(i);
            //if marked done before, continue
            if (this.tagDone.get(thisTag)) {
                continue;
            }
            //if done for this tag, mark as true and proceed to the next tag
            if (receiverSetMap.get(thisTag).size() >= this.goal) {
                tagDone.put(thisTag, true);
                this.doneTags++;
                continue;
            }
            //if not done yet, all available receivers in the queue send
            // store the list of list of new receivers into the temp arrayList and add the sender-tag pair into the
            //competitor list of the receiving node
            //After all tags are processed, decide for the receivers for each tag
            for (int k = 0; k < this.receiverMap.get(thisTag).size(); k++) {
                int thisSender = this.receiverMap.get(thisTag).get(k);
                ArrayList<Integer> tempNewReceiver = getNewReceivers(thisSender, this.nodeList, thisTag, this.rmax);
                addToPoolAndCompetitor(receiverPool, receiverSet, tempNewReceiver, thisSender, thisTag);
            }
        }
        //go through the receiverPool and decide for the signal for each pending receivers
        //each receiver confirms will choose one sender and take his signal
        //update tagCounter, receieverMap, receiverSetMap as well
        processReceiverPool(receiverPool);
        hops++;
        System.out.println("next hop: " + hops);
        findNextGroupAndProcess_full(hops);
    }

    public int chooseGreedyBest(String tag) {
        int maxReceivers = -1;
        int index = -1;
        ArrayList<Integer> temp;
        for (int i = 0; i < this.receiverMap.get(tag).size(); i++) {
            temp = getNewReceivers(this.receiverMap.get(tag).get(i), this.nodeList, tag, this.rmax);
            int tempSize = temp.size();
            if (tempSize > maxReceivers) {
                index = i;
                maxReceivers = tempSize;
            }
        }
        if (index == -1 || maxReceivers == -1) {
            return -1;
        } else {
            return this.receiverMap.get(tag).get(index);
        }
    }

    // Trim original list to get only a list of new receivers, not decided (hence marked yet)
    public ArrayList<Integer> getNewReceivers(int index, ArrayList<Node> nodeList, String tag, double r) {
        ArrayList<Integer> temp = this.nodeList.get(index).nodesInRange(nodeList, r);
        ArrayList<Integer> receivers = new ArrayList<>();
        int i = 0;
        while(i < temp.size()) {
            if (index != temp.get(i) && !this.nodeList.get(temp.get(i)).isKnown(tag)) {
                receivers.add(temp.get(i));
            }
            i++;
        }
        System.out.println("for node " + this.nodeList.get(index).getId() + " the number of new receivers: " + receivers.size());
        for (int j = 0; j < receivers.size(); j++) {
            System.out.print(this.nodeList.get(receivers.get(j)).getId() + " ");
        }
        System.out.println();
        return receivers;
    }

    // after the decision, mark the receiversLis
    public void markNewReceivers(ArrayList<Integer> receivers, String tag) {
        int cnt = receivers.size();
        for (int i = 0; i < receivers.size(); i++) {
            this.nodeList.get(receivers.get(i)).addTag(tag);
        }
        System.out.println("Mark new: " + cnt);
        int prevCnt = tagCounter.get(tag);
        int newCnt = prevCnt + cnt;
        tagCounter.put(tag, newCnt);
    }

    public void addToPoolAndCompetitor(ArrayList<Integer> receiverPool, HashSet<Integer> receiverSet, ArrayList<Integer> receivers, int sender, String tag) {
        for (int i = 0; i < receivers.size(); i++) {
            if (!receiverSet.contains(receivers.get(i))) {
                receiverSet.add(receivers.get(i));
                receiverPool.add(receivers.get(i));
            }
            this.nodeList.get(receivers.get(i)).addCompetitor(sender, tag, this.nodeList.get(sender));
        }
    }

    public void processReceiverPool(ArrayList<Integer> receiverPool) {
         for (int i = 0; i < receiverPool.size(); i++) {
             int thisNode = receiverPool.get(i); //this receiver
             System.out.println("processing: " + thisNode);
             String tag = this.nodeList.get(thisNode).chooseTag();
             this.nodeList.get(thisNode).clearCompetitor();
             if (tag.equals("")) {
                 System.out.println("no tag chosen");
                 continue;
             } //this receiver in the pool did not choose any sender due to clashes
             this.receiverMap.get(tag).add(thisNode); //add this receiver to the corresponding receiverMap under this tag
             if (!this.receiverSetMap.get(tag).contains(thisNode)) {
                 this.tagCounter.put(tag, (this.tagCounter.get(tag) + 1));
             }
             this.receiverSetMap.get(tag).add(thisNode);

         }
    }

    public void reportTagCounter() {
        System.out.println("Tags and their receivers: ");
        for (int i = 0; i < this.signals.size(); i++) {
            String sig = signals.get(i);
            System.out.println("signal: " + sig + " #receivers: " + this.receiverSetMap.get(sig).size());
            for (int j = 0; j < receiverMap.get(sig).size(); j++) {
                System.out.print(receiverMap.get(sig).get(j) + " ");
            }
            System.out.println();

        }
    }

    public void displayReceiverMap() {
        for (int i = 0; i < signals.size(); i++) {
            String sig = signals.get(i);
            System.out.println(sig + ": ");
            for (int j = 0; j < receiverMap.get(sig).size(); j++){
                System.out.print(receiverMap.get(sig).get(j) + " ");
            }
            System.out.println();
        }
    }
}
