package main.java;

import java.util.ArrayList;

public class tryMain {
    public static void main(String[] args) {
        ArrayList<Node> nList = new ArrayList<Node>();
        nList.add(new Node(1,1,1));
        nList.add(new Node(2,2,2));
        nList.add(new Node(3,3,3));
        printList(nList);
        ArrayList<Node> temp = nList;
        temp.remove(1);
        printList(temp);
        printList(nList);

    }

    public static void printList(ArrayList<Node> nList) {
        System.out.println("print out list");
        for (int i = 0; i < nList.size(); i++) {
            System.out.println(nList.get(i).toString());
        }
    }


}
