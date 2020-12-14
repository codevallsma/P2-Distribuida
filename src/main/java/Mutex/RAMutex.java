package Mutex;

import Clock.Clock;
import Clock.ClockType;
import Model.Message;
import Network.NetworkManager;

import java.util.LinkedList;

public class RAMutex extends CustomMutex {
    public static final int INFINITY = 2147483647;
    private Clock v;
    private int myts;
    private int numOkay = 0;



    public RAMutex(int id, int numNodes, NetworkManager manager) {
        super(id, numNodes, ClockType.LAMPORT_CLOCK, manager);
        myts = INFINITY;
        this.v = clock;
    }

    @Override
    public synchronized void requestCS() {
        // Clock update
        v.tick();
        myts = v.getValue(0);
        // Broadcast Message
        System.err.println("requestMessage");
        Message msg = new Message("REQUEST", myId, myts);
        networkManager.sendBroadcastMessage(msg);
        numOkay = 0;
        while (numOkay < numNodes -1 ) {
            //wait
        }
    }
    @Override
    public synchronized void releaseCS() {
        myts = INFINITY;
        while (!q.isEmpty()) {
            int pid = ((LinkedList<Integer>)q).removeFirst();
            //TODO: index is useless
            this.networkManager.sendMessageToDedicatedConnection(pid, v.getValue(0));
            //networkManager.
        }
    }

    @Override
    public void handleMsg(Message m) {
        int timeStamp = m.getTimestamp();
        v.receiveAction(m.getSrc(), timeStamp);
        System.err.println("missatge arribat");
        switch (m.getTag()) {
            case "REQUEST":
                System.err.println("request");
                if ((myts == INFINITY)
                        || (timeStamp < myts)
                        || ((timeStamp == myts) && (m.getSrc() < myId))) {
                    this.networkManager.sendMessageToDedicatedConnection(m.getSrc(), v.getValue(0));
                    //sendMsg();
                } else {
                    q.add(m.getSrc());
                }
                break;
            case "OKAY":
                System.err.println("OKEYMAKEY");
                numOkay++;
                if (numOkay == numNodes -1) {
                    notify();
                }
                break;
        }

    }
}
