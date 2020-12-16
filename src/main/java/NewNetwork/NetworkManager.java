package NewNetwork;

import DataParser.HeavyWeight;
import DataParser.LightWeight;
import DataParser.Node;
import Interfaces.ConnectionCallback;
import Interfaces.NetworkCallback;
import Model.Message;
import Utils.ThreadPoolManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class NetworkManager implements ConnectionCallback {
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
                heavyWeightConnection.initConnection(NetworkManager.this);
            }
        }).start();
    }

    public void connectToNode(Node node) {
        //System.out.println("(" + ourNode.getName() + ") Connecting to " + node.getName());
        Connection ds;
        if (node instanceof HeavyWeight) {
            ds = LightToHeavyConnection.getInstance(ourNode, node, callback);
        } else {
            ds = LightToLightConnection.getInstance(ourNode, connections, node, callback);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ds.initConnection(NetworkManager.this);
            }
        }).start();
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

    public synchronized void sendBroadcastMessage(Message msg) {
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

    public synchronized void sendMessageToDedicatedConnection(int myId, int nodeId, int queueValue){
        Message m = new Message("OKAY",myId, queueValue);
        connections.stream().filter(e-> e.getDstID() == nodeId).findFirst().get().sendTextAndObject("OKAY",m);
    }

    public void sendTextToHeavyWeight(String msg) {
        this.heavyWeightConnection.sendText(msg);
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

    public boolean isHeavyWeightReady() {
        boolean first_test = this.heavyWeightConnection != null;
        boolean second_test = this.nodesConnected == this.nodesToConnect;
        return first_test && second_test;
    }

    /******************************************************************************************** */
    /*                               CONNECTION CALLBACK                                          */
    /******************************************************************************************** */

    @Override
    public void onConnectionSuccess(Connection connection) {
        try {
            mutexConnections.acquire();
            connections.add(connection);
            mutexConnections.release();
        } catch (InterruptedException e) {
            isRunning = false;
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailure(Node nodeToConnect) {
        // ...
    }

    @Override
    public void onConnectionTypeKnown(Connection connection) {

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
            Utils.Utils.timeWait(500);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

            Connection res;
            String msg = dis.readUTF();
            if (msg.equals("LIGHTWEIGHT")) {
                if (isLightWeight) res = LightToLightConnection.getInstance(s, false, connections, ourNode, callback);
                else res = HeavyToLightConnection.getInstance(s, false, ourNode, callback);
            } else {
                if (isLightWeight) res = LightToHeavyConnection.getInstance(s, false, ourNode, callback);
                else res = HeavyToHeavyConnection.getInstance(s, false, ourNode, callback);
            }
            System.out.println("(" + ourNode.getName() + ") New connection accepted: " + msg);
            dos.writeUTF("REPLY");
            res.setStreams(oos, dos, dis, ois);
            return res;
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(ourNode.getPort());
                while (nodesToConnect > nodesConnected) {
                    //System.out.println("Waiting for a client...");
                    Socket socket = serverSocket.accept();

                    Connection res = checkConnectionType(socket);
                    //Connection res = LightToLightConnection.getInstance(socket, connections, ourNode, callback);
                    if (res instanceof LightToLightConnection || res instanceof HeavyToLightConnection) {
                        mutexConnections.acquire();
                        connections.add(res);
                        mutexConnections.release();
                        nodesConnected++;
                        //System.out.println("LightWeight connectat.");
                    } else {
                        heavyWeightConnection = res;
                        //System.out.println("HeavyWeight's connectats!");
                    }
                    res.setRunningTrue();
                    res.start();
                }
                System.out.println("Tots els nodes connectats");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
