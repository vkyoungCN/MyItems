package cn.vkyoung.myitems.sqlite;

/**
 * Created by VkYoung16 on 2017/9/2.
 */

public interface FourTermsModel {
    public static final long DONOT_HAVE_SUPER_LOCATION = 0;

    long id = 0;
    String name = "";
    String description = "";
    long superId = DONOT_HAVE_SUPER_LOCATION;

    //setters
    public void setId(long id);
    public void setName(String name);
    public void setDescription(String description);
    public void setSuperId(long superId);

    //getters
    public long getId();
    public String getName();
    public String getDescription();
    public long getSuperId();
}
