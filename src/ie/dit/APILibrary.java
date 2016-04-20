package ie.dit;
import org.json.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;

/**
 * This class is used to query the Twitch API, and to create
 * a JSON object based on the given URL stream
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

    /**
     * Creates a URL based on a given string
     */
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

    /**
     * Creates a JSON Object using text found at a given URL
     */
    private JSONObject getJSON(URL targetUrl)
    {
        try {
            reader = new BufferedReader(new InputStreamReader(targetUrl.openStream()));
        }
        catch(IOException e)
        {
            System.out.println("No file found at the URL given");
            return null;
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

    /**
     * Retrieves a stream's title from the Twitch API
     */
    public String getStreamTitle(String channel)
    {
        target = "";
        baseTwitchUrl = "https://api.twitch.tv/kraken/";
        target += baseTwitchUrl + "channels/" + channel + "?client_id=" + clientID;

        URL targetUrl = setUrl(target);

        JSONObject obj = getJSON(targetUrl);

        try {
            return obj.getString("status");
        }
        catch (JSONException e)
        {
            return "This stream has a null title";
        }
    }

    /**
     * Retrieves the last played game on the given channel
     */
    public String getCurrentGame(String channel)
    {
        target = "";
        target += baseTwitchUrl + "channels/" + channel + "?client_id=" + clientID;

        URL targetUrl = setUrl(target);

        JSONObject obj = getJSON(targetUrl);

        try {
            return obj.getString("game");
        }
        catch(JSONException e)
        {
            return "This stream has a null game";
        }
    }

    /**
     * Returns the period between the date a channel was created
     * and the date at the time the command is given
     */
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

    public Period getFollowAge(String channel, String sentBy)
    {
        target = "";
        target += baseTwitchUrl + "users/" + sentBy + "/follows/channels/" + channel + "?client_id=" + clientID;

        URL targetUrl = setUrl(target);

        JSONObject obj = getJSON(targetUrl);

        if(obj != null)
        {
            String value = obj.getString("created_at");
            value = value.substring(0, value.indexOf('T'));
            String[] split = value.split("-");
            LocalDate created = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            return Period.between(created, LocalDate.now());
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the duration of the stream from the stream's start time
     * to the time the command was given. Returns the value as a long
     */
    public long getChannelUptime(String channel)
    {
        target = "";
        target += baseTwitchUrl + "streams/" + channel + "?client_id=" + clientID;
        URL targetUrl = setUrl(target);

        JSONObject obj = getJSON(targetUrl);

        if(!obj.isNull("stream")) {
            String value = obj.getJSONObject("stream").getString("created_at");
            value = value.substring(value.indexOf('T')+1, value.indexOf('Z'));
            LocalTime timeStarted = LocalTime.parse(value);
            return Duration.between(timeStarted, LocalTime.now()).getSeconds();
        }
        else
        {
            return 0;
        }
    }
}
