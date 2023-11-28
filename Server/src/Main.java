import java.io.BufferedReader;
import java.io.InputStreamReader;

import Server.socket.server;
public class Main {
    public static void main(String[] args) throws Exception {
        server server = new server(1234);
		server.connect();
		server.close();
        // ProcessBuilder pb = new ProcessBuilder(".\\src\\Server\\temp\\941243.exe");
        // pb.redirectErrorStream(true);
		// Process process = pb.start();

        // BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // String line = "";
        // while ((line = br.readLine()) != null)
        //     System.out.println(line);
    }
    
}