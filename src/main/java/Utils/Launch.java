package Utils;

import Utils.OsCheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Launch {
    static ArrayList<Process> processes = new ArrayList<>();

    public static void launchProcesses(String[] commands) throws IOException, InterruptedException {
        execute("mvn compile", true);
        for(String command : commands) {
            execute(command, false);
        }
        for (Process p:
                processes) {
            p.waitFor();
        }

    }
    public static  void LaunchLightweights(String lightweight){
        try {
            if (lightweight.equals("Lamport")) {
                String[] command = {
                        Utils.getCommand("MainLamport", ""),
                };
                Launch.launchProcesses(command);
            } else {
                String[] command = {
                        Utils.getCommand("MainRicardAgrawala", ""),
                };
                Launch.launchProcesses(command);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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
