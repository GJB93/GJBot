package ie.dit;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;

/**
 * Created by Graham on 06-Apr-16.
 */
public class TwitchClient {

    private LocalTime lastSentBrainPower;
    private LocalTime lastReceivedBrainPower;
    private LocalTime lastMessageSent;
    private String lastSentBy;
    private final String server = "irc.chat.twitch.tv";
    private final int portNumber = 6667;
    private BufferedWriter writer;
    private BufferedReader reader;
    private Socket socket;
    private final APILibrary api;
    private int brainPowerCounter;
    boolean allowCommands;
    private String sendString = "\r\n";

    TwitchClient(String username, String token, String clientID)
    {
        allowCommands = false;
        brainPowerCounter = 0;
        lastSentBrainPower = LocalTime.now();
        lastReceivedBrainPower = LocalTime.now();
        lastMessageSent = LocalTime.now();
        lastSentBy = "";
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
        api = new APILibrary(clientID);
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
                    writer.write("PONG :tmi.twitch.tv" + sendString);
                    writer.flush();
                    System.out.println("Replied to ping");
                }
                else
                {
                    if(line.contains("PRIVMSG"))
                    {
                        String brainPower = "O-oooooooooo AAAAE-A-A-I-A-U- JO-oooooooooooo AAE-O-A-A-U-U-A- E-eee-ee-eee AAAAE-A-E-I-E-A-JO-ooo-oo-oo-oo EEEEO-A-AAA-AAAA";
                        String part = "O-oooooooooo AAAAE-A-A-I-A-U-";
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


                        if("test".equals(message))
                        {
                            writer.write("PRIVMSG #" + inChannel + " :test" + sendString);
                            writer.flush();
                        }

                        if(message != null && message.contains(part))
                        {
                            if(Duration.between(lastSentBrainPower, LocalTime.now()).getSeconds() > 30) {
                                if(Duration.between(lastReceivedBrainPower, LocalTime.now()).getSeconds() <= 2 && !lastSentBy.equals(sentBy))
                                {
                                    lastSentBrainPower = LocalTime.now();
                                    writer.write("PRIVMSG #" + inChannel + " :" + brainPower + sendString);
                                    writer.flush();
                                }
                                else if((Duration.between(lastReceivedBrainPower, LocalTime.now()).getSeconds() >= 5 && Duration.between(lastReceivedBrainPower, LocalTime.now()).getSeconds() <= 10)
                                        || ((lastSentBy.equals(sentBy) && Duration.between(lastSentBrainPower, LocalTime.now()).getSeconds() > 30)))
                                {
                                    lastSentBrainPower = LocalTime.now();
                                    writer.write("PRIVMSG #" + inChannel + " :SHIREE Don't Brain Power irresponsibly SHIREE" + sendString);
                                    writer.flush();
                                }
                            }
                            lastSentBy = sentBy;
                            lastReceivedBrainPower = LocalTime.now();
                            brainPowerCounter++;
                        }

                        if (message.charAt(0) == '!') {
                            System.out.println("Command received");
                            String command = message.substring(1);
                            System.out.println("Command is " + command + " given by " + sentBy);
                            if(Duration.between(lastMessageSent, LocalTime.now()).getSeconds() > 10) {
                                answerCommand(command, inChannel, sentBy);
                                lastMessageSent = LocalTime.now();
                            }
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

            writer.write("PRIVMSG #" + channel + " :Joined channel " + channel + ", type !leave to disconnect this bot" + "\r\n");
            writer.flush();

        }
        catch(IOException e)
        {
            System.out.println("IO error occurred when joining channel" + channel);
            e.printStackTrace();
        }
    }

    private void disconnect(String channel)
    {
        try
        {
            writer.write("PRIVMSG #" + channel + " :Leaving channel..." + sendString);
            writer.write("PART #" + channel + sendString);
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

    private void answerCommand(String command, String channel, String sentBy)
    {
        try {

            if ("leave".equals(command)) {
                this.disconnect(channel);
            }

            if ("title".equals(command)) {
                String title = api.getStreamTitle(channel);
                writer.write("PRIVMSG #" + channel + " :" + title + sendString);
                writer.flush();
            }

            if ("game".equals(command)) {
                String game = api.getCurrentGame(channel);
                writer.write("PRIVMSG #" + channel + " :" + game + sendString);
                writer.flush();
            }

            if("age".equals(command))
            {
                Period age = api.getChannelAge(channel);
                String response = "PRIVMSG #" + channel + " :This channel is ";
                if(age.getYears() > 0)
                {
                    response += age.getYears() + " years, ";
                }

                if(age.getMonths() > 0)
                {
                    response += age.getMonths() + " months, and ";
                }

                if(age.getDays() > 0)
                {
                    response += age.getDays() + " days old";
                }
                writer.write(response + sendString);
                writer.flush();
            }

            if("myage".equals(command))
            {
                Period age = api.getChannelAge(sentBy);
                String response = "PRIVMSG #" + channel + " :Your account is ";
                if(age.getYears() > 0)
                {
                    response += age.getYears() + " years, ";
                }

                if(age.getMonths() > 0)
                {
                    response += age.getMonths() + " months, and ";
                }

                if(age.getDays() > 0)
                {
                    response += age.getDays() + " days old";
                }
                writer.write(response + sendString);
                writer.flush();
            }

            if("sub".equals(command) || "subscribe".equals(command))
            {
                writer.write("PRIVMSG #" + channel + " :https://www.twitch.tv/" + channel + "/subscribe" + sendString);
                writer.flush();
            }

            if("brainpower".equals(command))
            {
                writer.write("PRIVMSG #" + channel + " :Brain Power Counter: " + brainPowerCounter + sendString);
                writer.flush();
            }

            if("uptime".equals(command))
            {
                long uptime = api.getUptime(channel).getSeconds();
                String response = "PRIVMSG #" + channel + " :Stream has been online for ";
                uptime = uptime - 3600;
                if(uptime > 0)
                {
                    response = getTime(uptime, response);
                    writer.write(response + sendString);
                    writer.flush();
                }
                else
                {
                    writer.write("PRIVMSG #" + channel + " :Stream is currently offline" + sendString);
                    writer.flush();
                }
            }

            if("botinfo".equals(command))
            {
                writer.write("PRIVMSG #" + channel + " :Bot created by GJB93. Source code and information about this bot can be found at https://github.com/GJB93/GJBot" + sendString);
                writer.flush();
            }
        }
        catch (IOException e)
        {
            System.out.println("IO exception occurred while attempting to answer a command");
            e.printStackTrace();
        }
    }

    private String getTime(long uptime, String response)
    {
        String reply = response;
        if(uptime >= 3600) {
            if(uptime/60 > 1)
                reply += uptime / 3600 + " hours, ";
            else
                reply += uptime / 3600 + " hour, ";
            uptime = uptime % 3600;
        }

        if(uptime >= 60) {
            if(uptime/60 > 1)
                reply += uptime / 60 + " minutes and ";
            else
                reply += uptime / 60 + " minute and ";
            uptime = uptime % 60;
        }

        if(uptime > 1)
            reply += uptime + " seconds";
        else
            reply += uptime + " second";

        return reply;
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
                    writer.write("PRIVMSG #" + channel + " :" + user + " went over excessive cap limit" + sendString);
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
}
