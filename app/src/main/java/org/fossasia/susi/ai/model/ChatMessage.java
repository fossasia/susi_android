package org.fossasia.susi.ai.model;

import io.realm.RealmObject;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessage extends RealmObject {
    private long id;
    private boolean isImage, isMine;
    private String content;

    public ChatMessage() {

    }

    public ChatMessage(long id, String message, boolean mine, boolean image) {
        this.id = id;
        content = message;
        isMine = mine;
        isImage = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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
