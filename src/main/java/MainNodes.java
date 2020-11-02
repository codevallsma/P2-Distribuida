import DataParser.Data;
import DataParser.Parser;
import Lamport.LamportNode;

public class MainNodes {
    public static long getPID() {
        String processName =
                java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }
    public static void main(String[] args) {
        System.out.println("It's eric modafaka bitch " + getPID());
        /*int nodeId = Integer.parseInt(args[0]);
        Data nodeNetwork = Parser.parseJson();
        LamportNode h = new LamportNode(nodeNetwork,nodeId);
        h.startServer();*/
        /*while(!h.isReady());

        h.doSomething();*/
    }
}