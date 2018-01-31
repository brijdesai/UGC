package in.dailyhunt.ugc.Recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

import in.dailyhunt.ugc.R;
import in.dailyhunt.ugc.Utilities.Post;

/**
 * Created by pinal on 29/1/18.
 */

public class ListAdapter extends RecyclerView.Adapter<PostCardView> {
    private final Context context;
    private List<Post> listItems;

    public ListAdapter(List<Post> items, Context con) {
        this.listItems = items;
        this.context = con;
    }


    @Override
    public PostCardView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_media_item, parent, false);
        return new PostCardView(view);
    }

    @Override
    public void onBindViewHolder(PostCardView holder, int position) {
        try {
            holder.updateView(listItems.get(position), position, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

}