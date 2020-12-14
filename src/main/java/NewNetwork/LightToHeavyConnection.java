package NewNetwork;

import DataParser.Node;
import Interfaces.NetworkCallback;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class LightToHeavyConnection extends Connection {

    public static LightToHeavyConnection getInstance(Node ourNode, Node infoConnectedNode, NetworkCallback nodeCallback) {
        return new LightToHeavyConnection(ourNode, infoConnectedNode, nodeCallback);
    }

    public static LightToHeavyConnection getInstance(Socket socket, Node ourNode, NetworkCallback nodeCallback) {
        return new LightToHeavyConnection(socket, ourNode, nodeCallback);
    }

    private LightToHeavyConnection(Socket socket, Node ourNode, NetworkCallback nodeCallback) {
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
            case "SERVICE-START":
                callback.onInitService(true);
                break;
            case "MESSAGE-RECEIVED":
                break;
        }
    }





}
