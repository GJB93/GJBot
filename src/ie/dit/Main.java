package ie.dit;

/**
 * Created by Graham on 06-Apr-16.
 */
public class Main {

    public static void main(String[] args)
    {
        TwitchClient client;

        client = new TwitchClient(args[0], args[1], args[2]);

        client.joinChannel("gjb93");
        //client.joinChannel("cirno_tv");

        while(true)
        {
            client.listen();
        }
    }
}
