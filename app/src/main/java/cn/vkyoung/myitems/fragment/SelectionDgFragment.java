package cn.vkyoung.myitems.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.vkyoung.myitems.R;
import cn.vkyoung.myitems.assistant.ActionType;
import cn.vkyoung.myitems.assistant.OnSelectionMade;
import cn.vkyoung.myitems.sqlite.Category;
import cn.vkyoung.myitems.sqlite.Location;
import cn.vkyoung.myitems.sqlite.MyItemsDbHelper;

/**
 * Created by VkYoung16 on 2017/9/2.
 */

public class SelectionDgFragment extends DialogFragment implements OnSelectionMade {
    private static final String TAG = "MyItems-SlcFragment";
    private OnDataPassOut targetActivity;
    private SelectionDgFragment self = this;
    private List<Location> locations;
    private List<Category> categories;
    private ActionType atp;
    private SelectionMode sMode;
    private long superId;

    public static List<List<Category>> CatSelectionBackStack = new ArrayList<>();
    public static List<List<Location>> LocSelectionBackStack = new ArrayList<>();
    //private Cat_SelectionAdapter.SelectionOnClickListener mListener;

    public interface OnDataPassOut{
        public void onIdPassOut(long id, ActionType at);
    }
    
    enum SelectionMode{
        Location, Category,Undefine
    }
    //Enum本身已实现了Serializable接口（若再实现Parcelable可能因方法二义性出错）

    public static SelectionDgFragment newInstance(long superId, ActionType atp){
        Log.i(TAG,"inside newInstance(), before any calls.");
        SelectionDgFragment sdg = new SelectionDgFragment();

        Bundle args = new Bundle();
        args.putLong("SUPER_ID", superId);
        args.putSerializable("SELECTION_TYPE", atp);
        sdg.setArguments(args);

        return sdg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Log.i(TAG,"inside onCreate(), before any calls.");

        superId = getArguments().getLong("SUPER_ID");//每次调用本Fragment都需要传入SuperId。
        atp = (ActionType) getArguments().getSerializable("SELECTION_TYPE");
        
        //提前判断，避免大量的||运算
        if(atp == ActionType.PURE_SELECT_LOCATION || atp == ActionType.SHOW_ITEMS_BY_LOCATION || atp==ActionType.MANAGE_LOCATION){
            sMode = SelectionMode.Location;
        }else if(atp == ActionType.PURE_SELECT_CATEGORY || atp == ActionType.SHOW_ITEMS_BY_CATEGORY || atp==ActionType.MANAGE_CATEGORY){
            sMode = SelectionMode.Category;
        }else {
            sMode = SelectionMode.Undefine;
        }

        MyItemsDbHelper mDbHelper = MyItemsDbHelper.getInstance(getActivity().getApplicationContext());

        if(sMode == SelectionMode.Location) {
            locations =  mDbHelper.getLocationsBySuperId(superId);
            LocSelectionBackStack.add(locations);//将获取到的第一组数据加入backStack
            if(!CatSelectionBackStack.isEmpty()) {//loc的检索，应把cat的Bs清空。
                CatSelectionBackStack.clear();
            }
        }else if(sMode == SelectionMode.Category){
            categories = mDbHelper.getCategoriesBySuperId(0);
            CatSelectionBackStack.add(categories);
            if(!LocSelectionBackStack.isEmpty()){
                LocSelectionBackStack.clear();
            }
        }else{
            Log.e(TAG,"Wrong logic (ActionType) got here...");
            return;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        Log.i(TAG,"inside onCreateView(), before any calls.");
        View rootView = inflater.inflate(R.layout.selection_rv_fragment, container, false);


        //通过调整root-VG的大小将dialogFg的宽度设置为90%，高度设为屏幕可用部分的85%。
        RelativeLayout rlt = rootView.findViewById(R.id.size_selection_fg);
        WindowManager appWm = (WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        appWm.getDefaultDisplay().getSize(point);
        RelativeLayout.LayoutParams gLp = new RelativeLayout.LayoutParams((int)(point.x*0.9),(int)(point.y*0.85));
        rlt.setLayoutParams(gLp);


        final RecyclerView mRecyclerView = rootView.findViewById(R.id.recyclerView_selection);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //Log.i(TAG,"inside onCreateView(), ready to set the Adapter.");
        if(sMode == SelectionMode.Location) {
            mRecyclerView.setAdapter(new Loc_SelectionAdapter(locations,getContext(),this,atp));
        }else if (sMode == SelectionMode.Category){
            mRecyclerView.setAdapter(new Cat_SelectionAdapter(categories,getContext(),this,atp));
            //Log.i(TAG,"inside onCreateView(),cat branch, C_Adapter with Context.");
        }

        ImageView leftArrow = rootView.findViewById(R.id.left_arrow);
        /*
        * 点击上一层按钮要有两部分处理：①BS处理；②swap Adapter。
        * */
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sMode == SelectionMode.Location) {
                    //BS中存放的最新数据是和当前显示一致的数据；当后退时，需要先把最近数据移除，
                    //然后将次一的数据加载给新Adapter。
                    if(LocSelectionBackStack.size()>1) {
                        LocSelectionBackStack.remove(LocSelectionBackStack.size()-1);
                        mRecyclerView.swapAdapter(new Loc_SelectionAdapter(LocSelectionBackStack.get(LocSelectionBackStack.size()-1), getContext(),self,atp), true);
                    }else{
                        Toast.makeText(getContext(),"已经是最上一层了", Toast.LENGTH_SHORT).show();
                    }
                }else if(sMode == SelectionMode.Category){
                    if(CatSelectionBackStack.size()>1) {
                        CatSelectionBackStack.remove(CatSelectionBackStack.size()-1);
                        Log.i(TAG, "inside onClick(),cat branch, ready to swap Adapter.");
                        mRecyclerView.swapAdapter(new Cat_SelectionAdapter(CatSelectionBackStack.get(CatSelectionBackStack.size()-1), getContext(),self,atp),true);
                    }else {
                        Toast.makeText(getContext(),"已经是最上一层了", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Log.e(TAG,"Wrong logic (ActionType) got here...");
                }

            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            targetActivity = (OnDataPassOut) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCancel(DialogInterface dialog){
        //当用户取消DialogFragment（点击了Back键或Dfg以外区域）时，应当将BackStack清空。
        if(!LocSelectionBackStack.isEmpty())
            LocSelectionBackStack.clear();
        if(!CatSelectionBackStack.isEmpty())
            CatSelectionBackStack.clear();
    }

    /*
    * 当Adapter处理onClick事件时，通过其对Fragment的引用，调用Fragment中的本方法，将所选ID传出；
    * 这里应进一步调用Activity的监听实现，把id进一步传给Activity以供最终使用。
    * */
    @Override
    public void onSingleSelectionMade(long id, ActionType atp){
        Log.i(TAG,"onSingleSelectionMade called");
        targetActivity.onIdPassOut(id,atp);

    }



}
