package Network;

import DataParser.Data;
import DataParser.HeavyWeight;
import DataParser.Node;
import Interfaces.NetworkCallback;
import Model.Message;
import Utils.ThreadPoolManager;
import Utils.Utils;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class ConnectionHeavyWeight  extends Thread{
    // General info
    private Node connectedNode;
    private Node ourNode;
    private Vector<DedicatedConnHw> dedicatedConnections;

    // Communication
    private Socket socket;
    private ObjectInputStream ois;
    private DataOutputStream dos;
    private DataInputStream dis;
    private ObjectOutputStream oos;

    private int connectionId;
    // Callback
    private NetworkCallback callback;

    // Logic
    private boolean isRunning;

    /**
     * Constructor of the dedicated server
     *  @param socket
     * @param dedicatedConnections
     */
    public ConnectionHeavyWeight(Socket socket, Vector<DedicatedConnHw> dedicatedConnections, Node ourNode, NetworkCallback nodeCallback) {
        try{
            this.socket = socket;
            this.ourNode = ourNode;
            this.dedicatedConnections = dedicatedConnections;
            callback = nodeCallback;oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor #2
     */
    public ConnectionHeavyWeight(Node ourNode, Vector<DedicatedConnHw> dedicatedConnections, Node infoConnectedNode, NetworkCallback nodeCallback)  {
        this.ourNode = ourNode;
        this.connectedNode = infoConnectedNode;
        this.callback = nodeCallback;
        this.dedicatedConnections = dedicatedConnections;

        if (connectedNode == null) {
            //System.out.println("AQUI TAMBE CONNECTED NODE NULL");
        }
    }

    /**
     * Function that sets communication channels ready and starts thread process
     */
    public void startServerConnection() {
        boolean connected = false;

        while(!connected) {
            Utils.timeWait(500);
            try {
                InetAddress ip = InetAddress.getByName(connectedNode.getIp());
                this.socket = new Socket(ip, connectedNode.getPort());
                connected = true;
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());
                isRunning = true;
                start();
            } catch (ConnectException exception){
                //System.out.println("Peta desde " + ourNode.getNodeId() + "cap a " + connectedNode.getNodeId());
                connected = false;
            } catch (IOException e) {
                e.printStackTrace();
                stopServerConnection();
            }
        }
    }

    /**
     * This function stops the thread that listens to the callbacks of the server
     */
    public void stopServerConnection() {
        isRunning = false;
        interrupt();
    }

    /**
     * Thread that listens all the requests the client sends
     */
    @Override
    public void run() {
        try {
            String incomingText;
            while (isRunning) {
                incomingText = dis.readUTF();
                managedInputMessage(incomingText);
            }

        } catch (IOException | ClassNotFoundException e) {
            dedicatedConnections.remove(this);
        } finally {
            try {
                ois.close();
                oos.close();
                dis.close();
                dos.close();
                socket.close();
            } catch (IOException e) {}
        }
    }

    /**
     * Function that handles the incoming text
     * @param messsage
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void managedInputMessage(String messsage) throws IOException, ClassNotFoundException {
        Message objectResponse;
        objectResponse = (Message)ois.readObject();
        callback.onMessageReceived(objectResponse);
    }

    /**
     * This function allows the client to send a text request to the server
     *
     * @param textToSend: text
     */
    public void sendText(String textToSend) {
        try {
            dos.writeUTF(textToSend);
        } catch (IOException e) {
            System.out.println("Enviamen de text fallit");
            isRunning = false;
            e.printStackTrace();
        }
    }

    public void sendTextAndObject(String text, Object objectToSend) {
        sendText(text);
        try {
            oos.writeObject(objectToSend);
        } catch (IOException e) {
            System.out.println("Enviamen de text i objecte fallit");
            isRunning = false;
            e.printStackTrace();
        }
    }

    // GETTERS AND SETTERS
    public String getConnectedNodeName() {
        //System.out.println("DataParser.Node Name: " + connectedNode.getName());
        return connectedNode != null ? String.valueOf(connectedNode.getName()) :"";
    }

    public void setRunningTrue() {
        isRunning = true;
    }
}