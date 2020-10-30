package Network;

import Model.Message;
import Interfaces.Callback;
import JsonParse.Node;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

/**
 * This class stores and manages all the information of each single user connected to the backend
 */
public class DedicatedConnection extends Thread {

    // General info
    private Node connectedNode;
    private Node ourNode;
    private Vector<DedicatedConnection> dedicatedConnections;

    // Communication
    private Socket socket;
    private ObjectInputStream ois;
    private DataOutputStream dos;
    private DataInputStream dis;
    private ObjectOutputStream oos;

    private int connectionId;
    // Callback
    private Callback callback;

    // Logic
    private boolean isRunning;

    /**
     * Constructor of the dedicated server
     *  @param socket
     * @param dedicatedConnections
     */
    public DedicatedConnection(Socket socket, Vector<DedicatedConnection> dedicatedConnections, Node ourNode, Callback nodeCallback) {
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
    public DedicatedConnection(Node ourNode, Node infoConnectedNode, Callback nodeCallback)  {
        this.ourNode = ourNode;
        this.connectedNode = infoConnectedNode;
        this.callback = nodeCallback;
        if (connectedNode == null) {
            System.out.println("AQUI TAMBE CONNECTED NODE NULL");
        }
    }

    /**
     * Function that sets communication channels ready and starts thread process
     */
    public void startServerConnection() {

        try {
            InetAddress ip = InetAddress.getByName(connectedNode.getIp());
            this.socket = new Socket(ip, connectedNode.getPort());
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            isRunning = true;
            start();

        } catch (IOException e) {
            e.printStackTrace();
            stopServerConnection();
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
        callback.handleMsg(objectResponse);
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
            isRunning = false;
            e.printStackTrace();
        }
    }

    public void sendTextAndObject(String text, Object objectToSend) {
        sendText(text);
        try {
            oos.writeObject(objectToSend);
        } catch (IOException e) {
            isRunning = false;
            e.printStackTrace();
        }
    }

    // GETTERS AND SETTERS
    public String getConnectedNodeName() {
        //System.out.println("JsonParse.Node Name: " + connectedNode.getName());
        return connectedNode != null ? String.valueOf(connectedNode.getNodeId()) :"";
    }

}
