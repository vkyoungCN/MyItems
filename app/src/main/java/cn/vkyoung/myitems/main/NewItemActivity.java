package cn.vkyoung.myitems;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cn.vkyoung.myitems.assistant.ActionType;
import cn.vkyoung.myitems.assistant.ActionType;
import cn.vkyoung.myitems.fragment.SelectionDgFragment;
import cn.vkyoung.myitems.sqlite.Item;
import cn.vkyoung.myitems.sqlite.MyItemsDbHelper;

public class NewItemActivity extends AppCompatActivity implements SelectionDgFragment.OnDataPassOut{
    private final String TAG = "MyItems-NewItemAct";
    private MyItemsDbHelper myItemsDbHelper;
    private DialogFragment dfg;
    private Item item = new Item();
    private long locId = 0;
    private long catId = 0;
    private String locName;
    private String catName;
    private int dropState = 0;
    private String mainImagePath = null;

    private ImageView iv_MainImage;
    private EditText et_ItemName;
    private EditText et_ItemDescription;
    private TextView tv_designateLocation;
    private TextView tv_designateCategory;
    private Spinner sp_ropStateSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        iv_MainImage = (ImageView)findViewById(R.id.main_image);
        et_ItemName = (EditText)findViewById(R.id.new_item_name);
        et_ItemDescription = (EditText)findViewById(R.id.new_item_description);
        tv_designateLocation = (TextView)findViewById(R.id.loc_deg_tv_ItemNew);
        tv_designateCategory = (TextView)findViewById(R.id.cat_deg_tv_ItemNew);
        sp_ropStateSpinner = (Spinner)findViewById(R.id.drop_spinner);
        myItemsDbHelper = MyItemsDbHelper.getInstance(getApplicationContext());
        if(savedInstanceState!=null){
            et_ItemName.setText(savedInstanceState.getString("ITEM_NAME"));
            Log.i(TAG,"INSIDE onCreate with savedInstanceState not null, name: "+savedInstanceState.getString("ITEM_NAME"));
            et_ItemDescription.setText(savedInstanceState.getString("ITEM_DES"));
            sp_ropStateSpinner.setSelection(savedInstanceState.getInt("DROP_STATE"));
            //其余暂不记录，待补
        }
    }

    protected void onSaveInstanceState(Bundle outState){
        outState.putString("ITEM_NAME",et_ItemName.getText().toString());
        outState.putString("ITEM_DES",et_ItemDescription.getText().toString());
        outState.putLong("ITEM_LOC_ID",locId);
        outState.putLong("ITEM_CAT_ID",catId);
        outState.putInt("DROP_STATE",dropState);
        //outState.putString("MAIN_IMAGE_PATH",mainImagePath);
        Log.i(TAG,"INSIDE onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    public void selectLocation(View view){
        showDialog(0,ActionType.PURE_SELECT_LOCATION);
    }

    public void selectCategory(View view){
        showDialog(0,ActionType.PURE_SELECT_CATEGORY);
    }

    @Override
    public void onIdPassOut(long id,ActionType at){
        Log.i(TAG,"onNamePassOut called");
        if(at == ActionType.PURE_SELECT_LOCATION) {
            String name =myItemsDbHelper.getLocationNameById(id);
            if(name!=null) {
                //因为位置是选填信息，可能为空
                tv_designateLocation.setText(name);
            }
            locId = id;
            locName = name;
        }else if(at == ActionType.PURE_SELECT_CATEGORY){
            String name =myItemsDbHelper.getCategoryNameById(id);
            if(name != null){
                //类别也是选填信息，可能为空
                tv_designateCategory.setText(name);
            }
            catId = id;
            catName = name;
        }else {
            Log.e(TAG,"Wrong logic (ActionType) got here...");
            return;
        }
        Log.i(TAG,"onNamePassOut called, TextView has been set.");
        dfg.dismiss();

    }


    public void showDialog(int superId, ActionType at){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("selection_dialog");

        if(prev != null){
            Log.i(TAG, "inside showDialog(), inside if prev!=null branch");
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        //从option menu的调用都是首层开始选择，所以SuperId=0。
        //DialogFragment dfg = SelectionDgFragment.newInstance(0,st);
        dfg = SelectionDgFragment.newInstance(superId,at);
        //Log.i(TAG, "inside showDialog(), fetched the SelectionDfg instance & passed with 0 and selectionType: "+st.toString());
        dfg.show(ft, "selection_dialog");
    }

    public void addSingleItem(View view){
        Log.i(TAG,"inside addSingleItem");
        if(et_ItemName.getText().toString().isEmpty()){
            Toast.makeText(this, "应当输入物品名称", Toast.LENGTH_SHORT).show();
        }else{
            item.setName(et_ItemName.getText().toString());
            item.setDescription(et_ItemDescription.getText().toString());
            item.setLocationId(locId);
            item.setCategoryId(catId);
            item.setDropStatus(sp_ropStateSpinner.getSelectedItemPosition());
            item.setMainImage(mainImagePath);
            long resultLine = myItemsDbHelper.dbCreateItem(item);
            Intent intent = new Intent(getBaseContext(),SingleItemDetail.class);
            intent.putExtra("RESULT_ID", resultLine);
            intent.putExtra("LOCATION_NAME",locName);
            intent.putExtra("CATEGORY_NAME",catName);
            intent.putExtra("ADD_NEW_ITEM",true);
            startActivity(intent);

        }


    }



}
