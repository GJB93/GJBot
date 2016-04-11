package ie.dit;
import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;

/**
 * Created by Graham on 07-Apr-16.
 */
public class APILibrary {

    String baseTwitchUrl;
    String target;
    private String clientID;
    private BufferedReader reader;

    APILibrary()
    {
        baseTwitchUrl = "https://api.twitch.tv/kraken/";
        target = "";
    }

    APILibrary(String clientID)
    {
        this();
        this.clientID = clientID;
    }

    private URL setUrl(String targetUrl)
    {
        URL url;
        try {
            url = new URL(targetUrl);
        }
        catch(MalformedURLException e)
        {
            url = null;
            System.out.println("Malformed URL exception found in StreamTitle method: " + targetUrl);
            e.printStackTrace();
        }

        return url;
    }

    private JSONObject getJSON(URL targetUrl)
    {
        try {
            reader = new BufferedReader(new InputStreamReader(targetUrl.openStream()));
        }
        catch(IOException e)
        {
            System.out.println("IO exception occurred when creating the URL reader");
            e.printStackTrace();
        }

        String line;
        String str = "";
        try {
            while ((line = reader.readLine()) != null)
            {
                str += line;
            }
        }
        catch(IOException e)
        {
            System.out.println("IO exception occurred when reading the URL");
            e.printStackTrace();
        }

        return new JSONObject(str);
    }

    public String getStreamTitle(String channel)
    {
        baseTwitchUrl = "https://api.twitch.tv/kraken/";
        target += baseTwitchUrl + "channels/" + channel + "?client_id=" + clientID;

        URL targetUrl = setUrl(target);

        JSONObject obj = getJSON(targetUrl);
        return obj.getString("status");
    }

    public String getCurrentGame(String channel)
    {
        target += baseTwitchUrl + "channels/" + channel + "?client_id=" + clientID;

        URL targetUrl = setUrl(target);

        JSONObject obj = getJSON(targetUrl);
        return obj.getString("game");
    }

    public Period getChannelAge(String channel)
    {
        target += baseTwitchUrl + "channels/" + channel + "?client_id=" + clientID;

        URL targetUrl = setUrl(target);

        JSONObject obj = getJSON(targetUrl);

        String value = obj.getString("created_at");
        value = value.substring(0, value.indexOf('T'));
        String[] split = value.split("-");
        LocalDate created = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        return Period.between(created, LocalDate.now());
    }
}
