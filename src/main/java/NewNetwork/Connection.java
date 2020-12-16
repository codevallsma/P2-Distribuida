package NewNetwork;

import DataParser.LightWeight;
import Interfaces.ConnectionCallback;
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
    protected int dstId;

    // Communication
    protected Socket socket;
    protected ObjectInputStream ois;
    protected DataOutputStream dos;
    protected DataInputStream dis;
    protected ObjectOutputStream oos;

    // Callback
    protected NetworkCallback callback;
    protected boolean replied;

    // Logic
    protected boolean isRunning;

    /**
     * Constructor of the dedicated server
     *  @param socket
     */
    public Connection(Socket socket, boolean initStreams, Node ourNode, NetworkCallback nodeCallback) {
        try{
            this.socket = socket;
            this.ourNode = ourNode;
            callback = nodeCallback;
            this.replied = false;
            if (initStreams) {
                oos = new ObjectOutputStream(socket.getOutputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());
                ois = new ObjectInputStream(socket.getInputStream());
            }
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
        this.replied = false;
        if (connectedNode == null) {
            //System.out.println("AQUI TAMBE CONNECTED NODE NULL");
        }
    }

    public void setStreams(ObjectOutputStream oos, DataOutputStream dos, DataInputStream dis, ObjectInputStream ois) throws IOException {
        this.oos = oos;
        this.oos.flush();
        this.dos = dos;
        this.dos.flush();
        this.dis = dis;
        this.ois = ois;
    }

    /**
     * Function that sets communication channels ready and starts thread process
     */
    public void initConnection(final ConnectionCallback callback) {
        boolean connected = false;

        while(!connected) {
            try {
                InetAddress ip = InetAddress.getByName(connectedNode.getIp());
                this.socket = new Socket(ip, connectedNode.getPort());
                connected = true;
                oos = new ObjectOutputStream(socket.getOutputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                isRunning = true;

                dos.writeUTF("LIGHTWEIGHT");
                dos.flush();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (dis.readUTF().equals("REPLY")) {
                                replied = true;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                while (!replied) {
                    Utils.timeWait(1000);
                    //dos.writeUTF("LIGHTWEIGHT");
                }
                //System.out.println("(" + ourNode.getName() + ") Connected to " + connectedNode.getName());
                callback.onConnectionSuccess(this);
                start();
            } catch (ConnectException exception){
                System.out.println("(" + ourNode.getName() + ") No s'ha pogut establir connexió");
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
            System.out.println("(" + ourNode.getName() + ") Closing...");
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
            System.out.println("(" + ourNode.getName() + ") Enviament de text fallit");
            isRunning = false;
            e.printStackTrace();
        }
    }

    public synchronized void sendTextAndObject(String text, Message objectToSend) {
        sendText(text);
        try {
            //oos.flush();
            this.oos.writeObject(objectToSend);
            oos.flush();
        } catch (IOException e) {
            System.out.println("(" + ourNode.getName() + ") Enviament d'objecte fallit");
            isRunning = false;
            e.printStackTrace();
        }
    }

    public void setRunningTrue() {
        isRunning = true;
    }

    public int getDstID() {
        return this.dstId;
    }

}
