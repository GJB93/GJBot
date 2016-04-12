package ie.dit;
import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;

/**
 * Created by Graham on 07-Apr-16.
 */
public class APILibrary {

    private String baseTwitchUrl;
    private String target;
    private String clientID;
    private BufferedReader reader;

    private APILibrary()
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
        target = "";
        baseTwitchUrl = "https://api.twitch.tv/kraken/";
        target += baseTwitchUrl + "channels/" + channel + "?client_id=" + clientID;

        URL targetUrl = setUrl(target);

        JSONObject obj = getJSON(targetUrl);
        return obj.getString("status");
    }

    public String getCurrentGame(String channel)
    {
        target = "";
        target += baseTwitchUrl + "channels/" + channel + "?client_id=" + clientID;

        URL targetUrl = setUrl(target);

        JSONObject obj = getJSON(targetUrl);
        return obj.getString("game");
    }

    public Period getChannelAge(String channel)
    {
        target = "";
        target += baseTwitchUrl + "channels/" + channel + "?client_id=" + clientID;

        URL targetUrl = setUrl(target);

        JSONObject obj = getJSON(targetUrl);

        String value = obj.getString("created_at");
        value = value.substring(0, value.indexOf('T'));
        String[] split = value.split("-");
        LocalDate created = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        return Period.between(created, LocalDate.now());
    }

    public Duration getUptime(String channel)
    {
        target = "";
        target += baseTwitchUrl + "streams/" + channel + "?client_id=" + clientID;
        URL targetUrl = setUrl(target);

        JSONObject obj = getJSON(targetUrl);

        if(!obj.isNull("stream")) {
            String value = obj.getJSONObject("stream").getString("created_at");
            value = value.substring(value.indexOf('T')+1, value.indexOf('Z'));
            LocalTime timeStarted = LocalTime.parse(value);
            return Duration.between(timeStarted, LocalTime.now());
        }
        else
        {
            return null;
        }
    }
}
