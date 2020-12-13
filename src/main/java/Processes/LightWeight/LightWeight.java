package Processes.LightWeight;

import DataParser.Data;
import DataParser.Node;
import Interfaces.NetworkCallback;
import Model.Message;
import Mutex.CustomMutex;
import Mutex.LamportMutex;
import Mutex.MutexType;
import Mutex.RAMutex;
import Network.NetworkManager;
import Utils.Utils;

import java.util.List;

public class LightWeight implements NetworkCallback {

    protected int myId;
    protected Data nodeNetwork;
    protected Node nodeInfo;

    // Comunication
    protected NetworkManager networkManager;
    protected CustomMutex mutex;

    protected List<Integer> dependencyList;
    protected int numNodes;

    public LightWeight(MutexType mutexType) {
        // to be implemented
        mutex = mutexType == MutexType.LAMPORT ?
                new LamportMutex(myId, numNodes, networkManager) :
                new RAMutex(myId, numNodes, networkManager);
    }

    public void doSomething(){
        Utils.timeWait(1000);
        //while(true) {
            this.mutex.requestCS();
            for (int i = 0; i < 10; i++) {
                System.out.println("Iteració " + i + " , node = " + nodeInfo.getNodeId());
                this.mutex.accessCriticalZone();
            }
            this.mutex.releaseCS();
        //}
        Utils.timeWait(5000);
        System.out.println("Ja he acabat i soc el node "+  nodeInfo.getNodeId());
        this.networkManager.stopServer();
    }

    /* *************************************************************************** */
    /*                              NETWORK CALLBACK                               */
    /* *************************************************************************** */

    @Override
    public synchronized void onMessageReceived(Message msg) {
        mutex.handleMsg(msg);
    }


}
