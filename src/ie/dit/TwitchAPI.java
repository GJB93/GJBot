package ie.dit;
import org.json.*;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Graham on 07-Apr-16.
 */
public class TwitchAPI {

    String baseUrl = "https://api.twitch.tv/kraken/";
    URL url;

    TwitchAPI()
    {
        try
        {
            url = new URL("https://api.twitch.tv/kraken/");
        }
        catch(MalformedURLException e)
        {
            System.out.println("Malformed URL");
            e.printStackTrace();
        }
    }
}
