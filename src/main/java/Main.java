import Processes.LaunchProcesses;
import Processes.ProcessArgument;

import java.io.IOException;

public class Main {
    public static void main(String args[]){
        //heavyWeight lamport
        try {
            String[] command= { ProcessArgument.getCommand("MainLamport","")};
            LaunchProcesses.launchProcesses(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
