package main.java.logic;

import main.java.SensorNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/* In DistributedAlternative, there is no central agent that sorts and controls the nodes. Each node sends freely in each round,
by following specific rules. The number of senders are artificially controled by the algorithm. And senders vary their power
alternatively in each round. Generally, odd round allow larger power but fewer senders, and even rounds are the opposite.
The information available includes:
- distance to another node

Key rules to follow:
- for all senders, the sending power is decided by the level of uniqueness: r = rmax * (#receivers / #neighbors) = rmax *
((#neighbors - #senders) / #neighbors). The more competitors there are in the neighborhood, the smaller the power range
- capture effect in deciding for the successful senders, the more senders, the less chance to get through
- R (success) -> S
- S (success) -> R
- S (fail) R(fail) -> p
- status value:
S: 2; R: 0; just received in the current round: 1; just sent in the current round: 3
 */

public class DistributedAlternative {

    public int N;
    public int L;
    public double rmax;
    public ArrayList<SensorNode> nodeList;
    public ArrayList<ArrayList<Integer>> bigList;
    public ArrayList<String> signals;
    public HashMap<String, Integer> tagCounter; //tag - #receivers
    public HashMap<String, HashSet<Integer>> tagCounter_set; //tag - receiver set
    public HashSet<String> doneTagSet;

    public int maxHop;
    public int round;

    public double failedSenderThreshold;
    //public int[] board; //dynamically updated in each round, SENDERS 2, RECEIVERS 0, JUST ReCEIVED 1; by default all 0

    public double performance;

    public DistributedAlternative(ArrayList<SensorNode> nodeList, ArrayList<String> signals, int L, double rmax, int H, double threshold1) {
        this.N = nodeList.size();
        this.L = L;
        this.rmax = rmax;
        this.nodeList = nodeList;
        this.bigList = new ArrayList<>();
        this.signals = signals;
        this.tagCounter = new HashMap<>();
        this.tagCounter_set = new HashMap<>();
        this.doneTagSet = new HashSet<>();

        this.maxHop = H; //average
        this.round = 0;

        this.failedSenderThreshold = threshold1;
        //this.board = new int[this.N];

        this.performance = 0.0;
    }

    public void run() {
        System.out.println("max hop: " + this.maxHop);
        initializeBigList();
        showBigList();
        ArrayList<Integer> oriSenders = prepareOriSenders();
        ArrayList<SensorNode> oriNodes = idToNodes(oriSenders);
        process(oriNodes);
    }

    public double run_res() {
        System.out.println("max hop: " + this.maxHop);
        initializeBigList();
        showBigList();
        ArrayList<Integer> oriSenders = prepareOriSenders();
        ArrayList<SensorNode> oriNodes = idToNodes(oriSenders);
        process(oriNodes);
        return this.performance;
    }

    public void process(ArrayList<SensorNode> senders) {
        if (isDone() || this.round >= maxHop) {
            if(isDone()) {
                System.out.println("Everything done");
            } else {
                System.out.println(round);
            }
            reportResults();
            return;
        }
        //This is round n starting from 1, till maxHop
        this.round ++;
        System.out.println("round: " + round);

        for (int i = 0; i < senders.size(); i++) {
            SensorNode thisSender = senders.get(i);
            thisSender.updateR_alternative(this.nodeList, this.rmax, round);
        }

        senders = convertWeakSenderToReceiver(senders); //if a sender's radius is chosen to be 0, convert it to a receiver

        for (int i = 0; i < senders.size(); i++) {
            //reach out to the targets and apply to be added to the competitor pool
            senders.get(i).reachOut(this.nodeList);
        }

        for (int i = 0; i < this.nodeList.size(); i++) {
            SensorNode thisReceiver = this.nodeList.get(i);
            //if not a receiver, skip
            if(thisReceiver.status != 0) {
                continue;
            }

            ArrayList<Integer> chosenSenderId = thisReceiver.chooseSender(this.nodeList);
            //if no competitors ever or nobody, skip
            if (chosenSenderId.size() == 0) {
                continue;
            }

            for (Integer id: chosenSenderId) {
                nodeList.get(id).sendTo(thisReceiver, tagCounter, tagCounter_set);
            }
        }

        senders = prepareSenderForNextRound();
        clearCompetitors();
        updateCarriedSignals(senders);
        process(senders);
    }

    //================================Initialize BigList=============================
    public void initializeBigList() {
        for (int i = 0; i < N; i++) {
            this.nodeList.get(i).initializeTargets(this.nodeList, this.rmax);
            this.nodeList.get(i).initializeNeighbors(this.nodeList, this.rmax);
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
            tagCounter_set.put(thisTag, new HashSet<Integer>());
            tagCounter_set.get(thisTag).add(thisIndex);

            SensorNode thisOriSender = this.nodeList.get(thisIndex);

            thisOriSender.addSignal(thisTag); //all original senders "has known" the corresponding tag
            thisOriSender.setCarriedSig(thisTag);
            thisOriSender.setStatus(2); //make sure the original ones are senders
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
    //update nodes' status: 2 -> 0; 1 -> 2; 0 -> 2 (if got at least one signal)
    //return a list of senders (of status 2) as senders for the next round
    public ArrayList<SensorNode> prepareSenderForNextRound() { //TODO: continuous failure should lead to freeze
        //update status:
        ArrayList<SensorNode> senders = new ArrayList<>();
        for (int i = 0; i < this.N; i++) {
            SensorNode thisNode = this.nodeList.get(i);
            if (thisNode.status == 3) { //successful SEND in this round, RECEIVE in the next round
                thisNode.setStatus(0);
            } else if (thisNode.status == 2) { //failed sender, flip a coin to decide whether to continue
                double coin = Math.random();
                if (coin > this.failedSenderThreshold) {
                    thisNode.setStatus(2);
                    senders.add(thisNode);
                } else {
                    thisNode.setStatus(0);
                }
            } else if (thisNode.status == 1) { //successful RECEIVE in this round, SEND in the next
                thisNode.setStatus(2);
                senders.add(thisNode);
            } else { //failed Receiver, check if got signal yet
                if (thisNode.signals_set.size() > 0) {
                    double coin = Math.random(); //flip a coin to to decide whether to continue
                    if (coin > this.failedSenderThreshold) {
                        thisNode.setStatus(2);
                        senders.add(thisNode);
                    } else {
                        thisNode.setStatus(0);
                    }
                } else {
                    thisNode.setStatus(0);
                }
            }
        }
        return senders;
    }

    public void clearCompetitors() {
        for (int i = 0; i < this.N; i++) {
            this.nodeList.get(i).clearCompetitors();
        }
    }

    //update the carried signal to the least successful signal in the achievement pool
    public void updateCarriedSignals(ArrayList<SensorNode> senders) {
        for (SensorNode sender: senders) {
            sender.updateCarriedSignal();
        }
    }

    public ArrayList<SensorNode> convertWeakSenderToReceiver(ArrayList<SensorNode> senders) {
        ArrayList<SensorNode> newSenders = new ArrayList<>();
        for (SensorNode s: senders) {
            if (s.getR() > 0) {
                newSenders.add(s);
            } else {
                s.setStatus(0);
            }
        }
        return newSenders;
    }

    //================================Report Results=============================
    //check whether every signal has reached all receivers
    public boolean isDone() {
        for (int i = 0; i < this.signals.size(); i++) {
            if (this.tagCounter.get(this.signals.get(i)) < this.N) {
                return false;
            }
        }
        return true;
    }

    public void reportResults() {
        System.out.println("==================Results=================");
        for (int i = 0; i < this.signals.size(); i++) {
            String sig = this.signals.get(i);
            int outcome = this.tagCounter.get(this.signals.get(i));
            System.out.println(sig + " " + outcome + " " + (outcome * 100.0 / this.N) + "%");
            this.performance += (outcome * 100.0 / this.N);
        }
        this.performance /= this.signals.size();
    }

    //================================Tools=============================
    public void printList(ArrayList<SensorNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println(nodes.get(i).getId());
        }
    }
}
