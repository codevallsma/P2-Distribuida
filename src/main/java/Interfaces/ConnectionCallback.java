package Interfaces;

import DataParser.Node;
import Network.Connection;

public interface ConnectionCallback {
    void onConnectionSuccess(Connection connection);
    void onConnectionFailure(Node nodeToConnect);
    void onConnectionTypeKnown(Connection connection);
    //void onConnectionClosed(Connection connection);
}
