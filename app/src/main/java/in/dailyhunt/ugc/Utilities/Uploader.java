package in.dailyhunt.ugc.Utilities;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by brij on 28/1/18.
 */

public class Uploader extends AppCompatActivity {


    private static final String USER_AGENT = "Mozilla/5.0";

    // HTTP GET request
    public static void sendGet() throws Exception {

        String url = "http://10.42.0.40/taggify-laravel/public/test_vision_api";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);


        int responseCode = con.getResponseCode();

        Log.d("Uploader", responseCode + "");
//        System.out.println("\nSending 'GET' request to URL : " + url);
//        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Log.d("Response", response.toString());


    }


    public void uploadFile(String file_path,String tags) throws IOException {
        String charset = "UTF-8";
        String requestURL = "http://10.42.0.40/taggify-laravel/public/test_vision_api";

        MultipartUtility multipart = new MultipartUtility(requestURL, charset);
        Log.d("Filename",file_path);

        multipart.addFormField("tags",tags);
        multipart.addFilePart("content", new File(file_path));

        String response = multipart.finish(); // response from server.
        Log.d("RESPONSE",response);
    }
}
