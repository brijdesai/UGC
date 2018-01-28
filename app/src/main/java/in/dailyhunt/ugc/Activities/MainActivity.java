package in.dailyhunt.ugc.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import in.dailyhunt.ugc.Utilities.GifImageView;
import in.dailyhunt.ugc.Utilities.Uploader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ImageView picture;
    private GifImageView gif;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_LOAD_IMG = 2;
    private static final int REQUEST_PERMISSIONS = 1;
    private static final String AUTHORITY="in.dailyhunt.ugcApp";

    private String currentMediaPath;

    private Button capture;
    private Button chooseFromGallary;
    private Button upload;
    private Uri currentGifUri;

    private EditText tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.dailyhunt.ugc.R.layout.activity_main);

        picture = findViewById(in.dailyhunt.ugc.R.id.picture);
        capture = findViewById(in.dailyhunt.ugc.R.id.capture);
        chooseFromGallary = findViewById(in.dailyhunt.ugc.R.id.chooseFromGallary);
        upload = findViewById(in.dailyhunt.ugc.R.id.upload);
        gif = (GifImageView) findViewById(in.dailyhunt.ugc.R.id.gif);
        tags = findViewById(in.dailyhunt.ugc.R.id.tag);

        checkAndRequestPermissions();

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        chooseFromGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, REQUEST_LOAD_IMG);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Uploader uploader = new Uploader();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
//                                Uploader.sendGet();

                                uploader.uploadFile(currentMediaPath, tags.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


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
                    ;
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
                            Toast.LENGTH_LONG).show();
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


    private boolean isPermissionGranted(String permission) {
        int permissionResult = ContextCompat.checkSelfPermission(this, permission);
        if (permissionResult != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;
    }

    private boolean checkAndRequestPermissions() {
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
