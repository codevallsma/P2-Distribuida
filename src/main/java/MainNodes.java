import JsonParse.JsonParser;
import JsonParse.Node;
import JsonParse.ParseOperation;
import Lamport.LamportNode;

public class MainNodes {
    public static void main(String[] args) {
        int nodeId = Integer.parseInt(args[0]);
        JsonParser nodeNetwork = ParseOperation.startParse();
        LamportNode h = new LamportNode(nodeNetwork,nodeId);
        h.startServer();
        while(!h.isReady());
        System.out.println("I've not crashed");
        h.doSomething();
    }
}
