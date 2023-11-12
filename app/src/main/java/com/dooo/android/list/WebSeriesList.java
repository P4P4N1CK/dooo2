package com.dooo.android.list;

public class WebSeriesList {
    private int ID;
    private int Type;
    private String Title;
    private String Year;
    private String Thumbnail;

    public WebSeriesList(int ID, int type, String title, String year, String thumbnail) {
        this.ID = ID;
        Type = type;
        Title = title;
        Year = year;
        Thumbnail = thumbnail;
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
}
