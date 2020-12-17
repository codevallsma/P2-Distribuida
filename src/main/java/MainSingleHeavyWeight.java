import DataParser.Data;
import DataParser.HeavyWeight;
import DataParser.Parser;
import Processes.HeavyweightProcess;
import Utils.Utils;

public class MainSingleHeavyWeight {
    public static void main(String args[]) {
        String hwType = args[0];
        Data nodeNetwork = Parser.parseJson("NetworkConfigLamport.json");
        HeavyWeight hw = nodeNetwork.getHeavyWeights().stream().filter(e -> e.getType().compareTo(hwType) == 0).findFirst().get();
        HeavyWeight connectedTo = nodeNetwork.getHeavyWeights().stream().filter(e -> e.getType().compareTo(hwType) != 0).findFirst().get();
        if (hw != null && connectedTo != null) {
            HeavyweightProcess process = new HeavyweightProcess(hw, connectedTo, hw.getConnectToOther());
            process.initBaseConnections();
            while (!process.isReady()) {
                System.out.println("(" + hw.getName() + ") Waiting to be ready...");
                Utils.timeWait(5000);
            }
            process.doSomething();
        } else {
            System.err.println("Error en els paràmetres d'entrada!");
        }
    }
}
