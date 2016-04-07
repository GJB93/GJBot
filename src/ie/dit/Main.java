package ie.dit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by Graham on 06-Apr-16.
 */
public class Main {

    public static void main(String[] args)
    {
        TwitchClient client;
        Scanner inputReader = new Scanner(System.in);

        client = new TwitchClient(args[0], args[1]);

        client.joinChannel("gjb93");
        client.joinChannel("lirik");

        while(true)
        {
            client.listen();
        }
    }
}
