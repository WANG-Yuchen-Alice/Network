package main.java.logic;

import main.java.SensorNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/* In DstributedRatio, there is no central agent that sorts and controls the nodes. Each node sends freely in each round,
by following specific rules.
The information available includes:
- distance to another node
- feed back from another node after each round of sending (in deciding the success rate)
- #senders in the neighborhood

Key rules to follow:
- for all senders, the sending power is decided by the level of uniqueness: r = rmax * (#receivers / #neighbors) = rmax *
((#neighbors - #senders) / #neighbors). The more competitors there are in the neighborhood, the smaller the power range
- capture effect in deciding for the successful senders [the strongest wins]
- R (success) -> S: 2
- R (fail) -> S (if non-empty pool); R (empty pool)
- S (fail) -> p(S) = 1/#failed senders in the previous round: 1
- S (success) -> R: 0
 */

//TODO: combined signals -> Do nodes memorize?

public class DistributedRatio {

    public int N;
    public int L;
    public double rmax;
    public ArrayList<SensorNode> nodeList;
    public ArrayList<ArrayList<Integer>> bigList;
    public ArrayList<String> signals;
    public HashMap<String, Integer> tagCounter; //tag - #receivers
    public HashSet<String> doneTagSet;

    public int maxHop;
    public int doneTags;
    public int round;
    //public int[] board; //dynamically updated in each round, SENDERS 2, RECEIVERS 0, JUST ReCEIVED 1; by default all 0

    public DistributedRatio(ArrayList<SensorNode> nodeList, ArrayList<String> signals, int L, double rmax, int H) {
        this.N = nodeList.size();
        this.L = L;
        this.rmax = rmax;
        this.nodeList = nodeList;
        this.bigList = new ArrayList<>();
        this.signals = signals;
        this.tagCounter = new HashMap<>();
        this.doneTagSet = new HashSet<>();

        this.maxHop = H; //average
        this.doneTags =  0;
        this.round = 0;
        //this.board = new int[this.N];
    }

    public void run() {
        System.out.println("max hop: " + this.maxHop);
        initializeBigList();
        showBigList();
        ArrayList<Integer> oriSenders = prepareOriSenders();
        ArrayList<SensorNode> oriNodes = idToNodes(oriSenders);
        process(oriNodes);
    }

    public void process(ArrayList<SensorNode> senders) {
        if ((this.doneTags >= signals.size() && signals.size() != 0) || this.round >= maxHop) {
            if(this.doneTags >= signals.size() && signals.size() != 0) {
                System.out.println("Everything done");
            } else {
                System.out.println(round);
            }
            reportResults();
            return;
        }
        //This is round n starting from 1, till maxHop
        this.round ++;

        for (int i = 0; i < senders.size(); i++) {
            SensorNode thisSender = senders.get(i);
            thisSender.updateR(this.nodeList);
        }

        //if updated r is too small, convert the sender to a receiver
        //senders = denyWeakSenders(senders);

        for (int i = 0; i < senders.size(); i++) {
            //reach out to the targets and apply to be added to the competitor pool
            senders.get(i).reachOut();
        }

        for (int i = 0; i < this.nodeList.size(); i++) {
            SensorNode thisReceiver = this.nodeList.get(i);
            thisReceiver.chooseSender();
        }

        senders = prepareSenderForNextRound_distributed();
        process(senders);
    }

    //================================Initialize BigList=============================
    public void initializeBigList() {
        for (int i = 0; i < N; i++) {
            this.nodeList.get(i).initializeTargets(this.nodeList, this.rmax);
            this.bigList.add(this.nodeList.get(i).getTargets());
        }
    }

    public void showBigList() {
        for (int i = 0; i < this.bigList.size(); i++) {
            System.out.print(i + ": ");
            ArrayList<Integer> list = this.nodeList.get(i).getTargets();
            for (int j = 0; j < list.size(); j++) {
                System.out.print(list.get(j) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    //================================Prepare Original Senders=============================
    //select #signals original senders, set their status to be 2 (all others are 0), set their #tags to be 1
    public ArrayList<Integer> prepareOriSenders() {
        Random random = new Random();
        //prepare a list of unique original senders, each carrying a signal
        HashSet<Integer> oriSenders = new HashSet<>();
        // prepare the original senders
        for(int i = 0; i < this.signals.size(); i++) {
            String thisTag = signals.get(i);

            int thisIndex = random.nextInt(N);
            while(oriSenders.contains(thisIndex)) {
                thisIndex = random.nextInt(N);
            }
            oriSenders.add(thisIndex);
            tagCounter.put(thisTag, 1);

            this.nodeList.get(thisIndex).addSignal(thisTag); //all original senders "has known" the corresponding tag
            this.nodeList.get(thisIndex).setCarriedSig(thisTag);
            this.nodeList.get(thisIndex).setStatus(2); //make sure the original ones are senders
        }
        return new ArrayList<>(oriSenders);
    }

    public ArrayList<SensorNode> idToNodes(ArrayList<Integer> ids) {
        ArrayList<SensorNode> nodes = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            nodes.add(this.nodeList.get(ids.get(i)));
        }
        return nodes;
    }

    //================================Adjust Sender Hierarchy=============================
    //after the currently best sender sends, receivers altered to 1, so the rest senders' num might change
    public void updateSenderHierarchy(ArrayList<SensorNode> senders) {
        for (int i = 0; i < senders.size(); i++) {
            senders.get(i).updateNum(this.nodeList, doneTagSet);
        }
    }

    //update nodes' status: 2 -> 0; 1 -> 2; 0 -> 2 (if got at least one signal)
    //return a list of senders (of status 2) as senders for the next round
    public ArrayList<SensorNode> prepareSenderForNextRound() { //TODO: continuous failure should lead to freeze
        ArrayList<SensorNode> senders = new ArrayList<>();
        for (int i = 0; i < this.N; i++) {
            SensorNode thisNode = this.nodeList.get(i);
            if (thisNode.status == 2) { //SEND in this round, RECEIVE in the next round
                thisNode.setStatus(0);
            } else if (thisNode.status == 1) { //successful RECEIVE in this round, SEND in the next
                thisNode.setStatus(2);
                senders.add(thisNode);
            } else { //0 in this round, unsuccessful RECEIVER, check if got signal yet
                if (thisNode.signals_set.size() > 0) {
                    thisNode.setStatus(2);
                    senders.add(thisNode);
                } else {
                    thisNode.setStatus(0);
                }
            }
        }
        return senders;
    }

    //================================Report Results=============================
    public void reportResults() {
        System.out.println("==================Results=================");
        for (int i = 0; i < this.signals.size(); i++) {
            String sig = this.signals.get(i);
            int outcome = this.tagCounter.get(this.signals.get(i));
            System.out.println(sig + " " + outcome + " " + (outcome * 100.0 / this.N) + "%");
        }
    }

    //================================Tools=============================
    public void printList(ArrayList<SensorNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println(nodes.get(i).getId());
        }
    }


}
