package Network;

import DataParser.Data;
import DataParser.HeavyWeight;
import Interfaces.NetworkCallback;
import DataParser.Node;
import Model.Message;
import Utils.ThreadPoolManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class NetworkManager {

    private final Semaphore mutexConnections = new Semaphore(1);

    // General info
    private final Node nodeData;
    private final HeavyWeight nodeNetwork;
    private Vector<DedicatedConnection> connections;
    private DedicatedConnection hwConnection;
    private int numNodesToConnect;
    private ServerSocket serverSocket;

    // Callback
    private final NetworkCallback callback;

    // Logic
    private boolean isRunning;

    public NetworkManager(Node nodeData, HeavyWeight nodeNetwork, int numNodesToConnect, NetworkCallback callback) {
        this.nodeData = nodeData;
        this.nodeNetwork = nodeNetwork;
        this.numNodesToConnect = numNodesToConnect;
        this.callback = callback;
        this.connections = new Vector<>();
    }

    public boolean start() {
        isRunning=true;
        List<Callable> threads = new ArrayList<>();
        threads.add(new ConnectThread(nodeData));
        threads.add(new ListeningThread(nodeData, numNodesToConnect, connections, callback));
        List<Object> res = ThreadPoolManager.manage(threads);
        return true;
    }

    public synchronized void sendBroadcastMessage(Message msg) {
        try {
            mutexConnections.acquire();
            for (DedicatedConnection dc : connections) {
                dc.sendTextAndObject(msg.getTag(), msg);
            }
            mutexConnections.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void stopServer(){
        isRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private synchronized void connectToNode(Node connectedNodeInfo) {
        DedicatedConnection ds= new DedicatedConnection(nodeData, connections, connectedNodeInfo, callback);
        try {
            mutexConnections.acquire();
            connections.add(ds);
            mutexConnections.release();
            ds.startServerConnection();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getConnectionsSize() {
        try {
            mutexConnections.acquire();
            int res = connections != null ? connections.size():0;
            mutexConnections.release();
            return res;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /******************************************************************************************** */
    /*                                 PRIVATE INNER CLASSES                                      */
    /******************************************************************************************** */
    private class ConnectThread implements Callable {

        private Node nodeData;

        public ConnectThread(Node nodeData) {
            this.nodeData = nodeData;
        }

        @Override
        public Object call() throws Exception {
            for (Integer nodeId: nodeData.getConnectedTo()) {
                connectToNode(nodeNetwork.getNodes().get(nodeId));
            }
            return true;
        }
    }

    private class ListeningThread implements Callable {

        private Node nodeData;
        private int nodesToConnect;
        private Vector<DedicatedConnection> connections;
        private NetworkCallback callback;

        public ListeningThread(Node nodeData, int nodesToConnect, Vector<DedicatedConnection> connections, NetworkCallback callback) {
            this.nodeData = nodeData;
            this.nodesToConnect = nodesToConnect;
            this.connections = connections;
            this.callback = callback;
        }

        @Override
        public Object call() {
            try {
                serverSocket = new ServerSocket(nodeData.getPort());
                while (nodesToConnect > 0) {
                    //System.out.println("Waiting for a client...");
                    Socket socket = serverSocket.accept();
                    System.out.println("Soc el node " + nodeData.getNodeId() + " i sem conecten del port " +socket.getRemoteSocketAddress());
                    System.out.println("New connection from node "+ nodeData.getNodeId());
                    DedicatedConnection dServer = new DedicatedConnection(socket, connections, nodeData, callback);
                    mutexConnections.acquire();
                    connections.add(dServer);
                    mutexConnections.release();
                    dServer.setRunningTrue();
                    dServer.start();
                    nodesToConnect--;
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
