package Mutex;

import Clock.ClockType;
import Model.Message;
import Network.NetworkManager;

import java.util.LinkedList;

public class RAMutex extends CustomMutex {
    public static final int INFINITY = 2147483647;

    private int myts;
    private LinkedList<Integer> pendingQ = new LinkedList<Integer>();
    private int numOkay = 0;



    public RAMutex(int id, int numNodes, NetworkManager manager) {
        super(id, numNodes, ClockType.LAMPORT_CLOCK, manager);
        myts = INFINITY;
    }

    @Override
    public synchronized void requestCS() {
        // Clock update
        clock.tick();
        myts = clock.getValue(0);
        // Broadcast Message
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
        while (!pendingQ.isEmpty()) {
            int pid = pendingQ.removeFirst();
            //networkManager.
        }
    }

    @Override
    public synchronized void handleMsg(Message m) {
        int timeStamp = m.getTimestamp();
        clock.receiveAction(m.getSrc(), timeStamp);

        switch (m.getTag()) {
            case "REQUEST":
                /*if ((myts == INFINITY)
                        || (timeStamp < myts)
                        || ((timeStamp == myts) && (m.getSrc() < myID))) {
                    //sendMsg();
                } else {
                    pendingQ.add(m.getSrc());
                }*/
                break;
            case "OKAY":
                numOkay++;
                if (numOkay == numNodes -1) {
                    notify();
                }
                break;
        }

    }
}
