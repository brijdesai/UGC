package in.dailyhunt.ugc.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.dailyhunt.ugc.R;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1;

    public static final int CAMERA_FEATURE = 1;
    public static final int CHOOSE_PHOTO_FEATURE = 2;
    public static final int CHOOSE_GIF_FEATURE = 3;

    private TextView mTextMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkAndRequestPermissions();

        mTextMessage = (TextView) findViewById(R.id.message);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }





    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_upload:
                    mTextMessage.setText(R.string.title_upload);
                    showDialog(1);
                    return true;
                case R.id.navigation_profile:
                    mTextMessage.setText(R.string.title_profile);
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
                                mTextMessage.setText("Taking photo");
                                intent.putExtra("FEATURE", CAMERA_FEATURE);
                                break;
                            case 1:
                                mTextMessage.setText("Choose photo");
                                intent.putExtra("FEATURE", CHOOSE_PHOTO_FEATURE);
                                break;
                            case 2:
                                mTextMessage.setText("Choose gif");
                                intent.putExtra("FEATURE", CHOOSE_GIF_FEATURE);
                                break;
                        }

                        startActivity(intent);

                    }
                });
        return builder.create();
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
