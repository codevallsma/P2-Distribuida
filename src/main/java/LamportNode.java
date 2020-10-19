import ClassesToSend.Message;
import Interfaces.LamportCallback;
import Interfaces.LamportInterface;

import java.util.ArrayList;

public class LamportNode implements LamportCallback<Integer>, LamportInterface<Integer> {
    private DirectClock v;
    private ArrayList<Integer> q;
    private int myId;
    //the max number for integer is 2147483647
    public static final int INFINITY = 2147483647;

    public LamportNode() {
        //this
    }

    @Override
    public void handleMsg(Message msg) {
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

    @Override
    public void onNewNode(Message msg) {
        if(this.q.size() <= msg.getSrc()){
            //the new node with the new index has to fit in our arraylist
            this.q.ensureCapacity(msg.getSrc() +1);
            this.v.getClock().ensureCapacity(msg.getSrc() +1);
        }
        this.v.addNewNode(msg.getSrc());
        this.q.set(msg.getSrc(),LamportNode.INFINITY);
    }

    @Override
    public void onDeleteNode(Message msg) {

    }

    @Override
    public boolean isGreater(int index1, int index2, int value2) {
       if(q.get(index2) == LamportNode.INFINITY) return false;
       return q.get(index1).compareTo(value2) > 0 || q.get(index1).compareTo(value2) == 0 && (index1 > index2);
    }

    /**
     * This is our active wait to check
     * @return: if returns false, we get permission to get the token
     */
    @Override
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

    /**
     * The function we use to create a request
     */
    @Override
    public void requestCS() {
        //updating our clock of requests to the new value
        q.set(myId,v.requestAction());
        Message<Integer> msg = new Message<>("REQUEST",myId, this.q.get(myId), null);
        //broadcast
        while (!okCS());
    }
}
