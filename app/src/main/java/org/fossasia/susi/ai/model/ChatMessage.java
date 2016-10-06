package org.fossasia.susi.ai.model;

import io.realm.RealmObject;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessage extends RealmObject {
    private boolean isImage, isMine;
    private String content, timeStamp;

    public ChatMessage() {

    }

    public ChatMessage(String content, boolean isMine, boolean isImage, String timeStamp) {
        this.isImage = isImage;
        this.isMine = isMine;
        this.content = content;
        this.timeStamp = timeStamp;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }
}
