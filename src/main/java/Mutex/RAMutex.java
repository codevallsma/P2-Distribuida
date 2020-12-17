package Mutex;

import Clock.Clock;
import Clock.ClockType;
import Model.Message;
import Network.NetworkManager;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class RAMutex extends CustomMutex {
    public static final int INFINITY = 2147483647;
    private Clock v;
    private int myts;
    private Semaphore okay;


    public RAMutex(int id, int numNodes, NetworkManager manager) {
        super(id, numNodes, ClockType.LAMPORT_CLOCK, manager);
        myts = INFINITY;
        this.v = clock;
        okay = new Semaphore(1);
    }

    @Override
    public void requestCS() {
        // Clock update
        v.tick();
        myts = v.getValue(0);
        // Broadcast Message
        Message msg = new Message("REQUEST", myId, myts);
        networkManager.sendBroadcastMessage(msg);
        try {
            for (int i=0; i<numNodes;i++)
            okay.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void releaseCS() {
        myts = INFINITY;
        while (!q.isEmpty()) {
            int pid = ((LinkedList<Integer>)q).removeFirst();
            //TODO: index is useless
            this.networkManager.sendMessageToDedicatedConnection(myId, pid, v.getValue(0));
            //networkManager.
        }
        okay = new Semaphore(1);
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
                    this.networkManager.sendMessageToDedicatedConnection(myId, m.getSrc(), v.getValue(0));
                    //sendMsg();
                } else {
                    q.add(m.getSrc());
                }
                break;
            case "OKAY":
                okay.release();
                break;
        }

    }
}
