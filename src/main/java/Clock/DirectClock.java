package Clock;

import Mutex.LamportMutex;

import java.util.ArrayList;
import java.util.Collections;

public class DirectClock implements Clock{
    private ArrayList<Integer> clock;
    private int myId;

    public DirectClock(int myId, int numConnectedNodes) {
        this.clock = new ArrayList<Integer>(Collections.nCopies(numConnectedNodes, 0));
        this.myId = myId;
        this.clock.set(myId,myId);
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
        this.clock.set(sender, Integer.max(sentValue, this.clock.get(sender)) );
        if(Integer.max(sentValue, this.clock.get(myId)) != LamportMutex.INFINITY){
            this.clock.set(myId, Integer.max(sentValue, this.clock.get(myId)) + 1 );
        }else{
            this.clock.set(myId,LamportMutex.INFINITY);
        }
    }
}
