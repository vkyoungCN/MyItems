package cn.vkyoung.myitems.sqlite;

/**
 * Created by VkYoung16 on 2017/8/13.
 */

public class Category implements FourTermsModel {
    long id;
    String name;
    String description;
    long superId = DONOT_HAVE_SUPER_LOCATION;

    /*
    * constructors
    */
    public Category(){
    }

    public Category(long id, String name, String description, long superId) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.superId = superId;
    }

    //setters
    public void setId(long id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setSuperId(long superId){
        this.superId = superId;
    }

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
    public long getSuperId(){
        return this.superId;
    }

}
