import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class httpClient {
    public static void main(String[] args) {

        try {
            String url = "http://192.168.0.120:8080"; // Replace with your server URL
            
            // Create a URL object
            URL apiUrl = new URL(url);
            while(true){
            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
             
            // Set request method to GET
            connection.setRequestMethod("GET");
           
            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            // Read the response content
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            // Print the response content
            //System.out.println("Response Content:");
            System.out.println(response.toString());
            String[] splitstr;
            splitstr=response.toString().split(" ",7);
            System.out.println("\n"+splitstr[1]+"\n"+splitstr[2]+"\n"+splitstr[3]);
            splitstr[1]=splitstr[1].replaceAll("X|Y|Z|z|x|y|=", "");
            splitstr[2]=splitstr[2].replaceAll("X|Y|Z|z|x|y|=", "");
            splitstr[3]=splitstr[3].replaceAll("X|Y|Z|z|x|y|=", "");
            Float X,Y,Z;
            X=Float.parseFloat(splitstr[1]);
            System.out.println("\n"+X);
            Y=Float.parseFloat(splitstr[2]);
            System.out.println("\n"+Y);
            Z=Float.parseFloat(splitstr[3]);
            System.out.println("\n"+Z);
            // Close the connection
         
         connection.disconnect();
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
