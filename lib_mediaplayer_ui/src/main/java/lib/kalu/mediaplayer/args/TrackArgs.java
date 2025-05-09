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

    private int groupCount;
    private int trackCount;
    private int groupIndex = -1;
    private int trackIndex = -1;
    private int trackType = -1;
    private boolean isGroupAdaptiveSupported;
    private boolean isGroupSupported;
    private boolean isGroupSelected;
    private boolean isTrackSupported;
    private boolean isTrackSelected;


    private int bitrate;
    private int width;
    private int height;
    private String sampleMimeType;

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

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public int getTrackIndex() {
        return trackIndex;
    }

    public void setTrackIndex(int trackIndex) {
        this.trackIndex = trackIndex;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public int getTrackType() {
        return trackType;
    }

    public void setTrackType(int trackType) {
        this.trackType = trackType;
    }

    public boolean isGroupAdaptiveSupported() {
        return isGroupAdaptiveSupported;
    }

    public void setGroupAdaptiveSupported(boolean groupAdaptiveSupported) {
        isGroupAdaptiveSupported = groupAdaptiveSupported;
    }

    public boolean isGroupSupported() {
        return isGroupSupported;
    }

    public void setGroupSupported(boolean groupSupported) {
        isGroupSupported = groupSupported;
    }

    public boolean isGroupSelected() {
        return isGroupSelected;
    }

    public void setGroupSelected(boolean groupSelected) {
        isGroupSelected = groupSelected;
    }

    public boolean isTrackSupported() {
        return isTrackSupported;
    }

    public void setTrackSupported(boolean trackSupported) {
        isTrackSupported = trackSupported;
    }

    public boolean isTrackSelected() {
        return isTrackSelected;
    }

    public void setTrackSelected(boolean trackSelected) {
        isTrackSelected = trackSelected;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSampleMimeType() {
        return sampleMimeType;
    }

    public void setSampleMimeType(String sampleMimeType) {
        this.sampleMimeType = sampleMimeType;
    }
}
