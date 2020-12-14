package NewNetwork;

import DataParser.HeavyWeight;
import DataParser.LightWeight;
import DataParser.Node;
import Interfaces.NetworkCallback;
import Network.DedicatedConnection;
import Utils.ThreadPoolManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class NetworkManager {
    private final Semaphore mutexConnections = new Semaphore(1);

    // General info
    private final Node ourNode;
    private final HeavyWeight nodeNetwork;

    // Connections
    private List<Connection> connections;
    private Connection heavyWeightConnection;
    private int nodesToConnect;

    // Communication
    private ServerSocket serverSocket;

    // Callback
    private final NetworkCallback callback;

    // Logic
    private boolean isRunning;

    public NetworkManager(Node ourNode, HeavyWeight nodeNetwork, NetworkCallback callback) {
        this.ourNode = ourNode;
        this.nodeNetwork = nodeNetwork;
        this.callback = callback;
        this.nodesToConnect = 0;
        this.connections = new ArrayList<>();
    }

    public boolean start() {
        isRunning=true;
        List<Callable> threads = new ArrayList<>();
        //threads.add(new ListeningThread(nodeData, numNodesToConnect, connections, callback));
        List<Object> res = ThreadPoolManager.manage(threads);
        return true;
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

    public void connectToHeavyWeight() {
        if (ourNode instanceof HeavyWeight) {
            heavyWeightConnection = HeavyToHeavyConnection.getInstance(ourNode, nodeNetwork, callback);
        } else {
            heavyWeightConnection = LightToHeavyConnection.getInstance(ourNode, nodeNetwork, callback);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                heavyWeightConnection.initConnection();
            }
        }).start();
    }

    public void connectToNode(Node node) {
        Connection ds;
        if (node instanceof HeavyWeight) {
            ds = LightToHeavyConnection.getInstance(ourNode, node, callback);
        } else {
            ds = LightToLightConnection.getInstance(ourNode, connections, node, callback);
        }
        try {
            mutexConnections.acquire();
            connections.add(ds);
            mutexConnections.release();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ds.initConnection();
                }
            }).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /******************************************************************************************** */
    /*                                 GETTER's & SETTER's                                        */
    /******************************************************************************************** */
    public void setNodesToConnect(int nodesToConnect) {
        this.nodesToConnect = nodesToConnect;
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
            for (Integer nodeId: ((LightWeight) nodeData).getConnectedTo()) {
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
                    System.out.println("Soc el node " + nodeData.getName() + " i sem conecten del port " +socket.getRemoteSocketAddress());
                    System.out.println("New connection from node "+ nodeData.getName());
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
