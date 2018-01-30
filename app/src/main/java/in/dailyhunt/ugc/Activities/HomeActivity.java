package in.dailyhunt.ugc.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import in.dailyhunt.ugc.R;
import in.dailyhunt.ugc.Recyclerview.ListAdapter;
import in.dailyhunt.ugc.Utilities.GifImageView;
import in.dailyhunt.ugc.Utilities.Permissions;
import in.dailyhunt.ugc.Utilities.Post;
import in.dailyhunt.ugc.Utilities.Uploader;

public class HomeActivity extends AppCompatActivity {


    public static final int CAMERA_FEATURE = 1;
    public static final int CHOOSE_PHOTO_FEATURE = 2;
    public static final int CHOOSE_GIF_FEATURE = 3;

    private String response;
    private final String postApi = "http://10.42.0.40/taggify-laravel/public/user/";
    private String filepath = "/storage/emulated/0/Android/data/in.dailyhunt.ugc/";
    private final String serverDir = "http://10.42.0.40/taggify-laravel/public/storage/content/";
    private int userId;

    private ArrayList<Post> userPosts;

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private final int REQUEST_PERMISSIONS = 1;
    private final int UPLOAD_MEDIA=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        checkAndRequestPermissions();


        userId = getIntent().getIntExtra("userId", 0);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        recyclerView = findViewById(R.id.list);
        showPosts();

    }

    private void showPosts() {
        getPostsOfThisUser();

        Log.d("User id", userId + "");
        Log.d("Response", response + " ");

        parseResponse();

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Android" + File.separator + "data" + File.separator, getString(R.string.package_name));
        if (!folder.exists())
            folder.mkdirs();


        DownloadTask downloadTask;
        File file;
        for (Post p : userPosts) {
            file = new File(filepath + p.getLocation());
            if (file.exists()) {
                p.setLocation(filepath + p.getLocation());
                continue;
            }
            downloadTask = new DownloadTask(HomeActivity.this, p.getLocation());
            downloadTask.execute(serverDir + p.getLocation());
            p.setLocation(filepath + p.getLocation());
        }

        listAdapter = new ListAdapter(userPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }


    private void parseResponse() {
        try {
            userPosts = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            Log.d("length", jsonArray.length() + "");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dataObject = (JSONObject) jsonArray.get(i);
                String _filename = dataObject.getString("file_name");
                _filename = _filename.substring(_filename.lastIndexOf('/') + 1);

                JSONArray tagsJsonArray = dataObject.getJSONArray("tags");

                String _tags = "";
                for (int j = 0; j < tagsJsonArray.length(); j++)
                    _tags += "#" + tagsJsonArray.getString(j) + " ";
                _tags = _tags.trim();

                userPosts.add(new Post(_filename, _tags));

//                Log.d("File Name",_filename);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showPosts();
                    return true;
                case R.id.navigation_upload:
                    showDialog(1);
                    return true;
                case R.id.navigation_logout:
                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    public Dialog onCreateDialog(int id) {
        Log.d("dialog", "Comes here");
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(R.string.choose_option)
                .setItems(R.array.upload_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        Intent intent = new Intent(HomeActivity.this, AddPostActivity.class);

                        switch (which) {
                            case 0:
                                intent.putExtra("FEATURE", CAMERA_FEATURE);
                                break;
                            case 1:
                                intent.putExtra("FEATURE", CHOOSE_PHOTO_FEATURE);
                                break;
                            case 2:
                                intent.putExtra("FEATURE", CHOOSE_GIF_FEATURE);
                                break;
                        }
                        intent.putExtra("userId", userId);
                        startActivityForResult(intent,UPLOAD_MEDIA);

                    }
                });
        return builder.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
            if(requestCode==UPLOAD_MEDIA){
                    showPosts();
                    Log.d("Here","Added successfully");
            }
    }

    public void getPostsOfThisUser() {

        Thread postThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    response = Uploader.sendGetRequest(postApi + userId + "?device=android");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        postThread.start();
        try {
            postThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private String filename;

        public DownloadTask(Context context, String filename) {
            this.context = context;
            this.filename = filename;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(filepath + filename);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if (result != null) {
                Log.d("Download Failed", filename + " download failed");
            } else {
                Log.d("Downloaded Successfully", filename + " downloaded");

            }
        }

    }


    public boolean isPermissionGranted(String permission) {
        int permissionResult = ContextCompat.checkSelfPermission(this, permission);
        if (permissionResult != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;
    }

    public boolean checkAndRequestPermissions() {
        String permissionsRequired[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        List<String> permissionsToBeGranted = new ArrayList<String>();

        for (String pRequired : permissionsRequired)
            if (!isPermissionGranted(pRequired))
                permissionsToBeGranted.add(pRequired);

        if (!permissionsToBeGranted.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToBeGranted.toArray(new String[permissionsToBeGranted.size()]), REQUEST_PERMISSIONS);
            Log.d("Permission requested", permissionsToBeGranted + "");
            return false;
        }
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    Log.d("ALLOWED", permissions[i]);
                else
                    Log.d("DENIED", permissions[i]);
            }
        }
    }


}
