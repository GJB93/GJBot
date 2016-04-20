package ie.dit;

import java.io.IOException;
import java.time.Duration;
import java.time.Period;
import java.util.Hashtable;

/**
 * This class contains definitions for how to answer the various commands
 * that can be given to the bot. It references the Twitch API for some
 * commands, and uses a Hashtable to store the various MOTDs stored
 * across different channels
 */
public class CommandDictionary {

    private APILibrary api;
    private Hashtable<String, String> streamMessage;

    CommandDictionary(String clientID)
    {
        api = new APILibrary(clientID);
        streamMessage = new Hashtable<>();
    }

    /**
     * This method takes a message given from the client and checks if there
     * is a command given
     */
    public String checkLine(String inChannel, String sentBy, String message, TwitchClient client)
    {
        if (message.charAt(0) == '!') {
            System.out.println("Command received");
            String command = message.substring(1);
            System.out.println("Command is " + command + " given by " + sentBy);
            return answerCommand(command, inChannel, sentBy, client);
        }

        if(checkCaps(message))
        {
            return MessageBuilder.buildSendMessage(inChannel, sentBy + " went over the cap limit!");
        }

        return null;
    }

    /**
     * This method returns a response to the command given by the user
     */

    private String answerCommand(String command, String channel, String sentBy, TwitchClient client)
    {
        if("test".equals(command))
        {
            return MessageBuilder.buildSendMessage(channel, "test");
        }

        if("leave".equals(command))
        {
            client.disconnect(channel);
        }

        if ("title".equals(command)) {
            return MessageBuilder.buildSendMessage(channel, api.getStreamTitle(channel));
        }

        if ("game".equals(command)) {
            return MessageBuilder.buildSendMessage(channel, api.getCurrentGame(channel));
        }

        if("age".equals(command))
        {
            Period age = api.getChannelAge(channel);
            return MessageBuilder.buildSendMessage(channel, "This channel is " + getAge(age));
        }

        if("myage".equals(command))
        {
            Period age = api.getChannelAge(sentBy);
            return MessageBuilder.buildSendMessage(channel, "Your account is " + getAge(age));
        }

        if("followage".equals(command))
        {
            Period age = api.getFollowAge(channel, sentBy);
            if(age != null) {
                return MessageBuilder.buildSendMessage(channel, sentBy + " has been following " + channel + " for " + getAge(age));
            }
            else
            {
                return MessageBuilder.buildSendMessage(channel, sentBy + " isn't following " + channel);
            }
        }

        if(("sub".equals(command) || "subscribe".equals(command)) && api.checkPartnered(channel))
        {
            return MessageBuilder.buildSendMessage(channel, "https://www.twitch.tv/" + channel + "/subscribe");
        }

        if("uptime".equals(command))
        {
            long uptime = api.getChannelUptime(channel);
            String response = "Stream has been online for ";
            uptime = uptime - 3600;

            /**
             * If-else to check if the stream is
             * currently online
             */
            if(uptime > 0)
            {
                response += getTime(uptime);
                return MessageBuilder.buildSendMessage(channel, response);
            }
            else
            {
                return MessageBuilder.buildSendMessage(channel, "Stream is currently offline");
            }
        }

        if("botinfo".equals(command))
        {
            return MessageBuilder.buildSendMessage(channel, "Bot created by GJB93. Source code and information about this bot can be found at https://github.com/GJB93/GJBot");
        }

        if("help".equals(command))
        {
            return MessageBuilder.buildSendMessage(channel, "Commands: !botinfo, !uptime, !game, !title, !motd, !age, !myage, !followage, !followers, !viewcount, !leave");
        }

        if("followers".equals(command))
        {
            return MessageBuilder.buildSendMessage(channel, "This channel has " + api.getFollowCount(channel) + " followers");
        }

        if("viewers".equals(command))
        {
            if(api.getChannelViewerCount(channel) >= 0)
                return MessageBuilder.buildSendMessage(channel, "This channel currently has " + api.getChannelViewerCount(channel) + " viewers");
            else
                return MessageBuilder.buildSendMessage(channel, "This channel is currently offline");
        }

        if(command.contains("motd")) {
            System.out.println("Checking message of the day");
            /**
             * If the message contains a parameter, one of these
             * two commands is executed. This comand works across
             * multiple channels, meaning that each message is unique
             * to the channel that created it
             */
            if(command.split(" ").length > 1) {
                String param = command.split(" ")[1];
                if ("set".equals(param)) {
                    String motd = command.substring(command.indexOf("set") + 3);
                    streamMessage.put(channel, motd);
                    return MessageBuilder.buildSendMessage(channel, "New MOTD: " + streamMessage.get(channel));
                }

                if ("delete".equals(param) && streamMessage.get(channel) != null) {
                    streamMessage.remove(channel);
                    return MessageBuilder.buildSendMessage(channel, "Message of the day has been deleted");
                }
            }
            else {
                if (streamMessage.get(channel) != null) {
                    return MessageBuilder.buildSendMessage(channel, "MOTD: " + streamMessage.get(channel));
                }
            }
        }

        if("gjb93".equals(sentBy)) {
            /**
             * This allows the bot to be added and removed from
             * different chat channels without having to restart
             * the program
             */
            if (command.contains("join") && "gjb93".equals(channel)) {
                if(command.split(" ").length > 1) {
                    String param = command.split(" ")[1];
                    client.joinChannel(param);
                    return MessageBuilder.buildSendMessage(channel, "Joining channel " + param);
                }
                else
                {
                    return MessageBuilder.buildSendMessage(channel, "Parameter missing for the join command");
                }
            }

            if (command.contains("leave") && "gjb93".equals(channel)) {
                if(command.split(" ").length > 1) {
                    String param = command.split(" ")[1];
                    client.disconnect(param);
                    return MessageBuilder.buildSendMessage(channel, "Leaving channel " + param);
                }
                else
                {
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * This method takes a value given in seconds and outputs
     * the number of hours, minutes and seconds in the given time
     * amount
     */
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

    /**
     * This series of method creates a String that displays the date
     * in a readable string. Itchecks to see if any of
     * the values are zero, so that the bot knows not to output
     * that value
     */
    private String getAge(Period age)
    {
        String reply = "";
        if (age.getYears() > 0) {
            if(age.getMonths() > 0 && age.getDays() > 0)
                reply += age.getYears() + " years, ";
            else if(age.getMonths() > 0 || age.getDays() > 0)
                reply += age.getYears() + " years and ";
            else
                reply += age.getYears() + " years";
        }

        if (age.getMonths() > 0) {
            if(age.getDays() > 0)
                reply += age.getMonths() + " months, and ";
            else
                reply += age.getMonths() + " months";
        }

        if (age.getDays() > 0) {
            reply += age.getDays() + " days";
        }

        return reply;
    }

    /**
     * Method used to check messages for excessive use of capital letters
     * over a certain message length
     */
    private boolean checkCaps(String message)
    {
        int successiveCaps = 0;
        int overallCaps = 0;
        boolean charBefore = false;
        String check = message.replace(" ", "");
        for(int i=0; i<check.length(); i++)
        {
            if(Character.isUpperCase(check.charAt(i)))
            {
                overallCaps++;
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

            if((successiveCaps >= check.length()*0.5f || overallCaps >= check.length()*0.5f) && check.length() > 20)
            {
                return true;
            }
        }

        return false;
    }

    /*
    //String brainPower = "O-oooooooooo AAAAE-A-A-I-A-U- JO-oooooooooooo AAE-O-A-A-U-U-A- E-eee-ee-eee AAAAE-A-E-I-E-A-JO-ooo-oo-oo-oo EEEEO-A-AAA-AAAA";
    //String part = "O-oooooooooo AAAAE-A-A-I-A-U-";
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
