package Network;

import DataParser.Node;
import Interfaces.NetworkCallback;
import Model.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class LightToLightConnection extends Connection {

    private List<Connection> dedicatedConnections;

    public static LightToLightConnection getInstance(Node ourNode, List<Connection> dedicatedConnections, Node infoConnectedNode, NetworkCallback nodeCallback) {
        return new LightToLightConnection(ourNode, dedicatedConnections, infoConnectedNode, nodeCallback);
    }

    public static LightToLightConnection getInstance(Socket socket, boolean initStreams, List<Connection> dedicatedConnections, Node ourNode, NetworkCallback nodeCallback) {
        return new LightToLightConnection(socket, initStreams, dedicatedConnections, ourNode, nodeCallback);
    }

    private LightToLightConnection(Socket socket, boolean initStreams, List<Connection> dedicatedConnections, Node ourNode, NetworkCallback nodeCallback) {
        super(socket, initStreams, ourNode, nodeCallback);
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
        try {
        objectResponse = (Message) ois.readObject();
        //System.out.println("(" + ourNode.getName() + ") Object response from " + connectedNode.getName() + ": " + objectResponse.getSrc());
        callback.onMessageReceived(objectResponse);
        } catch  (Exception e) {
            System.err.println("Excepcio en la lectura de l'objecte!");
            e.printStackTrace();
        }
    }

}
