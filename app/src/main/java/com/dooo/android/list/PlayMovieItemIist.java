package com.dooo.android.list;

public class PlayMovieItemIist {
    private int id;
    private String name;
    private String size;
    private String quality;
    private int movie_id;
    private String url;
    private String type;
    public int skip_available;
    public String intro_start;
    public String intro_end;
    public int link_type;
    String DrmUuid;
    String DrmLicenseUri;

    public PlayMovieItemIist(int id, String name, String size, String quality, int movie_id, String url, String type, int skip_available, String intro_start, String intro_end, int link_type, String drmUuid, String drmLicenseUri) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.quality = quality;
        this.movie_id = movie_id;
        this.url = url;
        this.type = type;
        this.skip_available = skip_available;
        this.intro_start = intro_start;
        this.intro_end = intro_end;
        this.link_type = link_type;
        DrmUuid = drmUuid;
        DrmLicenseUri = drmLicenseUri;
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

    public int getSkip_available() {
        return skip_available;
    }

    public void setSkip_available(int skip_available) {
        this.skip_available = skip_available;
    }

    public String getIntro_start() {
        return intro_start;
    }

    public void setIntro_start(String intro_start) {
        this.intro_start = intro_start;
    }

    public String getIntro_end() {
        return intro_end;
    }

    public void setIntro_end(String intro_end) {
        this.intro_end = intro_end;
    }

    public int getLink_type() {
        return link_type;
    }

    public void setLink_type(int link_type) {
        this.link_type = link_type;
    }

    public String getDrmUuid() {
        return DrmUuid;
    }

    public void setDrmUuid(String drmUuid) {
        DrmUuid = drmUuid;
    }

    public String getDrmLicenseUri() {
        return DrmLicenseUri;
    }

    public void setDrmLicenseUri(String drmLicenseUri) {
        DrmLicenseUri = drmLicenseUri;
    }
}
