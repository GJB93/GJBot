package ie.dit;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by Graham on 06-Apr-16.
 */
public class TwitchClient {

    CommandDictionary cd;
    private boolean motdSent;
    private LocalTime lastCheck;
    private BufferedWriter writer;
    private BufferedReader reader;
    private final APILibrary api;
    private String sendString = "\r\n";
    private Hashtable<String, Boolean> streamOnline;
    private Hashtable<String, String> streamMessage;
    ArrayList<String> connectedChannels;

    public void setStreamMessage(String channel, String message)
    {
        streamMessage.put(channel, message);
    }

    public String getStreamMessage(String channel)
    {
        return streamMessage.get(channel);
    }

    public void removeStreamMessage(String channel)
    {
        streamMessage.remove(channel);
    }

    TwitchClient(String username, String token, String clientID)
    {
        String server = "irc.chat.twitch.tv";
        int portNumber = 6667;
        Socket socket;
        motdSent = false;
        streamOnline = new Hashtable<>();
        streamMessage = new Hashtable<>();
        lastCheck = LocalTime.now();
        connectedChannels = new ArrayList<>();
        try
        {
            System.out.println("Bot Started at: " + LocalTime.now().toString());
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
        api = new APILibrary(clientID);
    }

    public void listen()
    {
        try
        {
            System.out.println("Listening...");
            if(Duration.between(lastCheck, LocalTime.now()).getSeconds() > 59)
            {
                this.checkStatus();
            }
            String line;
            while ((line = reader.readLine()) != null)
            {
                if(line.startsWith("PING"))
                {
                    writer.write(MessageBuilder.pingReply());
                    writer.flush();
                    System.out.println("Replied to ping");
                }
                else
                {
                    if(line.contains("PRIVMSG"))
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
                        String response = cd.checkLine(inChannel, sentBy, message, this);

                        if(response != null) {
                            writer.write(MessageBuilder.buildSendMessage(inChannel, response));
                            writer.flush();
                        }
                        checkCaps(message, inChannel, sentBy);
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
            writer.write("JOIN #" + channel + sendString);
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

            if(api.isOnline(channel))
            {
                streamOnline.put(channel, true);
            }
            else
            {
                streamOnline.put(channel, false);
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

    public void checkStatus()
    {
        Set<String> keys = streamOnline.keySet();
        for(String key: keys)
        {
            if(streamOnline.get(key))
            {
                api.checkGame(key);
            }
            else
            {
                api.isOnline(key);
            }
        }
    }

    private void checkCaps(String message, String channel, String user)
    {
        int successiveCaps = 0;
        boolean charBefore = false;
        String check = message.replace(" ", "");
        for(int i=0; i<check.length(); i++)
        {
            if(Character.isUpperCase(check.charAt(i)))
            {
                if(charBefore) {
                    successiveCaps++;
                }
                charBefore = true;
            }
            else
            {
                charBefore = false;
                successiveCaps = 0;
            }

            if(successiveCaps >= check.length()*0.5f )
            {
                try {
                    writer.write(MessageBuilder.buildSendMessage(channel, user + " went over the cap limit!"));
                    writer.flush();
                    break;
                }
                catch(IOException e)
                {
                    System.out.println("IO exception occurred when writing cap limit message");
                    e.printStackTrace();
                }
            }
        }
    }

    private String messageSendPrefix(String channel)
    {
        return "PRIVMSG #" + channel + " :";
    }
    private String channelLeavePrefix(String channel)
    {
        return "PART #" + channel;
    }
}
