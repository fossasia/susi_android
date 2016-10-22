package org.fossasia.susi.ai.model;

import org.fossasia.susi.ai.rest.model.Datum;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessage extends RealmObject {
    private boolean isImage, isMine, isMap, isPieChart, isDelivered, isHavingLink;
    private long id;
    private String content, timeStamp;
    private RealmList<Datum> datumRealmList;

    public ChatMessage() {
        datumRealmList = new RealmList<>();
    }

    public ChatMessage(long id, String content, boolean isMine, boolean isImage, boolean isMap, boolean isHavingLink, boolean isPieChart, String timeStamp, RealmList<Datum> datumRealmList) {
        this.id = id;
        this.isImage = isImage;
        this.isMine = isMine;
        this.content = content;
        this.timeStamp = timeStamp;
        this.isMap = isMap;
        this.isHavingLink = isHavingLink;
        this.datumRealmList = datumRealmList;
        this.isPieChart = isPieChart;
    }

    public boolean isMap() {
        return isMap;
    }

    public void setMap(boolean map) {
        isMap = map;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public boolean isHavingLink() {return isHavingLink;}

    public void setHavingLink(boolean havingLink) {isHavingLink = havingLink;}

    public void setDatumRealmList(RealmList<Datum> datumRealmList){ this.datumRealmList = datumRealmList; }

    public RealmList<Datum> getDatumRealmList() { return datumRealmList; }

    public void setIsPieChart(boolean isPieChart){ this.isPieChart = isPieChart; }

    public boolean isPieChart() { return isPieChart; }

    public boolean getIsDelivered() { return isDelivered; }

    public void setIsDelivered(boolean isDelivered) { this.isDelivered = isDelivered; }

}
