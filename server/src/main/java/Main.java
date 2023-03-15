import functional_classes.Server;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        String url = "jdbc:postgresql://localhost:5000/postgres";
        String user = "postgres";
        String password = "23aitip22DZ";
//        String password = "rkemWfU26OYiwbkD";
        Properties properties = new Properties();
        properties.setProperty("url", url);
        properties.setProperty("user", user);
        properties.setProperty("password", password);

        Server server = new Server(properties);
        server.serverStartup();
    }
}