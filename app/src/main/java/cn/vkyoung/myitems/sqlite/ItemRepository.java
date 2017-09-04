package cn.vkyoung.myitems.sqlite;

/**
 * Created by VkYoung16 on 2017/8/21.
 */

public class ItemRepository {

    private static final String[] ItemNames ={"手机（Vivo）","手机（ZTE大）","手机（iPhone)"};
    private static final String[] descriptions ={"X9,白色、5寸,主用","z7max，前黑后白、大","iPhone 6,暂不使用、长期放置","iPhone 6水货"};

    private static final String mainImagePathEmpty ="";


    public static final Item phone01 = new Item(0,ItemNames[0],descriptions[0],Item.ITEM_DROPPED_NO,0,0,mainImagePathEmpty );
    public static final Item phone02 = new Item(0,ItemNames[1],descriptions[1],Item.ITEM_DROPPED_YES,0,0,mainImagePathEmpty );
    public static final Item phone04 = new Item(0,ItemNames[2],descriptions[2],Item.ITEM_DROPPED_NO,0,0,mainImagePathEmpty );
    public static final Item phone05 = new Item(0,ItemNames[2],descriptions[3],Item.ITEM_TO_DROP,0,0,mainImagePathEmpty );

}
