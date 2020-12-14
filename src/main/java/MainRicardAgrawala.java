import Utils.Launch;

import java.io.IOException;

public class MainRicardAgrawala {

    public static void main(String[] args){
        String[] commands = {
                "mvn exec:java -Dexec.mainClass=MainNodeRicardAgrawala -Dexec.args='1'",
                "mvn exec:java -Dexec.mainClass=MainNodeRicardAgrawala -Dexec.args='0'"};
        try {
            Launch.launchProcesses(commands);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
