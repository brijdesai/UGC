package in.dailyhunt.ugc.Recyclerview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;

import in.dailyhunt.ugc.R;
import in.dailyhunt.ugc.Utilities.Post;
import in.dailyhunt.ugc.Utilities.UtilProperties;

/**
 * Created by pinal on 29/1/18.
 */

public class PostCardView extends RecyclerView.ViewHolder {
    private ImageView img;
    private TextView tags;

    public PostCardView(View itemView) {
        super(itemView);
        img = (ImageView) itemView.findViewById(R.id.r_img);
        tags = (TextView) itemView.findViewById(R.id.r_img_tag);
/*
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(view.getContext(), ContactDetailActivity.class);
                intent.putExtra("pos", position);
                view.getContext().startActivity(intent);
            }
        });
*/

    }

    public void updateView(Post item, int position, Context context) throws IOException {

        tags.setText(item.getTags());
//        Glide.with(context).load(UtilProperties.getProperty("serverFetchPostDir",context) + item.getFileName()).apply(new RequestOptions().centerCrop()).into(img);
        Glide.with(context).load(UtilProperties.getProperty("serverFetchPostDir",context) + item.getFileName()).apply(new RequestOptions()).into(img);
    }

}