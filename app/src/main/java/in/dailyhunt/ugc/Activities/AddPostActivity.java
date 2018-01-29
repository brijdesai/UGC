package in.dailyhunt.ugc.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import in.dailyhunt.ugc.R;
import in.dailyhunt.ugc.Utilities.GifImageView;
import in.dailyhunt.ugc.Utilities.Pair;
import in.dailyhunt.ugc.Utilities.Uploader;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class AddPostActivity extends AppCompatActivity {


    private ImageView picture;
    private GifImageView gif;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_LOAD_IMG = 2;
    private static final String AUTHORITY = "in.dailyhunt.ugcApp";

    private String currentMediaPath;

    private Uri currentGifUri;

    private EditText tags;
    private String tagsToBeSent;
    private String mediaUrl = "http://10.42.0.40/taggify-laravel/public/user_contents";
    private ArrayList<Pair> mediaData;
    private String response = "";
    private int responseCode;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Toolbar toolbar = findViewById(R.id.add_post_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = getIntent().getIntExtra("userId", 0);

        picture = findViewById(in.dailyhunt.ugc.R.id.picture);
        gif = (GifImageView) findViewById(in.dailyhunt.ugc.R.id.gif);
        tags = findViewById(in.dailyhunt.ugc.R.id.tag);


        int featureRequested = getIntent().getIntExtra("FEATURE", 1);

        switch (featureRequested) {

            case HomeActivity.CAMERA_FEATURE: {
                dispatchTakePictureIntent();
                break;
            }
            case HomeActivity.CHOOSE_PHOTO_FEATURE: {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, REQUEST_LOAD_IMG);
                break;
            }
            case HomeActivity.CHOOSE_GIF_FEATURE: {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, REQUEST_LOAD_IMG);
                break;
            }

        }

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        AUTHORITY,
                        photoFile);
                Log.d("Main" +
                        "", photoURI + "");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentMediaPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {

            case REQUEST_TAKE_PHOTO: {

                try {
                    Log.d("Taking Photo", "Photo captured");
                } catch (Exception e) {
                    Toast.makeText(this, "Something went wrong while launching camera", Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }
                break;
            }

            case REQUEST_LOAD_IMG: {

                try {

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    currentGifUri = selectedImage;
                    currentMediaPath = imgDecodableString;

                } catch (Exception e) {
                    Toast.makeText(this, "You haven't picked Image",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            }
        }

        setAppropriateView();
    }

    private void setAppropriateView() {
        Log.d("PATH", currentMediaPath);
        String extension = currentMediaPath.substring(currentMediaPath.lastIndexOf('.') + 1);
        extension = extension.toLowerCase();
        if (extension.equals("jpg") || extension.equals("jpeg")) {
            picture.setImageBitmap(BitmapFactory.decodeFile(currentMediaPath));
            picture.setVisibility(picture.VISIBLE);
            gif.setVisibility(gif.INVISIBLE);
        } else {
            Log.d("GIF", "Playing gif : " + currentMediaPath);
            gif.setGifImageUri(currentGifUri);
            picture.setVisibility(gif.INVISIBLE);
            gif.setVisibility(gif.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.tick_mark: {

                tagsToBeSent = parseTags();

                if (tagsToBeSent == null) {
                    Toast.makeText(this, "Enter valid format in Tags", Toast.LENGTH_SHORT).show();
                    return false;
                }

                Log.d("Tags sent", tagsToBeSent);

                final ProgressDialog progressDialog = new ProgressDialog(AddPostActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Uploading pic...");
                progressDialog.show();

                makeUploadRequest();
                parseJson();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    Toast.makeText(getApplicationContext(), "Media uploaded successfully", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to upload media", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        }, 1000);
                break;
            }

            case android.R.id.home: {
                Log.d("MENUBAR", "Back");
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void parseJson() {
        // Parse response json string
        try {
            JSONObject jsonObject = new JSONObject(response);
            responseCode = jsonObject.getInt("status");
            Log.d("JSON", "Response code : " + responseCode);
            Log.d("Response", response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void makeUploadRequest() {

        mediaData = new ArrayList<>();
        mediaData.add(new Pair("user_id", userId + ""));
        mediaData.add(new Pair("tags", tagsToBeSent));
        mediaData.add(new Pair("content", new File(currentMediaPath)));

        Thread uploadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    response = Uploader.sendPostRequest(mediaUrl, mediaData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        uploadThread.start();

        try {
            uploadThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String parseTags() {
        String text = tags.getText().toString();
        text = text.replace('\n', ' ').trim();

        if (text.length() == 0)
            return null;

        StringTokenizer st = new StringTokenizer(text);


        String hashTag;
        String finalTags = "";
        while (st.hasMoreTokens()) {
            hashTag = st.nextToken();
            if (hashTag.startsWith("#") && hashTag.length() > 1) {
                finalTags += hashTag.substring(1) + " ";
            } else
                return null;
        }
        return finalTags.trim();
    }
}
