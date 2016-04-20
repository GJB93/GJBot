package ie.dit;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.util.*;

/**
 * This class creates a connection to the Twitch Client using
 * a given username, password and application client ID. It then
 * listens to the chat and outputs the messages to the terminal.
 */
public class TwitchClient implements Runnable{

    private CommandDictionary cd;
    private BufferedWriter writer;
    private LocalTime startTime;
    private BufferedReader reader;
    boolean allowCommands;
    private String sendString = "\r\n";
    private ArrayList<String> connectedChannels;

    TwitchClient(String username, String token, String clientID)
    {
        startTime = LocalTime.now();
        String server = "irc.chat.twitch.tv";
        int portNumber = 6667;
        Socket socket;
        allowCommands = false;
        connectedChannels = new ArrayList<>();
        try
        {
            System.out.println("Bot Started at: " + startTime.toString());
            System.out.println("Creating connection to Twitch server...");
            socket = new Socket(server, portNumber);
            System.out.println("Creating writer...");
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Creating reader...");
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Signing in...");
            writer.write("PASS " + token + sendString);
            writer.write("NICK " + username + sendString);
            writer.flush();

            String line;

            /**
             * Connection has been established if the line contains
             * the 376 code
             */
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
        cd = new CommandDictionary(clientID);
    }

    /**
     * This method is critical to how the program works. It reads each
     * message given and outputs it to the terminal. It listens for PING
     * messages and replies with PONG in order to keep the bot connected
     * until the user chooses to terminate the program
     */
    public void run()
    {
        try
        {
            System.out.println("Listening...");

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("PING")) {
                    writer.write(MessageBuilder.pingReply());
                    writer.flush();
                    System.out.println("Replied to ping");
                } else
                {
                    /**
                     * A line contains PRIVMSG if it is a normal chat message,
                     * so these messages must be handled according to the content
                     * of the message itself
                     */
                    if (line.contains("PRIVMSG")) {
                        line = line.trim();
                        String sentBy = null;

                        /**
                         * These three try-catch blocks are used for formatting the message
                         * so that it is readable in the terminal window. It also takes the
                         * user that sent the message, as well as the channel that the message
                         * was sent in.
                         */
                        try {
                            sentBy = line.substring(1, line.indexOf('!'));
                        } catch (StringIndexOutOfBoundsException e) {
                            System.out.println("String index out of bounds on sentBy string" + line);
                            e.printStackTrace();
                        }
                        String inChannel = null;
                        try {
                            inChannel = line.substring(line.indexOf('#') + 1, line.indexOf(':', line.indexOf('#')) - 1);
                        } catch (StringIndexOutOfBoundsException e) {
                            System.out.println("String index out of bounds on inChannel string" + line);
                            e.printStackTrace();
                        }

                        String message = null;
                        try {
                            message = line.substring(line.indexOf(':', 1) + 1);
                        } catch (StringIndexOutOfBoundsException e) {

                            System.out.println("String index out of bounds on message string" + line);
                            e.printStackTrace();
                        }

                        // Output the message to the terminal
                        System.out.println("#" + inChannel + " " + sentBy + ": " + message);
                        String response = cd.checkLine(inChannel, sentBy, message, this);

                        // Write a response to the client if it is necessary to do so
                        if (response != null) {
                            writer.write(response);
                            writer.flush();
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

    /**
     * This method is used to write a message to the client that will
     * allow the bot to connect to a given channel. Writes a greeting
     * message in the channel once connected, and adds the channel to
     * the connectedChannels ArrayList
     */
    public void joinChannel(String channel)
    {
        System.out.println("Joining channel " + channel + "...");
        try
        {
            writer.write(MessageBuilder.buildJoinMessage(channel));
            writer.flush();
            String line;

            while((line = reader.readLine()) != null)
            {
                if(line.contains("366"))
                {
                    break;
                }
            }

            writer.write(MessageBuilder.buildSendMessage(channel, "Joined channel " + channel + ", type !leave to disconnect this bot"));
            writer.flush();

            connectedChannels.add(channel);
        }
        catch(IOException e)
        {
            System.out.println("IO error occurred when joining channel" + channel);
            e.printStackTrace();
        }
    }

    /**
     * Same as the joinChannel method, except it disconnects the bot from
     * the given channel instead
     */
    public void disconnect(String channel)
    {
        try
        {
            writer.write(MessageBuilder.buildSendMessage(channel, "Leaving channel..."));
            writer.write(MessageBuilder.buildLeaveMessage(channel));
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

            connectedChannels.remove(channel);
        }
        catch(IOException e)
        {
            System.out.println("IO error occurred when disconnecting");
            e.printStackTrace();
        }
    }
}
