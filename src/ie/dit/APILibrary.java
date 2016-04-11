package ie.dit;
import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.Temporal;

/**
 * Created by Graham on 07-Apr-16.
 */
public class APILibrary {

    String baseTwitchUrl;
    String baseEmotesUrl;
    String targetUrl;
    String clientID;
    URL url;
    BufferedReader reader;

    APILibrary()
    {
        baseTwitchUrl = "https://api.twitch.tv/kraken/";
        baseEmotesUrl = "https://twitchemotes.com/api_cache/v2/global.json";
        targetUrl = "";
    }

    APILibrary(String clientID)
    {
        this();
        this.clientID = clientID;
    }

    public void setUrl(String urlPassed)
    {
        try {
            url = new URL(urlPassed);
        }
        catch(MalformedURLException e)
        {
            url = null;
            System.out.println("Malformed URL exception found in StreamTitle method: " + targetUrl);
            e.printStackTrace();
        }
    }

    public String getStreamTitle(String channel)
    {
        baseTwitchUrl = "https://api.twitch.tv/kraken/";
        String key = "";
        targetUrl += baseTwitchUrl + "channels/" + channel + "?client_id=" + clientID;

        setUrl(targetUrl);

        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
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

        JSONObject obj = new JSONObject(str);
        key = obj.getString("status");

        return key;
    }

    public String getCurrentGame(String channel)
    {
        String key = "";
        targetUrl += baseTwitchUrl + "channels/" + channel + "?client_id=" + clientID;

        setUrl(targetUrl);

        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
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

        JSONObject obj = new JSONObject(str);
        key = obj.getString("game");

        return key;
    }

    public String getChannelCreated(String channel)
    {
        String key = "";
        targetUrl += baseTwitchUrl + "channels/" + channel + "?client_id=" + clientID;

        setUrl(targetUrl);

        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
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

        JSONObject obj = new JSONObject(str);
        key = obj.getString("created_at");
        key = key.substring(0, key.indexOf('T'));

        return key;
    }

    public Period getChannelAge(String channel)
    {
        String key = "";
        targetUrl += baseTwitchUrl + "channels/" + channel + "?client_id=" + clientID;

        setUrl(targetUrl);

        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
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

        JSONObject obj = new JSONObject(str);
        key = obj.getString("created_at");
        key = key.substring(0, key.indexOf('T'));
        String[] split = key.split("-");
        LocalDate created = LocalDate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        Period age = Period.between(created, LocalDate.now());

        return age;
    }
}
