package ie.dit;

/**
 * Created by Graham on 06-Apr-16.
 */
public class Main {

    public static void main(String[] args)
    {
        TwitchClient client = new TwitchClient(args[0], args[1], args[2]);
        Thread clientThread = new Thread(client);

        client.joinChannel("gjb93");
        //client.joinChannel("cirno_tv");
        clientThread.run();
    }
}
