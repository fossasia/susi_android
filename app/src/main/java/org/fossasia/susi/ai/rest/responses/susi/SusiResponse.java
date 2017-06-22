package org.fossasia.susi.ai.rest.responses.susi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>POJO class to parse retrofit response from susi client.</h1>
 */
public class SusiResponse {
    @SerializedName("client_id")
    @Expose
    private String clientId;
    @SerializedName("query")
    @Expose
    private String query;
    @SerializedName("query_date")
    @Expose
    private String queryDate;
    @SerializedName("answer_date")
    @Expose
    private String answerDate;
    @SerializedName("answer_time")
    @Expose
    private int answerTime;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("answers")
    @Expose
    private List<Answer> answers = new ArrayList<>();
    @SerializedName("session")
    @Expose
    private Session session;

    /**
     * Gets client id.
     *
     * @return the client id
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Gets query.
     *
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Gets query date.
     *
     * @return the query date
     */
    public String getQueryDate() {
        return queryDate;
    }

    /**
     * Gets answer date.
     *
     * @return the answer date
     */
    public String getAnswerDate() {
        return answerDate;
    }

    /**
     * Gets answer time.
     *
     * @return the answer time
     */
    public int getAnswerTime() {
        return answerTime;
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
     * Gets answers.
     *
     * @return the answers
     */
    public List<Answer> getAnswers() {
        return answers;
    }

    /**
     * Gets session.
     *
     * @return the session
     */
    public Session getSession() {
        return session;
    }
}
