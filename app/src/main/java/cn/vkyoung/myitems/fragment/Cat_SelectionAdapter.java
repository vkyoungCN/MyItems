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
import android.widget.Toast;

import java.util.List;

import cn.vkyoung.myitems.R;
import cn.vkyoung.myitems.assistant.ActionType;
import cn.vkyoung.myitems.sqlite.Category;
import cn.vkyoung.myitems.sqlite.MyItemsDbHelper;

/**
 * Created by VkYoung16 on 2017/9/2.
 */

public class Cat_SelectionAdapter extends RecyclerView.Adapter<Cat_SelectionAdapter.ViewHolder> implements OnClickListener {
    private static final String TAG = "MyItems-RvAdp-CS";
    private List<Category> categories;
    private SelectionDgFragment mSdFg;
    private MyItemsDbHelper myItemsDbHelper;
    private RecyclerView mRv;
    private Context context;
    private ActionType atp;

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
    *  减少错误概率，早期2参版本停用
    public Cat_SelectionAdapter(List<Category> categories, Context context){
        this.categories = categories;
        this.context = context;
    }*/

    //带Fragment引用才能将数据传回Fragment
    public Cat_SelectionAdapter(List<Category> categories, Context context,SelectionDgFragment sdFg, ActionType atp){
        this.categories = categories;
        this.context = context;
        this.mSdFg = sdFg;
        this.myItemsDbHelper = MyItemsDbHelper.getInstance(context);
        this.atp = atp;
    }
    //Create new views(由layout manager调用)
    @Override
    public Cat_SelectionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        Log.i(TAG,"inside onCreateViewHolder, before any calls");
        //Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.selection_rv_row, viewGroup, false);
        //Log.i(TAG, "inside onCreateViewHolder, inflated with selection_rv_row: "+String.valueOf(R.layout.selection_rv_row));
        return new Cat_SelectionAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(Cat_SelectionAdapter.ViewHolder viewHolder, final int position){
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        Category category = categories.get(position);

        viewHolder.getTitle().setText(category.getName());
        viewHolder.getDescription().setText(category.getDescription());
        //Log.i(TAG,"inside onBindViewHolder, ready to set the Listener");
        if(myItemsDbHelper.hasChildCategory(category.getId())) {
            viewHolder.getNextLevel().setOnClickListener(this);
            Log.i(TAG, "inside onBindViewHolder, the Listener has been set");
        }else {
            viewHolder.getNextLevel().setVisibility(View.GONE);
        }

        viewHolder.getMakeSelection().setOnClickListener(this);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView rv){
        this.mRv = rv;
    }

    @Override
    public int getItemCount(){
        return categories.size();
    }

    @Override
    public void onClick(View view){

        //Log.i(TAG,"inside RvAdapter's onClick, view's pv: "+view.getParent().getParent().toString());
        int viewPosition = mRv.getChildAdapterPosition((CardView)view.getParent().getParent());
        //Log.i(TAG,"inside RvAdapter's onClick, got the viewPosition: "+viewPosition);
        switch (view.getId()){
            case R.id.next_level:
                //Log.i(TAG,"inside RvAdapter's onClick, next_level branch");
                List<Category> c = myItemsDbHelper.getCategoriesBySuperId(categories.get(viewPosition).getId());
                if(!c.isEmpty()) {
                    SelectionDgFragment.CatSelectionBackStack.add(c);
                    //Log.i(TAG,"inside RvAdapter's onClick, next_level branch, cat_id: "+categories.get(viewPosition).getId());
                    mRv.swapAdapter(new Cat_SelectionAdapter(c, context,mSdFg, atp), true);
                }else{
                    Toast.makeText(context,"下一层没有数据了",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.make_selection:
                //Log.i(TAG,"inside onClick-make selection. "+locations.get(viewPosition).getName()+sdFg.toString());
                mSdFg.onSingleSelectionMade(categories.get(viewPosition).getId(), atp);
                break;
        }
    }




}
