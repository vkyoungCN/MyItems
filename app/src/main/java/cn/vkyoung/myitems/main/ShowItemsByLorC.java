package cn.vkyoung.myitems;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import cn.vkyoung.myitems.fragment.ItemAdapter;
import cn.vkyoung.myitems.sqlite.Item;

public class ShowItemsByLorC extends AppCompatActivity {
    private static final String TAG = "MyItems-ShwItsByLC";
    public static final String ITEMS = "item_for_SIBCL_use";
    public static final String BUNDLE = "bundle_for_SIBCL";
    private RecyclerView mRecyclerView;
    private ArrayList<Item> items = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items_by_lorc);

        items = getIntent().getExtras().getParcelableArrayList(ITEMS);

        //if(items.isEmpty()){
        // 已在发起调用的Activity进行了判断。
       // }else {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_items_showByLorC);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.i(TAG, "inside onCreate(), ready to set the Adapter.");
        mRecyclerView.setAdapter(new ItemAdapter(items));
        //}
    }
}
