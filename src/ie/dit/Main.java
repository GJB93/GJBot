package ie.dit;

/**
 * Created by Graham on 06-Apr-16.
 */
public class Main {

    public static void main(String[] args)
    {
        // Creating a new Twitch Client and assigning it to a thread
        TwitchClient client = new TwitchClient(args[0], args[1], args[2]);
        Thread clientThread = new Thread(client);

        //Initial channels to join
        client.joinChannel("gjb93");
        client.joinChannel("skooter500");

        //Running the client
        clientThread.run();
    }
}
