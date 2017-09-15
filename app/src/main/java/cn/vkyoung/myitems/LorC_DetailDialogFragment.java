package cn.vkyoung.myitems;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.vkyoung.myitems.assistant.ActionType;
import cn.vkyoung.myitems.assistant.OnSelectionMade;
import cn.vkyoung.myitems.fragment.SelectionDgFragment;
import cn.vkyoung.myitems.sqlite.Category;
import cn.vkyoung.myitems.sqlite.Location;
import cn.vkyoung.myitems.sqlite.MyItemsDbHelper;

/**
 * Created by VkYoung16 on 2017/9/15.
 */

public class LorC_DetailDialogFragment extends DialogFragment implements OnSelectionMade {
    private static final String TAG = "MyItems-LorC_Ddfg";
    private Location mLocation;
    private Category mCategory;
    private ActionType atp;
    private long selfId;

    public static LorC_DetailDialogFragment newInstance(long selfId, ActionType atp){
        Log.i(TAG,"inside newInstance(), before any calls.");
        LorC_DetailDialogFragment sdg = new LorC_DetailDialogFragment();

        Bundle args = new Bundle();
        args.putLong("SELF_ID", selfId);
        args.putSerializable("ACTION_TYPE", atp);
        sdg.setArguments(args);

        return sdg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i(TAG,"inside onCreate(), before any calls.");

        selfId = getArguments().getLong("SELF_ID");
        atp = (ActionType)getArguments().getSerializable("ACTION_TYPE");

        MyItemsDbHelper mDbHelper = MyItemsDbHelper.getInstance(getActivity().getApplicationContext());
        if(atp == ActionType.MANAGE_LOCATION) {
            mLocation = mDbHelper.getLocationById(selfId);
        }else if(atp == ActionType.MANAGE_CATEGORY){
            mCategory = mDbHelper.getCategoryById(selfId);
        }else{
            Log.e(TAG,"Wrong logic (ActionType) got here...");
            return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "inside onCreateView(), before any calls.");
        View rootView = inflater.inflate(R.layout.lorc_detail_fragment, container, false);

        LinearLayout sizeSettingRt = rootView.findViewById(R.id.sizeSettingLt);
        //LinearLayout locationDegLt = rootView.findViewById(R.id.loc_deg_lay_locdfg);
        //LinearLayout categoryDegLt = rootView.findViewById(R.id.cat_deg_lay_locdfg);
        //Log.i(TAG, "inside onCreateView(), 3 VG found");

        //通过调整root-VG的大小将dialogFg的宽度设置为90%，高度设为屏幕可用部分的60%。

        WindowManager appWm = (WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        appWm.getDefaultDisplay().getSize(point);
        LinearLayout.LayoutParams gLp = new LinearLayout.LayoutParams((int)(point.x*0.9),(int)(point.y*0.6));
        sizeSettingRt.setLayoutParams(gLp);
        //Log.i(TAG, "inside onCreateView(), size set");
        /*
        if(atp == ActionType.MANAGE_LOCATION){
            locationDegLt.setVisibility(View.VISIBLE);
            categoryDegLt.setVisibility(View.GONE);
        }else if(atp == ActionType.MANAGE_CATEGORY){
            locationDegLt.setVisibility(View.GONE);
            categoryDegLt.setVisibility(View.VISIBLE);
        }
*/

        return rootView;
    }

    /*
    @Override
    public void onResume(){
        super.onResume();

        Log.i(TAG, "inside onResume(), before any calls.");
        //调整dialogFg的高度为可用屏幕的60%
        WindowManager appWm = (WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        appWm.getDefaultDisplay().getSize(point);
        WindowManager.LayoutParams wLp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(point.y*0.6));
        Log.i(TAG, "inside onResume(), point.y: "+point.y);
        try {
            getDialog().getWindow().setAttributes(wLp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

    @Override
    public void onSingleSelectionMade(long id, ActionType atp){
        Log.i(TAG,"onSingleSelectionMade called");
        //要在这里接收所选定的新归属父节点的id；
        //对该id进行防闭环检测
        //然后设置tv，以及mLocation的SuperId属性。

    }

    }
