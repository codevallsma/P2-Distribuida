package NewNetwork;

import DataParser.Node;
import Interfaces.NetworkCallback;
import Model.Message;
import Network.DedicatedConnection;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class LightToLightConnection extends Connection {

    private List<Connection> dedicatedConnections;

    public static LightToLightConnection getInstance(Node ourNode, List<Connection> dedicatedConnections, Node infoConnectedNode, NetworkCallback nodeCallback) {
        return new LightToLightConnection(ourNode, dedicatedConnections, infoConnectedNode, nodeCallback);
    }

    public static LightToLightConnection getInstance(Socket socket, List<Connection> dedicatedConnections, Node ourNode, NetworkCallback nodeCallback) {
        return new LightToLightConnection(socket, dedicatedConnections, ourNode, nodeCallback);
    }

    private LightToLightConnection(Socket socket, List<Connection> dedicatedConnections, Node ourNode, NetworkCallback nodeCallback) {
        super(socket, ourNode, nodeCallback);
        this.dedicatedConnections = dedicatedConnections;
    }

    private LightToLightConnection(Node ourNode, List<Connection> dedicatedConnections, Node infoConnectedNode, NetworkCallback nodeCallback) {
        super(ourNode, infoConnectedNode, nodeCallback);
        this.dedicatedConnections = dedicatedConnections;
    }

    @Override
    protected void onCatchingExceptions() {
        dedicatedConnections.remove(this);
    }

    /**
     * Function that handles the incoming text
     * @param message
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    protected void managedInputMessage(String message) throws IOException, ClassNotFoundException {
        Message objectResponse;
        objectResponse = (Message)ois.readObject();
        callback.onMessageReceived(objectResponse);
    }

}
