package main.java.logic;

import main.java.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Conditions: fixed position; one starting point; message tags counter
 * Goal: 100% received
 * Next node: farest from the new receivers
 */
public class RmaxGreedy {
    public int N;
    public int L;
    public ArrayList<Node> nodeList;
    public double rmax;
    public ArrayList<String> signals;
    public ArrayList<ArrayList<Integer>> bigList;
    public HashMap<String, Integer> tagCounter; //tag - #receivers
    public HashMap<String, ArrayList<Integer>> receiverMap; //tag - its carrier
    public HashMap<String, HashSet<Integer>> receiverSetMap; //tag - its carrier in set
    public HashMap<String, Boolean> tagDone; //tag - whether it is done
    public int maxHop;
    public int doneTags;
    public int goal;

    public RmaxGreedy(ArrayList<Node> nodeList, int L, double r, int H) {
        this.N = nodeList.size();
        this.L = L;
        this.nodeList = nodeList;
        this.rmax = r;

        signals = new ArrayList<>();
        bigList = new ArrayList<>();
        tagCounter = new HashMap<>();
        receiverMap = new HashMap<>();
        receiverSetMap = new HashMap<>();
        tagDone = new HashMap<>();
        this.maxHop = H; //average
        this.doneTags = 0;
        this.goal = N;
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

    public void addReceiversToMap(ArrayList<Integer> receivers, String tag) {
        for (int i = 0; i < receivers.size(); i++) {
            int thisId = receivers.get(i);
            if (!this.receiverSetMap.get(tag).contains(thisId)) {
                this.receiverSetMap.get(tag).add(thisId);
                this.receiverMap.get(tag).add(thisId);
            }
        }
    }

    // for each tag each time choose one best next; messages are sent together at time stamp 0;
    // each node can only be sender / receiver
    public void run() {
        System.out.println("RmaxGreedy_multiple is running");
        this.goal = N;
        System.out.println("goal: " + goal);

        initStorage();
        initializeBigList();

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
            this.nodeList.get(thisIndex).setStatus(1); //make sure the original ones are senders
            this.nodeList.get(thisIndex).setCarriedTag(thisTag);
        }

        findNextGroupAndProcess(1, new ArrayList<>(oriSenders));
    }

    //============================================Initialize===================================
    public void initializeBigList() {
        for (int i = 0; i < N; i++) {
            this.bigList.add(this.nodeList.get(i).nodesInRange(this.nodeList, this.rmax));
            this.nodeList.get(i).setTargets(this.bigList.get(i));
        }
    }

    //update the carried signals and target receivers of each sender for the coming round
    public void updateCarriedTagAndReceivers() {
        for (int i = 0; i < this.nodeList.size(); i++) {
            //consider only senders
            if (this.nodeList.get(i).status != 1) {
                continue;
            }
            //after updates, each sender has refreshed carried signal and nextReceivers
            this.nodeList.get(i).updateCarriedTagAndReceivers(this.nodeList, this.rmax);
        }
    }

    //===========================================Recursion==================================
    public void findNextGroupAndProcess(int hops, ArrayList<Integer> senders) {
        int num = this.signals.size();
        if (this.doneTags >= num) {
            System.out.println("doneTags: " + doneTags);
            System.out.println(":D all tags are done.");
            return;
        }
        updateCarriedTagAndReceivers();
        if (hops > this.maxHop) {
            System.out.println("exceed max hops.");
            reportPerformance();
            return;
        }

        //receiverPool and receiverSet are for this round only
        ArrayList<Integer> receiverPool = new ArrayList<>();
        HashSet<Integer> receiverSet = new HashSet<>();
        for (int i = 0; i < num; i++) {
            String thisTag = signals.get(i);
            if (tagCounter.get(thisTag) >= N) {
                continue;
            }

            //from all available receivers of this tag, choose one with the most number of new receivers
            int nextSender = chooseGreedyBest(senders, thisTag);
            if (nextSender == -1) {
                System.out.println(thisTag + " fails to find itself a sender");
                continue;
            }
            //update the receiverPool, and let the nodes in the receiverPool know its competing senders
            addToPoolAndCompetitor(receiverPool, receiverSet, this.nodeList.get(nextSender).nextReceivers, nextSender, thisTag);
        }
        //go through the receiverPool and decide for the signal for each pending receivers
        processReceiverPool(receiverPool);
        ArrayList<Integer> nextSenders = prepareNextRoundSenders();
        hops++;
        System.out.println("next hop: " + hops);
        findNextGroupAndProcess(hops, nextSenders);
    }

    //choose the best in this sender list
    //condition: of status SEND, choose this tag
    public int chooseGreedyBest(ArrayList<Integer> senders, String tag) {
        int maxReceivers = -1;
        int index = -1;

        for (int i = 0; i < senders.size(); i++) {
            int senderId = senders.get(i);
            Node thisSender = this.nodeList.get(senderId);
            if (thisSender.carriedTag.equals(tag) && thisSender.nextReceivers.size() > maxReceivers) {
                maxReceivers = thisSender.nextReceivers.size();
                index = senderId;
            }
        }
        return index;
    }

    public void addToPoolAndCompetitor(ArrayList<Integer> receiverPool, HashSet<Integer> receiverSet, ArrayList<Integer> receivers, int sender, String tag) {
        for (int i = 0; i < receivers.size(); i++) {
            int receiverId = receivers.get(i);
            if (!receiverSet.contains(receiverId)) {
                receiverSet.add(receiverId);
                receiverPool.add(receiverId);
            }
            this.nodeList.get(receiverId).addCompetitor(sender, tag, this.nodeList.get(sender));
        }
    }

    //===================================================Decision making: Capture Effect============================================
    //let each receiver choose a successful sender and update the tag
    public void processReceiverPool(ArrayList<Integer> receiverPool) {
        for (int i = 0; i < receiverPool.size(); i++) {
            int thisNode = receiverPool.get(i); //this receiver
            int chosenSender = this.nodeList.get(thisNode).chooseSender();

            if (chosenSender == -1) {
                System.out.println(thisNode + " chooses no one");
            } else {
                String tag = this.nodeList.get(chosenSender).carriedTag;
                System.out.println(chosenSender + " -> " + thisNode + " [" + tag + "]");
                this.nodeList.get(thisNode).addTag(tag);
                this.nodeList.get(thisNode).clearCompetitor();
                this.tagCounter.put(tag, (this.tagCounter.get(tag) + 1));
                if (tagCounter.get(tag) == N) {
                    doneTags++;
                }
            }
        }
    }

    //==================================================Prepare for next round==================================
    public ArrayList<Integer> prepareNextRoundSenders() {
        ArrayList<Integer> senders = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            Node thisNode = this.nodeList.get(i);
            if (thisNode.status == 1) {
                thisNode.setStatus(0);
            } else {
                if (thisNode.tags.size() != 0) {
                    thisNode.setStatus(1);
                    senders.add(i);
                }
            }
        }
        return senders;
    }

    //===========================================Report==================================
    public void reportPerformance() {
        for (int i = 0; i < signals.size(); i++) {
            String sig = signals.get(i);
            System.out.println(sig + "  " + this.tagCounter.get(sig)* 100.0 / this.N);
        }
    }

    // Trim original list to get only a list of new receivers, not decided (hence marked yet)
    public ArrayList<Integer> getNewReceivers(int index, ArrayList<Node> nodeList, String tag, double r) {
        ArrayList<Integer> temp = this.nodeList.get(index).nodesInRange(nodeList, r);
        ArrayList<Integer> receivers = new ArrayList<>();
        int i = 0;
        while(i < temp.size()) {
            if (index != temp.get(i) && !this.nodeList.get(temp.get(i)).isKnown(tag) && this.nodeList.get(temp.get(i)).status == 0) {
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

    /**
     * Returns average number of receivers.
     * @return
     */
    public int performance() {
        int numTags = signals.size();
        int total = 0;
        for (int j = 0; j < signals.size(); j++) {
            total += tagCounter.get(signals.get(j));
        }
        return (int)(total/numTags);
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
