package Processes.HeavyWeight;

import DataParser.*;
import Interfaces.NetworkCallback;
import Model.Message;
import Mutex.LamportMutex;
import Network.DedicatedConnection;
import NewNetwork.NetworkManager;
import Utils.Utils;
import Utils.Launch;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class HeavyweightProcess implements NetworkCallback {

    private int myId;
    private HeavyWeight ourData;
    private HeavyWeight connectedTo;

    // Communication
    private NetworkManager networkManager;
    private int numNodes;

    public HeavyweightProcess(int id, HeavyWeight data, HeavyWeight connectedTo, NetworkManager networkManager) {
        this.myId = id;
        this.ourData = data;
        this.connectedTo = connectedTo;
        this.networkManager = networkManager;
        this.numNodes = data.getNodes().size();
    }

    public void initBaseConnections() {
        this.networkManager.connectToHeavyWeight(connectedTo);
        this.networkManager;

    }

    /* *************************************************************************** */
    /*                            NETWORK CALLBACK                                 */
    /* *************************************************************************** */
    @Override
    public void onMessageReceived(Message msg) {

    }

    @Override
    public void onInitService(boolean init) {

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
