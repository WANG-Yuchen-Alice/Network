package main.java;

import main.java.logic.StrongSigSends;

import java.util.ArrayList;

public class tryMain {
    public static void main(String[] args) {
        ArrayList<SensorNode> nodes = new ArrayList<>();
        nodes.add(new SensorNode(1, 1, 0, 7));
        nodes.get(0).setNum(50);
        nodes.add(new SensorNode(2, 2, 1, 7));
        nodes.get(1).setNum(30);
        nodes.add(new SensorNode(3, 3, 2, 7));
        nodes.get(2).setNum(50);

        ArrayList<Integer> senders = new ArrayList<>();
        senders.add(0);
        senders.add(2);
        senders.add(1);

        StrongSigSends sss = new StrongSigSends(nodes, new ArrayList<String>(), 3, 1.5);
        sss.process(nodes);

        for (int i = 0; i < nodes.size(); i++) {
            System.out.println(nodes.get(i).getId());
        }

    }


}
