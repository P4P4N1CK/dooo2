package com.dooo.android.list;

public class MultiqualityList {
    String quality;
    String extension;
    String url;

    public MultiqualityList(String quality, String extension, String url) {
        this.quality = quality;
        this.extension = extension;
        this.url = url;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
