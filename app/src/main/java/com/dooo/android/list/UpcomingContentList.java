package com.dooo.android.list;

public class UpcomingContentList {
    private int id;
    private String name;
    private String description;
    private String poster;
    private String trailer_url;
    private String release_date;
    private int type;

    public UpcomingContentList(int id, String name, String description, String poster, String trailer_url, String release_date, int type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.poster = poster;
        this.trailer_url = trailer_url;
        this.release_date = release_date;
        this.type = type;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTrailer_url() {
        return trailer_url;
    }

    public void setTrailer_url(String trailer_url) {
        this.trailer_url = trailer_url;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
