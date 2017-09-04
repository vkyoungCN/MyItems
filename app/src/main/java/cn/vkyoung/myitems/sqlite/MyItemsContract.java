package cn.vkyoung.myitems.sqlite;

import android.provider.BaseColumns;

/**
 * Created by VkYoung16 on 2017/8/12.
 */


public final class MyItemsContract {
    private static MyItemsContract instance;


    static {
        instance = new MyItemsContract();
    }

    //为防止意外实例化此类，需令构造器为private
    private MyItemsContract(){}

    public static MyItemsContract getInstance(){
        return instance;
    }


    /* 内部类用于定义表的内容；本DB共有四张表*/
    public static class ItemsMain implements BaseColumns  {
        public static final String TABLE_NAME = "items_main";
        //public static final String COLUMN_ITEM_ID = "item_id";
        public static final String COLUMN_ITEM_NAME = "itemName";
        public static final String COLUMN_ITEM_Description = "itemDescription";
        public static final String COLUMN_DROPPED = "dropped";
        public static final String COLUMN_LOCATIONID = "locationID";
        public static final String COLUMN_CATEGORYID = "categoryID";
        public static final String COLUMN_MAIN_IMAGE = "mainImage";
    }

    public static class ItemsImagesExtra implements BaseColumns {
        public static final String TABLE_NAME = "items_image_extra";
        public static final String COLUMN_ITEM_ID = "item_ID";
        public static final String COLUMN_IMAGE_PATH = "imageString";
    }

    public static class Locations implements BaseColumns {
        public static final String TABLE_NAME = "locations";
        //public static final String COLUMN_LOC_ID = "locationID";
        public static final String COLUMN_LOC_NAME = "locationName";
        public static final String COLUMN_LOC_DESCRIPTION = "locationDescription";
        public static final String COLUMN_SUPER_LOCATION_ID = "superLocationID";
    }

    public static class Categories implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        //public static final String COLUMN_CAT_ID = "categoryID";
        public static final String COLUMN_CAT_NAME = "categoryName";
        public static final String COLUMN_CAT_DESCRIPTION = "categoryDescription";
        public static final String COLUMN_SUPER_CAT_ID = "superCategoryID";
    }




}
