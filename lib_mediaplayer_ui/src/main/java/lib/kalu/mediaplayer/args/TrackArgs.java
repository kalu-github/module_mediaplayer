package lib.kalu.mediaplayer.args;

import java.io.Serializable;

import lib.kalu.mediaplayer.type.PlayerType;

public final class TrackArgs implements Serializable {

    @PlayerType.TrackType.Value
    private String mimeType;
    private String language;
    private String label;
    private String url;


    private String id;
    private int roleFlags = -1;
    private int selectionFlags = -1;

    @PlayerType.TrackType.Value
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(@PlayerType.TrackType.Value String mimeType) {
        this.mimeType = mimeType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRoleFlags() {
        return roleFlags;
    }

    public void setRoleFlags(int roleFlags) {
        this.roleFlags = roleFlags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSelectionFlags() {
        return selectionFlags;
    }

    public void setSelectionFlags(int selectionFlags) {
        this.selectionFlags = selectionFlags;
    }
}
