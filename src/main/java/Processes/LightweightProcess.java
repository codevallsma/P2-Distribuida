package Processes;

import DataParser.Data;
import DataParser.Node;
import Interfaces.NetworkCallback;
import Lamport.LamportMutex;
import Model.Message;
import Network.NetworkManager;
import Utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class LightweightProcess implements NetworkCallback {

    private NetworkManager networkManager;
    private LamportMutex lamportMutex;

    // Comunication
    private int myId;
    private Data nodeNetwork;
    private Node nodeInfo;


    private List<Integer> dependencyList;
    private int numNodes;

    public LightweightProcess(int id, Data networkInfo) {
        this.myId= id;
        this.nodeNetwork= networkInfo;
        this.nodeInfo = nodeNetwork.getNodes().get(myId);
        this.numNodes=nodeNetwork.getNodes().size();
        dependencyList= nodeNetwork.getNodes().stream().filter(e -> e.getConnectedTo().contains(myId)).map(Node::getNodeId).collect(Collectors.toList());
        this.networkManager = new NetworkManager(nodeInfo, nodeNetwork, dependencyList.size(),this);
        this.lamportMutex = new LamportMutex(myId, numNodes, this.networkManager);
    }

    public void start() {
        this.networkManager.start();
    }

    public boolean isReady() {
        return lamportMutex.isReady();
    }

    public void doSomething(){
        Utils.timeWait(1000);
        //System.out.println("He entrat i soc el node "+ nodeInfo.getNodeId());
        for (int i=0; i<10; i++){
            this.lamportMutex.requestCS();
            System.out.println("Iteració "+ i +" , node = "+ nodeInfo.getNodeId());
            this.lamportMutex.accessCriticalZone();
            this.lamportMutex.releaseCS();
            Utils.timeWait(1000);
        }
        this.networkManager.stopServer();
    }

    /* *************************************************************************** */
    /* ************************** NETWORK CALLBACK ******************************* */
    /* *************************************************************************** */

    @Override
    public synchronized void onMessageReceived(Message msg) {
        lamportMutex.handleMsg(msg);
    }
}
