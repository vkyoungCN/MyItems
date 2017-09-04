package cn.vkyoung.myitems.fragment;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.util.List;

import cn.vkyoung.myitems.R;
import cn.vkyoung.myitems.sqlite.Location;
import cn.vkyoung.myitems.sqlite.MyItemsDbHelper;

/**
 * Created by VkYoung16 on 2017/9/2.
 */

public class Loc_SelectionAdapter extends RecyclerView.Adapter<Loc_SelectionAdapter.ViewHolder> implements OnClickListener{
    private static final String TAG = "MyItems-RvAdp-LS";
    private List<Location> locations = null;
    private RecyclerView mRv;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView title;
        private final TextView description;
        private final TextView nextLevel;
        private final TextView makeSelection;

        public ViewHolder(View v){
            super(v);
            Log.i(TAG,"inside VH constructor, behind super");

            title = v.findViewById(R.id.title_slc);
            description = v.findViewById(R.id.description_slc);
            nextLevel = v.findViewById(R.id.next_level);
            makeSelection = v.findViewById(R.id.make_selection);
        }

        public TextView getTitle(){
            return title;
        }

        public TextView getDescription(){
            return description;
        }

        public TextView getNextLevel(){
            return nextLevel;
        }

        public TextView getMakeSelection(){
            return makeSelection;
        }

    }

    /*
    * 初始化此Adapter的数据源
    * */
    public Loc_SelectionAdapter(List<Location> locations, Context context){
        this.locations = locations;
        this.context = context;

    }

    //Create new views(由layout manager调用)
    @Override
    public Loc_SelectionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        Log.i(TAG,"inside onCreateViewHolder, before any calls");
        //Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.selection_rv_row, viewGroup, false);
        //Log.i(TAG, "inside onCreateViewHolder, inflated,loc_rv: "+String.valueOf(R.layout.selection_rv_row));
        return new Loc_SelectionAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(Loc_SelectionAdapter.ViewHolder viewHolder, final int position){
        // Get element from your dataset at this position and replace the contents of the view with that element
        //Log.i(TAG,"inside onBindViewHolder, before any calls, position= "+String.valueOf(position));
        Location location = locations.get(position);

        //Log.i(TAG,"inside onBindViewHolder, location.getName()= "+location.getName());
        viewHolder.getTitle().setText(location.getName());
        //Log.i(TAG,"inside onBindViewHolder 2, location.getDes()= "+location.getDescription());
        viewHolder.getDescription().setText(location.getDescription());
        //Log.i(TAG,"inside onBindViewHolder, ready to set the Listener");
        viewHolder.getNextLevel().setOnClickListener(this);
        Log.i(TAG, "inside onBindViewHolder, the Listener has been set");
        viewHolder.getMakeSelection().setOnClickListener(this);

        //Log.i(TAG, "inside onBindViewHolder, after Load items");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView rv){
        this.mRv = rv;
    }

    @Override
    public int getItemCount(){
        return locations.size();
    }

    @Override
    public void onClick(View view){

        //Log.i(TAG,"inside RvAdapter's onClick, view's pv: "+view.getParent().getParent().toString());
        int viewPosition = mRv.getChildAdapterPosition((CardView)view.getParent().getParent());
        //Log.i(TAG,"inside RvAdapter's onClick, got the viewPosition: "+viewPosition);
        switch (view.getId()){
            case R.id.next_level:
                //Log.i(TAG,"inside RvAdapter's onClick, next_level branch");
                List<Location> l = MyItemsDbHelper.getInstance(context).getLocationsBySuperId(locations.get(viewPosition).getId());
                SelectionDgFragment.LocSelectionBackStack.add(l);
                Log.i(TAG,"inside RvAdapter's onClick, next_level branch, cat_id: "+locations.get(viewPosition).getId());
                mRv.swapAdapter(new Loc_SelectionAdapter(l, context), true);
                break;

            case R.id.make_selection:
                break;


        }
    }

}
