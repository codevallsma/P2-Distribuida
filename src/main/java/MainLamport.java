import Utils.Launch;
import Utils.OsCheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainLamport {
    static ArrayList<Process> processes = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        //"mvn exec:java -Dexec.mainClass=MainNodes -Dexec.args='2'",
        String[] commands = {
                "mvn exec:java -Dexec.mainClass=MainNodeLamport -Dexec.args='2'",
                "mvn exec:java -Dexec.mainClass=MainNodeLamport -Dexec.args='1'",
                "mvn exec:java -Dexec.mainClass=MainNodeLamport -Dexec.args='0'"};
        Launch.launchProcesses(commands);
    }
}