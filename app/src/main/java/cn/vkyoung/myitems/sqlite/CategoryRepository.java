package cn.vkyoung.myitems.sqlite;

/**
 * Created by VkYoung16 on 2017/8/30.
 */

public class CategoryRepository {
    private static final String[] CategoryNames ={"手机","废弃","手机（废弃）"};
    private static final String[] descriptions ={"无描述","废弃","废弃手机"};


    public static final Category Category01 = new Category(0,CategoryNames[0],descriptions[0],Location.DONOT_HAVE_SUPER_LOCATION);
    public static final Category Category02 = new Category(0,CategoryNames[1],descriptions[1],Location.DONOT_HAVE_SUPER_LOCATION);
    public static final Category Category03 = new Category(0,CategoryNames[2],descriptions[2],Location.DONOT_HAVE_SUPER_LOCATION);
}
