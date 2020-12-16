package Interfaces;

import DataParser.Node;
import NewNetwork.Connection;

public interface ConnectionCallback {
    void onConnectionSuccess(Connection connection);
    void onConnectionFailure(Node nodeToConnect);
    void onConnectionTypeKnown(Connection connection);
    //void onConnectionClosed(Connection connection);
}
