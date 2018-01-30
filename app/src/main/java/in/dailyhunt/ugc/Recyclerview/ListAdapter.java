package in.dailyhunt.ugc.Recyclerview;

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

public class ListAdapter extends RecyclerView.Adapter<MyRecyclerItem> {
    private List<Post> listItems;
    public ListAdapter(List<Post> items){
        this.listItems=items;
    }


    @Override
    public MyRecyclerItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent,false);
        return new MyRecyclerItem(view);
    }

    @Override
    public void onBindViewHolder(MyRecyclerItem holder, int position) {
        try {
            holder.updateView(listItems.get(position),position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

}