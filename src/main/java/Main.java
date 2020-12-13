import Processes.LaunchProcesses;
import Utils.Utils;
import java.io.IOException;

public class Main {
    public static void main(String args[]){
        //heavyWeight lamport
        try {
            String[] command= { Utils.getCommand("MainLamport","")};
            LaunchProcesses.launchProcesses(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
