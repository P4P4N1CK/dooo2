package com.dooo.android.list;


public class LiveTvAllList {
    int ID;
    String Name;
    String Banner;
    String stream_type;
    String url;
    int content_type;
    int type;
    boolean Play_Premium;
    String DrmUuid;
    String DrmLicenseUri;

    public LiveTvAllList(int ID, String name, String banner, String stream_type, String url, int content_type, int type, boolean play_Premium, String drmUuid, String drmLicenseUri) {
        this.ID = ID;
        Name = name;
        Banner = banner;
        this.stream_type = stream_type;
        this.url = url;
        this.content_type = content_type;
        this.type = type;
        Play_Premium = play_Premium;
        DrmUuid = drmUuid;
        DrmLicenseUri = drmLicenseUri;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBanner() {
        return Banner;
    }

    public void setBanner(String banner) {
        Banner = banner;
    }

    public String getStream_type() {
        return stream_type;
    }

    public void setStream_type(String stream_type) {
        this.stream_type = stream_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getContent_type() {
        return content_type;
    }

    public void setContent_type(int content_type) {
        this.content_type = content_type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isPlay_Premium() {
        return Play_Premium;
    }

    public void setPlay_Premium(boolean play_Premium) {
        Play_Premium = play_Premium;
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
