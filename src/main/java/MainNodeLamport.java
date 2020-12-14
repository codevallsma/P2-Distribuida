import DataParser.Data;
import DataParser.HeavyWeight;
import DataParser.Node;
import DataParser.Parser;
import Processes.LightWeight.LightweightProcess;

import java.util.List;
import java.util.stream.Collectors;

public class MainNodeLamport {
    public static long getPID() {
        String processName =
                java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }
    public static void main(String[] args) {
        int nodeId = Integer.parseInt(args[0]);
        Data nodeNetwork = Parser.parseJson("NetworkConfigLamport.json");
        HeavyWeight hw =nodeNetwork.getHeavyWeights().stream().filter(e -> e.getType().compareTo("Lamport")==0).findFirst().get();
        List<HeavyWeight> hwToCoonnect = nodeNetwork.getHeavyWeights().stream().filter(e -> e.getType().compareTo("Lamport")!=0).collect(Collectors.toList());
        LightweightProcess process = new LightweightProcess(nodeId,hw, hwToCoonnect);
        process.start();
        while (!process.isReady());
        process.doSomethingLamport();
    }
}