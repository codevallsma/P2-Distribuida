package Processes;

import DataParser.*;
import Interfaces.NetworkCallback;
import Model.Message;
import Network.NetworkManager;
import Utils.Utils;
import Utils.Launch;

import java.io.IOException;

public class HeavyweightProcess implements NetworkCallback {

    private HeavyWeight ourData;
    private HeavyWeight connectedTo;

    // Communication
    private NetworkManager networkManager;
    private int numNodes;

    // Token & Logic
    private boolean isRunning;
    private boolean hasToken;
    private boolean connectedIsReady;
    private int finishedNodes;

    public HeavyweightProcess(HeavyWeight data, HeavyWeight connectedTo, boolean hasToken) {
        this.ourData = data;
        this.connectedTo = connectedTo;
        this.networkManager = new NetworkManager(ourData, ourData, this);
        this.numNodes = data.getNodes().size();
        this.hasToken = hasToken;
        this.connectedIsReady = false;
        this.isRunning = false;
        this.finishedNodes = 0;
    }

    public void initBaseConnections() {
        this.networkManager.setNodesToConnect(ourData.getNodes().size());
        this.networkManager.startListening();
        if (ourData.getConnectToOther()) {
            this.networkManager.connectToHeavyWeight(connectedTo);
        }
    }

    public void doSomething() {
        this.networkManager.sendTextToHeavyWeight("HEAVYWEIGHT-READY");
        Utils.timeWait(500);
        isRunning = true;
        while(isRunning) {
            Utils.timeWait(2000);
            while (!connectedIsReady) {
                Utils.timeWait(1000);
                if (connectedIsReady) break;
            }
            while(!hasToken) {
                Utils.timeWait(1000);
            }
            this.networkManager.sendBroadcastMessage("SERVICE-START");
            while (finishedNodes < numNodes) {
                Utils.timeWait(1000);
                if (finishedNodes >= numNodes) break;
            }
            finishedNodes = 0;
            hasToken = false;
            this.networkManager.notifyHeavyWeight();
        }
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
    public void onHeavyReady() {
        this.connectedIsReady = true;
    }

    @Override
    public void onNodeFinished() {
        this.finishedNodes++;
    }

    public boolean isReady() {
        return this.networkManager.isHeavyWeightReady();
    }
}
