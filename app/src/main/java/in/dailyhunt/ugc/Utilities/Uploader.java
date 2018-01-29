package in.dailyhunt.ugc.Utilities;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import in.dailyhunt.ugc.Activities.AddPostActivity;

/**
 * Created by brij on 28/1/18.
 */

public class Uploader extends AppCompatActivity {


    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String charset = "UTF-8";

    // HTTP POST request
    public static String sendPostRequest(String url,ArrayList<Pair> data) throws IOException {

        MultipartUtility multipart = new MultipartUtility(url, charset);

        for(Pair currPair : data)
        {
            if(currPair.getValue()!=null)
                multipart.addFormField(currPair.getKey(),currPair.getValue());
            else if(currPair.getFile()!=null)
                multipart.addFilePart(currPair.getKey(),currPair.getFile());
        }



        return multipart.finish();      //returns response from server;
    }

    // HTTP GET request
    public static String sendGetRequest(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer responseBuffer = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            responseBuffer.append(inputLine);
        }
        in.close();

        String response = responseBuffer.toString();

        Log.d("GET REQUEST", "Response Code : "+responseCode);
        Log.d("GET REQUEST", "Response : "+response);

        return response;
    }

}
