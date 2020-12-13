package Processes.HeavyWeight;

import DataParser.Data;
import DataParser.HeavyWeight;
import DataParser.Node;
import DataParser.Parser;
import Interfaces.NetworkCallback;
import Model.Message;
import Mutex.LamportMutex;
import Network.NetworkManager;
import Utils.Utils;
import Utils.Launch;

import java.io.IOException;
import java.util.stream.Collectors;

public class HeavyweightProcess implements NetworkCallback {

    private NetworkManager networkManager;
    private HeavyWeight hwData;
    private int num_nodes;
    public HeavyweightProcess(Data data) {
        this.networkManager = networkManager;
        num_nodes = data.getNodes().size();
        hwData = data.getHeavyWeight();
        if(hwData.getConnectToOther()){

        }
        this.myId= id;
        this.nodeNetwork= networkInfo;
        this.nodeInfo = nodeNetwork.getNodes().get(myId);
        this.numNodes=nodeNetwork.getNodes().size();
        dependencyList= nodeNetwork.getNodes().stream().filter(e -> e.getConnectedTo().contains(myId)).map(Node::getNodeId).collect(Collectors.toList());
        this.networkManager = new NetworkManager(nodeInfo, nodeNetwork, num_nodes,this);
        this.lamportMutex = new LamportMutex(myId, numNodes, this.networkManager);
    }

    /* *************************************************************************** */
    /*                            NETWORK CALLBACK                                 */
    /* *************************************************************************** */
    @Override
    public void onMessageReceived(Message msg) {

    }

    private void setNetworkManager(HeavyWeight data){

    }
    private static void launchSons(String className){
        try {
            String[] command = {Utils.getCommand(className, "")};
            Launch.launchProcesses(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main for heavyweights
     * @param args: Args[0] -> filename
     */
    public static void main(String[] args) {

        Data d = Parser.parseJson(args[0]);
        //heavyWeight lamport
        if (args[0].compareTo("Mutex") == 0) {
            launchSons("MainLamoport");
            //HeavyweightProcess hwp = HeavyweightProcess();
        } else  {
            //heavyWeight ricardAgrawala
            launchSons("MainRicardAgrawala");
        }
    }
}
