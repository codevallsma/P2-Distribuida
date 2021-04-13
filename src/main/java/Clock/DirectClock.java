package Clock;

import Mutex.LamportMutex;

import java.util.ArrayList;
import java.util.Collections;

public class DirectClock implements Clock{
    private ArrayList<Integer> clock;
    private int myId;

    public DirectClock(int myId, int numConnectedNodes) {
        this.clock = new ArrayList<Integer>(Collections.nCopies(numConnectedNodes, 0));
        for (int i = 0; i < clock.size(); i++) clock.set(i, 0);
        this.myId = myId;
        //this.clock.set(myId, myId*10);

        // A1 -> a2 ->a0

        if (myId == 1){
            this.clock.set(myId,0);
        } else if (myId ==0){
            this.clock.set(myId,10);
        } else if(myId ==2 ){
            this.clock.set(myId,5);
        }
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

    public void tick(int sender, int value) {
        this.clock.set(sender, Integer.max(value, this.clock.get(sender)) );
    }

    public int requestAction(){
        int res = this.clock.get(myId);
        this.tick();
        return res;
    }

    public void receiveAction(int sender, int sentValue){
        this.clock.set(sender, Integer.max(sentValue, this.clock.get(sender)) );
        //this.clock.set(myId, Integer.max(sentValue, this.clock.get(myId))+1);

        if (myId == 1){
            this.clock.set(myId, Integer.max(sentValue, this.clock.get(myId)+1));
        } else if (myId ==0){
            this.clock.set(myId, Integer.max(sentValue, this.clock.get(myId)+7));
        } else if(myId ==2 ){
            this.clock.set(myId, Integer.max(sentValue, this.clock.get(myId)+3));
        }
        //if(Integer.max(sentValue, this.clock.get(myId)) != LamportMutex.INFINITY){
        //}else{
        //    this.clock.set(myId,LamportMutex.INFINITY);
        //}
    }
}
