package main.java;

import java.util.ArrayList;
import java.util.HashSet;

public class SensorNode {

    public int i;
    public int j;
    public int id;
    public ArrayList<Integer> targets; //list of receivers within the range
    public HashSet<String> signals;

    public SensorNode(int i, int j, int id) {
        this.i = i;
        this.j = j;
        this.id = id;
        this.targets = new ArrayList<Integer>();
        this.signals = new HashSet<String>();
    }

    
}
