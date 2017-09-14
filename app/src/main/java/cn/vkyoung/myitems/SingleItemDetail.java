package cn.vkyoung.myitems;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cn.vkyoung.myitems.sqlite.Item;
import cn.vkyoung.myitems.sqlite.MyItemsDbHelper;

public class SingleItemDetail extends AppCompatActivity {
    private MyItemsDbHelper myItemsDbHelper;
    private boolean fromNewItemActivity = false;
    private Item item;
    private long itemId = 0;
    private long locId = 0;
    private long catId = 0;
    private String locName;
    private String catName;
    private int dropState = 0;
    private String mainImagePath = null;

    private ImageView iv_MainImage;
    private TextView tv_ItemName;
    private TextView tv_ItemDescription;
    private TextView tv_designateLocation;
    private TextView tv_designateCategory;
    private TextView tv_dropped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item_detail);

        itemId = getIntent().getLongExtra("RESULT_ID",0);
        fromNewItemActivity = getIntent().getBooleanExtra("ADD_NEW_ITEM",false);
        locName = getIntent().getStringExtra("LOCATION_NAME");
        catName = getIntent().getStringExtra("CATEGORY_NAME");

        if(itemId!=0){
            myItemsDbHelper = MyItemsDbHelper.getInstance(getApplicationContext());
            item = myItemsDbHelper.getItemsById(itemId);

            iv_MainImage = (ImageView)findViewById(R.id.main_image_item_detail);
            tv_ItemName = (TextView) findViewById(R.id.detail_item_name);
            tv_ItemDescription = (TextView) findViewById(R.id.detail_item_description);
            tv_designateLocation = (TextView)findViewById(R.id.loc_shw_tv_ItemDetail);
            tv_designateCategory = (TextView)findViewById(R.id.cat_shw_tv_ItemDetail);
            tv_dropped = (TextView)findViewById(R.id.dropped_shw_single_item);


            tv_ItemName.setText(item.getName());
            tv_ItemDescription.setText(item.getDescription());
            if(item.getDropStatus()== 1){
                tv_dropped.setVisibility(View.VISIBLE);
            }

            if(locName!=null) {
                //因为位置是选填信息，可能为空；且并不是所有调用方都会传递该参数
                tv_designateLocation.setText(locName);
            }
            if(catName!=null) {
                //因为位置是选填信息，可能为空；且并不是所有调用方都会传递该参数
                tv_designateCategory.setText(catName);
            }

            if(fromNewItemActivity==true){
                findViewById(R.id.frame_new_item_succeed).setVisibility(View.VISIBLE);
                AnimatorSet setAnimator = (AnimatorSet) AnimatorInflater.
                        loadAnimator(getBaseContext(),R.animator.banner_show);
                setAnimator.setTarget(findViewById(R.id.frame_new_item_succeed));
                setAnimator.start();
            }

        }else {
            Toast.makeText(getBaseContext(),"参数错误，id=0.",Toast.LENGTH_SHORT).show();
        }


    }
}
