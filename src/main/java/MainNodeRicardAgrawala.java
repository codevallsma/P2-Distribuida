import DataParser.Data;
import DataParser.HeavyWeight;
import DataParser.LightWeight;
import DataParser.Parser;
import Mutex.MutexType;
import Processes.LightWeightProcess;

public class MainNodeRicardAgrawala {
    public static void main(String[] args) {
        //System.out.println("It's eric modafaka bitch " + getPID());
        int nodeId = Integer.parseInt(args[0]);
        Data nodeNetwork = Parser.parseJson("NetworkConfigLamport.json");
        HeavyWeight hw =nodeNetwork.getHeavyWeights().stream().filter(e -> e.getType().compareTo("Ricard-Agrawala")==0).findFirst().get();
        LightWeight lw = (LightWeight) hw.getNodes().get(nodeId);
        //List<HeavyWeight> hwToCoonnect = nodeNetwork.getHeavyWeights().stream().filter(e -> e.getType().compareTo("Ricard-Agrawala")!=0).collect(Collectors.toList());
        LightWeightProcess process = new LightWeightProcess(nodeId, lw, hw, MutexType.RICARD_AGRAWALA);
        process.initBaseConnections();
        //System.out.println("Esperant a estar ready...");
        while (!process.isReady()) {
            Utils.Utils.timeWait(5000);
        }
        process.doSomething();
    }
}
