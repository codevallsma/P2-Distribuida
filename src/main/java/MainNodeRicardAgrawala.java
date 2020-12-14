import DataParser.Data;
import DataParser.HeavyWeight;
import DataParser.Parser;
import Processes.LightWeight.LightweightProcess;

import java.util.List;
import java.util.stream.Collectors;

public class MainNodeRicardAgrawala {
    public static void main(String[] args) {
        //System.out.println("It's eric modafaka bitch " + getPID());
        int nodeId = Integer.parseInt(args[0]);
        Data nodeNetwork = Parser.parseJson("NetworkConfigLamport.json");
        HeavyWeight hw =nodeNetwork.getHeavyWeights().stream().filter(e -> e.getType().compareTo("Ricard-Agrawala")==0).findFirst().get();
        List<HeavyWeight> hwToCoonnect = nodeNetwork.getHeavyWeights().stream().filter(e -> e.getType().compareTo("Ricard-Agrawala")!=0).collect(Collectors.toList());
        LightweightProcess process = new LightweightProcess(nodeId, hw,hwToCoonnect);
        process.start();
        while (!process.isReady());
        process.doSomethingLamport();
    }
}
