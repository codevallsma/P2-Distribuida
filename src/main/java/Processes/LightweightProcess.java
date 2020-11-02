package Processes;

import DataParser.Data;
import DataParser.Node;
import Interfaces.NetworkCallback;
import Lamport.LamportMutex;
import Model.Message;
import Network.NetworkManager;

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

        this.networkManager = new NetworkManager(nodeInfo, dependencyList.size(),this);
        this.lamportMutex = new LamportMutex(myId, numNodes);
    }

    /* *************************************************************************** */
    /* ************************** PUBLIC FUNCTIONS ******************************* */
    /* *************************************************************************** */
    public synchronized Node getNodeByID(int id) {
        return nodeNetwork.getNodes().get(id);
    }

    /* *************************************************************************** */
    /* ************************** NETWORK CALLBACK ******************************* */
    /* *************************************************************************** */

    @Override
    public void onMessageReceived(Message msg) {

    }
}
