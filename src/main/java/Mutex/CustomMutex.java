package Mutex;

import Clock.Clock;
import Clock.ClockType;
import Clock.DirectClock;
import Clock.LamportClock;
import Model.Message;
import Network.NetworkManager;
import Utils.Utils;

public abstract class CustomMutex {
    public static final int INFINITY = 2147483647;

    // Local params
    protected int myId;
    protected int numNodes;

    // Clock manager
    protected Clock clock;

    // Network manager
    protected NetworkManager networkManager;

    CustomMutex(int myId, int numNodes, ClockType type, NetworkManager networkManager) {
        this.myId = myId;
        this.numNodes = numNodes;
        this.clock = type == ClockType.DIRECT_CLOCK ? new DirectClock(myId,numNodes) : new LamportClock();
        this.networkManager = networkManager;
    }

    public synchronized void requestCS() {
        // ...
    }

    public synchronized void releaseCS() {
        // ...
    }

    public void accessCriticalZone() {
        System.out.println("Sóc el procés lightweight "+ this.myId);
        Utils.timeWait(1000);
    }

    public synchronized void handleMsg(Message m) {
        // ...
    }
}
