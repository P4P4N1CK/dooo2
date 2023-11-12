package com.dooo.android.list;

public class EpisodeList {
    private int id;
    private String Episoade_Name;
    private String episoade_image;
    private String episoade_description;
    private int season_id;
    private int downloadable;
    private int type;
    private String source;
    private String url;
    public int skip_available;
    public String intro_start;
    public String intro_end;
    boolean Play_Premium;
    boolean downloadPremium;
    String DrmUuid;
    String DrmLicenseUri;

    public EpisodeList(int id, String episoade_Name, String episoade_image, String episoade_description, int season_id, int downloadable, int type, String source, String url, int skip_available, String intro_start, String intro_end, boolean play_Premium, boolean downloadPremium, String drmUuid, String drmLicenseUri) {
        this.id = id;
        Episoade_Name = episoade_Name;
        this.episoade_image = episoade_image;
        this.episoade_description = episoade_description;
        this.season_id = season_id;
        this.downloadable = downloadable;
        this.type = type;
        this.source = source;
        this.url = url;
        this.skip_available = skip_available;
        this.intro_start = intro_start;
        this.intro_end = intro_end;
        Play_Premium = play_Premium;
        this.downloadPremium = downloadPremium;
        DrmUuid = drmUuid;
        DrmLicenseUri = drmLicenseUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEpisoade_Name() {
        return Episoade_Name;
    }

    public void setEpisoade_Name(String episoade_Name) {
        Episoade_Name = episoade_Name;
    }

    public String getEpisoade_image() {
        return episoade_image;
    }

    public void setEpisoade_image(String episoade_image) {
        this.episoade_image = episoade_image;
    }

    public String getEpisoade_description() {
        return episoade_description;
    }

    public void setEpisoade_description(String episoade_description) {
        this.episoade_description = episoade_description;
    }

    public int getSeason_id() {
        return season_id;
    }

    public void setSeason_id(int season_id) {
        this.season_id = season_id;
    }

    public int getDownloadable() {
        return downloadable;
    }

    public void setDownloadable(int downloadable) {
        this.downloadable = downloadable;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public boolean isPlay_Premium() {
        return Play_Premium;
    }

    public void setPlay_Premium(boolean play_Premium) {
        Play_Premium = play_Premium;
    }

    public boolean isDownloadPremium() {
        return downloadPremium;
    }

    public void setDownloadPremium(boolean downloadPremium) {
        this.downloadPremium = downloadPremium;
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
