import Utils.Launch;
import Utils.Utils;

import java.io.IOException;

public class Main {
    public static void main(String args[]){
        String[] commands = {
                "mvn exec:java -Dexec.mainClass=MainSingleHeavyWeight -Dexec.args='Ricard-Agrawala'",
                "mvn exec:java -Dexec.mainClass=MainSingleHeavyWeight -Dexec.args='Lamport'"};
        try {
            Launch.launchProcesses(commands);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
