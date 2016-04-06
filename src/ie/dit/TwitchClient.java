package ie.dit;

import java.io.*;
import java.net.Socket;

/**
 * Created by Graham on 06-Apr-16.
 */
public class TwitchClient {

    String server = "irc.chat.twitch.tv";
    int portNumber = 6667;
    BufferedWriter writer;
    BufferedReader reader;
    Socket socket;

    TwitchClient(String username, String token)
    {
        try
        {
            System.out.println("Creating connection to Twitch server...");
            socket = new Socket(server, portNumber);
            System.out.println("Creating writer...");
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Creating reader...");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Signing in...");
            writer.write("PASS " + token + "\r\n");
            writer.write("NICK " + username + "\r\n");
            writer.flush();

            String line = null;
            System.out.println("Writing messages...");
            while((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }
        }
        catch (IOException e)
        {
            System.out.println("IO error occurred when creating socket");
            e.printStackTrace();
        }
    }

    public void disconnect()
    {
        try
        {
            writer.write("/quit");
        }
        catch(IOException e)
        {
            System.out.println("IO error occurred when disconnecting");
            e.printStackTrace();
        }
    }
}
