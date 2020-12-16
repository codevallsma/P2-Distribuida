import Utils.Launch;
import Utils.Utils;
import java.io.IOException;

public class Main {
    public static void main(String args[]){
        //heavyWeight lamport
        // Utils.getCommand("MainHeavyWeight","RICARD-AGRAWALA")
        try {
            String[] command= {
                    Utils.getCommand("MainHeavyWeight","LAMPORT"),
            };
            Launch.launchProcesses(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
