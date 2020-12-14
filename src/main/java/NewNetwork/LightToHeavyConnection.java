package NewNetwork;

import DataParser.Node;
import Interfaces.NetworkCallback;

import java.io.IOException;
import java.net.Socket;

public class LightToHeavyConnection extends Connection {

    public static LightToHeavyConnection getInstance(Node ourNode, Node infoConnectedNode, NetworkCallback nodeCallback) {
        return new LightToHeavyConnection(ourNode, infoConnectedNode, nodeCallback);
    }

    public LightToHeavyConnection(Socket socket, Node ourNode, NetworkCallback nodeCallback) {
        super(socket, ourNode, nodeCallback);
    }

    private LightToHeavyConnection(Node ourNode, Node infoConnectedNode, NetworkCallback nodeCallback) {
        super(ourNode, infoConnectedNode, nodeCallback);
    }

    @Override
    protected void onRunningProcess() throws IOException, ClassNotFoundException {
        sendText("SESSION-IN");
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
            case "SESSION-CONFIRMED":
                // to be implemented
                break;
            case "HEAVYWEIGHT-REPLY":
                // to be implemented
                // when
                break;
            case "SERVICE-START":
                // to be implemented
                break;
        }
    }

}
