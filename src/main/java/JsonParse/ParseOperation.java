package JsonParse;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class ParseOperation {
    public static long getPID() {
        String processName =
                java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }
    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = ParseOperation.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());

            return new File(resource.toURI());
        }
    }
    public static JsonParser startParse(){
        ObjectMapper mapper = new ObjectMapper();
        JsonParser jsonParser = null;
        try {
            // JSON file to Java object
            jsonParser = mapper.readValue(getFileFromResource("NetworkConfig.json"), JsonParser.class);
            System.out.println();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return jsonParser;
    }
}
