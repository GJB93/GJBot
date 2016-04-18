package ie.dit;

import java.time.Period;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by Graham on 13-Apr-16.
 */
public class CommandDictionary {

    APILibrary api;
    private Hashtable<String, String> streamMessage;
    private Hashtable<String, Boolean> streamOnline;

    CommandDictionary(String clientID)
    {
        api = new APILibrary(clientID);
        streamMessage = new Hashtable<>();
        streamOnline = new Hashtable<>();
    }

    public String checkLine(String inChannel, String sentBy, String message, TwitchClient client)
    {
        if("test".equals(message))
        {
            return "test";
        }

        if (message.charAt(0) == '!') {
            System.out.println("Command received");
            String command = message.substring(1);
            System.out.println("Command is " + command + " given by " + sentBy);
            return answerCommand(command, inChannel, sentBy, client);
        }

        return null;
    }

    private String answerCommand(String command, String channel, String sentBy, TwitchClient client)
    {
        if("leave".equals(command))
        {
            client.disconnect(channel);
        }

        if ("title".equals(command)) {
            return api.getStreamTitle(channel);
        }

        if ("game".equals(command)) {
            return api.getCurrentGame(channel);
        }

        if("age".equals(command))
        {
            Period age = api.getChannelAge(channel);
            String response = "This channel is ";
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
            return response;
        }

        if("myage".equals(command))
        {
            Period age = api.getChannelAge(sentBy);
            String response = "Your account is ";
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
            return response;
        }

        if("sub".equals(command) || "subscribe".equals(command))
        {
            return "https://www.twitch.tv/" + channel + "/subscribe";
        }

        if("uptime".equals(command))
        {
            long uptime = api.getChannelUptime(channel);
            String response = "Stream has been online for ";
            uptime = uptime - 3600;
            if(uptime > 0)
            {
                response += getTime(uptime);
                return response;
            }
            else
            {
                return "Stream is currently offline";
            }
        }

        if("botinfo".equals(command))
        {
            return "Bot created by GJB93. Source code and information about this bot can be found at https://github.com/GJB93/GJBot";
        }

        if(command.contains("motd")) {
            System.out.println("Checking message of the day");
            try {
                String param = command.split(" ")[1];
                if ("set".equals(param)) {
                    String motd = command.substring(command.indexOf("set") + 3);
                    streamMessage.put(channel, motd);
                    return "New MOTD: " + streamMessage.get(channel);
                }

                if ("delete".equals(param)) {
                    streamMessage.remove(channel);
                    return "Message of the day has been deleted";
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                if (streamMessage.get(channel) != null) {
                    return "MOTD: " + streamMessage.get(channel);
                }
            }
        }

        if("gjb93".equals(sentBy)) {
            if (command.contains("join") && "gjb93".equals(channel)) {
                try {
                    String param = command.split(" ")[1];
                    client.joinChannel(param);
                    return "Joining channel " + param;
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Incorrect join parameter given");
                    return "Invite command is missing channel parameter";
                }
            }

            if (command.contains("leave") && "gjb93".equals(channel)) {
                try {
                    String param = command.split(" ")[1];
                    client.disconnect(param);
                    return "Leaving channel " + param;
                } catch (ArrayIndexOutOfBoundsException e) {
                    return null;
                }
            }
        }

        return null;
    }

    private String getTime(long uptime)
    {
        String reply = "";
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

    /*

    //private LocalTime lastSentBrainPower;
    //private LocalTime lastReceivedBrainPower;
    //private String lastSentBy;
    //private int brainPowerCounter;
    //brainPowerCounter = 0;
    //lastSentBrainPower = LocalTime.now();
    //lastReceivedBrainPower = LocalTime.now();
    //lastSentBy = "";
    private void brainPower()
    {
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
    }
    */
}
