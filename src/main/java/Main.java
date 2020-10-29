import JsonParse.ParseOperation;

import java.io.IOException;

/**
 * Main class for starting up a single node
 */
public class Main {
    public static long getPID() {
        String processName =
                java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }
    public static void main(String[] args) {
        System.out.println("caca del main main " + getPID());
        System.out.println("Console is: " + System.console());
        //ParseOperation.main(new String[0]);
        //Process process = new ProcessBuilder("ParseOperation").start();
    }
}
