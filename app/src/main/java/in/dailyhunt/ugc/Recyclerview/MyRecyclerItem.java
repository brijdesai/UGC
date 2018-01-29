package in.dailyhunt.ugc.Recyclerview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import in.dailyhunt.ugc.R;
import in.dailyhunt.ugc.Utilities.Post;
import in.dailyhunt.ugc.Utilities.GifImageView;

/**
 * Created by pinal on 29/1/18.
 */

public class MyRecyclerItem extends RecyclerView.ViewHolder {
    private ImageView img;
    private GifImageView gif;
    private TextView tags;
    private int position;
    private String currentMediaPath;

    public MyRecyclerItem(View itemView) {
        super(itemView);
        img = (ImageView) itemView.findViewById(R.id.re_picture);
        gif = (GifImageView) itemView.findViewById(R.id.re_gif);
        tags = (TextView) itemView.findViewById(R.id.recycle_tag);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent= new Intent(view.getContext(), ContactDetailActivity.class);
//                intent.putExtra("pos", position);
//                view.getContext().startActivity(intent);
            }
        });

    }

    public void updateView(Post item, int position) throws IOException {

        currentMediaPath = item.getLocation();
        String tag = item.getTags();
        Log.d("displaying item image", "image is fetching");

//        new DownLoadImageTask(img).execute(imgurl);

        tags.setText(tag);
        setAppropriateView();
        this.position = position;
    }

    private void setAppropriateView() {
        String extension = currentMediaPath.substring(currentMediaPath.lastIndexOf('.') + 1);
        extension = extension.toLowerCase();
        if (extension.equals("jpg") || extension.equals("jpeg")) {
            img.setImageBitmap(BitmapFactory.decodeFile(currentMediaPath));
            img.setVisibility(img.VISIBLE);
            gif.setVisibility(gif.INVISIBLE);
        } else {
            Log.d("GIF", "Playing gif : " + currentMediaPath);
            gif.setGifImageUri(Uri.fromFile(new File(currentMediaPath)));
            img.setVisibility(gif.INVISIBLE);
            gif.setVisibility(gif.VISIBLE);
        }
    }


    /*private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        *//*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         *//*
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {

                InputStream is = new URL(urlOfImage).openStream();
                *//*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 *//*
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        *//*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         *//*
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }*/

}