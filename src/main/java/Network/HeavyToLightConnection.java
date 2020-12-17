package Network;

import DataParser.Node;
import Interfaces.NetworkCallback;

import java.io.IOException;
import java.net.Socket;

public class HeavyToLightConnection extends Connection {

    public static HeavyToLightConnection getInstance(Node ourNode, Node infoConnectedNode, NetworkCallback nodeCallback) {
        return new HeavyToLightConnection(ourNode, infoConnectedNode, nodeCallback);
    }

    public static HeavyToLightConnection getInstance(Socket socket, boolean initStreams, Node ourNode, NetworkCallback nodeCallback) {
        return new HeavyToLightConnection(socket, initStreams, ourNode, nodeCallback);
    }

    private HeavyToLightConnection(Socket socket, boolean initStreams, Node ourNode, NetworkCallback nodeCallback) {
        super(socket, initStreams, ourNode, nodeCallback);
    }

    private HeavyToLightConnection(Node ourNode, Node infoConnectedNode, NetworkCallback nodeCallback) {
        super(ourNode, infoConnectedNode, nodeCallback);
    }

    @Override
    protected void onRunningProcess() throws IOException, ClassNotFoundException {
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
        System.out.println("\n(" + ourNode.getName() + ") Missatge rebut: " + message);
        switch (message) {
            case "SESSION-IN":
                // to be implemented
                dos.writeUTF("SESSION-CONFIRMED");
                break;
            case "SERVICE-EXECUTED":
                // when a lightweight finishes printing 10 times
                dos.writeUTF("MESSAGE-RECEIVED");
                this.callback.onNodeFinished();
                // to be implemented
                break;
            case "SERVICE-FINISHED":
                break;
        }
    }

}
