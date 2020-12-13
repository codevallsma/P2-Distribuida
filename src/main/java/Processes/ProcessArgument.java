package Processes;

public class ProcessArgument {
    public static  String getCommand(String className, String args){
        return "mvn exec:java -Dexec.mainClass=" + className + " -Dexec.args="+"\"" +args+ "\"";
    }
}
