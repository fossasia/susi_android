package org.fossasia.susi.ai.model;

import org.fossasia.susi.ai.rest.responses.susi.Datum;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessage extends RealmObject {

    @PrimaryKey
    private long id;
    private String content, timeStamp, date, webquery, actionType;
    private RealmList<Datum> datumRealmList;
    private WebLink webLinkData;
    private RealmList<WebSearchModel> webSearchList;
    private boolean isDelivered, isHavingLink, isDate, isImportant, isMine;
    private double latitude, longitude, zoom;

    public ChatMessage() {
        datumRealmList = new RealmList<>();
    }

    public ChatMessage(long id, String content, String date, boolean isDate, boolean isMine, boolean isHavingLink, String timeStamp, RealmList<Datum> datumRealmList, String webquery) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.isDate = isDate;
        this.timeStamp = timeStamp;
        this.isHavingLink = isHavingLink;
        this.datumRealmList = datumRealmList;
        this.webquery = webquery;
        this.isMine = isMine;
        this.webLinkData = null;
        this.webSearchList = null;
    }

    public RealmList<WebSearchModel> getWebSearchList() {
        return webSearchList;
    }

    public void setWebSearchList(RealmList<WebSearchModel> webSearchList) {
        this.webSearchList = webSearchList;
    }

    public WebLink getWebLinkData() {
        return webLinkData;
    }

    public void setWebLinkData(WebLink webLinkData) {
        this.webLinkData = webLinkData;
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

    public boolean isDate() {
        return isDate;
    }

    public void setIsDate(boolean date) {
        isDate = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public boolean isHavingLink() {return isHavingLink;}

    public void setHavingLink(boolean havingLink) {isHavingLink = havingLink;}

    public RealmList<Datum> getDatumRealmList() { return datumRealmList; }

    public void setDatumRealmList(RealmList<Datum> datumRealmList) {
        this.datumRealmList = datumRealmList;
    }

    public boolean getIsDelivered() { return isDelivered; }

    public void setIsDelivered(boolean isDelivered) { this.isDelivered = isDelivered; }

    public void setIsImportant(boolean isImportant)
    {
        this.isImportant = isImportant;
    }

    public boolean isImportant()
    {
        return isImportant;
    }

    public String getWebquery() {
        return webquery;
    }

    public void setWebquery(String webquery) {
        this.webquery = webquery;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longtitude) {
        this.longitude = longtitude;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }
}
