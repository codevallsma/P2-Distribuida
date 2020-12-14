package Processes.LightWeight;

import DataParser.Data;
import DataParser.HeavyWeight;
import DataParser.LightWeight;
import DataParser.Node;
import Interfaces.NetworkCallback;
import Model.Message;
import Mutex.CustomMutex;
import Mutex.LamportMutex;
import Mutex.RAMutex;
import Network.NetworkManager;
import Utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class LightweightProcess implements NetworkCallback {

    private NetworkManager networkManager;
    private NetworkManager heavyWeightManager;
    private CustomMutex lamportMutex;

    // Comunication
    private int myId;
    private HeavyWeight nodeNetwork;
    private Node nodeInfo;


    private List<Integer> dependencyList;
    private int numNodes;

    public LightweightProcess(int id, HeavyWeight networkInfo, List<HeavyWeight> hwToConnect) {
        this.myId= id;
        this.nodeNetwork= networkInfo;
        this.nodeInfo = nodeNetwork.getNodes().get(myId);
        this.numNodes=nodeNetwork.getNodes().size();
        dependencyList= nodeNetwork.getNodes().stream()
                .filter( e -> ((LightWeight)e).getConnectedTo().contains(myId))
                .map((Node t) -> ((LightWeight)t).getNodeId())
                .collect(Collectors.toList());
        this.networkManager = new NetworkManager(nodeInfo, nodeNetwork, dependencyList.size(),this);
        this.lamportMutex = networkInfo.getType().compareTo("Lamport") == 0 ? new LamportMutex(myId, numNodes, this.networkManager) : new RAMutex(myId, numNodes, this.networkManager);
    }

    public void start() {
        this.networkManager.start();
    }

    public boolean isReady() {
        return lamportMutex.isReady();
    }

    public void doSomethingLamport(){
        Utils.timeWait(1000);
        //while(true) {
            this.lamportMutex.requestCS();
            for (int i = 0; i < 10; i++) {
                System.out.println("Iteració " + i + " , node = " + nodeInfo.getName());
                this.lamportMutex.accessCriticalZone();
            }
            this.lamportMutex.releaseCS();
        //}
        this.networkManager.stopServer();
    }

    /* *************************************************************************** */
    /* ************************** NETWORK CALLBACK ******************************* */
    /* *************************************************************************** */

    @Override
    public void onMessageReceived(Message msg) {
        this.lamportMutex.handleMsg(msg);
    }

    @Override
    public void onInitService(boolean init) {

    }

    @Override
    public void onTokenAssigned() {
        //...
    }

    @Override
    public void onNodeFinished() {
        //..
    }

}
