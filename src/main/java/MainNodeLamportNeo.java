import DataParser.Data;
import DataParser.HeavyWeight;
import DataParser.LightWeight;
import DataParser.Parser;
import Mutex.MutexType;
import Processes.LightWeightProcess;

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
        LightWeightProcess process = new LightWeightProcess(nodeId, lw, hw, MutexType.LAMPORT);
        process.initBaseConnections();
        //System.out.println("Esperant a estar ready...");
        while (!process.isReady()) {
            Utils.Utils.timeWait(5000);
        }
        //System.out.println("Ready!-----");
        process.doSomething();
    }
}