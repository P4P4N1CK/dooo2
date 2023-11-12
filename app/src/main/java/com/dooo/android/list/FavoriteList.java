package com.dooo.android.list;

public class FavoriteList {
    private int ID;
    private int Type;
    private String Name;
    private String Release_Date;
    private String poster;
    private  String content_type;

    public FavoriteList(int ID, int type, String name, String release_Date, String poster, String content_type) {
        this.ID = ID;
        Type = type;
        Name = name;
        Release_Date = release_Date;
        this.poster = poster;
        this.content_type = content_type;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRelease_Date() {
        return Release_Date;
    }

    public void setRelease_Date(String release_Date) {
        Release_Date = release_Date;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }
}
