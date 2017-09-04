package cn.vkyoung.myitems.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.vkyoung.myitems.sqlite.MyItemsContract;

/**
 * Created by VkYoung16 on 2017/8/13.
 */

public class MyItemsDbHelper extends SQLiteOpenHelper {
    //如果修改了数据库结构方案，则应当改动（增加）版本号
    private static final String TAG = "MyItems-MyItemsDBHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyItems.db";
    private volatile static MyItemsDbHelper sMyItemsDbHelper = null;
    private SQLiteDatabase mSQLiteDatabase = null;

    /* 建表语句，在此构造 */
    public static final String SQL_CREATE_ITEMS_MAIN =
            "CREATE TABLE " + MyItemsContract.ItemsMain.TABLE_NAME + " (" +
                    MyItemsContract.ItemsMain._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MyItemsContract.ItemsMain.COLUMN_ITEM_NAME + " TEXT, " +
                    MyItemsContract.ItemsMain.COLUMN_ITEM_Description+ " TEXT, " +
                    MyItemsContract.ItemsMain.COLUMN_LOCATIONID + " INTEGER, " +
                    MyItemsContract.ItemsMain.COLUMN_CATEGORYID + " INTEGER, " +
                    MyItemsContract.ItemsMain.COLUMN_MAIN_IMAGE + " TEXT, " +
                    MyItemsContract.ItemsMain.COLUMN_DROPPED + " INTEGER)";

    private static final String SQL_CREATE_EXTRA_IMAGES =
            "CREATE TABLE " + MyItemsContract.ItemsImagesExtra.TABLE_NAME + " (" +
                    MyItemsContract.ItemsImagesExtra._ID + " INTEGER PRIMARY KEY," +
                    MyItemsContract.ItemsImagesExtra.COLUMN_ITEM_ID + " INTEGER," +
                    MyItemsContract.ItemsImagesExtra.COLUMN_IMAGE_PATH + " TEXT)";

    private static final String SQL_CREATE_LOCATIONS =
            "CREATE TABLE " + MyItemsContract.Locations.TABLE_NAME + " (" +
                    MyItemsContract.Locations._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MyItemsContract.Locations.COLUMN_LOC_NAME + " TEXT," +
                    MyItemsContract.Locations.COLUMN_LOC_DESCRIPTION + " TEXT," +
                    MyItemsContract.Locations.COLUMN_SUPER_LOCATION_ID + " INTEGER)";

    private static final String SQL_CREATE_CATEGORIES =
            "CREATE TABLE " + MyItemsContract.Categories.TABLE_NAME + " (" +
                    MyItemsContract.Categories._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MyItemsContract.Categories.COLUMN_CAT_NAME + " TEXT," +
                    MyItemsContract.Categories.COLUMN_CAT_DESCRIPTION + " TEXT," +
                    MyItemsContract.Categories.COLUMN_SUPER_CAT_ID + " INTEGER)";

    private static final String SQL_DROP_ITEMS_MAIN =
            "DROP TABLE IF EXISTS " + MyItemsContract.ItemsMain.TABLE_NAME;
    private static final String SQL_DROP_ITEMS_IMAGES =
            "DROP TABLE IF EXISTS " + MyItemsContract.ItemsImagesExtra.TABLE_NAME;
    private static final String SQL_DROP_LOCATIONS =
            "DROP TABLE IF EXISTS " + MyItemsContract.Locations.TABLE_NAME;
    private static final String SQL_DROP_CATEGORIES =
            "DROP TABLE IF EXISTS " + MyItemsContract.Categories.TABLE_NAME;


    //constructor
    private MyItemsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //Log.i(TAG,"inside MyItemsDbHelper Constructor, after the super ");
        mSQLiteDatabase = getWritableDatabase();
        //Log.i(TAG,"inside MyItemsDbHelper Constructor, got the Wdb: "+mSQLiteDatabase.toString());
    }

    //DCL模式单例，因为静态内部类模式不支持传参
    public static MyItemsDbHelper getInstance(Context context){
        //Log.i(TAG,"inside MyItemsDbHelper getInstance, before any calls");
        if(sMyItemsDbHelper == null){
            synchronized (MyItemsDbHelper.class){
                if(sMyItemsDbHelper == null){
                    sMyItemsDbHelper = new MyItemsDbHelper(context);
                }
            }
        }
        return sMyItemsDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.i(TAG,"inside MyItemsDbHelper onCreate");
        db.execSQL(SQL_CREATE_ITEMS_MAIN);
        //Log.i(TAG,"inside MyItemsDbHelper.onCreate(),behind 1st CREATE");
        //db.execSQL(SQL_CREATE_EXTRA_IMAGES);
        db.execSQL(SQL_CREATE_LOCATIONS);
        //Log.i(TAG,"inside MyItemsDbHelper.onCreate(),behind 2nd CREATE");
        db.execSQL(SQL_CREATE_CATEGORIES);
        //Log.i(TAG,"inside MyItemsDbHelper.onCreate(),behind 3rd CREATE");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //删掉旧表
        db.execSQL(SQL_DROP_ITEMS_MAIN);
        db.execSQL(SQL_DROP_ITEMS_IMAGES);
        db.execSQL(SQL_DROP_LOCATIONS);
        db.execSQL(SQL_DROP_CATEGORIES);

        //重建新表
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db,oldVersion,newVersion);

    }

    //以下是CRUD (Create, Read, Update and Delete)部分

    /*
    *  Creating(Inserting) an Item in table items_main
    *  returning value: the row ID of the newly inserted row, or -1 if an error occurred
    */

    public long dbCreateItem(Item item){
        long l;
        Log.i(TAG,"inside dbCreateItem(),before any calls, now DB open ?"+String.valueOf(mSQLiteDatabase.isOpen()));

        getWDbIfClosedOrNull();
        Log.i(TAG,"inside dbCreateItem(),after getWritableDatabase:"+mSQLiteDatabase.toString());
        ContentValues values = new ContentValues();
        Log.i(TAG,"inside dbCreateItem(), CV created");
        //values.put(MyItemsContract.ItemsMain._ID,0);
        values.put(MyItemsContract.ItemsMain.COLUMN_ITEM_NAME,item.getName());
        values.put(MyItemsContract.ItemsMain.COLUMN_ITEM_Description,item.getDescription());
        values.put(MyItemsContract.ItemsMain.COLUMN_DROPPED,item.getDropStatus());
        values.put(MyItemsContract.ItemsMain.COLUMN_LOCATIONID,item.getLocationId());
        values.put(MyItemsContract.ItemsMain.COLUMN_CATEGORYID,item.getCategoryId());
        //values.put(MyItemsContract.ItemsMain.COLUMN_MAIN_IMAGE,item.getMainImage());
        Log.i(TAG,"inside dbCreateItem(),after cv.put()");

        l = mSQLiteDatabase.insert(MyItemsContract.ItemsMain.TABLE_NAME, null, values);
        closeDB();
        return l;

    }

    /*
    * 本方法可以独立向item_image_extra表增加数据，用于只修改item的extraImage属性
    * 而其他属性保持不变时；需传入含有不定数量扩展图片的Item model类。
    * 如果返回值为0，表示没有插入任何数据（item的ExtraImages属性为空）。
    * (方法暂时停用，等待ExtraImages类上线后再改造启用
    public long dbCreateItemImages(Item item){
        SQLiteDatabase db = this.getWritableDatabase();

        long id = item.getId();
        long itemImage_id_last = 0;

        if(item.getExtraImages() !=null) {
            for (String s : item.getExtraImages()) {
                ContentValues values = new ContentValues();
                values.put(MyItemsContract.ItemsImagesExtra.COLUMN_ITEM_ID, id);
                values.put(MyItemsContract.ItemsImagesExtra.COLUMN_IMAGE_PATH, s);
                itemImage_id_last = db.insert(MyItemsContract.ItemsImagesExtra.TABLE_NAME, null, values);
            }

        }
        return itemImage_id_last;
    }
    */

    /*
     *本方法用于在dbCreateItem()方法内，同时完成向item_image_extr插入数据
     *
    public long dbCreateItemImages(long item_id, String path){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyItemsContract.ItemsImagesExtra.COLUMN_ITEM_ID,item_id);
        values.put(MyItemsContract.ItemsImagesExtra.COLUMN_IMAGE_PATH, path);

        long id = db.insert(MyItemsContract.ItemsImagesExtra.TABLE_NAME, null, values);

        return id;

    } */

    //向位置表插入一行
    public long dbCreateLocation(Location location){
        Log.i(TAG,"inside dbCreateLocation(),before any calls，now DB open ?"+String.valueOf(mSQLiteDatabase.isOpen()));

        getWDbIfClosedOrNull();

        ContentValues values = new ContentValues();
        values.put(MyItemsContract.Locations.COLUMN_LOC_NAME,location.getName());
        values.put(MyItemsContract.Locations.COLUMN_LOC_DESCRIPTION,location.getDescription());
        values.put(MyItemsContract.Locations.COLUMN_SUPER_LOCATION_ID,location.getSuperId());

        Log.i(TAG,"inside dbCreateLocation(),after cv.put()");
        return mSQLiteDatabase.insert(MyItemsContract.Locations.TABLE_NAME, null, values);

    }

    //向类别表插入一行
    public long dbCreateCategory(Category category){
        Log.i(TAG,"inside dbCreateCategory(),before any calls，now DB open ?"+String.valueOf(mSQLiteDatabase.isOpen()));

        getWDbIfClosedOrNull();
        ContentValues values = new ContentValues();
        values.put(MyItemsContract.Categories.COLUMN_CAT_NAME,category.getName());
        values.put(MyItemsContract.Categories.COLUMN_CAT_DESCRIPTION,category.getDescription());
        values.put(MyItemsContract.Categories.COLUMN_SUPER_CAT_ID,category.getSuperId());

        Log.i(TAG,"inside dbCreateCategory(),after cv.put()");
        return mSQLiteDatabase.insert(MyItemsContract.Categories.TABLE_NAME, null, values);

    }

    //以下是Read方法

    /*
    * 查询items的总数量；方法实现上只请求ID列减少开销；
    * 另外的getAllItem()请求所有字段，开销略大不适合查数量。
    * */
    public int getItemTotalNum(){
        //Log.i(TAG,"inside getItemTotalNum,before any calls,，now DB open ?"+String.valueOf(mSQLiteDatabase.isOpen()));
        getWDbIfClosedOrNull();
        String selectQuery = "SELECT " + MyItemsContract.ItemsMain._ID + " FROM " +
                MyItemsContract.ItemsMain.TABLE_NAME;
        Cursor cursor = mSQLiteDatabase.rawQuery(selectQuery, null);

        int i = cursor.getCount();
        //Log.i(TAG,"inside getItemTotalNum,after rawQuery, lines: "+ String.valueOf(i));
        cursor.close();
        closeDB();
        return i;
    }

    public int getLocationTotalNum(){
        //Log.i(TAG,"inside getLocTotalNum,before any calls,，now DB open ?"+String.valueOf(mSQLiteDatabase.isOpen()));

        getWDbIfClosedOrNull();
        String selectQuery = "SELECT " + MyItemsContract.Locations._ID + " FROM " +
                MyItemsContract.Locations.TABLE_NAME;
        Cursor cursor = mSQLiteDatabase.rawQuery(selectQuery, null);

        int i = cursor.getCount();
        //Log.i(TAG,"inside getLocTotalNum,after rawQuery, lines: "+ String.valueOf(i));
        cursor.close();
        closeDB();
        return i;
    }

    /*
    * 检索全部items（不包括扩展图片，在应用中扩展图片也是需要另外点击才能获取的）
    * 本方法耗时较长，谨慎调用
    * */
/*
    public List<Item> getAllItem(){
        List<Item> items = new ArrayList<Item>();
        String selectQuery = "SELECT * FROM" + MyItemsContract.ItemsMain.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //循环遍历，将所有row添加至list
        if(cursor.moveToFirst()){
            Item item = new Item();
            do{
                item.setId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain._ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_ITEM_NAME)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_ITEM_Description)));
                item.setDropStatus(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_DROPPED)));
                item.setLocationId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_LOCATIONID)));
                //item.setLocationName(getLocationNameById(item.getLocationId()));
                //item.setCategoryName(getCategoryNameById(item.getCategoryId()));
                item.setCategoryId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_CATEGORYID)));
                item.setMainImage(cursor.getString(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_MAIN_IMAGE)));
                items.add(item);
            }while (cursor.moveToNext());
        }

        return items;
    }
*/
    /*
    * 按名称检索items
    * */
    public List<Item> getItemsByName(String name){
        //Log.i(TAG,"inside getItemsByName,before any call，now DB open ?"+String.valueOf(mSQLiteDatabase.isOpen()));
        List<Item> items = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + MyItemsContract.ItemsMain.TABLE_NAME + " WHERE "
                + MyItemsContract.ItemsMain.COLUMN_ITEM_NAME + " LIKE '%" + name +"%'";
        //Log.i(TAG,"inside getItemsByName,before getReadableDatabase()");

        getWDbIfClosedOrNull();
        //Log.i(TAG,"inside getItemsByName,before rawQuery, after getReadableDatabase()");
        Cursor cursor = mSQLiteDatabase.rawQuery(selectQuery, null);

        //Log.i(TAG,"inside getItemsByName,after rawQuery, before Iterator");
        //循环遍历，将所有row添加至list
        if(cursor.moveToFirst()){

            //Log.i(TAG,"inside Iterator, before do - while ");
            do{
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain._ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_ITEM_NAME)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_ITEM_Description)));
                item.setDropStatus(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_DROPPED)));
                item.setLocationId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_LOCATIONID)));
                item.setCategoryId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_CATEGORYID)));
                //item.setLocationName(getLocationNameById(item.getLocationId()));
                //item.setCategoryName(getCategoryNameById(item.getCategoryId()));
                item.setMainImage(cursor.getString(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_MAIN_IMAGE)));
                items.add(item);
                //Log.i(TAG,"inside Iterator, Item: "+ item.toString()+" just loaded");
                //Log.i(TAG,"inside Iterator, Item has mainPath: "+ item.getMainImage());
            }while (cursor.moveToNext());
        }
        Log.i(TAG,"inside getItemsByName, after Iterator");
        cursor.close();
        closeDB();
        return items;
    }

    /*
    * 按位置检索items；目前暂时接受LocationId作为参数；后期有可能可以直接接受位置名参数（需转为id）
    * */
    public List<Item> getItemByLocation(long location_id){
        Log.i(TAG,"inside getItemsByLocation,before any calls，now DB open ?"+String.valueOf(mSQLiteDatabase.isOpen()));
        List<Item> items = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + MyItemsContract.ItemsMain.TABLE_NAME + " WHERE "
                + MyItemsContract.ItemsMain.COLUMN_LOCATIONID + " = " + location_id;

        getWDbIfClosedOrNull();
        Cursor cursor = mSQLiteDatabase.rawQuery(selectQuery, null);

        Log.i(TAG,"inside getItemsByLocation,after rawQuery, before Iterator");
        //循环遍历，将所有row添加至list
        if(cursor.moveToFirst()){

            do{
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain._ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_ITEM_NAME)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_ITEM_Description)));
                item.setDropStatus(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_DROPPED)));
                item.setLocationId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_LOCATIONID)));
                item.setCategoryId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_CATEGORYID)));
                //item.setLocationName(getLocationNameById(item.getLocationId()));
                //item.setCategoryName(getCategoryNameById(item.getCategoryId()));
                item.setMainImage(cursor.getString(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_MAIN_IMAGE)));
                items.add(item);
            }while (cursor.moveToNext());
        }
        Log.i(TAG,"inside getItemsByLocation, after Iterator");
        cursor.close();
        closeDB();

        return items;
    }

    /*
    * 按类别检索items
    * */
    public List<Item> getItemByCategory(long category_id){
        Log.i(TAG,"inside getItemByCategory,before any calls，now DB open ?"+String.valueOf(mSQLiteDatabase.isOpen()));
        List<Item> items = new ArrayList<Item>();
        String selectQuery = "SELECT * FROM " + MyItemsContract.ItemsMain.TABLE_NAME + " WHERE "
                + MyItemsContract.ItemsMain.COLUMN_CATEGORYID + " = " + category_id;

        getWDbIfClosedOrNull();
        Cursor cursor = mSQLiteDatabase.rawQuery(selectQuery, null);

        Log.i(TAG,"inside getItemByCategory,after rawQuery, before Iterator");
        //循环遍历，将所有row添加至list
        if(cursor.moveToFirst()){
            do{
                Item item = new Item();
                item.setId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain._ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_ITEM_NAME)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_ITEM_Description)));
                item.setDropStatus(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_DROPPED)));
                item.setLocationId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_LOCATIONID)));
                item.setCategoryId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_CATEGORYID)));
                //item.setLocationName(getLocationNameById(item.getLocationId()));
                //item.setCategoryName(getCategoryNameById(item.getCategoryId()));
                item.setMainImage(cursor.getString(cursor.getColumnIndex(MyItemsContract.ItemsMain.COLUMN_MAIN_IMAGE)));

                items.add(item);
            }while (cursor.moveToNext());
        }
        Log.i(TAG,"inside getItemByCategory, after Iterator");
        cursor.close();
        closeDB();

        return items;
    }

    /*
    * 从location表检索全部位置
    * */
    public List<Location> getAllLocations(){
        //Log.i(TAG,"inside getAllLocations,before any calls，now DB open ?"+String.valueOf(mSQLiteDatabase.isOpen()));
        List<Location> locations = new ArrayList<Location>();
        String selectQuery = "SELECT * FROM " + MyItemsContract.Locations.TABLE_NAME;

        getWDbIfClosedOrNull();
        Cursor cursor = mSQLiteDatabase.rawQuery(selectQuery, null);
        //Log.i(TAG,"inside getAllLocations,after rawQuery, before Iterator");

        //循环遍历，将所有row添加至list
        if(cursor.moveToFirst()){
            do{
                Location location = new Location();
                location.setId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.Locations._ID)));
                location.setName(cursor.getString(cursor.getColumnIndex(MyItemsContract.Locations.COLUMN_LOC_NAME)));
                location.setDescription(cursor.getString(cursor.getColumnIndex(MyItemsContract.Locations.COLUMN_LOC_DESCRIPTION)));
                location.setSuperId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.Locations.COLUMN_SUPER_LOCATION_ID)));

                locations.add(location);
            }while (cursor.moveToNext());
        }
        Log.i(TAG,"inside getAllLocations, after the Iterator");
        cursor.close();
        closeDB();

        return locations;
    }

    /*
    * 按SuperId从location表检索位置；分层显示Location时，可以按层检索
    * */
    public List<Location> getLocationsBySuperId(long superId){
        //Log.i(TAG,"inside getLocationsBySuperId,before any calls，now DB open ?"+String.valueOf(mSQLiteDatabase.isOpen()));

        List<Location> locations = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + MyItemsContract.Locations.TABLE_NAME + " WHERE "+
                MyItemsContract.Locations.COLUMN_SUPER_LOCATION_ID + " = " + superId;

        getWDbIfClosedOrNull();
        Cursor cursor = mSQLiteDatabase.rawQuery(selectQuery, null);
        //Log.i(TAG,"inside getLocationsBySuperId,after rawQuery, before Iterator");

        //循环遍历，将所有row添加至list
        if(cursor.moveToFirst()){
            do{
                Location location = new Location();
                location.setId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.Locations._ID)));
                location.setName(cursor.getString(cursor.getColumnIndex(MyItemsContract.Locations.COLUMN_LOC_NAME)));
                location.setDescription(cursor.getString(cursor.getColumnIndex(MyItemsContract.Locations.COLUMN_LOC_DESCRIPTION)));
                location.setSuperId(superId);

                locations.add(location);
            }while (cursor.moveToNext());
        }
        Log.i(TAG,"inside getLocationsBySuperId, after the Iterator");
        cursor.close();
        closeDB();

        return locations;
    }


    /*
    * 从category表检索全部类别
    * */
    public List<Category> getAllCategories(){
        Log.i(TAG,"inside getAllCategories,before any calls，now DB open ?"+String.valueOf(mSQLiteDatabase.isOpen()));
        List<Category> categories = new ArrayList<Category>();
        String selectQuery = "SELECT * FROM" + MyItemsContract.Categories.TABLE_NAME;

        getWDbIfClosedOrNull();
        Cursor cursor = mSQLiteDatabase.rawQuery(selectQuery, null);

        Log.i(TAG,"inside getAllCategories,after rawQuery, before Iterator");
        //循环遍历，将所有row添加至list
        if(cursor.moveToFirst()){
            Category category = new Category();
            do{
                category.setId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.Categories._ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(MyItemsContract.Categories.COLUMN_CAT_NAME)));
                category.setDescription(cursor.getString(cursor.getColumnIndex(MyItemsContract.Categories.COLUMN_CAT_DESCRIPTION)));
                category.setSuperId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.Categories.COLUMN_SUPER_CAT_ID)));

                categories.add(category);
            }while (cursor.moveToNext());
        }
        Log.i(TAG,"inside getAllCategories, after the Iterator");
        cursor.close();
        closeDB();

        return categories;
    }

    /*
    * 从category表按superId检索类别
    * */
    public List<Category> getCategoriesBySuperId(long superId){
        //Log.i(TAG,"inside getCategoriesBySuperId,before any calls，now DB open ?"+String.valueOf(mSQLiteDatabase.isOpen()));
        List<Category> categories = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + MyItemsContract.Categories.TABLE_NAME +
                " WHERE "+MyItemsContract.Categories.COLUMN_SUPER_CAT_ID+" = "+superId;

        getWDbIfClosedOrNull();
        Cursor cursor = mSQLiteDatabase.rawQuery(selectQuery, null);

        //Log.i(TAG,"inside getCategoriesBySuperId,after rawQuery, before Iterator");
        //循环遍历，将所有row添加至list
        if(cursor.moveToFirst()){
            do{
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(MyItemsContract.Categories._ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(MyItemsContract.Categories.COLUMN_CAT_NAME)));
                category.setDescription(cursor.getString(cursor.getColumnIndex(MyItemsContract.Categories.COLUMN_CAT_DESCRIPTION)));
                category.setSuperId(superId);

                categories.add(category);
            }while (cursor.moveToNext());
        }
        Log.i(TAG,"inside getCategoriesBySuperId, after the Iterator");
        cursor.close();
        closeDB();

        return categories;
    }

    /*
    * 从位置ID转换为位置名称（单项转换）
    * */
/*    public String getLocationNameById(long id){
        String selectQuery = "SELECT " + MyItemsContract.Locations.COLUMN_LOC_NAME + " FROM "
                + MyItemsContract.Locations.TABLE_NAME + " WHERE "
                + MyItemsContract.Locations._ID + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor !=null)
            cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(MyItemsContract
                .Locations.COLUMN_LOC_NAME));

    }
*/
    /*
    * 从类别ID转换为类别名称（单项转换）
    * */
/*    public String getCategoryNameById(long id){
        String selectQuery = "SELECT " + MyItemsContract.Categories.COLUMN_CAT_NAME + " FROM "
                + MyItemsContract.Categories.TABLE_NAME + " WHERE "
                + MyItemsContract.Categories._ID + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor !=null)
            cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(MyItemsContract
                .Categories.COLUMN_CAT_NAME));

    }


*/
    //以下是各update方法
    /*
    *更新一条Item记录的所有属性，对应单条item的详细修改业务。
    * */
/*    public int updateItem(Item item){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyItemsContract.ItemsMain.COLUMN_ITEM_NAME, item.getName());
        values.put(MyItemsContract.ItemsMain.COLUMN_ITEM_Description, item.getDescription());
        values.put(MyItemsContract.ItemsMain.COLUMN_LOCATIONID, item.getLocationId());
        values.put(MyItemsContract.ItemsMain.COLUMN_CATEGORYID, item.getCategoryId());
        values.put(MyItemsContract.ItemsMain.COLUMN_DROPPED, item.getDropStatus());
        values.put(MyItemsContract.ItemsMain.COLUMN_MAIN_IMAGE, item.getMainImage());

        return  db.update(MyItemsContract.ItemsMain.TABLE_NAME, values,
                MyItemsContract.ItemsMain._ID + " = ?", new String[]{String.valueOf(item.getId())});
    }
*/
    /*
    *更新多条Item记录的部分属性，对应多条item多选后的修改业务。
    * 批量模式下只能修改位置、类别、丢弃状态三项属性。
    * 注意需要确保调用时传递的List<item>至少含有一个item元素。
    * *
    public int updateItemsLCD(List<Item> items){
        SQLiteDatabase db = this.getWritableDatabase();

        //将items中各item的id提取出来，转换成一个String数组。
        List<String> strIds = new ArrayList<String>();
        for(Item item : items){
            strIds.add(String.valueOf(item.getId()));
        }
        String[] ids = strIds.toArray(new String[0]);

        //批量修改（多选）所有item按相同L/C/D修改，所以直接按取第一个item属性值即可；
        ContentValues values = new ContentValues();
        Item item0 = items.get(0);//确保以下三句引用的item是同一个。
        values.put(MyItemsContract.ItemsMain.COLUMN_LOCATIONID, item0.getLocationId());
        values.put(MyItemsContract.ItemsMain.COLUMN_CATEGORYID, item0.getCategoryId());
        values.put(MyItemsContract.ItemsMain.COLUMN_DROPPED, item0.getDropStatus());

        return  db.update(MyItemsContract.ItemsMain.TABLE_NAME, values,
                MyItemsContract.ItemsMain._ID + " = ?", ids);
    }*/

    /*
    * 修改一条位置记录的所有可修改属性（名称、描述、所属父节点）
    * */
/*    public int updateLocation(Location location){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyItemsContract.Locations.COLUMN_LOC_NAME, location.getName());
        values.put(MyItemsContract.Locations.COLUMN_LOC_DESCRIPTION, location.getDescription());
        values.put(MyItemsContract.Locations.COLUMN_SUPER_LOCATION_ID, location.getSuperId());

        return db.update(MyItemsContract.Locations.TABLE_NAME, values,
                MyItemsContract.Locations._ID +" = ?", new String[]{String.valueOf(location.getId())});
    }
*/
    /*
    * 批量修改多条位置记录的所属父节点
    * */
    /*
    public int updateLocationsSid(List<Location> locations){
        SQLiteDatabase db = this.getWritableDatabase();

        //将各Location的id提取出来，转换成一个String数组。
        List<String> strIds = new ArrayList<String>();
        for(Location location : locations){
            strIds.add(String.valueOf(location.getId()));
        }
        String[] ids = strIds.toArray(new String[0]);

        //批量修改Locations的归属（父节点），必然有所有Location的superId相同
        //因而直接按第一个取值
        ContentValues values = new ContentValues();
        Location location0 = locations.get(0);//确保以下三句引用的item是同一个。
        values.put(MyItemsContract.Locations.COLUMN_SUPER_LOCATION_ID, location0.getSuperId());

        return  db.update(MyItemsContract.Locations.TABLE_NAME, values,
                MyItemsContract.Locations._ID + " = ?", ids);

    }*/

    /*
    * 修改一条类别记录的所有可修改属性（名称、描述、所属父节点）
    * */
    /*
    public int updateCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MyItemsContract.Categories.COLUMN_CAT_NAME, category.getName());
        values.put(MyItemsContract.Categories.COLUMN_CAT_DESCRIPTION, category.getDescription());
        values.put(MyItemsContract.Categories.COLUMN_SUPER_CAT_ID, category.getSuperId());

        return db.update(MyItemsContract.Categories.TABLE_NAME, values,
                MyItemsContract.Categories._ID +" = ?", new String[]{String.valueOf(category.getId())});
    }
    */
    /*
    * 批量修改多条位置记录的所属父节点
    * */
    /*DEBUGGING
    public int updateCategoriesSid(List<Category> Categories){
        SQLiteDatabase db = this.getWritableDatabase();

        //将各Category的id提取出来，转换成一个String数组。
        List<String> strIds = new ArrayList<String>();
        for(Category category : Categories){
            strIds.add(String.valueOf(category.getId()));
        }
        String[] ids = strIds.toArray(new String[0]);

        //批量修改Categories的归属（父节点），必然有所有Category的superId相同
        //因而直接按第一个取值
        ContentValues values = new ContentValues();
        Category category0 = Categories.get(0);//确保以下三句引用的item是同一个。
        values.put(MyItemsContract.Categories.COLUMN_SUPER_CAT_ID, category0.getSuperId());

        return  db.update(MyItemsContract.Categories.TABLE_NAME, values,
                MyItemsContract.Categories._ID + " = ?", ids);

    }DEBUGGING*/

    /*
    * 删除操作，批量删/单独删采用同一方法，区别在于所传入List<>的元素数量。
    * */
    /*DEBUGGING
    public void deleteItems(List<Item> items){
        SQLiteDatabase db = this.getWritableDatabase();

        //将items中各item的id提取出来，转换成一个String数组。
        List<String> strIds = new ArrayList<String>();
        for(Item item : items){
            strIds.add(String.valueOf(item.getId()));
        }
        String[] ids = strIds.toArray(new String[0]);

        db.delete(MyItemsContract.ItemsMain.TABLE_NAME, MyItemsContract.ItemsMain._ID +
                " = ?", ids);
        }

    public void deleteLocations(List<Location> locations){
        SQLiteDatabase db = this.getWritableDatabase();

        //将locations中各location的id提取出来，转换成一个String数组。
        List<String> strIds = new ArrayList<String>();
        for(Location location : locations){
            strIds.add(String.valueOf(location.getId()));
        }
        String[] ids = strIds.toArray(new String[0]);

        db.delete(MyItemsContract.Locations.TABLE_NAME, MyItemsContract.Locations._ID +
                " = ?", ids);
    }

    public void deleteCategories(List<Category> categories){
        SQLiteDatabase db = this.getWritableDatabase();

        //将categories中各category的id提取出来，转换成一个String数组。
        List<String> strIds = new ArrayList<String>();
        for(Category category : categories){
            strIds.add(String.valueOf(category.getId()));
        }
        String[] ids = strIds.toArray(new String[0]);

        db.delete(MyItemsContract.Categories.TABLE_NAME, MyItemsContract.Categories._ID +
                " = ?", ids);
    }
    DEBUGGING*/

    public void initialDbData(){
        //Log.i(TAG,"inside initialDbData, before any calls.");
        //创建Item表的4项纪录
        //Log.i(TAG,"inside initialDbData, ready to create phone01: "+ItemRepository.phone01.toString());
        dbCreateItem(ItemRepository.phone01);
        dbCreateItem(ItemRepository.phone02);
        dbCreateItem(ItemRepository.phone04);
        dbCreateItem(ItemRepository.phone05);
        Log.i(TAG,"inside initialDbData, all items inserted.");

        //创建Location表的三项默认记录（此时superid均设为0，不存在）
        dbCreateLocation(LocationRepository.Location01);
        dbCreateLocation(LocationRepository.Location02);
        dbCreateLocation(LocationRepository.Location03);
        //Log.i(TAG,"inside initialDbData, all Locations inserted.");

        //创建Category表的三项默认记录（此时superid均设为0，不存在）
        dbCreateCategory(CategoryRepository.Category01);
        dbCreateCategory(CategoryRepository.Category02);
        dbCreateCategory(CategoryRepository.Category03);
        //Log.i(TAG,"inside initialDbData, all Categories inserted.");

    }


    public void wayMuchCategoriesInsert(){
        for(int i=0; i<10; i++) {
            dbCreateCategory(new Category(0, String.valueOf(i), "Just Default.", 2));
        }
    }

    void getWDbIfClosedOrNull(){
        if(mSQLiteDatabase==null || !mSQLiteDatabase.isOpen()) {
            mSQLiteDatabase = this.getWritableDatabase();
            //Log.i(TAG,"inside GetWDbIfClosedOrNull(),so Got the W-DB");
        }
    }

    //关数据库
    public void closeDB(){
        //Log.i(TAG,"inside closeDB, before any calls.");
        if(mSQLiteDatabase != null && mSQLiteDatabase.isOpen()){
                try{
                    mSQLiteDatabase.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            } // end if
    }

}
