package Lamport;

import ClassesToSend.Message;
import Interfaces.Callback;
import Interfaces.LamportInterface;
import JsonParse.JsonParser;
import JsonParse.Node;
import JsonParse.ParseOperation;
import Network.DedicatedConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class LamportNode extends Thread implements Callback<Integer>, LamportInterface<Integer> {
    private DirectClock v;
    private ArrayList<Integer> q;
    private Vector<DedicatedConnection> dedicatedConnections;
    // Communication
    private ServerSocket serverSocket;
    private Node nodeInfo;
    private int myId;
    private boolean isRunning;
    //the max number for integer is 2147483647
    public static final int INFINITY = 2147483647;
    private JsonParser nodeNetwork;

    public LamportNode(JsonParser nodeNetwork, Integer myId) {
        this.nodeNetwork= nodeNetwork;
        this.myId= myId;
        this.nodeInfo = nodeNetwork.getNodes().get(myId);
    }

    @Override
    public void handleMsg(Message msg) {
        int timestamp = msg.getTimestamp();
        v.receiveAction(msg.getSrc(), msg.getTimestamp());
        switch (msg.getTag()){
            case "REQUEST":
                this.q.set(msg.getSrc(), timestamp);
                //send ack back
                    break;
            case "RELEASE":
                this.q.set(msg.getSrc(), LamportNode.INFINITY);
                break;
        }
    }

    @Override
    public boolean isGreater(int index1, int index2, int value2) {
       if(q.get(index2) == LamportNode.INFINITY) return false;
       return q.get(index1).compareTo(value2) > 0 || q.get(index1).compareTo(value2) == 0 && (index1 > index2);
    }

    /**
     * This is our active wait to check
     * @return: if returns false, we get permission to get the token
     */
    @Override
    public boolean okCS() {
        for(int i =0; i < q.size(); i++){
            if(this.isGreater(myId, i, this.q.get(i))){
                return false;
            } else if (this.isGreater(myId, i, this.v.getValue(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * The function we use to create a request
     */
    @Override
    public void requestCS() {
        //updating our clock of requests to the new value
        q.set(myId,v.requestAction());
        Message msg = new Message("REQUEST",myId, this.q.get(myId));
        //broadcast
        this.sendBroadcastMessage(msg);
        while (!okCS());
    }

    @Override
    public void accessCriticalZone() {
        System.out.println("Sóc el procés lightweight "+ this.myId);
    }

    private void timeWait(int duration) {
        try {
            sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doSomething(){
        while(true){
            //waitHeavyWeight();
            requestCS();
            for (int i=0; i<10; i++){
                if(!okCS()) {
                    accessCriticalZone();
                    timeWait(1000);
                } else {
                    requestCS();
                }
            }
            releaseCS();
            //notifyHeavyWeight();
        }
    }
    @Override
    public void onNewNode(Message msg) {
        if(this.q.size() <= msg.getSrc()){
            //the new node with the new index has to fit in our arraylist
            this.q.ensureCapacity(msg.getSrc() +1);
            this.v.getClock().ensureCapacity(msg.getSrc() +1);
        }
        this.v.addNewNode(msg.getSrc());
        this.q.set(msg.getSrc(),LamportNode.INFINITY);
    }

    @Override
    public synchronized void releaseCS(){
        q.set(myId, INFINITY);
        Message msg = new Message("RELEASE", myId, v.getValue(myId));
        this.sendBroadcastMessage(msg);
    }
    @Override
    public void startServer(){
        startListeningThread();
        connectToServersThread();
    }
    public void connectToServersThread(){
        Runnable service2 = new Runnable() {
            public void run() {
                connectToServers();
            }
        };
        new Thread(service2).start();
    }
    private synchronized void connectToNode(Node connectedNodeInfo) {
        DedicatedConnection ds= new DedicatedConnection(nodeInfo, connectedNodeInfo, this);
        dedicatedConnections.add(ds);
        ds.startServerConnection();
    }

    private void connectToServers(){
        for (Integer nodeId: nodeInfo.getConnectedTo()) {
            connectToNode(nodeNetwork.getNodes().get(nodeId));
        }
    }

    @Override
    public void startListeningThread() {
        Runnable service2 = new Runnable() {
            public void run() {
                startListening();
            }
        };
        new Thread(service2).start();
    }
    private synchronized void sendBroadcastMessage(Message msg) {
        for (DedicatedConnection dc : dedicatedConnections) {
            dc.sendTextAndObject(msg.getTag(), msg);
        }
    }
    /**
     * This function manages all the connections made by clients
     */
    private void startListening() {
        try {
            serverSocket = new ServerSocket(nodeInfo.getPort());
            isRunning = true;

            while (isRunning) {
                //System.out.println("Waiting for a client...");
                Socket socket = serverSocket.accept();
                //System.out.println("Client connected to JsonParse.Node " + nodeInfo.getName());
                DedicatedConnection dServer = new DedicatedConnection(socket, dedicatedConnections, nodeInfo, this);
                dedicatedConnections.add(dServer);
                dServer.startServerConnection();
            }


        } catch (IOException e) {
            System.err.println(e.getMessage());
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
}
