import DataParser.Data;
import DataParser.HeavyWeight;
import DataParser.LightWeight;
import DataParser.Parser;
import Mutex.MutexType;
import Processes.LightWeight.LamportLightWeight;
import Processes.LightWeight.LightweightProcess;

public class MainNodeLamportNeo {
    public static long getPID() {
        String processName =
                java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }
    public static void main(String[] args) {
        int nodeId = Integer.parseInt(args[0]);
        Data nodeNetwork = Parser.parseJson("NetworkConfigLamport.json");
        HeavyWeight hw =nodeNetwork.getHeavyWeights().stream().filter(e -> e.getType().compareTo("Lamport")==0).findFirst().get();
        LightWeight lw = (LightWeight) hw.getNodes().get(nodeId);
        LamportLightWeight process = new LamportLightWeight(nodeId, lw, hw, MutexType.LAMPORT);
        process.initBaseConnections();
        while (!process.isReady());
        process.doSomething();
    }
}