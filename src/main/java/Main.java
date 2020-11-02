import DataParser.Data;
import DataParser.Parser;
import Processes.LightweightProcess;

import static Utils.Utils.getPID;

/**
 * Main class for starting up a single node
 */
public class Main {

    public static void main(String[] args) {
        int nodeId = 2;
        Data nodeNetwork = Parser.parseJson();
        LightweightProcess process = new LightweightProcess(nodeId, nodeNetwork);
        process.start();
        while(!process.isReady());
        process.doSomething();
        //Parser.main(new String[0]);
        //Process process = new ProcessBuilder("Parser").start();
    }
}
