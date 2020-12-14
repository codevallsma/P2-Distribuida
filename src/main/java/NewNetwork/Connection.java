package NewNetwork;

import Interfaces.NetworkCallback;
import Model.Message;
import DataParser.Node;
import Utils.*;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

enum ConnectionType {
    LIGHT_TO_LIGHT,
    LIGHT_TO_WEIGHT,
    WEIGHT_TO_LIGHT,
    WEIGHT_TO_WEIGHT,
}

/**
 * This class stores and manages all the information of each single user connected to the backend
 */
public abstract class Connection extends Thread {

    // General info
    protected Node ourNode;
    protected Node connectedNode;

    // Communication
    protected Socket socket;
    protected ObjectInputStream ois;
    protected DataOutputStream dos;
    protected DataInputStream dis;
    protected ObjectOutputStream oos;

    // Callback
    protected NetworkCallback callback;

    // Logic
    protected boolean isRunning;

    /**
     * Constructor of the dedicated server
     *  @param socket
     */
    public Connection(Socket socket, Node ourNode, NetworkCallback nodeCallback) {
        try{
            this.socket = socket;
            this.ourNode = ourNode;
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
    public Connection(Node ourNode, Node connectedNode, NetworkCallback nodeCallback)  {
        this.ourNode = ourNode;
        this.connectedNode = connectedNode;
        this.callback = nodeCallback;

        if (connectedNode == null) {
            //System.out.println("AQUI TAMBE CONNECTED NODE NULL");
        }
    }

    /**
     * Function that sets communication channels ready and starts thread process
     */
    public void initConnection() {
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
            onRunningProcess();
        } catch (Exception e) {
            onCatchingExceptions();
        } finally {
            onClosingProcess();
        }
    }

    protected void onRunningProcess() throws IOException, ClassNotFoundException {
        String incomingText;
        while (isRunning) {
            incomingText = dis.readUTF();
            managedInputMessage(incomingText);
        }
    }

    protected void onCatchingExceptions()  {}

    /**
     * Function that handles the incoming text
     * @param messsage
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected void managedInputMessage(String messsage) throws IOException, ClassNotFoundException {
        Message objectResponse;
        objectResponse = (Message)ois.readObject();
        callback.onMessageReceived(objectResponse);
    }

    protected void onClosingProcess() {
        try {
            ois.close();
            oos.close();
            dis.close();
            dos.close();
            socket.close();
        } catch (IOException e) {}
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
            System.out.println("Enviament de text fallit");
            isRunning = false;
            e.printStackTrace();
        }
    }

    public void sendTextAndObject(String text, Object objectToSend) {
        sendText(text);
        try {
            oos.writeObject(objectToSend);
        } catch (IOException e) {
            System.out.println("Enviament de text i objecte fallit");
            isRunning = false;
            e.printStackTrace();
        }
    }

    public void setRunningTrue() {
        isRunning = true;
    }
}
