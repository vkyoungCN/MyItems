package cn.vkyoung.myitems.sqlite;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by VkYoung16 on 2017/8/13.
 * Model类，大致反映items_main表的一行，但并不完全对应；
 * 某些需要从其他表查询后合并到本类中
 * locationName, categoryName,和extraImages是其他表的内容，需要调用另外的查询。
 */

public class Item implements Parcelable {
    public static final int ITEM_DROPPED_YES = 1;
    public static final int ITEM_DROPPED_NO = 0;
    public static final int ITEM_TO_DROP = 2;

    private int id = 0;
    private String name;
    private String description;
    private long locationId;
    private long categoryId;
    private int dropStatus = ITEM_DROPPED_NO;
    private String mainImage;

    //private String locationName;//locationName字段需另行检索转换，不是表内字段
    //private String categoryName;//categoryName字段需另行检索转换，不是表内字段

    /*
    * 几个构造器
    */
    //空构造器
    public Item(){
    }
    //传入id的构造器可以供系统从DB取回数据时使用
    public Item(int id){
        this.id = id;
    }
    //全值段构造器
    public Item(int id, String name, String description, int dropStatus,
                int locationId, int categoryId, String mainImage){
        this.id = id;
        this.name = name;
        this.description = description;
        this.dropStatus = dropStatus;
        this.locationId = locationId;
        this.categoryId = categoryId;
        this.mainImage = mainImage;
    }

    /*
    * Parcelable接口所要求覆写的一些内容
    * */
    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeInt(id);
        out.writeString(name);
        out.writeString(description);
        out.writeLong(locationId);
        out.writeLong(categoryId);
        out.writeInt(dropStatus);
        out.writeString(mainImage);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>(){
        public Item createFromParcel(Parcel in){
            return new Item(in);
        }

        public Item[] newArray(int size){
            return new Item[size];
        }
    };

    private Item(Parcel in){
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        locationId = in.readLong();
        categoryId = in.readLong();
        dropStatus = in.readInt();
        mainImage = in.readString();
    }


    // setters
    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setDropStatus(int dropStatus){
        this.dropStatus = dropStatus;
    }

    public void setLocationId(long locationId){
        this.locationId = locationId;
    }

    public void setCategoryId(long categoryId){
        this.categoryId = categoryId;
    }

    public void setMainImage(String mainImage){
        this.mainImage = mainImage;
    }

    /*public void setLocationName(String locationName){
        this.locationName = locationName;
    }*/

    /*public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }*/

    /* 暂时保留，待转移到新类中
    public void setExtraImages(String[] extraImages){
        for(int i=0; i<extraImages.length;i++){
            this.extraImages[i] = extraImages[i];
        }
    }
    */

    //getters
    public long getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public int getDropStatus(){
        return this.dropStatus;
    }

    public long getLocationId(){
        return this.locationId;
    }

    public long getCategoryId(){
        return this.categoryId;
    }

    public String getMainImage(){
        return this.mainImage;
    }

    /*public String getLocationName(){
        return this.locationName;
    }*/

    /*public String getCategoryName(){
        return this.categoryName;
    }*/

}
