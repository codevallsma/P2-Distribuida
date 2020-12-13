package Processes.HeavyWeight;

import DataParser.Data;
import DataParser.HeavyWeight;
import DataParser.Parser;
import Interfaces.NetworkCallback;
import Model.Message;
import Network.NetworkManager;
import Utils.Utils;
import Utils.Launch;

import java.io.IOException;

public class HeavyweightProcess implements NetworkCallback {

    private NetworkManager networkManager;

    public HeavyweightProcess(Data data) {
        this.networkManager = networkManager;
    }

    /* *************************************************************************** */
    /*                            NETWORK CALLBACK                                 */
    /* *************************************************************************** */
    @Override
    public void onMessageReceived(Message msg) {

    }

    void launchLightWeightLamport() {
        String[] commands= {
                Utils.getCommand("MainNodes","'2', NetworkConfigLamport.json" ),
                Utils.getCommand("MainNodes","'1', NetworkConfigLamport.json" ),
                Utils.getCommand("MainNodes","'0', NetworkConfigLamport.json" )
        };
        try {
            Launch.launchProcesses(commands);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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
            launchSons("MainLamport");
            //HeavyweightProcess hwp = HeavyweightProcess();
        } else  {
            //heavyWeight ricardAgrawala
            launchSons("MainRicardAgrawala");
        }
    }
}
