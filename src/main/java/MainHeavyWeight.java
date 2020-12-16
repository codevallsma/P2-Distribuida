import Utils.Launch;
import Utils.Utils;

import java.io.IOException;

public class MainHeavyWeight {
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

    public void oldImplementation(String args[]) {
        try {
            String type = args[0];
            if (type.equals("LAMPORT")) {
                Launch.launchProcesses(new String[] {Utils.getCommand("MainLamport", "")});
            } else {
                Launch.launchProcesses(new String[]{Utils.getCommand("MainRicardAgrawala", "")});
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
