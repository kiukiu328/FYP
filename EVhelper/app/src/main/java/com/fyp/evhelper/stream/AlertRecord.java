package com.fyp.evhelper.stream;

public class AlertRecord {
    private String date;
    private String iconPath;
    private String videoPath;
    private boolean videoState;
    public AlertRecord(String date,String iconPath,String videoPath,boolean videoState){
        this.iconPath=iconPath;
        this.date=date;
        this.videoPath=videoPath;
        this.videoState=videoState;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public boolean getVideoState(){
        return videoState;
    }

    public void setVideoState(boolean videoState){
        this.videoState=videoState;
    }
}
