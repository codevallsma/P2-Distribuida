package DataParser;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class Parser {

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = Parser.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());
            return new File(resource.toURI());
        }
    }

    public static Data parseJson(){
        ObjectMapper mapper = new ObjectMapper();
        Data data = null;
        try {
            // JSON file to Java object
            data = mapper.readValue(getFileFromResource("NetworkConfig.json"), Data.class);
            System.out.println();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return data;
    }
}
