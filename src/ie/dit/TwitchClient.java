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
    String username;

    TwitchClient(String username, String token)
    {
        try
        {
            this.username = username;
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

            String line;
            while((line = reader.readLine()) != null)
            {
                if(line.contains("376"))
                {
                    break;
                }
            }

            System.out.println("Connection created");
        }
        catch (IOException e)
        {
            System.out.println("IO error occurred when creating socket");
            e.printStackTrace();
        }
    }

    public void listen()
    {
        try
        {
            System.out.println("Listening...");
            String line;
            while ((line = reader.readLine()) != null)
            {
                if(line.startsWith("PING"))
                {
                    writer.write("PONG :tmi.twitch.tv" + "\r\n");
                    writer.flush();
                    System.out.println("Replied to ping");
                }
                else
                {
                    if(line.contains("PRIVMSG"));
                    {
                        line = line.trim();
                        String sentBy = null;
                        try {
                            sentBy = line.substring(1, line.indexOf('!'));
                        }
                        catch(StringIndexOutOfBoundsException e)
                        {
                            System.out.println("String index out of bounds on sentBy string" + line);
                            e.printStackTrace();
                        }
                        String inChannel = null;
                        try {
                            inChannel = line.substring(line.indexOf('#') + 1, line.indexOf(':', line.indexOf('#')) - 1);
                        }
                        catch(StringIndexOutOfBoundsException e)
                        {
                            System.out.println("String index out of bounds on inChannel string" + line);
                            e.printStackTrace();
                        }

                        String message = null;
                        try {
                            message = line.substring(line.indexOf(':', 1) + 1);
                        }
                        catch(StringIndexOutOfBoundsException e)
                        {

                            System.out.println("String index out of bounds on message string" + line);
                            e.printStackTrace();
                        }

                        System.out.println("#" + inChannel + " " + sentBy + ": " + message);
                        if (message.charAt(0) == '!') {
                            System.out.println("Command received");
                            String command = message.substring(1);
                            System.out.println("Command is " + command + " given by " + sentBy);

                            if ("leave".equals(command)) {
                                this.disconnect(inChannel);
                            }
                        }
                    }
                }
            }
        }
        catch(IOException e)
        {
            System.out.println("Error occurred while listening to chat");
            e.printStackTrace();
        }

    }

    public void joinChannel(String channel)
    {
        System.out.println("Joining channel " + channel + "...");
        try
        {
            writer.write("JOIN #" + channel + "\r\n");
            writer.flush();
            String line;

            while((line = reader.readLine()) != null)
            {
                if(line.contains("366"))
                {
                    break;
                }
            }
        }
        catch(IOException e)
        {
            System.out.println("IO error occurred when joining channel" + channel);
            e.printStackTrace();
        }
    }

    public void disconnect(String channel)
    {
        try
        {
            writer.write("PRIVMSG #" + channel + " :Leaving channel..." + "\r\n");
            writer.write("PART #" + channel + "\r\n");
            writer.flush();

            String line;

            while((line = reader.readLine()) != null)
            {
                if(line.contains("PART"))
                {
                    break;
                }
            }
            System.out.println("Left #" + channel);
        }
        catch(IOException e)
        {
            System.out.println("IO error occurred when disconnecting");
            e.printStackTrace();
        }
    }
}
