package Processes.LightWeight;

import DataParser.Data;
import DataParser.HeavyWeight;
import DataParser.LightWeight;
import DataParser.Node;
import Interfaces.NetworkCallback;
import Model.Message;
import Mutex.CustomMutex;
import Mutex.LamportMutex;
import Mutex.MutexType;
import Mutex.RAMutex;
import NewNetwork.NetworkManager;
import Utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class LightWeightPrc implements NetworkCallback {

    protected int myId;
    protected LightWeight nodeInfo;
    protected HeavyWeight parentInfo;

    // Comunication
    protected NetworkManager networkManager;
    protected boolean initService;
    protected CustomMutex mutex;

    protected List<Integer> dependencyList;
    protected int numNodes;

    public LightWeightPrc(int id, LightWeight nodeInfo, HeavyWeight parentInfo, MutexType mutexType) {
        // to be implemented
        this.myId = id;
        this.nodeInfo = nodeInfo;
        this.parentInfo = parentInfo;
        this.numNodes=parentInfo.getNodes().size();
        this.initService = false;
        this.networkManager = new NetworkManager(nodeInfo, parentInfo,this);
        //this.mutex = mutexType == MutexType.LAMPORT ?
        //        new LamportMutex(myId, numNodes, networkManager) :
        //        new RAMutex(myId, numNodes, networkManager);
    }

    public void initBaseConnections() {
        dependencyList= parentInfo.getNodes().stream()
                .filter( e -> ((LightWeight)e).getConnectedTo().contains(myId))
                .map((Node t) -> ((LightWeight)t).getNodeId())
                .collect(Collectors.toList());

        this.networkManager.connectToHeavyWeight();
        this.networkManager.setNodesToConnect(dependencyList.size());

        for (Integer nodeId: dependencyList) {
            this.networkManager.connectToNode(parentInfo.getNodes().get(nodeId));
        }

    }

    public void doSomething(){
        Utils.timeWait(1000);
        initService = true; // While not having heavyweight
        //while(true) {
            while (!initService) {
             Utils.timeWait(1000);
            }
            this.mutex.requestCS();
            for (int i = 0; i < 10; i++) {
                System.out.println("Iteració " + i + " , node = " + nodeInfo.getName());
                this.mutex.accessCriticalZone();
            }
            this.mutex.releaseCS();
            this.initService = false;
            this.networkManager.notifyHeavyWeight();
        //}
        Utils.timeWait(2000);
        System.out.println("Ja he acabat i soc el node "+  nodeInfo.getName());
        this.networkManager.stopServer();
    }

    /* *************************************************************************** */
    /*                              NETWORK CALLBACK                               */
    /* *************************************************************************** */

    @Override
    public synchronized void onMessageReceived(Message msg) {
        mutex.handleMsg(msg);
    }

    @Override
    public void onInitService(boolean init) {
        this.initService = init;
    }


}
