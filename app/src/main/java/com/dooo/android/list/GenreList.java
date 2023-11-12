package com.dooo.android.list;

public class GenreList {
    private int id;
    private String name;
    private String icon;
    private String description;
    private int featured;
    private int status;

    public GenreList(int id, String name, String icon, String description, int featured, int status) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.featured = featured;
        this.status = status;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFeatured() {
        return featured;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
