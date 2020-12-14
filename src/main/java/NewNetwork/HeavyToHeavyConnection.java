package NewNetwork;

import DataParser.HeavyWeight;
import DataParser.Node;
import Interfaces.NetworkCallback;

import java.io.IOException;
import java.net.Socket;

public class HeavyToHeavyConnection extends Connection {

    public static HeavyToHeavyConnection getInstance(Node ourNode, Node infoConnectedNode, NetworkCallback nodeCallback) {
        return new HeavyToHeavyConnection(ourNode, infoConnectedNode, nodeCallback);
    }

    public HeavyToHeavyConnection(Socket socket, Node ourNode, NetworkCallback nodeCallback) {
        super(socket, ourNode, nodeCallback);
    }

    private HeavyToHeavyConnection(Node ourNode, Node infoConnectedNode, NetworkCallback nodeCallback) {
        super(ourNode, infoConnectedNode, nodeCallback);
    }

    @Override
    protected void onRunningProcess() throws IOException, ClassNotFoundException {
        if (((HeavyWeight)ourNode).getConnectToOther()) {
            sendText("SESSION-IN");
        }
        String incomingText;
        while (isRunning) {
            incomingText = dis.readUTF();
            managedInputMessage(incomingText);
        }
    }

    /**
     * Function that handles the incoming text
     * @param message
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    protected void managedInputMessage(String message) throws IOException, ClassNotFoundException {
        switch (message) {
            case "HEAVYWEIGHT-REQUEST":
                // to be implemented
                break;
            case "HEAVYWEIGHT-REPLY":
                // to be implemented
                break;
            case "TOKEN-REQUEST":
                // to be implemented
                break;
            case "TOKEN-ASSIGNATION":
                // to be implemented
                break;
        }
    }

    /* *************************************************************************** */
    /*                              NETWORK CALLBACK                               */
    /* *************************************************************************** */

}
