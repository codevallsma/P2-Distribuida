package Utils;

import java.util.Random;

import static java.lang.Thread.sleep;

public class Utils {

    public static long getPID() {
        String processName =
                java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }

    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static void timeWait(int duration) {
        try {
            sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getCommand(String className, String args) {
        return "mvn exec:java -Dexec.mainClass=" + className + " -Dexec.args="+"\"" +args+ "\"";
    }
}
