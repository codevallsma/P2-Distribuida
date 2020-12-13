import DataParser.Data;
import DataParser.Parser;
import Processes.LightweightProcess;

public class MainNodeLamport {
    public static long getPID() {
        String processName =
                java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }
    public static void main(String[] args) {
        //System.out.println("It's eric modafaka bitch " + getPID());
        int nodeId = Integer.parseInt(args[0]);
        Data nodeNetwork = Parser.parseJson("NetworkConfigLamport.json");
        LightweightProcess process = new LightweightProcess(nodeId, nodeNetwork);
        process.start();
        while (!process.isReady());
        process.doSomethingLamport();
    }
}