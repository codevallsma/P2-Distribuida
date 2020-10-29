import JsonParse.JsonParser;
import JsonParse.Node;
import JsonParse.ParseOperation;

public class MainNodes {
    public static void main(String[] args) {
        int nodeId = Integer.parseInt(args[1]);
        JsonParser nodeNetwork = ParseOperation.startParse();
        Node nodeSelf= nodeNetwork.getNodes().get(nodeId);
    }
}
