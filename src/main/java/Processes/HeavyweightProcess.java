package Processes;

import Interfaces.NetworkCallback;
import Model.Message;
import Network.NetworkManager;

public class HeavyweightProcess implements NetworkCallback {

    private NetworkManager networkManager;



    /* *************************************************************************** */
    /*                            NETWORK CALLBACK                                 */
    /* *************************************************************************** */
    @Override
    public void onMessageReceived(Message msg) {

    }
}
