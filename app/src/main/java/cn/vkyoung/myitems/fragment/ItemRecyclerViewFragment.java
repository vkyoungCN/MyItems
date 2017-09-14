package cn.vkyoung.myitems.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.vkyoung.myitems.R;
import cn.vkyoung.myitems.sqlite.Item;

import java.util.ArrayList;

/**
 * Created by VkYoung16 on 2017/8/18.
 */

public class ItemRecyclerViewFragment extends Fragment {
    private static final String TAG = "MyItems-RvFragment";
    private static final String ITEM_RESULT = "item_search_result";
    private RecyclerView mRecyclerView;
    private ArrayList<Item> items;

   @Override
    public void onCreate(Bundle savedInstanceState){
        //Log.i(TAG,"inside onCreate(), before any calls.");
        super.onCreate(savedInstanceState);
        items = getArguments().getParcelableArrayList(ITEM_RESULT);
        //Log.i(TAG,"inside Fragment onCreate(), got data(the items)"+items.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        //Log.i(TAG,"inside onCreateView(), before any calls.");
        View rootView = inflater.inflate(R.layout.item_result_rv_fragment, container, false);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.i(TAG,"inside onCreateView(), ready to set the Adapter.");
        mRecyclerView.setAdapter(new ItemAdapter(items));

        return rootView;
    }

    @Override
    public void onPause(){
        super.onPause();
    }
}
