package Mutex;

import Clock.Clock;
import Clock.ClockType;
import Clock.DirectClock;
import Clock.LamportClock;
import Model.Message;
import Network.NetworkManager;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

public abstract class CustomMutex {
    public static final int INFINITY = 2147483647;
    protected Clock clock;
    protected ArrayList<Integer> q;
    protected int myId;
    protected int numNodes;
    protected NetworkManager networkManager;

    CustomMutex(int myId, int numNodes, ClockType type, NetworkManager networkManager) {
        this.myId = myId;
        this.numNodes = numNodes;
        this.clock = type == ClockType.DIRECT_CLOCK ? new DirectClock(myId,numNodes): new LamportClock();
        this.networkManager = networkManager;
        q = new ArrayList<>(Collections.nCopies(numNodes, INFINITY));
    }

    public abstract void requestCS();
    public abstract void releaseCS();
    public abstract void handleMsg(Message m);

    public void accessCriticalZone() {
        System.out.println("Sóc el procés lightweight "+ this.myId);
        Utils.timeWait(1000);
    }
}
