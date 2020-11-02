package Lamport;

import Model.Message;
import Interfaces.LamportInterface;
import DataParser.Data;
import DataParser.Node;
import Network.DedicatedConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class LamportNode extends Thread implements LamportInterface {
    public static final int INFINITY = 2147483647;

    private ArrayList<Integer> q;
    private Vector<DedicatedConnection> dedicatedConnections;

    // Communication
    private DirectClock v;
    private ServerSocket serverSocket;
    private Node nodeInfo;
    private int myId;

    private Data nodeNetwork;
    private List<Integer> dependencyList;
    private int numNodes;

    public LamportNode(Data nodeNetwork, Integer myId) {
        this.nodeNetwork= nodeNetwork;
        this.myId= myId;
        this.nodeInfo = nodeNetwork.getNodes().get(myId);
        this.numNodes=nodeNetwork.getNodes().size();
        dedicatedConnections = new Vector<>();
        dependencyList= nodeNetwork.getNodes().stream().filter(e -> e.getConnectedTo().contains(myId)).map(Node::getNodeId).collect(Collectors.toList());
        v = new DirectClock(myId,numNodes);
        q = new ArrayList<>(Collections.nCopies(numNodes, INFINITY));
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
    public synchronized void releaseCS(){
        q.set(myId, INFINITY);
        Message msg = new Message("RELEASE", myId, v.getValue(myId));
        this.sendBroadcastMessage(msg);
    }

    public void startServer(){
        Runnable connectToServersRunnable = new Runnable() {
            public void run() {
                connectToServers();
            }
        };
        startListeningThread(connectToServersRunnable);
    }

    private synchronized void connectToNode(Node connectedNodeInfo) {
        DedicatedConnection ds= new DedicatedConnection(nodeInfo, null, connectedNodeInfo, null);
        dedicatedConnections.add(ds);
        ds.startServerConnection();
    }

    private void connectToServers(){
        for (Integer nodeId: nodeInfo.getConnectedTo()) {
            connectToNode(nodeNetwork.getNodes().get(nodeId));
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


    private synchronized void sendBroadcastMessage(Message msg) {
        for (DedicatedConnection dc : dedicatedConnections) {
            dc.sendTextAndObject(msg.getTag(), msg);
        }
    }
    public boolean isReady(){
        return dedicatedConnections.size()+1 == numNodes;
    }
    /**
     * This function manages all the connections made by clients
     */
    private void startListening(Runnable connectToServersRunnable) {
        try {
            serverSocket = new ServerSocket(nodeInfo.getPort());
            int nodesToConnect = dependencyList.size();

            while (nodesToConnect>0) {
                //System.out.println("Waiting for a client...");
                Socket socket = serverSocket.accept();
                DedicatedConnection dServer = new DedicatedConnection(socket, dedicatedConnections, nodeInfo, null);
                dedicatedConnections.add(dServer);
                dServer.start();
                nodesToConnect--;
            }
            connectToServersRunnable.run();

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
