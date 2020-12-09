package Lamport;

import Interfaces.LamportInterface;
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

    public LamportMutex(int myId, int numNodes, NetworkManager networkManager) {
        this.myId= myId;
        this.numNodes = numNodes;
        this.networkManager = networkManager;
        v = new DirectClock(myId,numNodes);
        q = new ArrayList<>(Collections.nCopies(numNodes, INFINITY));
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
        while (!okCS()) {
            //System.out.println("Hello");
            Utils.timeWait(1000);
        }
    }

    public synchronized void releaseCS(){
        q.set(myId, INFINITY);
        Message msg = new Message("RELEASE", myId, v.getValue(myId));
        this.networkManager.sendBroadcastMessage(msg);
    }

    /**
     * This is our active wait to check
     * @return: if returns true, we get permission to get the token
     */
    public boolean okCS() {
        //System.out.println("Q size: " + q.size());
        //System.out.println("My value: " + this.q.get(myId));
        for(int i =0; i < q.size(); i++){
            //System.out.println("compare value to: " + this.q.get(i));
            if(this.isGreater(myId, i, this.q.get(myId), this.q.get(i))){
                return false;
            } else if (this.isGreater(myId, i, this.v.getValue(myId), this.v.getValue(i))){
                return false;
            }
        }
        //System.out.println("Return true of okCS()");
        return true;
    }


    public boolean isGreater(int index1, int index2, int value1, int value2) {
        if(value2 == LamportMutex.INFINITY) return false;
        return value1 > value2 || value1 == value2 && (index1 > index2);
    }

    @Override
    public void accessCriticalZone() {
        System.out.println("Sóc el procés lightweight "+ this.myId);
    }

    @Override
    public synchronized void handleMsg(Message msg) {
        int timestamp = msg.getTimestamp();
        v.receiveAction(msg.getSrc(), msg.getTimestamp());
        //System.out.println("Msg rebut: "+msg.getTag());
        switch (msg.getTag()){
            case "REQUEST":
                this.q.set(msg.getSrc(), timestamp);
                //send ack back
                break;
            case "RELEASE":
                this.q.set(msg.getSrc(), LamportMutex.INFINITY);
                break;
        }
    }

    public boolean isReady(){
        return networkManager != null && networkManager.getConnectionsSize() + 1 == numNodes;
    }


}
