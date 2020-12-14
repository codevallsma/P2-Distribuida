package Mutex;

import Clock.Clock;
import Clock.ClockType;
import Model.Message;
import Network.NetworkManager;
import Utils.Utils;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class RAMutex extends CustomMutex {
    public static final int INFINITY = 2147483647;
    private Clock v;
    private int myts;
    private int numOkay = 0;
    private final Semaphore okay;


    public RAMutex(int id, int numNodes, NetworkManager manager) {
        super(id, numNodes, ClockType.LAMPORT_CLOCK, manager);
        myts = INFINITY;
        this.v = clock;
        okay = new Semaphore(numNodes-1);
    }

    @Override
    public synchronized void requestCS() {
        // Clock update
        v.tick();
        myts = v.getValue(0);
        // Broadcast Message
        Message msg = new Message("REQUEST", myId, myts);
        networkManager.sendBroadcastMessage(msg);
        numOkay = 0;
        while (numOkay < numNodes -1 ) {
            Utils.timeWait(500);
        }
        numOkay = 0;
        try {
            okay.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public synchronized void releaseCS() {
        myts = INFINITY;
        while (!q.isEmpty()) {
            int pid = ((LinkedList<Integer>)q).removeFirst();
            //TODO: index is useless
            this.networkManager.sendMessageToDedicatedConnection(this.myId,pid, v.getValue(0));
            //networkManager.
        }
    }

    @Override
    public void handleMsg(Message m) {
        int timeStamp = m.getTimestamp();
        v.receiveAction(m.getSrc(), timeStamp);
        switch (m.getTag()) {
            case "REQUEST":
                if ((myts == INFINITY)
                        || (timeStamp < myts)
                        || ((timeStamp == myts) && (m.getSrc() < myId))) {
                    this.networkManager.sendMessageToDedicatedConnection(this.myId,m.getSrc(), v.getValue(0));
                    //sendMsg();
                } else {
                    q.add(m.getSrc());
                }
                break;
            case "OKAY":
                okay.release();
                numOkay++;
                break;
        }

    }
}
