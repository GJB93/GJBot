package ie.dit;

/**
 * Created by Graham on 06-Apr-16.
 */
public class Main {

    public static void main(String[] args)
    {
        TwitchClient client;
        APILibrary api = new APILibrary(args[2]);

        client = new TwitchClient(args[0], args[1], args[2]);

        client.joinChannel("gjb93");
        api.getChannelAge("gjb93");

        while(true)
        {
            client.listen();
        }
    }
}
