package Lamport;

import java.util.ArrayList;

public class DirectClock {
    private ArrayList<Integer> clock;
    private int myId;

    public DirectClock(int myId, int numConnectedNodes) {
        this.clock = new ArrayList<Integer>(numConnectedNodes);
        this.myId = myId;
    }

    public void addNewNode(int nodeId){
        //adding the nodeValue to the new node of the array
        clock.add(nodeId, LamportNode.INFINITY);
    }

    public ArrayList<Integer> getClock() {
        return clock;
    }

    public int getValue(int index){
        return clock.get(index);
    }
    public void tick(){
        this.clock.set(myId, this.clock.get(myId)+ 1);
    }
    public int requestAction(){
        this.tick();
        return this.clock.get(myId);
    }
    public void receiveAction(int sender, int sentValue){
        this.clock.set(sender, Integer.max(sentValue, this.clock.get(myId)) );
        this.clock.set(myId, Integer.max(sentValue, this.clock.get(myId)) + 1 );
    }
}
