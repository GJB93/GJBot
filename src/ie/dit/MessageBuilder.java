package ie.dit;

/**
 * Class used to create messages to send to the IRC client
 */
public class MessageBuilder {

    static String sendString = "\r\n";

    public static String buildSendMessage(String channel, String body)
    {
        return "PRIVMSG #" + channel + " :" + body + sendString;
    }

    public static String buildLeaveMessage(String channel)
    {
        return "PART #" + channel + sendString;
    }

    public static String pingReply()
    {
        return "PONG :tmi.twitch.tv" + sendString;
    }
}