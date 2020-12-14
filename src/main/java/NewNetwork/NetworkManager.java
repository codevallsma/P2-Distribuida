package NewNetwork;

import DataParser.HeavyWeight;
import DataParser.LightWeight;
import DataParser.Node;
import Interfaces.NetworkCallback;
import Model.Message;
import Utils.ThreadPoolManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class NetworkManager {
    private final Semaphore mutexConnections = new Semaphore(1);

    // General info
    private final Node ourNode;
    private final HeavyWeight nodeNetwork;
    private boolean isLightWeight;

    // Connections
    private List<Connection> connections;
    private Connection heavyWeightConnection;
    private int nodesToConnect;
    private int nodesConnected;

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
        this.nodesConnected = 0;
        this.connections = new ArrayList<>();
        this.isLightWeight = ourNode instanceof LightWeight;
    }

    public boolean start() {
        isRunning=true;
        List<Callable> threads = new ArrayList<>();
        //threads.add(new ListeningThread(nodeData, numNodesToConnect, connections, callback));
        List<Object> res = ThreadPoolManager.manage(threads);
        return true;
    }
    public void startListening() {
        Thread t = new ListeningThread();
        t.start();
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

    public void connectToHeavyWeight(Node heavyweight) {
        if (ourNode instanceof HeavyWeight) {
            heavyWeightConnection = HeavyToHeavyConnection.getInstance(ourNode, heavyweight, callback);
        } else {
            heavyWeightConnection = LightToHeavyConnection.getInstance(ourNode, heavyweight, callback);
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

    public void sendBroadcastMessage(String msg) {
        try {
            mutexConnections.acquire();
            for (Connection dc : connections) {
                dc.sendText(msg);
            }
            mutexConnections.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public  void sendBroadcastMessage(Message msg) {
        try {
            mutexConnections.acquire();
            for (Connection dc : connections) {
                dc.sendTextAndObject(msg.getTag(), msg);
            }
            mutexConnections.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void notifyHeavyWeight() {
        if (heavyWeightConnection != null) {
            if (ourNode instanceof LightWeight) {
                heavyWeightConnection.sendText("SERVICE-EXECUTED");
            } else {
                heavyWeightConnection.sendText("TOKEN-ASSIGNATION");
            }
        }
    }

    /******************************************************************************************** */
    /*                                 GETTER's & SETTER's                                        */
    /******************************************************************************************** */
    public void setNodesToConnect(int nodesToConnect) {
        this.nodesToConnect = nodesToConnect;
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
            for (Integer nodeId: ((LightWeight) nodeData).getConnectedTo()) {
                connectToNode(nodeNetwork.getNodes().get(nodeId));
            }
            return true;
        }
    }

    private class ListeningThread extends Thread {

        private Connection checkConnectionType(Socket s) throws IOException {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            Connection res;
            if (dis.readUTF().equals("LIGHTWEIGHT")) {
                if (isLightWeight) res = LightToLightConnection.getInstance(s, connections, ourNode, callback);
                else res = HeavyToLightConnection.getInstance(s, ourNode, callback);
            } else {
                if (isLightWeight) res = LightToHeavyConnection.getInstance(s, ourNode, callback);
                else res = HeavyToHeavyConnection.getInstance(s, ourNode, callback);
            }
            dis.close();
            return res;
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(ourNode.getPort());
                while (nodesToConnect > nodesConnected) {
                    //System.out.println("Waiting for a client...");
                    Socket socket = serverSocket.accept();
                    System.out.println("Soc el node " + ourNode.getName());

                    Connection res = checkConnectionType(socket);
                    if (res instanceof LightToLightConnection || res instanceof HeavyToLightConnection) {
                        mutexConnections.acquire();
                        connections.add(res);
                        mutexConnections.release();
                        nodesConnected++;
                        System.out.println("LightWeight connectat.");
                    } else {
                        heavyWeightConnection = res;
                        System.out.println("HeavyWeight's connectats!");
                    }
                    res.setRunningTrue();
                    res.start();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
