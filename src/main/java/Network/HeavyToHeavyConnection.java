package Network;

import DataParser.HeavyWeight;
import DataParser.Node;
import Interfaces.NetworkCallback;

import java.io.IOException;
import java.net.Socket;

public class HeavyToHeavyConnection extends Connection {

    public static HeavyToHeavyConnection getInstance(Node ourNode, Node infoConnectedNode, NetworkCallback nodeCallback) {
        return new HeavyToHeavyConnection(ourNode, infoConnectedNode, nodeCallback);
    }

    public static HeavyToHeavyConnection getInstance(Socket socket, boolean initStreams, Node ourNode, NetworkCallback nodeCallback) {
        return new HeavyToHeavyConnection(socket, initStreams, ourNode, nodeCallback);
    }

    private HeavyToHeavyConnection(Socket socket, boolean initStreams, Node ourNode, NetworkCallback nodeCallback) {
        super(socket, initStreams, ourNode, nodeCallback);
    }

    private HeavyToHeavyConnection(Node ourNode, Node infoConnectedNode, NetworkCallback nodeCallback) {
        super(ourNode, infoConnectedNode, nodeCallback);
    }

    @Override
    protected void onRunningProcess() throws IOException, ClassNotFoundException {
        if (((HeavyWeight)ourNode).getConnectToOther()) {
            sendText("HEAVYWEIGHT-REQUEST");
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
            case "HEAVYWEIGHT-READY":
                System.out.println("(" + ourNode.getName() + ") Enviament ready rebut! -----------------");
                this.callback.onHeavyReady();
                break;
            case "TOKEN-REQUEST":
                // to be implemented
                break;
            case "TOKEN-ASSIGNATION":
                this.callback.onTokenAssigned();
                break;
        }
    }

    /* *************************************************************************** */
    /*                              NETWORK CALLBACK                               */
    /* *************************************************************************** */

}
