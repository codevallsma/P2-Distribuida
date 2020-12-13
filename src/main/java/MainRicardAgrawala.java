import DataParser.Data;
import DataParser.Parser;
import Processes.LightweightProcess;

public class MainRicardAgrawala {
    public static void main(String[] args) {
        //System.out.println("It's eric modafaka bitch " + getPID());
        int nodeId = Integer.parseInt(args[0]);
        Data nodeNetwork = Parser.parseJson("NetworkConfigRicardAgrawala.json");
        LightweightProcess process = new LightweightProcess(nodeId, nodeNetwork);
        process.start();
        while (!process.isReady());
        process.doSomethingLamport();
    }
}
