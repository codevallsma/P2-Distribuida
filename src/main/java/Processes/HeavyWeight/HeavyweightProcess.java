package Processes.HeavyWeight;

import DataParser.*;
import Interfaces.NetworkCallback;
import Model.Message;
import NewNetwork.NetworkManager;
import Utils.Utils;
import Utils.Launch;

import java.io.IOException;

public class HeavyweightProcess implements NetworkCallback {

    private int myId;
    private HeavyWeight ourData;
    private HeavyWeight connectedTo;

    // Communication
    private NetworkManager networkManager;
    private int numNodes;

    // Token & Logic
    private boolean hasToken;
    private int finishedNodes;

    public HeavyweightProcess(int id, HeavyWeight data, HeavyWeight connectedTo, boolean hasToken, NetworkManager networkManager) {
        this.myId = id;
        this.ourData = data;
        this.connectedTo = connectedTo;
        this.networkManager = networkManager;
        this.numNodes = data.getNodes().size();
        this.hasToken = hasToken;
        this.finishedNodes = 0;
    }

    public void initBaseConnections() {
        this.networkManager.startListening();
        if (ourData.getConnectToOther()) {
            this.networkManager.connectToHeavyWeight(connectedTo);
        }
    }

    public void doSomething() {
        Utils.timeWait(1000);
        //while(true) {
            while(!hasToken) {
                Utils.timeWait(1000);
            }
            this.networkManager.sendBroadcastMessage("SERVICE-START");
            while (finishedNodes < numNodes) {
                Utils.timeWait(1000);
            }
            finishedNodes = 0;
            hasToken = false;
            this.networkManager.notifyHeavyWeight();
        //}
    }

    /* *************************************************************************** */
    /*                            NETWORK CALLBACK                                 */
    /* *************************************************************************** */
    @Override
    public void onMessageReceived(Message msg) {
        // ...
    }

    @Override
    public void onInitService(boolean init) {
        // ...
    }

    @Override
    public void onTokenAssigned() {
        this.hasToken = true;
    }

    @Override
    public void onNodeFinished() {
        this.finishedNodes++;
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
            launchSons("MainLamport");
            //HeavyweightProcess hwp = HeavyweightProcess();
        } else  {
            //heavyWeight ricardAgrawala
            launchSons("MainRicardAgrawala");
        }
    }
}
