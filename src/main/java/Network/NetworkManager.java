package Network;

import DataParser.Data;
import Interfaces.NetworkCallback;
import DataParser.Node;
import Model.Message;
import Processes.LightweightProcess;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class NetworkManager {

    private final Semaphore mutexConnections = new Semaphore(1);

    // General info
    private final Node nodeData;
    private final Data nodeNetwork;
    private Vector<DedicatedConnection> connections;
    private int numNodesToConnect;
    private ServerSocket serverSocket;

    // Callback
    private final NetworkCallback callback;

    // Logic
    private boolean isRunning;

    public NetworkManager(Node nodeData, Data nodeNetwork, int numNodesToConnect, NetworkCallback callback) {
        this.nodeData = nodeData;
        this.nodeNetwork = nodeNetwork;
        this.numNodesToConnect = numNodesToConnect;
        this.callback = callback;
        this.connections = new Vector<>();
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

    public void startListeningThread(Runnable connectToServersRunnable) {
        Runnable service2 = new Runnable() {
            public void run() {
                startListening(connectToServersRunnable);
            }
        };
        new Thread(service2).start();
    }

    public void startServer(){
        Runnable connectToServersRunnable = new Runnable() {
            public void run() {
                connectToServers();
            }
        };
        startListeningThread(connectToServersRunnable);
    }

    /**
     * This function manages all the connections made by clients
     */
    private void startListening(Runnable connectToServersRunnable) {
        try {
            serverSocket = new ServerSocket(nodeData.getPort());
            int nodesToConnect = this.numNodesToConnect;

            while (nodesToConnect>0) {
                //System.out.println("Waiting for a client...");
                Socket socket = serverSocket.accept();
                DedicatedConnection dServer = new DedicatedConnection(socket, connections, nodeData, callback);
                mutexConnections.acquire();
                connections.add(dServer);
                mutexConnections.release();
                dServer.start();
                nodesToConnect--;
            }
            connectToServersRunnable.run();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private void connectToServers(){
        for (Integer nodeId: nodeData.getConnectedTo()) {
            connectToNode(nodeNetwork.getNodes().get(nodeId));
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
}
