package Mutex;
import Clock.Clock;
import Clock.ClockType;
import Interfaces.LamportInterface;
import Model.Message;
import Network.NetworkManager;
import Utils.Utils;

public class LamportMutex extends CustomMutex implements LamportInterface {
    private Clock v;
    private int acksReceived;


    public LamportMutex(int myId, int numNodes, NetworkManager networkManager) {
        super(myId, numNodes, ClockType.DIRECT_CLOCK ,networkManager);
        this.v = this.clock;
        this.acksReceived = 0;
    }

    /**
     * The function we use to create a request
     */
    @Override
    public void requestCS() {

        //updating our clock of requests to the new value
        q.set(myId,v.requestAction());
        Message msg = new Message("REQUEST",myId, this.q.get(myId));
        //broadcast
        this.networkManager.sendBroadcastMessage(msg);
        System.out.println("(" + myId + ") Waiting acks...\n");
        while (acksReceived < this.numNodes-1) {
            Utils.timeWait(500);
        }
        System.out.println("(" + myId + ") Acks received...\n");
        acksReceived = 0;
        while (!okCS()) {
            Utils.timeWait(1000);
        }
    }
    @Override
    public void releaseCS(){
        q.set(myId, INFINITY);
        Message msg = new Message("RELEASE", myId, v.getValue(myId));
        this.networkManager.sendBroadcastMessage(msg);
    }

    /**
     * This is our active wait to check
     * @return: if returns true, we get permission to get the token
     */
    public boolean okCS() {
        //System.out.println("(" + myId + ") Q size: " + q.size()) ;
        System.out.println("My value: " + this.q.get(myId));
        for(int i =0; i < q.size(); i++){
            if(i!=myId) {
                //System.out.println("compare value to: " + this.q.get(i));
                if (this.isGreater(myId, i, this.q.get(myId), this.q.get(i))) {
                    return false;
                } else {
                    if (this.isGreater(myId, i, this.q.get(myId), this.v.getValue(i)))
                        return false;
                }
            }
        }
        for (int i = 0; i < q.size(); i++) {
            System.out.println("(" + i + ") Q timestamp: " + q.get(i));
        }
        //System.out.println("Return true of okCS()");
        return true;
    }

    @Override
    public boolean isGreater(int myIdIndex, int otherIndex, int value1, int value2) {
        if(value2 == LamportMutex.INFINITY) return false;
        return value1 > value2 || value1 == value2 && (myIdIndex > otherIndex);
    }

    @Override
    public void handleMsg(Message msg) {
        int timestamp = msg.getTimestamp();
        v.receiveAction(msg.getSrc(), msg.getTimestamp());
        //System.out.println("Msg rebut: "+msg.getTag());
        switch (msg.getTag()){
            case "REQUEST":
                this.q.set(msg.getSrc(), timestamp);
                this.networkManager.sendMessageToConnection(myId, "ACK", v.getValue(myId), msg.getSrc());
                break;
            case "ACK":
                this.v.tick(msg.getSrc(), msg.getTimestamp());
                acksReceived++;
                break;
            case "RELEASE":
                this.q.set(msg.getSrc(), LamportMutex.INFINITY);
                break;
        }
    }
}
