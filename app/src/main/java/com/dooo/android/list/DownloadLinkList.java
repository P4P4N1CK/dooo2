package com.dooo.android.list;

public class DownloadLinkList {
    int id;
    String name;
    String size;
    String quality;
    int link_order;
    int movie_id;
    String url;
    String type;
    String download_type;

    public DownloadLinkList(int id, String name, String size, String quality, int link_order, int movie_id, String url, String type, String download_type) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.quality = quality;
        this.link_order = link_order;
        this.movie_id = movie_id;
        this.url = url;
        this.type = type;
        this.download_type = download_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public int getLink_order() {
        return link_order;
    }

    public void setLink_order(int link_order) {
        this.link_order = link_order;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDownload_type() {
        return download_type;
    }

    public void setDownload_type(String download_type) {
        this.download_type = download_type;
    }
}
