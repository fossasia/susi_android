package org.fossasia.susi.ai.data.model;

import org.fossasia.susi.ai.rest.responses.susi.Datum;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * <h1>Model Class extending realm object to store messaged in local database.</h1>
 *
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessage extends RealmObject {

    @PrimaryKey
    private long id;
    private String content, timeStamp, date, webquery, actionType;
    private RealmList<Datum> datumRealmList;
    private WebLink webLinkData;
    private RealmList<WebSearchModel> webSearchList;
    private boolean isDelivered, isHavingLink, isDate, isMine;
    private double latitude, longitude, zoom;
    private int count;

    /**
     * Instantiates a new Chat message.
     */
    public ChatMessage() {
        datumRealmList = new RealmList<>();
    }

    /**
     * Instantiates a new Chat message.
     *
     * @param id             the id of message
     * @param content        the content
     * @param date           the date
     * @param isDate         the is date
     * @param isMine         the is mine
     * @param isHavingLink   the is having link
     * @param timeStamp      the time stamp
     * @param datumRealmList the datum realm list for rss and piechart actiontype
     * @param webquery       the webquery of websearch action type
     * @param count          the count of rss results
     */
    public ChatMessage(long id, String content, String date, boolean isDate, boolean isMine, boolean isHavingLink, String timeStamp, RealmList<Datum> datumRealmList, String webquery, int count) {
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
        this.count = count;
    }

    /**
     * Gets web search list.
     *
     * @return the web search list
     */
    public RealmList<WebSearchModel> getWebSearchList() {
        return webSearchList;
    }

    /**
     * Sets web search list.
     *
     * @param webSearchList the web search list
     */
    public void setWebSearchList(RealmList<WebSearchModel> webSearchList) {
        this.webSearchList = webSearchList;
    }

    /**
     * Gets web link data.
     *
     * @return the web link data
     */
    public WebLink getWebLinkData() {
        return webLinkData;
    }

    /**
     * Sets web link data.
     *
     * @param webLinkData the web link data
     */
    public void setWebLinkData(WebLink webLinkData) {
        try {
            this.webLinkData = webLinkData;
        } catch(IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets time stamp.
     *
     * @return the time stamp
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets time stamp.
     *
     * @param timeStamp the time stamp
     */
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Is date boolean.
     *
     * @return the boolean
     */
    public boolean isDate() {
        return isDate;
    }

    /**
     * Sets is date.
     *
     * @param date the date
     */
    public void setIsDate(boolean date) {
        isDate = date;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Is mine boolean.
     *
     * @return the boolean
     */
    public boolean isMine() {
        return isMine;
    }

    /**
     * Sets is mine.
     *
     * @param isMine the is mine
     */
    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    /**
     * Is having link boolean.
     *
     * @return the boolean
     */
    public boolean isHavingLink() {return isHavingLink;}

    /**
     * Sets having link.
     *
     * @param havingLink the having link
     */
    public void setHavingLink(boolean havingLink) {isHavingLink = havingLink;}

    /**
     * Gets datum realm list.
     *
     * @return the datum realm list
     */
    public RealmList<Datum> getDatumRealmList() { return datumRealmList; }

    /**
     * Sets datum realm list.
     *
     * @param datumRealmList the datum realm list
     */
    public void setDatumRealmList(RealmList<Datum> datumRealmList) {
        this.datumRealmList = datumRealmList;
    }

    /**
     * Gets is delivered.
     *
     * @return the is delivered
     */
    public boolean getIsDelivered() { return isDelivered; }

    /**
     * Sets is delivered.
     *
     * @param isDelivered the is delivered
     */
    public void setIsDelivered(boolean isDelivered) { this.isDelivered = isDelivered; }

    /**
     * Gets webquery.
     *
     * @return the webquery
     */
    public String getWebquery() {
        return webquery;
    }

    /**
     * Sets webquery.
     *
     * @param webquery the webquery
     */
    public void setWebquery(String webquery) {
        this.webquery = webquery;
    }

    /**
     * Gets action type.
     *
     * @return the action type
     */
    public String getActionType() {
        return actionType;
    }

    /**
     * Sets action type.
     *
     * @param actionType the action type
     */
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets latitude.
     *
     * @param latitude the latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets longitude.
     *
     * @param longtitude the longtitude
     */
    public void setLongitude(double longtitude) {
        this.longitude = longtitude;
    }

    /**
     * Gets zoom.
     *
     * @return the zoom
     */
    public double getZoom() {
        return zoom;
    }

    /**
     * Sets zoom.
     *
     * @param zoom the zoom
     */
    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets count
     *
     * @param count the count
     */
    public void setCount(int count) {
        this.count = count;
    }
}
