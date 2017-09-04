package cn.vkyoung.myitems.sqlite;

/**
 * Created by VkYoung16 on 2017/8/30.
 */

public class LocationRepository {
    private static final String[] LocationNames ={"默认1","默认2","默认3"};
    private static final String[] descriptions ={"在铁货架上","铁货架下层的大纸箱","铁货架从上数2层的袋子"};


    public static final Location Location01 = new Location(0,LocationNames[0],descriptions[0],Location.DONOT_HAVE_SUPER_LOCATION);
    public static final Location Location02 = new Location(0,LocationNames[1],descriptions[1],Location.DONOT_HAVE_SUPER_LOCATION);
    public static final Location Location03 = new Location(0,LocationNames[2],descriptions[2],Location.DONOT_HAVE_SUPER_LOCATION);

}
