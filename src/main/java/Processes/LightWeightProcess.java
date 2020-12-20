package Processes;

import DataParser.HeavyWeight;
import DataParser.LightWeight;
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
import java.util.stream.Collectors;

public class LightWeightProcess implements NetworkCallback {

    protected int myId;
    protected LightWeight nodeInfo;
    protected HeavyWeight parentInfo;

    // Comunication
    protected NetworkManager networkManager;
    protected boolean initService;
    protected CustomMutex mutex;

    // Logic
    protected boolean isRunning;

    protected List<Integer> dependencyList;
    protected int numNodes;

    public LightWeightProcess(int id, LightWeight nodeInfo, HeavyWeight parentInfo, MutexType mutexType) {
        this.myId = id;
        this.nodeInfo = nodeInfo;
        this.parentInfo = parentInfo;
        this.numNodes=parentInfo.getNodes().size();
        this.initService = false;
        this.isRunning = false;
        this.networkManager = new NetworkManager(nodeInfo, parentInfo,this);
        this.mutex = mutexType == MutexType.LAMPORT ?
                new LamportMutex(myId, numNodes, networkManager) :
                new RAMutex(myId, numNodes, networkManager);
    }

    public void initBaseConnections() {
        dependencyList= parentInfo.getNodes().stream()
                .filter( e -> ((LightWeight)e).getConnectedTo().contains(myId))
                .map((Node t) -> ((LightWeight)t).getNodeId())
                .collect(Collectors.toList());

        this.networkManager.startListening();
        this.networkManager.connectToHeavyWeight(parentInfo);
        this.networkManager.setNodesToConnect(dependencyList.size());

        for (Integer nodeId: nodeInfo.getConnectedTo()) {
            this.networkManager.connectToNode(parentInfo.getNodes().get(nodeId));
        }
    }

    public void doSomething(){
        //Utils.timeWait(Utils.getRandomNumberInRange(500, 2000));
        //initService = true; // While not having heavyweight
        isRunning = true;
        while(isRunning) {
            while (!initService) {
             Utils.timeWait(1000);
             if (initService) break;
            }
            this.mutex.requestCS();
            for (int i = 0; i < 10; i++) {
                System.out.println("Iteració " + (i+1) + " , node = " + nodeInfo.getName() + "\n");
                Utils.timeWait(1000);
                //this.mutex.accessCriticalZone();
            }
            this.mutex.releaseCS();
            this.initService = false;
            this.networkManager.notifyHeavyWeight();
        }
        Utils.timeWait(3000);
        this.networkManager.stopServer();
    }

    public boolean isReady() {
        if (this.mutex instanceof LamportMutex) {
            return ((LamportMutex)this.mutex).isReady();
        }
        return this.networkManager.getConnectionsSize() == (parentInfo.getNodes().size() - 1);
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
        this.initService = true;
    }

    @Override
    public void onTokenAssigned() {
        // ...
    }

    @Override
    public void onHeavyReady() {

    }

    @Override
    public void onNodeFinished() {
        //...
    }


}
