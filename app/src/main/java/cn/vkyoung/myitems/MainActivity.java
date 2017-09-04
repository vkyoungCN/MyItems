package cn.vkyoung.myitems;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.vkyoung.myitems.assistant.ForgiveSize;
import cn.vkyoung.myitems.assistant.SelectionType;
import cn.vkyoung.myitems.fragment.Cat_SelectionAdapter;
import cn.vkyoung.myitems.fragment.ItemRecyclerViewFragment;
import cn.vkyoung.myitems.fragment.SelectionDgFragment;
import cn.vkyoung.myitems.sqlite.Item;
import cn.vkyoung.myitems.sqlite.MyItemsDbHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyItems-MainActivity";
    private static final String ITEM_RESULT = "item_search_result";

    static ForgiveSize current_size_flag = ForgiveSize.LARGE;//初始状态

    private MyItemsDbHelper mDbHelper;
    private ArrayList<Item> resultItems;
    private String mItemName = "";
    private String mItemNameLastTime = "";//记录检索框上次输入值，避免同词多次检索消耗DB资源；

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        Log.i(TAG," Inside onCreate(), after super().");

        setSupportActionBar((Toolbar)findViewById(R.id.m_toolbar));

        mDbHelper  = MyItemsDbHelper.getInstance(getApplicationContext());
        final TextView tv_item = (TextView)findViewById(R.id.item_total_num) ;
        final TextView tv_location = (TextView)findViewById(R.id.location_total_num);
        int i = mDbHelper.getItemTotalNum();//i还要用于判断所以需要保留，不能匿名。

        tv_item.setText(getResources().getString(R.string.current_total_items_num01)
                +String.valueOf(i)
                +getResources().getString(R.string.current_total_items_num02));
        tv_location.setText(getResources().getString(R.string.current_total_locations_num01)
               +String.valueOf(mDbHelper.getLocationTotalNum())
               +getResources().getString(R.string.current_total_locations_num02));

       if(i==0){
           //Log.i(TAG," Inside newly Btn, now items = 0");
           final Button b = (Button)findViewById(R.id.debug_data_import);
           b.setVisibility(View.VISIBLE);
           b.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Log.i(TAG," Inside newly Btn, clicking");
                   mDbHelper.wayMuchCategoriesInsert();
                   b.setVisibility(View.GONE);
                   tv_item.setText(getResources().getString(R.string.current_total_items_num01)
                           +String.valueOf(mDbHelper.getItemTotalNum())
                           +getResources().getString(R.string.current_total_items_num02));
                   tv_location.setText(getResources().getString(R.string.current_total_locations_num01)
                           +String.valueOf(mDbHelper.getLocationTotalNum())
                           +getResources().getString(R.string.current_total_locations_num02));
               }
           });
       }

       //Log.i(TAG," Inside MainActivity, onCreat(); after retrieve itemNumber & Initial Data.");
        final Button button = (Button) findViewById(R.id.search_go_btn);
            button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                //点击按钮之后，先从编辑框取字符（检索关键字）
                final EditText editText = (EditText)findViewById(R.id.search_input);
                mItemName = editText.getText().toString();

                if (mItemName.isEmpty()) {
                    Toast.makeText(getBaseContext(),"请填入检索关键词", Toast.LENGTH_SHORT).show();
                }else {
                    if(mItemNameLastTime.isEmpty()|| !mItemName.equals(mItemNameLastTime)) {
                        //mItemNameLastTime为空则是首次检索；非空且不等新输入的mItemName才可触发DB查询；
                        //为短路判断，保留前半判别式。

                        mItemNameLastTime = mItemName;
                        resultItems = (ArrayList<Item>) mDbHelper.getItemsByName(mItemName);
                        // Log.i(TAG,"after returned MyItemsDbHelper now.");
                        if (resultItems.size() != 0) {
                            //Log.i(TAG,"inside MainActivity, resultItems the ArrayList： "+ resultItems.toString());
                            //将检索结果ArrayList<Item>存入新建Fragment的Bundle
                            ItemRecyclerViewFragment resultFragment = new ItemRecyclerViewFragment();
                            //Log.i(TAG,"inside MainActivity, ItemResultFragment: "+ resultFragment.toString());
                            Bundle args = new Bundle();
                            args.putParcelableArrayList(ITEM_RESULT, resultItems);
                            resultFragment.setArguments(args);
                            //    Log.i(TAG,"检索有结果");

                            //缩小上部区域
                            forgiveAreaSize(ForgiveSize.SMALL);

                            //替换Fragment
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            //Log.i(TAG,"inside MainActivity, after FT: "+ transaction.toString());
                            transaction.replace(R.id.item_result, resultFragment, "item_result").commit();
                        } else {
                            if (getFragmentManager().findFragmentByTag("item_result") != null) {
                                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("item_result")).commit();
                                forgiveAreaSize(ForgiveSize.LARGE);//把原结果Fragment移除，然后将顶部恢复扩大
                                //Log.i(TAG,"NOT NULL FG");
                            }
                            Toast.makeText(getBaseContext(), "没有匹配的记录", Toast.LENGTH_SHORT).show();
                            //Log.i(TAG,"NULL FG");
                        }
                    }else{
                        Toast.makeText(getBaseContext(), "输入的字符没变哦", Toast.LENGTH_SHORT).show();
                    }//if (resultItems.size() != 0)
                } //end of if(mItemName.isEmpty())


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.i(TAG,"inside onOptionsItemSelected(), before any calls.");
        switch (item.getItemId()){
            case R.id.item_show_by_loc:
                Log.i(TAG,"inside onOptionsItemSelected-SW, SHOW BY LOC branch.");
                showDialog(0,SelectionType.LOCATION_SELECTION);
                return true;

            case R.id.item_show_by_cat:
                Log.i(TAG,"inside onOptionsItemSelected-SW, SHOW BY CAT branch.");
                showDialog(0,SelectionType.CATEGORY_SELECTION);
                return true;

            case R.id.loc_manage:
                return true;

            case R.id.cat_manage:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void forgiveAreaSize(ForgiveSize toSize) {

        //需要先获取屏幕情况，以便计算dpi-pixel比例;
        //LayoutParams的各设置方法都需要以pixel为单位的数值，而在XML中各参数均为dpi单位数值
        //为保证一致性，在此进行了转换操作
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float ratio = ratioCalculate(metrics); // 计算当前屏幕的 dpi-pixel比例；
        //Log.i(TAG,String.valueOf(metrics.densityDpi));

        if (ratio != 0) {
            changeTheSize(ratio, toSize);
        } else {
            return;
        }
    }

    void changeTheSize(float ratio, ForgiveSize toSize){
        //LayoutParams设置时需要用到的几个数据；
        //按dpi计算，可修改。
        int smallHeight = 100;
        int smallTopMargin = 10;
        int largeHeight = 220;
        int largeTopMargin = 50;

        //不同面积下用到的Logo图案大小不同
        int smallLogoResourceId = R.drawable.ic_android_white_48dp;
        int largeLogoResourceId = R.drawable.ic_android_white_64dp;

        //robot两次设置时的layoutParams基础属性一致，可共用
        RelativeLayout.LayoutParams robotLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //取到控件
        RelativeLayout forgiveArea = (RelativeLayout) findViewById(R.id.the_forgive_area);
        ImageView robot = (ImageView)findViewById(R.id.robot_logo);

        if(toSize == ForgiveSize.SMALL && current_size_flag == ForgiveSize.LARGE){
            //Log.i(TAG,"inside last several IFs,the first; before any");
            forgiveArea.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.
                    MATCH_PARENT,(int)(smallHeight*ratio)));
            //Log.i(TAG,"inside last several IFs,the first; already set the pms");
            robotLayoutParams.setMargins(0,(int)(smallTopMargin*ratio),0,0);
            robotLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            robot.setLayoutParams(robotLayoutParams);
            robot.setImageResource(smallLogoResourceId);
            //Log.i(TAG,"inside last several IFs,the first; already set the robot src");
            current_size_flag = ForgiveSize.SMALL;
            //Log.i(TAG,"inside last several IFs,the first; made the current_size_flag to SMALL");
        }else if(toSize == ForgiveSize.LARGE && current_size_flag==ForgiveSize.SMALL) {
            //Log.i(TAG,"inside last several IFs,the else-if");
            forgiveArea.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT,(int)(largeHeight*ratio)));
            robotLayoutParams.setMargins(0,(int)(largeTopMargin*ratio),0,0);
            robotLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            robot.setLayoutParams(robotLayoutParams);
            robot.setImageResource(largeLogoResourceId);
            current_size_flag = ForgiveSize.LARGE;
        }else{
            Log.i(TAG,"inside last several IFs,the last else");
            Log.i(TAG,"Skipping OR wrong params for the method");
        }


    }

    private float ratioCalculate(DisplayMetrics metrics){
        /*
        * dpi和像素的换算比例：
        * 当屏幕为xhdpi(320密度）时，1dpi=2pixel
        * 当屏幕为hdpi（240密度）时，1.5
        * 当屏幕为mdpi（160密度）时，1
        * 当屏幕为ldpi（120密度）时，0.5
        * 当屏幕为xxhdpi（480密度）时，3
        * 当屏幕为xxxhdpi（640密度）时，4
        * */
        float ratio = 0;
        ratio = metrics.densityDpi/160;//这样直接计算可兼容任意密度的屏幕

        /*
        switch (metrics.densityDpi) {
            case 320:
                ratio = 2;
                Log.i(TAG,"inside SW，320-branch");
                break;
            case 240:
                ratio = (float)1.5;
                break;
            case 160:
                ratio = 1;
                break;
            case 120:
                ratio = (float)0.75;
                break;
            case 480:
                ratio = 3;
                break;
            case 640:
                ratio = 4;
                break;
            default:
                //无匹配时屏幕非常规配置，返回0，应禁用LOGO变化动作
                Log.i(TAG,"inside SW, DF-branch");
        }*/
        return ratio;

    }

    public void showDialog(int superId, SelectionType st){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("selection_dialog");

         if(prev != null){
             Log.i(TAG, "inside showDialog(), inside if prev!=null branch");
             ft.remove(prev);
         }
         ft.addToBackStack(null);
         //从option menu的调用都是首层开始选择，所以SuperId=0。
         //DialogFragment dfg = SelectionDgFragment.newInstance(0,st);
         DialogFragment dfg = SelectionDgFragment.newInstance(superId,st);
         //Log.i(TAG, "inside showDialog(), fetched the SelectionDfg instance & passed with 0 and selectionType: "+st.toString());
         dfg.show(ft, "selection_dialog");
    }

}
