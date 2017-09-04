package cn.vkyoung.myitems.fragment;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.vkyoung.myitems.R;
import cn.vkyoung.myitems.assistant.SelectionType;
import cn.vkyoung.myitems.sqlite.Category;
import cn.vkyoung.myitems.sqlite.Location;
import cn.vkyoung.myitems.sqlite.MyItemsDbHelper;

/**
 * Created by VkYoung16 on 2017/9/2.
 */

public class SelectionDgFragment extends DialogFragment {
    private static final String TAG = "MyItems-SlcFragment";
    private List<Location> locations;
    private List<Category> categories;
    private SelectionType stp;
    private int superId;

    public static List<List<Category>> CatSelectionBackStack = new ArrayList<>();
    public static List<List<Location>> LocSelectionBackStack = new ArrayList<>();
    //private Cat_SelectionAdapter.SelectionOnClickListener mListener;

    //Enum本身已实现了Serializable接口（若再实现Parcelable可能因方法二义性出错）
    public static SelectionDgFragment newInstance(int superId, SelectionType st){
        SelectionDgFragment sdg = new SelectionDgFragment();

        Bundle args = new Bundle();
        args.putInt("SUPER_ID", superId);
        args.putSerializable("SELECTION_TYPE", st);
        sdg.setArguments(args);

        return sdg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i(TAG,"inside onCreate(), before any calls.");

        superId = getArguments().getInt("SUPER_ID");//每次调用本Fragment都需要传入SuperId。
        stp = (SelectionType) getArguments().getSerializable("SELECTION_TYPE");
        Log.i(TAG,"inside onCreate(), got superId & type form the Bundle,type= "+stp.toString());

        MyItemsDbHelper mDbHelper = MyItemsDbHelper.getInstance(getActivity().getApplicationContext());
        if(stp == SelectionType.LOCATION_SELECTION) {
            locations =  mDbHelper.getLocationsBySuperId(superId);
            LocSelectionBackStack.add(locations);//将获取到的第一组数据加入backStack
            if(!CatSelectionBackStack.isEmpty()) {//loc的检索，应把cat的Bs清空。
                CatSelectionBackStack.clear();
            }
            Log.i(TAG, "inside onCreate(), got locations form DB.");
        }else{
            categories = mDbHelper.getCategoriesBySuperId(0);
            CatSelectionBackStack.add(categories);
            if(!LocSelectionBackStack.isEmpty()){
                LocSelectionBackStack.clear();
            }
            Log.i(TAG, "inside onCreate(), got categories form DB.");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Log.i(TAG,"inside onCreateView(), before any calls.");
        View rootView = inflater.inflate(R.layout.selection_rv_fragment, container, false);

        final RecyclerView mRecyclerView = rootView.findViewById(R.id.recyclerView_selection);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.i(TAG,"inside onCreateView(), ready to set the Adapter.");
        if(stp == SelectionType.LOCATION_SELECTION) {
            mRecyclerView.setAdapter(new Loc_SelectionAdapter(locations,getContext()));
        }else{
            mRecyclerView.setAdapter(new Cat_SelectionAdapter(categories,getContext()));
            Log.i(TAG,"inside onCreateView(),cat branch, C_Adapter with Context.");
        }

        ImageView leftArrow = rootView.findViewById(R.id.left_arrow);
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stp == SelectionType.LOCATION_SELECTION) {
                    //BS中存放的最新数据是和当前显示一致的数据；当后退时，需要先把最近数据移除，
                    //然后将次一的数据加载给新Adapter。
                    if(LocSelectionBackStack.size()>1) {
                        LocSelectionBackStack.remove(LocSelectionBackStack.size()-1);
                        mRecyclerView.swapAdapter(new Loc_SelectionAdapter(LocSelectionBackStack.get(LocSelectionBackStack.size()-1), getContext()), true);
                    }else{
                        Toast.makeText(getContext(),"已经是最上一层了", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(CatSelectionBackStack.size()>1) {
                        CatSelectionBackStack.remove(CatSelectionBackStack.size()-1);
                        Log.i(TAG, "inside onClick(),cat branch, ready to swap Adapter.");
                        mRecyclerView.swapAdapter(new Cat_SelectionAdapter(CatSelectionBackStack.get(CatSelectionBackStack.size()-1), getContext()),true);
                    }else {
                        Toast.makeText(getContext(),"已经是最上一层了", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        return rootView;
    }
    @Override
    public void onCancel(DialogInterface dialog){
        //当用户取消DialogFragment（点击了Back键或Dfg以外区域）时，应当将BackStack清空。
        if(!LocSelectionBackStack.isEmpty())
            LocSelectionBackStack.clear();
        if(!CatSelectionBackStack.isEmpty())
            CatSelectionBackStack.clear();
    }




}
