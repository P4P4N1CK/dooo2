package com.dooo.android.list;

public class SearchList {
    private int ID;
    private int Type;
    private String Title;
    private String Year;
    private String Thumbnail;
    private int Content_Type;

    public SearchList(int ID, int type, String title, String year, String thumbnail, int content_Type) {
        this.ID = ID;
        Type = type;
        Title = title;
        Year = year;
        Thumbnail = thumbnail;
        Content_Type = content_Type;
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

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public int getContent_Type() {
        return Content_Type;
    }

    public void setContent_Type(int content_Type) {
        Content_Type = content_Type;
    }
}
