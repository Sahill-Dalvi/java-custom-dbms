import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManagement {
    private static String delimiter = "^&%";
    private static String delimiter1 = "#";


    /**
     * @param u is the username of the user
     * @param s indicates that the user has successfully logged in
     * @param q indicated that the user has initiated a query
     */
    public void logger(String u, String s, String q){
        try {
            String dateti = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
            String log = u + delimiter + s + delimiter + dateti + delimiter + q + delimiter1;


            FileWriter myWriter = new FileWriter("log.txt", true);
            myWriter.write(log);
            myWriter.close();
        } catch(Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}
