package ie.dit;

/**
 * Created by Graham on 06-Apr-16.
 */
public class Main {

    public static void main(String[] args)
    {
        TwitchClient client;

        client = new TwitchClient(args[0], args[1]);

        client.joinChannel("lirik");

        while(true)
        {
            client.listen();
        }
    }
}
