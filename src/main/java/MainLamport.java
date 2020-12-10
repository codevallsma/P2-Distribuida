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
                "mvn exec:java -Dexec.mainClass=MainNodes -Dexec.args='2'",
                "mvn exec:java -Dexec.mainClass=MainNodes -Dexec.args='1'",
                "mvn exec:java -Dexec.mainClass=MainNodes -Dexec.args='0'"};
        execute("mvn compile", true);
        for(String command : commands) {
            execute(command, false);
        }
        for (Process p:
                processes) {
            p.waitFor();
        }

    }

    public static void execute(String command, boolean wait) throws InterruptedException, IOException {
        OsCheck.OSType ostype= OsCheck.getOperatingSystemType();
        ProcessBuilder builder=new ProcessBuilder();
        switch (ostype) {
            case Windows:
                builder= new ProcessBuilder("cmd.exe", "/c", command);
                break;
            case MacOS:
            case Linux:
                builder = new ProcessBuilder("bash", "-c",command);
                break;
            default:
                break;
        }

        Process process = builder.inheritIO().start();
        if(wait){
            process.waitFor();
        }else {
            processes.add(process);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String readline;
        while ((readline = reader.readLine()) != null) {
            System.out.println(readline);
        }
    }
}