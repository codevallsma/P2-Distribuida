package Lamport;

import Interfaces.LamportInterface;
import Interfaces.NetworkCallback;
import Model.Message;
import Network.NetworkManager;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class LamportMutex implements LamportInterface {

    public static final int INFINITY = 2147483647;

    private DirectClock v;
    private ArrayList<Integer> q;
    private int myId;
    private int numNodes;

    private NetworkManager networkManager;

    public LamportMutex(int myId, int numNodes) {
        this.myId= myId;
        this.numNodes = numNodes;
        v = new DirectClock(myId,numNodes);
        q = new ArrayList<>(Collections.nCopies(numNodes, INFINITY));
    }


    public void doSomething(){
        while(true){
            //waitHeavyWeight();
            requestCS();
            for (int i=0; i<10; i++){
                if(!okCS()) {
                    accessCriticalZone();
                    Utils.timeWait(1000);
                } else {
                    requestCS();
                }
            }
            releaseCS();
            //notifyHeavyWeight();
        }
    }

    /**
     * The function we use to create a request
     */
    public void requestCS() {
        //updating our clock of requests to the new value
        q.set(myId,v.requestAction());
        Message msg = new Message("REQUEST",myId, this.q.get(myId));
        //broadcast
        this.networkManager.sendBroadcastMessage(msg);
        while (!okCS());
    }

    public synchronized void releaseCS(){
        q.set(myId, INFINITY);
        Message msg = new Message("RELEASE", myId, v.getValue(myId));
        this.networkManager.sendBroadcastMessage(msg);
    }

    /**
     * This is our active wait to check
     * @return: if returns false, we get permission to get the token
     */
    public boolean okCS() {
        for(int i =0; i < q.size(); i++){
            if(this.isGreater(myId, i, this.q.get(i))){
                return false;
            } else if (this.isGreater(myId, i, this.v.getValue(i))){
                return false;
            }
        }
        return true;
    }


    public boolean isGreater(int index1, int index2, int value2) {
        if(q.get(index2) == LamportNode.INFINITY) return false;
        return q.get(index1).compareTo(value2) > 0 || q.get(index1).compareTo(value2) == 0 && (index1 > index2);
    }

    @Override
    public void accessCriticalZone() {
        System.out.println("Sóc el procés lightweight "+ this.myId);
    }

    @Override
    public synchronized void handleMsg(Message msg) {
        int timestamp = msg.getTimestamp();
        v.receiveAction(msg.getSrc(), msg.getTimestamp());
        switch (msg.getTag()){
            case "REQUEST":
                this.q.set(msg.getSrc(), timestamp);
                //send ack back
                break;
            case "RELEASE":
                this.q.set(msg.getSrc(), LamportNode.INFINITY);
                break;
        }
    }

    public boolean isReady(){
        return networkManager.getConnectionsSize() + 1 == numNodes;
    }


}
