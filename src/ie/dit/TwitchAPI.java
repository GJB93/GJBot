package ie.dit;
import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by Graham on 07-Apr-16.
 */
public class TwitchAPI {

    String baseUrl;
    String targetUrl;
    String clientID;
    URL url;
    BufferedReader reader;

    TwitchAPI()
    {
        baseUrl = "https://api.twitch.tv/kraken/";
        targetUrl = "";
    }

    TwitchAPI(String clientID)
    {
        this();
        this.clientID = clientID;
    }

    public String getStreamTitle(String channel)
    {
        String key = "";
        targetUrl += baseUrl + "channels/" + channel + "?client_id=" + clientID;

        try {
            url = new URL(targetUrl);
        }
        catch(MalformedURLException e)
        {
            url = null;
            System.out.println("Malformed URL exception found in StreamTitle method: " + targetUrl);
            e.printStackTrace();
        }

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
        targetUrl += baseUrl + "channels/" + channel + "?client_id=" + clientID;

        try {
            url = new URL(targetUrl);
        }
        catch(MalformedURLException e)
        {
            url = null;
            System.out.println("Malformed URL exception found in StreamTitle method: " + targetUrl);
            e.printStackTrace();
        }

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
}
