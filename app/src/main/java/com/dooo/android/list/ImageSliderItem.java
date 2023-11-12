package com.dooo.android.list;

public class ImageSliderItem {
    private String image;
    private String Title;
    private int Content_Type;
    private int Content_ID;
    private String URL;

    public ImageSliderItem(String image, String title, int content_Type, int content_ID, String URL) {
        this.image = image;
        Title = title;
        Content_Type = content_Type;
        Content_ID = content_ID;
        this.URL = URL;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getContent_Type() {
        return Content_Type;
    }

    public void setContent_Type(int content_Type) {
        Content_Type = content_Type;
    }

    public int getContent_ID() {
        return Content_ID;
    }

    public void setContent_ID(int content_ID) {
        Content_ID = content_ID;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
