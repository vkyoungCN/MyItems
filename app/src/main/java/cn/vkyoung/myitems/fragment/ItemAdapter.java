package cn.vkyoung.myitems.fragment;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.vkyoung.myitems.R;
import cn.vkyoung.myitems.sqlite.Item;

import java.util.List;

/**
 * Created by VkYoung16 on 2017/8/19.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> items;
    private static final String TAG = "MyItems-RvAdapter-I";

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageView;
        private final TextView title;
        private final TextView description;
        private final TextView dropped;

        public ViewHolder(View v){
            super(v);

            Log.i(TAG,"inside ViewHolder construncor, before any calls");
            imageView = v.findViewById(R.id.imageView);
            title = v.findViewById(R.id.title);
            description = v.findViewById(R.id.description);
            dropped = v.findViewById(R.id.dropped);
        }

        public ImageView getImageView(){
            return imageView;
        }

        public TextView getTitle(){
            return title;
        }

        public TextView getDescription(){
            return description;
        }

        public TextView getDropped(){
            return dropped;
        }

    }

    /*
    * 初始化此Adapter的数据源
    * @param items List<Item>中含有用于RecyclerView填充views的数据
    * */
    public ItemAdapter(List<Item> items){
        this.items = items;
    }

    //Create new views(由layout manager调用)
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        Log.i(TAG,"inside onCreateViewHolder, before any calls");
        //Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_result_rv_row, viewGroup, false);
        //Log.i(TAG, "inside ItemAdapter, inside VH onCreateVH");
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position){
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        Log.i(TAG,"inside onBindViewHolder, before any calls");
        Item item = items.get(position);
        if(item.getMainImage()!= null){
            //如果存入DB的是""空串，取出时返回的是null；
            //如果是null则暂时采用XML中默认设置的icon图像，在此不需要处理。
            viewHolder.getImageView().setImageDrawable(Drawable.createFromPath(item.getMainImage()));
        }
        viewHolder.getTitle().setText(item.getName());
        viewHolder.getDescription().setText(item.getDescription());
        if(item.getDropStatus() == Item.ITEM_DROPPED_NO) {
            viewHolder.getDropped().setVisibility(View.INVISIBLE);
        }
        //Log.i(TAG, "inside ItemAdapter, inside VH onBindVH,after Load items");
    }

    @Override
    public int getItemCount(){
        return items.size();
    }



}






